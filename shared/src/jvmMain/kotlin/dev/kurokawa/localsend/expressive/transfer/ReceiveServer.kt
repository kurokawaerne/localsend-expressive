package dev.kurokawa.localsend.expressive.transfer

import dev.kurokawa.localsend.expressive.LocalDevice
import dev.kurokawa.localsend.expressive.app.AppSettings
import dev.kurokawa.localsend.expressive.app.FileProgress
import dev.kurokawa.localsend.expressive.app.IncomingOffer
import dev.kurokawa.localsend.expressive.app.TransferStatus
import dev.kurokawa.localsend.expressive.platform.platformDownloadsDir
import dev.kurokawa.localsend.expressive.protocol.FileDto
import dev.kurokawa.localsend.expressive.protocol.LocalSendJson
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
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.readAvailable
import io.ktor.utils.io.writeFully
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.io.FileOutputStream
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
    private val callbacks: Callbacks,
) {
    interface Callbacks {
        fun onPeerRegistered(dto: RegisterDto, ip: String)

        /** Ask the user (or auto-accept rule) about an incoming offer. */
        suspend fun onOffer(offer: IncomingOffer): Boolean

        fun onReceiveProgress(sessionId: String, files: List<FileProgress>)
        fun onFileReceived(sessionId: String, peer: RegisterDto, file: FileDto, savedTo: File)
        fun onSessionFinished(sessionId: String, peer: RegisterDto)
    }

    private class Session(
        val id: String,
        val peer: RegisterDto,
        val files: Map<String, FileDto>,
        val tokens: Map<String, String>,
    ) {
        val progress: MutableMap<String, FileProgress> = ConcurrentHashMap(
            files.mapValues { (id, f) -> FileProgress(id, f.fileName, f.size) },
        )
        val received: MutableSet<String> = ConcurrentHashMap.newKeySet()
    }

    private var server: EmbeddedServer<*, *>? = null

    @Volatile
    private var activeSession: Session? = null

    private val downloadSessions = ConcurrentHashMap<String, Map<String, File>>()

    fun start() {
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
        activeSession = null
        downloadSessions.clear()
    }

    private fun Application.module() {
        install(ContentNegotiation) { json(LocalSendJson) }
        routing {
            get(ProtocolConstants.PATH_INFO) {
                call.respond(localProvider().toInfoDto())
            }

            post(ProtocolConstants.PATH_REGISTER) {
                val peer = call.receive<RegisterDto>()
                callbacks.onPeerRegistered(peer, call.request.local.remoteHost)
                call.respond(localProvider().toRegisterDto())
            }

            post(ProtocolConstants.PATH_PREPARE_UPLOAD) {
                val settings = settingsProvider()
                val request = call.receive<PrepareUploadRequestDto>()

                // PIN gate first (spec: 401), then the busy check (409).
                if (settings.pin.isNotEmpty() &&
                    call.request.queryParameters["pin"] != settings.pin
                ) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }
                if (activeSession != null) {
                    call.respond(HttpStatusCode.Conflict)
                    return@post
                }

                val offer = IncomingOffer(
                    sessionId = UUID.randomUUID().toString(),
                    peer = request.info,
                    peerIp = call.request.local.remoteHost,
                    files = request.files.values.toList(),
                )
                val accepted = withTimeoutOrNull(OFFER_TIMEOUT_MS) {
                    callbacks.onOffer(offer)
                } ?: false
                if (!accepted) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@post
                }

                val tokens = request.files.keys.associateWith { UUID.randomUUID().toString() }
                val session = Session(offer.sessionId, request.info, request.files, tokens)
                activeSession = session
                callbacks.onReceiveProgress(session.id, session.progress.values.toList())
                call.respond(PrepareUploadResponseDto(session.id, tokens))
            }

            post(ProtocolConstants.PATH_UPLOAD) {
                val session = activeSession
                val sessionId = call.request.queryParameters["sessionId"]
                val fileId = call.request.queryParameters["fileId"]
                val token = call.request.queryParameters["token"]
                if (session == null || sessionId == null || fileId == null || token == null ||
                    session.id != sessionId
                ) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                val fileDto = session.files[fileId]
                if (fileDto == null || session.tokens[fileId] != token) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val target = uniqueTarget(File(platformDownloadsDir(), sanitizeFileName(fileDto.fileName)))
                val digest = MessageDigest.getInstance("SHA-256")
                var receivedBytes = 0L
                var failed = false
                try {
                    val channel = call.receiveChannel()
                    val buffer = ByteArray(BUFFER_SIZE)
                    FileOutputStream(target).use { out ->
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

                val expectedSha = fileDto.sha256
                val shaMismatch = expectedSha != null &&
                    !sha256Hex(digest.digest()).equals(expectedSha, ignoreCase = true)
                if (failed || receivedBytes != fileDto.size || shaMismatch) {
                    target.delete()
                    session.progress[fileId] = FileProgress(
                        fileId, fileDto.fileName, fileDto.size, receivedBytes, TransferStatus.FAILED,
                    )
                    callbacks.onReceiveProgress(session.id, session.progress.values.toList())
                    call.respond(if (shaMismatch) HttpStatusCode.UnprocessableEntity else HttpStatusCode.InternalServerError)
                    return@post
                }

                session.received += fileId
                session.progress[fileId] = FileProgress(
                    fileId, fileDto.fileName, fileDto.size, receivedBytes, TransferStatus.DONE,
                )
                callbacks.onReceiveProgress(session.id, session.progress.values.toList())
                callbacks.onFileReceived(session.id, session.peer, fileDto, target)
                if (session.received.size == session.files.size) {
                    activeSession = null
                    callbacks.onSessionFinished(session.id, session.peer)
                }
                call.respond(HttpStatusCode.OK)
            }

            post(ProtocolConstants.PATH_CANCEL) {
                val sessionId = call.request.queryParameters["sessionId"]
                if (activeSession?.id == sessionId) activeSession = null
                if (sessionId != null) downloadSessions.remove(sessionId)
                call.respond(HttpStatusCode.OK)
            }

            post(ProtocolConstants.PATH_PREPARE_DOWNLOAD) {
                if (!settingsProvider().downloadApi) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@post
                }
                val offered = (platformDownloadsDir().listFiles() ?: emptyArray())
                    .filter { it.isFile }
                    .associate { file ->
                        val id = UUID.randomUUID().toString()
                        id to file
                    }
                val sessionId = UUID.randomUUID().toString()
                downloadSessions[sessionId] = offered
                val files = offered.mapValues { (id, file) ->
                    FileDto(
                        id = id,
                        fileName = file.name,
                        size = file.length(),
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
                if (file == null || !file.isFile) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    "attachment; filename=\"${file.name}\"",
                )
                call.respond(object : OutgoingContent.WriteChannelContent() {
                    override val contentLength: Long = file.length()
                    override val contentType: ContentType = ContentType.Application.OctetStream
                    override suspend fun writeTo(channel: ByteWriteChannel) {
                        file.inputStream().use { input ->
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
        }
    }

    /** Avoid clobbering an existing file: "name (1).ext", "name (2).ext", … */
    private fun uniqueTarget(file: File): File {
        if (!file.exists()) return file
        val base = file.nameWithoutExtension
        val ext = file.extension
        var i = 1
        while (true) {
            val candidate = File(file.parentFile, if (ext.isEmpty()) "$base ($i)" else "$base ($i).$ext")
            if (!candidate.exists()) return candidate
            i++
        }
    }

    private companion object {
        const val BUFFER_SIZE = 64 * 1024
        const val OFFER_TIMEOUT_MS = 5 * 60 * 1000L
    }
}
