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

    private val downloadSessions = ConcurrentHashMap<String, Map<String, WebShareFile>>()

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
        downloadSessions.clear()
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
                if (sessionId != null) downloadSessions.remove(sessionId)
                call.respond(HttpStatusCode.OK)
            }

            post(ProtocolConstants.PATH_PREPARE_DOWNLOAD) {
                if (!settingsProvider().downloadApi) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@post
                }
                // Share via link serves the files the user picked for web
                // sharing in the Receive tab (the official behavior), not
                // whatever happens to sit in a folder.
                val offered = webShareProvider().associateBy { UUID.randomUUID().toString() }
                val sessionId = UUID.randomUUID().toString()
                downloadSessions[sessionId] = offered
                val files = offered.mapValues { (id, file) ->
                    FileDto(
                        id = id,
                        fileName = file.name,
                        size = file.size,
                        fileType = guessFileType(file.name),
                    )
                }
                call.respond(
                    PrepareDownloadResponseDto(localProvider().toRegisterDto(), sessionId, files),
                )
            }

            get(ProtocolConstants.PATH_DOWNLOAD) {
                val sessionId = call.request.queryParameters["sessionId"]
                val fileId = call.request.queryParameters["fileId"]
                val file = sessionId?.let { downloadSessions[it] }?.get(fileId)
                if (file == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    "attachment; filename=\"${file.name}\"",
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

            // Share-via-link landing page for browsers (the official app
            // serves one too): lists the shared files with download links.
            get("/") {
                if (!settingsProvider().downloadApi) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                val alias = localProvider().alias
                val shared = webShareProvider()
                val items = shared.joinToString("") { f ->
                    "<li>${f.name} (${f.size} bytes)</li>"
                }.ifEmpty { "<li><i>Nothing shared yet — pick files in the LSEx Receive tab.</i></li>" }
                call.respondText(
                    "<!doctype html><html><head><meta charset=\"utf-8\">" +
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" +
                        "<title>LSEx — $alias</title></head><body style=\"font-family:sans-serif;margin:2em\">" +
                        "<h1>LSEx — $alias</h1><p>Files shared via link:</p><ul>$items</ul>" +
                        "<p>Use the LocalSend app or the Download API " +
                        "(POST /api/localsend/v2/prepare-download) to fetch them.</p></body></html>",
                    ContentType.Text.Html,
                )
            }
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
