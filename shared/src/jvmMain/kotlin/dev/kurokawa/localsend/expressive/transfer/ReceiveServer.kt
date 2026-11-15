package dev.kurokawa.localsend.expressive.transfer

import dev.kurokawa.localsend.expressive.LocalDevice
import dev.kurokawa.localsend.expressive.app.AppSettings
import dev.kurokawa.localsend.expressive.app.FileProgress
import dev.kurokawa.localsend.expressive.app.IncomingOffer
import dev.kurokawa.localsend.expressive.app.TransferStatus
import dev.kurokawa.localsend.expressive.protocol.ChunkStatusDto
import dev.kurokawa.localsend.expressive.protocol.FileDto
import dev.kurokawa.localsend.expressive.protocol.LocalSendJson
import dev.kurokawa.localsend.expressive.protocol.LsexCapabilitiesDto
import dev.kurokawa.localsend.expressive.protocol.PrepareDownloadResponseDto
import dev.kurokawa.localsend.expressive.protocol.PrepareUploadRequestDto
import dev.kurokawa.localsend.expressive.protocol.PrepareUploadResponseDto
import dev.kurokawa.localsend.expressive.protocol.ProtocolConstants
import dev.kurokawa.localsend.expressive.protocol.RegisterDto
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.request.receiveChannel
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.readAvailable
import io.ktor.utils.io.writeFully
import kotlinx.coroutines.withTimeoutOrNull
import java.security.KeyStore
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * The receiving server: full Protocol v2.2 surface.
 *
 * - /info, /register (two-way discovery)
 * - /prepare-upload with PIN (401), busy (409) and a user accept gate (403)
 * - /upload streaming to disk with sha256 verification (422 on mismatch)
 * - /cancel
 * - Download API: /prepare-download, /download (M5)
 *
 * Runs over HTTPS with the self-signed certificate when enabled in settings,
 * matching the official app's default mode.
 */
class ReceiveServer(
    private val localProvider: () -> LocalDevice,
    private val settingsProvider: () -> AppSettings,
    private val keyStoreProvider: () -> KeyStore?,
    /** Network allow/block lists from settings, enforced on every route. */
    private val ipAllowed: (String) -> Boolean = { true },
    /** Files offered through the Download API (share via link). */
    private val webShareProvider: () -> List<WebShareFile> = { emptyList() },
    private val callbacks: Callbacks,
) {
    interface Callbacks {
        fun onPeerRegistered(dto: RegisterDto, ip: String)

        /** A text message arrived (message-only prepare-upload, answered 204). */
        fun onMessage(peer: RegisterDto, text: String)

        /**
         * Ask the user (or an auto-accept rule) about an incoming offer.
         * Returns the accepted file ids (the official accept dialog allows
         * per-file selection), or null to decline the whole offer.
         */
        suspend fun onOffer(offer: IncomingOffer): Set<String>?

        fun onReceiveProgress(sessionId: String, files: List<FileProgress>)

        /** [savedUri] lets History open the file later (content URI / path). */
        fun onFileReceived(
            sessionId: String,
            peer: RegisterDto,
            file: FileDto,
            savedToDisplay: String,
            savedUri: String?,
        )
        fun onSessionFinished(sessionId: String, peer: RegisterDto)

        /**
         * A browser asked to download the via-link shared files
         * (POST /prepare-download). The official flow: the request
         * pends until the app answers — true accepts (the file list
         * is served and the session opens), false declines (403).
         */
        suspend fun onWebDownloadRequest(ip: String, userAgent: String?): Boolean
    }

    private class Session(
        val id: String,
        val peer: RegisterDto,
        /** Uploads are only accepted from the IP that opened the session. */
        val peerIp: String,
        val files: Map<String, FileDto>,
        val tokens: Map<String, String>,
    ) {
        val progress: MutableMap<String, FileProgress> = ConcurrentHashMap(
            files.mapValues { (id, f) -> FileProgress(id, f.fileName, f.size) },
        )
        val received: MutableSet<String> = ConcurrentHashMap.newKeySet()

        /** Chunk staging per file (LSEx resumable extension only). */
        val staging: MutableMap<String, ChunkStaging> = ConcurrentHashMap()

        /** Last upload activity; idle sessions expire so a dead sender can't block the slot forever. */
        @Volatile
        var lastActivityMillis: Long = System.currentTimeMillis()

        fun touch() {
            lastActivityMillis = System.currentTimeMillis()
        }

        fun abortStaging() {
            staging.values.forEach { runCatching { it.abort() } }
            staging.clear()
        }
    }

    private var server: EmbeddedServer<*, *>? = null

    @Volatile
    private var activeSession: Session? = null

    /** A web download session (upstream model): keyed by the client's IP. */
    private class WebSession(val ip: String) {
        @Volatile
        var accepted: Boolean = false
    }

    private val webSessions = ConcurrentHashMap<String, WebSession>()

    /** Wrong-PIN count per IP for the download API (upstream check_pin). */
    private val webPinAttempts = ConcurrentHashMap<String, Int>()

    /**
     * The offered files with their stable ids. The official server
     * assigns the ids once when the share is built; ours are cached
     * and rebuilt only when the shared set (name + size list) changes,
     * so a page reload re-fetches the same ids.
     */
    @Volatile
    private var offeredCache: Pair<List<Pair<String, Long>>, Map<String, WebShareFile>>? = null

    private fun offeredFiles(): Map<String, WebShareFile> {
        val files = webShareProvider()
        val signature = files.map { it.name to it.size }
        offeredCache?.let { if (it.first == signature) return it.second }
        val map = files.associateBy { UUID.randomUUID().toString() }
        offeredCache = signature to map
        return map
    }

    /** Web send (the download page + API) is live only while files are offered. */
    private fun webSendActive(): Boolean =
        settingsProvider().downloadApi && webShareProvider().isNotEmpty()

    /**
     * The upstream PIN gate for the download API: 429 once 3 wrong
     * PINs are on record for the IP (checked before the PIN itself),
     * 401 on a missing or wrong PIN, success clears the record.
     * Returns the error status to answer, or null to proceed.
     */
    private fun webPinError(remoteIp: String, given: String?): HttpStatusCode? {
        val required = settingsProvider().pin
        if (required.isEmpty()) return null
        val count = webPinAttempts[remoteIp] ?: 0
        if (count >= MAX_PIN_FAILURES) return HttpStatusCode.TooManyRequests
        return when {
            given == null -> HttpStatusCode.Unauthorized
            given == required -> {
                webPinAttempts.remove(remoteIp)
                null
            }
            else -> {
                webPinAttempts[remoteIp] = count + 1
                HttpStatusCode.Unauthorized
            }
        }
    }

    private fun prepareDownloadResponse(sessionId: String): PrepareDownloadResponseDto {
        val files = offeredFiles().mapValues { (id, file) ->
            FileDto(
                id = id,
                fileName = file.name,
                size = file.size,
                fileType = guessFileType(file.name),
            )
        }
        return PrepareDownloadResponseDto(
            localProvider().toInfoDto().copy(download = true),
            sessionId,
            files,
        )
    }

    /** Failed PIN attempts per peer IP: (consecutive failures, last failure millis). */
    private val pinAttempts = ConcurrentHashMap<String, Pair<Int, Long>>()

    fun start() {
        // Sessions live in memory, so staged chunks from a previous run
        // can never be resumed — start from a clean staging area.
        clearChunkStaging()
        val local = localProvider()
        val keyStore = keyStoreProvider()
        // Netty engine: it terminates TLS. (Ktor's CIO server cannot do HTTPS
        // at all — it throws UnsupportedOperationException at startup.)
        server = embeddedServer(Netty, configure = {
            if (local.protocol == "https" && keyStore != null) {
                sslConnector(
                    keyStore = keyStore,
                    keyAlias = CertManager.ALIAS,
                    keyStorePassword = { CertManager.PASSWORD.toCharArray() },
                    privateKeyPassword = { CertManager.PASSWORD.toCharArray() },
                ) { port = local.port }
            } else {
                connector { port = local.port }
            }
        }) { module() }.start(wait = false)
    }

    fun stop() {
        server?.stop(500, 1000)
        server = null
        activeSession?.abortStaging()
        activeSession = null
        webSessions.clear()
    }

    /**
     * Shared validation for the LSEx chunk routes: the session must be the
     * active one, the token must match, the file must belong to the
     * session, and the caller must be the IP that opened the session.
     * Returns (session, fileDto) or null (caller answers 403).
     */
    private fun chunkSession(
        sessionId: String?,
        fileId: String?,
        token: String?,
        remoteIp: String,
    ): Pair<Session, FileDto>? {
        val session = activeSession ?: return null
        if (sessionId == null || fileId == null || token == null) return null
        if (session.id != sessionId || session.peerIp != remoteIp) return null
        val fileDto = session.files[fileId] ?: return null
        if (session.tokens[fileId] != token) return null
        return session to fileDto
    }

    private fun Application.module() {
        install(ContentNegotiation) { json(LocalSendJson) }
        routing {
            get(ProtocolConstants.PATH_INFO) {
                call.respond(localProvider().toInfoDto())
            }

            post(ProtocolConstants.PATH_REGISTER) {
                val remoteIp = call.request.local.remoteHost
                if (!ipAllowed(remoteIp)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@post
                }
                val peer = call.receive<RegisterDto>()
                callbacks.onPeerRegistered(peer, remoteIp)
                // The official app answers /register with its InfoDto shape
                // (no port/protocol fields) — match it exactly.
                call.respond(localProvider().toInfoDto())
            }

            post(ProtocolConstants.PATH_PREPARE_UPLOAD) {
                val settings = settingsProvider()
                val request = call.receive<PrepareUploadRequestDto>()
                val remoteIp = call.request.local.remoteHost
                if (!ipAllowed(remoteIp)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@post
                }

                // PIN gate first (spec: 401; 429 once a peer keeps guessing —
                // the official app locks out after 3 failed attempts).
                if (settings.pin.isNotEmpty()) {
                    val given = call.request.queryParameters["pin"]
                    if (given != settings.pin) {
                        val (count, lastFail) = pinAttempts[remoteIp] ?: (0 to 0L)
                        val now = System.currentTimeMillis()
                        val failures = if (now - lastFail > PIN_COOLDOWN_MS) 1 else count + 1
                        pinAttempts[remoteIp] = failures to now
                        call.respond(
                            if (failures >= MAX_PIN_FAILURES) {
                                HttpStatusCode.TooManyRequests
                            } else {
                                HttpStatusCode.Unauthorized
                            },
                        )
                        return@post
                    }
                    pinAttempts.remove(remoteIp)
                }

                if (request.files.isEmpty()) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                // Message-only transfer: every file carries its text in
                // `preview` and expects no upload. The official app answers
                // 204 and shows the text as a message — do the same.
                val isMessageOnly = request.files.values.all { dto ->
                    dto.preview != null &&
                        (dto.fileType.startsWith("text") ||
                            dto.size == dto.preview.toByteArray(Charsets.UTF_8).size.toLong())
                }
                if (isMessageOnly) {
                    request.files.values.forEach { dto ->
                        callbacks.onMessage(request.info, dto.preview.orEmpty())
                    }
                    call.respond(HttpStatusCode.NoContent)
                    return@post
                }

                // A session whose sender vanished (app killed, Wi-Fi drop)
                // must not block the single session slot forever: expire it
                // after an idle timeout so a retry can start fresh.
                val current = activeSession
                if (current != null &&
                    System.currentTimeMillis() - current.lastActivityMillis > SESSION_IDLE_TIMEOUT_MS
                ) {
                    current.abortStaging()
                    activeSession = null
                }

                // The SAME peer (same fingerprint, same IP) re-preparing —
                // e.g. its app restarted mid-send and it is retrying —
                // replaces its own stale session immediately instead of
                // eating a 409. A different peer still gets the 409.
                val stale = activeSession
                if (stale != null &&
                    stale.peer.fingerprint == request.info.fingerprint &&
                    stale.peerIp == remoteIp
                ) {
                    stale.abortStaging()
                    activeSession = null
                }

                if (activeSession != null) {
                    call.respond(HttpStatusCode.Conflict)
                    return@post
                }

                val offer = IncomingOffer(
                    sessionId = UUID.randomUUID().toString(),
                    peer = request.info,
                    peerIp = remoteIp,
                    files = request.files.values.toList(),
                )
                val acceptedIds = withTimeoutOrNull(OFFER_TIMEOUT_MS) {
                    callbacks.onOffer(offer)
                }
                if (acceptedIds.isNullOrEmpty()) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@post
                }

                // Per-file selection: tokens are issued only for accepted
                // files; the session tracks just those (official semantics).
                val acceptedFiles = request.files.filterKeys { it in acceptedIds }
                val tokens = acceptedFiles.keys.associateWith { UUID.randomUUID().toString() }
                val session = Session(offer.sessionId, request.info, remoteIp, acceptedFiles, tokens)
                activeSession = session
                callbacks.onReceiveProgress(session.id, session.progress.values.toList())
                call.respond(PrepareUploadResponseDto(session.id, tokens))
            }

            post(ProtocolConstants.PATH_UPLOAD) {
                val session = activeSession
                val sessionId = call.request.queryParameters["sessionId"]
                val fileId = call.request.queryParameters["fileId"]
                val token = call.request.queryParameters["token"]
                if (sessionId == null || fileId == null || token == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                // Wrong/unknown session, token or a different sender IP than
                // the one that opened the session: 403 (official semantics).
                if (session == null || session.id != sessionId ||
                    session.peerIp != call.request.local.remoteHost
                ) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@post
                }
                val fileDto = session.files[fileId]
                if (fileDto == null || session.tokens[fileId] != token) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@post
                }

                // Folder sends carry the structure in fileName; the sink
                // decides where it lands (MediaStore on Android, a real
                // file on desktop) so received files are always visible.
                val settings = settingsProvider()
                val target = createReceiveTarget(
                    relativePath = sanitizeRelativePath(fileDto.fileName),
                    mimeType = fileDto.fileType,
                    size = fileDto.size,
                    saveToGallery = settings.saveToGallery,
                    destinationFolder = settings.destinationFolder,
                    destinationTreeUri = settings.destinationTreeUri,
                )
                val digest = MessageDigest.getInstance("SHA-256")
                var receivedBytes = 0L
                var failed = false
                try {
                    val channel = call.receiveChannel()
                    val buffer = ByteArray(BUFFER_SIZE)
                    target.openOutput().use { out ->
                        while (true) {
                            val n = channel.readAvailable(buffer, 0, buffer.size)
                            if (n == -1) break
                            if (n > 0) {
                                out.write(buffer, 0, n)
                                digest.update(buffer, 0, n)
                                receivedBytes += n
                                session.progress[fileId] = FileProgress(
                                    fileId, fileDto.fileName, fileDto.size,
                                    receivedBytes, TransferStatus.ACTIVE,
                                )
                                callbacks.onReceiveProgress(session.id, session.progress.values.toList())
                            }
                        }
                    }
                } catch (e: Exception) {
                    failed = true
                }

                // Ernest (v0.10.0): the checking phase is a real,
                // visible state — the UI shows the wavy indicator only
                // here, like UDYAT's completed-file check. Checking is
                // the verifyChecksums TOGGLE: users who leave it off
                // never see this state.
                if (settings.verifyChecksums && fileDto.sha256 != null) {
                    session.progress[fileId] = FileProgress(
                        fileId, fileDto.fileName, fileDto.size,
                        receivedBytes, TransferStatus.VERIFYING,
                    )
                    callbacks.onReceiveProgress(session.id, session.progress.values.toList())
                }

                // Checksum verification is a setting in the official app:
                // when off, a mismatched hash does not reject the file.
                val expectedSha = fileDto.sha256
                val shaMismatch = settings.verifyChecksums && expectedSha != null &&
                    !sha256Hex(digest.digest()).equals(expectedSha, ignoreCase = true)
                if (failed || receivedBytes != fileDto.size || shaMismatch) {
                    runCatching { target.abort() }
                    session.progress[fileId] = FileProgress(
                        fileId, fileDto.fileName, fileDto.size, receivedBytes, TransferStatus.FAILED,
                    )
                    callbacks.onReceiveProgress(session.id, session.progress.values.toList())
                    call.respond(if (shaMismatch) HttpStatusCode.UnprocessableEntity else HttpStatusCode.InternalServerError)
                    return@post
                }
                runCatching { target.commit() }
                session.touch()

                session.received += fileId
                session.progress[fileId] = FileProgress(
                    fileId, fileDto.fileName, fileDto.size, receivedBytes, TransferStatus.DONE,
                )
                callbacks.onReceiveProgress(session.id, session.progress.values.toList())
                callbacks.onFileReceived(session.id, session.peer, fileDto, target.displayName, target.resultUri)
                if (session.received.size == session.files.size) {
                    activeSession = null
                    callbacks.onSessionFinished(session.id, session.peer)
                }
                call.respond(HttpStatusCode.OK)
            }

            // ── LSEx extension: chunked, resumable uploads ──────────────
            // Probed by LSEx senders before uploading; official peers never
            // call these and keep using the whole-file /upload above.

            get(ProtocolConstants.PATH_LSEX_CAPABILITIES) {
                if (!ipAllowed(call.request.local.remoteHost)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@get
                }
                call.respond(
                    LsexCapabilitiesDto(chunked = true, chunkSize = TransferMath.CHUNK_SIZE),
                )
            }

            get(ProtocolConstants.PATH_LSEX_UPLOAD_STATUS) {
                val remoteIp = call.request.local.remoteHost
                if (!ipAllowed(remoteIp)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@get
                }
                val q = call.request.queryParameters
                val (session, fileDto) = chunkSession(
                    q["sessionId"], q["fileId"], q["token"], remoteIp,
                ) ?: run {
                    call.respond(HttpStatusCode.Forbidden)
                    return@get
                }
                val fileId = q["fileId"]!!
                val received = session.staging[fileId]?.contiguousLength() ?: 0L
                call.respond(ChunkStatusDto(fileId, received, fileDto.size))
            }

            put(ProtocolConstants.PATH_LSEX_UPLOAD_CHUNK) {
                val remoteIp = call.request.local.remoteHost
                if (!ipAllowed(remoteIp)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@put
                }
                val q = call.request.queryParameters
                val offset = q["offset"]?.toLongOrNull()
                val (session, fileDto) = chunkSession(
                    q["sessionId"], q["fileId"], q["token"], remoteIp,
                ) ?: run {
                    call.respond(HttpStatusCode.Forbidden)
                    return@put
                }
                val fileId = q["fileId"]!!
                if (offset == null || offset < 0 || offset > fileDto.size) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@put
                }
                // Read the whole chunk (≤ 1 MiB by protocol; cap generously).
                val body = java.io.ByteArrayOutputStream()
                val channel = call.receiveChannel()
                val buffer = ByteArray(BUFFER_SIZE)
                var overflow = false
                while (true) {
                    val n = channel.readAvailable(buffer, 0, buffer.size)
                    if (n == -1) break
                    if (n > 0) {
                        body.write(buffer, 0, n)
                        if (body.size() > MAX_CHUNK_BYTES) {
                            overflow = true
                            break
                        }
                    }
                }
                val bytes = body.toByteArray()
                if (overflow || offset + bytes.size > fileDto.size) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@put
                }
                // Per-chunk integrity: a torn chunk is rejected before it
                // ever touches the staging file (422, like a bad checksum).
                val chunkSha = call.request.headers[ProtocolConstants.HEADER_CHUNK_SHA256]
                if (chunkSha != null &&
                    !TransferMath.sha256Hex(bytes).equals(chunkSha, ignoreCase = true)
                ) {
                    call.respond(HttpStatusCode.UnprocessableEntity)
                    return@put
                }
                val staging = session.staging.computeIfAbsent(fileId) {
                    createChunkStaging(session.id, fileId)
                }
                staging.writeAt(offset, bytes, 0, bytes.size)
                session.touch()
                val receivedNow = staging.contiguousLength()
                session.progress[fileId] = FileProgress(
                    fileId, fileDto.fileName, fileDto.size, receivedNow, TransferStatus.ACTIVE,
                )
                callbacks.onReceiveProgress(session.id, session.progress.values.toList())
                call.respond(ChunkStatusDto(fileId, receivedNow, fileDto.size))
            }

            post(ProtocolConstants.PATH_LSEX_FINISH_FILE) {
                val remoteIp = call.request.local.remoteHost
                if (!ipAllowed(remoteIp)) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@post
                }
                val q = call.request.queryParameters
                val (session, fileDto) = chunkSession(
                    q["sessionId"], q["fileId"], q["token"], remoteIp,
                ) ?: run {
                    call.respond(HttpStatusCode.Forbidden)
                    return@post
                }
                val fileId = q["fileId"]!!
                val staging = session.staging[fileId]
                if (staging == null || staging.contiguousLength() != fileDto.size) {
                    // Not all bytes are staged yet — the sender keeps going.
                    call.respond(HttpStatusCode.Conflict)
                    return@post
                }
                val settings = settingsProvider()
                val target = createReceiveTarget(
                    relativePath = sanitizeRelativePath(fileDto.fileName),
                    mimeType = fileDto.fileType,
                    size = fileDto.size,
                    saveToGallery = settings.saveToGallery,
                    destinationFolder = settings.destinationFolder,
                    destinationTreeUri = settings.destinationTreeUri,
                )
                // With the verify toggle on, the publish pass's
                // re-read + hash of every staged byte IS the check —
                // mark it VERIFYING first (v0.10.0, Ernest's
                // wavy-only-here rule). Toggle off: no checking state.
                if (settings.verifyChecksums && fileDto.sha256 != null) {
                    session.progress[fileId] = FileProgress(
                        fileId, fileDto.fileName, fileDto.size, fileDto.size,
                        TransferStatus.VERIFYING,
                    )
                    callbacks.onReceiveProgress(session.id, session.progress.values.toList())
                }
                val digest = MessageDigest.getInstance("SHA-256")
                val copied = runCatching { staging.publishTo(target, digest) }.getOrElse {
                    runCatching { target.abort() }
                    call.respond(HttpStatusCode.InternalServerError)
                    return@post
                }
                session.staging.remove(fileId)
                val expectedSha = fileDto.sha256
                val shaMismatch = settings.verifyChecksums && expectedSha != null &&
                    !sha256Hex(digest.digest()).equals(expectedSha, ignoreCase = true)
                if (copied != fileDto.size || shaMismatch) {
                    runCatching { target.abort() }
                    session.progress[fileId] = FileProgress(
                        fileId, fileDto.fileName, fileDto.size, copied, TransferStatus.FAILED,
                    )
                    callbacks.onReceiveProgress(session.id, session.progress.values.toList())
                    call.respond(
                        if (shaMismatch) {
                            HttpStatusCode.UnprocessableEntity
                        } else {
                            HttpStatusCode.InternalServerError
                        },
                    )
                    return@post
                }
                runCatching { target.commit() }
                session.touch()
                session.received += fileId
                session.progress[fileId] = FileProgress(
                    fileId, fileDto.fileName, fileDto.size, copied, TransferStatus.DONE,
                )
                callbacks.onReceiveProgress(session.id, session.progress.values.toList())
                callbacks.onFileReceived(session.id, session.peer, fileDto, target.displayName, target.resultUri)
                if (session.received.size == session.files.size) {
                    session.abortStaging()
                    activeSession = null
                    callbacks.onSessionFinished(session.id, session.peer)
                }
                call.respond(HttpStatusCode.OK)
            }

            post(ProtocolConstants.PATH_CANCEL) {
                val sessionId = call.request.queryParameters["sessionId"]
                if (activeSession?.id == sessionId) {
                    activeSession?.abortStaging()
                    activeSession = null
                }
                if (sessionId != null) webSessions.remove(sessionId)
                call.respond(HttpStatusCode.OK)
            }

            // The Download API, with the official implementation's
            // semantics (v0.12.0 port): the session id is the client's
            // IP, an accepted client can re-fetch the list on reload,
            // the PIN gate answers 401/429, and a fresh request pends
            // until the app accepts or declines it.
            post(ProtocolConstants.PATH_PREPARE_DOWNLOAD) {
                val remoteIp = call.request.local.remoteHost
                if (!ipAllowed(remoteIp) || !webSendActive()) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@post
                }
                val sessionParam = call.request.queryParameters["sessionId"]
                if (sessionParam != null) {
                    val existing = webSessions[sessionParam]
                    if (existing != null && existing.accepted && existing.ip == remoteIp) {
                        call.respond(prepareDownloadResponse(sessionParam))
                        return@post
                    }
                }
                webPinError(remoteIp, call.request.queryParameters["pin"])?.let { status ->
                    call.respond(status)
                    return@post
                }
                // One session per IP; a new request replaces the old one.
                val session = WebSession(remoteIp)
                webSessions[remoteIp] = session
                val accepted = callbacks.onWebDownloadRequest(
                    remoteIp,
                    call.request.headers[HttpHeaders.UserAgent],
                )
                if (!accepted) {
                    webSessions.remove(remoteIp, session)
                    call.respond(HttpStatusCode.Forbidden)
                    return@post
                }
                session.accepted = true
                call.respond(prepareDownloadResponse(remoteIp))
            }

            get(ProtocolConstants.PATH_DOWNLOAD) {
                val remoteIp = call.request.local.remoteHost
                val sessionId = call.request.queryParameters["sessionId"]
                val fileId = call.request.queryParameters["fileId"]
                if (sessionId == null || fileId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val session = webSessions[sessionId]
                if (session == null || !session.accepted || session.ip != remoteIp) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@get
                }
                val file = offeredFiles()[fileId]
                if (file == null) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@get
                }
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    "attachment; filename=\"${encodeDispositionFileName(file.name)}\"",
                )
                call.respond(object : OutgoingContent.WriteChannelContent() {
                    override val contentLength: Long = file.size
                    override val contentType: ContentType = ContentType.Application.OctetStream
                    override suspend fun writeTo(channel: ByteWriteChannel) {
                        file.open().use { input ->
                            val buffer = ByteArray(BUFFER_SIZE)
                            while (true) {
                                val n = input.read(buffer)
                                if (n == -1) break
                                if (n > 0) channel.writeFully(buffer, 0, n)
                            }
                        }
                        channel.flush()
                    }
                })
            }

            // Share / receive via link, the official implementation's
            // pages (v0.12.0 port, see WebPages): the download page
            // while files are offered, else the upload page while the
            // link is open, else the 403 page. Uploads from the browser
            // go through the normal /prepare-upload + /upload flow, so
            // the app's approval rules (accept dialog, Quick Save,
            // PIN, link auto-accept) apply unchanged.
            get("/") {
                when {
                    webSendActive() ->
                        call.respondText(WebPages.DOWNLOAD, ContentType.Text.Html)
                    settingsProvider().downloadApi ->
                        call.respondText(WebPages.UPLOAD, ContentType.Text.Html)
                    else ->
                        call.respondText(
                            WebPages.ERROR_403,
                            ContentType.Text.Html,
                            HttpStatusCode.Forbidden,
                        )
                }
            }

            get("/i18n.json") {
                if (!settingsProvider().downloadApi) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@get
                }
                call.respondText(
                    webI18nJson(settingsProvider().language),
                    ContentType.Application.Json,
                )
            }
        }
    }

    /**
     * The upstream Content-Disposition encoding: RFC 2396 component
     * percent-encoding of the UTF-8 bytes, '/' first flattened to '-'.
     */
    private fun encodeDispositionFileName(raw: String): String {
        val name = raw.replace('/', '-')
        val keep = "-_.!~*'()"
        val sb = StringBuilder()
        for (byte in name.toByteArray(Charsets.UTF_8)) {
            val value = byte.toInt() and 0xFF
            val c = value.toChar()
            if (c in 'A'..'Z' || c in 'a'..'z' || c in '0'..'9' || c in keep) {
                sb.append(c)
            } else {
                sb.append('%').append(String.format("%02X", value))
            }
        }
        return sb.toString()
    }

    /**
     * The strings the official web pages fetch from /i18n.json
     * (upstream WebI18n; English defaults, Spanish when the app
     * language is Spanish).
     */
    private fun webI18nJson(language: String): String {
        val strings = if (language == "es") {
            listOf(
                "waiting" to "Esperando respuesta…",
                "enterPin" to "Introduce el PIN",
                "invalidPin" to "PIN no válido",
                "tooManyAttempts" to "Demasiados intentos",
                "rejected" to "Rechazado",
                "uploadRejected" to "El destinatario ha rechazado la solicitud.",
                "busy" to "El destinatario está ocupado con otra solicitud.",
                "files" to "Archivos",
                "fileName" to "Nombre del archivo",
                "size" to "Tamaño",
                "dropHint" to "Coloca aquí los elementos a compartir.",
            )
        } else {
            listOf(
                "waiting" to "Waiting for response…",
                "enterPin" to "Enter PIN",
                "invalidPin" to "Invalid PIN",
                "tooManyAttempts" to "Too many attempts",
                "rejected" to "Rejected",
                "uploadRejected" to "The recipient has rejected the request.",
                "busy" to "The recipient is busy with another request.",
                "files" to "Files",
                "fileName" to "File name",
                "size" to "Size",
                "dropHint" to "Place items to share.",
            )
        }
        fun esc(value: String): String = value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
        return strings.joinToString(prefix = "{", postfix = "}", separator = ",") {
            "\"${it.first}\":\"${esc(it.second)}\""
        }
    }

    private companion object {
        const val BUFFER_SIZE = 64 * 1024
        const val OFFER_TIMEOUT_MS = 5 * 60 * 1000L
        const val MAX_PIN_FAILURES = 3
        const val PIN_COOLDOWN_MS = 60_000L
        /** Hard cap on one chunk body (protocol chunk is 1 MiB). */
        const val MAX_CHUNK_BYTES = 16 * 1024 * 1024
        /** A receive session with no upload activity for this long is expired. */
        const val SESSION_IDLE_TIMEOUT_MS = 10 * 60 * 1000L
    }
}
