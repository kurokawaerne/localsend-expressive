package dev.kurokawa.localsend.expressive.transfer

import dev.kurokawa.localsend.expressive.LocalDevice
import dev.kurokawa.localsend.expressive.discovery.DiscoveredDevice
import dev.kurokawa.localsend.expressive.protocol.FileDto
import dev.kurokawa.localsend.expressive.protocol.InfoDto
import dev.kurokawa.localsend.expressive.protocol.LocalSendJson
import dev.kurokawa.localsend.expressive.protocol.PrepareUploadRequestDto
import dev.kurokawa.localsend.expressive.protocol.PrepareUploadResponseDto
import dev.kurokawa.localsend.expressive.protocol.ProtocolConstants
import dev.kurokawa.localsend.expressive.protocol.RegisterDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeFully
import java.io.InputStream

/** A protocol-level failure carrying the peer's HTTP status. */
class SendException(val status: HttpStatusCode, message: String) : Exception(message)

/**
 * Client side of the protocol: register/info for discovery, and the send
 * flow (prepare-upload → upload per file → optional cancel).
 *
 * HTTPS peers are verified by certificate fingerprint (see
 * [FingerprintTrustManager]) — the LocalSend trust model.
 */
class SendClient(private val trustManager: FingerprintTrustManager) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) { json(LocalSendJson) }
        engine {
            https {
                trustManager = this@SendClient.trustManager
            }
        }
    }

    private fun base(peer: DiscoveredDevice): String {
        val scheme = if (peer.info.protocol == "http") "http" else "https"
        return "$scheme://${peer.ip}:${peer.info.port}"
    }

    private fun pinFingerprint(peer: DiscoveredDevice) {
        trustManager.expected = setOf(peer.fingerprint)
    }

    /** POST /register — returns the peer's own device info (two-way discovery). */
    suspend fun register(peer: DiscoveredDevice, local: LocalDevice): RegisterDto? =
        runCatching {
            pinFingerprint(peer)
            client.post("${base(peer)}${ProtocolConstants.PATH_REGISTER}") {
                contentType(ContentType.Application.Json)
                setBody(local.toRegisterDto())
            }.body<RegisterDto>()
        }.getOrNull()

    /** GET /info — debugging / legacy discovery aid. */
    suspend fun fetchInfo(peer: DiscoveredDevice): InfoDto? =
        runCatching {
            pinFingerprint(peer)
            client.get("${base(peer)}${ProtocolConstants.PATH_INFO}").body<InfoDto>()
        }.getOrNull()

    /**
     * POST /prepare-upload. Throws [SendException] with the peer's status on
     * failure (401 = PIN required, 403 = declined, 409 = busy, …).
     */
    suspend fun prepareUpload(
        peer: DiscoveredDevice,
        local: LocalDevice,
        files: Map<String, FileDto>,
        pin: String? = null,
    ): PrepareUploadResponseDto {
        pinFingerprint(peer)
        val response: HttpResponse =
            client.post("${base(peer)}${ProtocolConstants.PATH_PREPARE_UPLOAD}") {
                if (!pin.isNullOrEmpty()) parameter("pin", pin)
                contentType(ContentType.Application.Json)
                setBody(PrepareUploadRequestDto(info = local.toRegisterDto(), files = files))
            }
        if (!response.status.isSuccess()) {
            throw SendException(response.status, "prepare-upload failed: ${response.status}")
        }
        return response.body()
    }

    /**
     * POST /upload — the whole file in one request body, streamed from
     * [openStream], reporting bytes sent through [onProgress].
     */
    suspend fun upload(
        peer: DiscoveredDevice,
        sessionId: String,
        fileId: String,
        token: String,
        size: Long,
        openStream: () -> InputStream,
        onProgress: (Long) -> Unit,
    ) {
        pinFingerprint(peer)
        val response = client.post("${base(peer)}${ProtocolConstants.PATH_UPLOAD}") {
            parameter("sessionId", sessionId)
            parameter("fileId", fileId)
            parameter("token", token)
            setBody(object : OutgoingContent.WriteChannelContent() {
                override val contentLength: Long = size
                override val contentType: ContentType = ContentType.Application.OctetStream
                override suspend fun writeTo(channel: ByteWriteChannel) {
                    openStream().use { input ->
                        val buffer = ByteArray(BUFFER_SIZE)
                        var sent = 0L
                        while (true) {
                            val n = input.read(buffer)
                            if (n == -1) break
                            if (n > 0) {
                                channel.writeFully(buffer, 0, n)
                                sent += n
                                onProgress(sent)
                            }
                        }
                    }
                    channel.flush()
                }
            })
        }
        if (!response.status.isSuccess()) {
            throw SendException(response.status, "upload failed: ${response.status}")
        }
    }

    /** POST /cancel for an in-flight session (best effort). */
    suspend fun cancel(peer: DiscoveredDevice, sessionId: String) {
        runCatching {
            pinFingerprint(peer)
            client.post("${base(peer)}${ProtocolConstants.PATH_CANCEL}") {
                parameter("sessionId", sessionId)
            }
        }
    }

    fun close() = client.close()

    private companion object {
        const val BUFFER_SIZE = 64 * 1024
    }
}
