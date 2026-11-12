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
import io.ktor.client.engine.okhttp.OkHttp
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
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext

/** A protocol-level failure carrying the peer's HTTP status. */
class SendException(val status: HttpStatusCode, message: String) : Exception(message)

/** Result of a successful prepare-upload. */
data class PreparedResult(
    /** Null when the peer accepted a message-only transfer (HTTP 204). */
    val sessionId: String?,
    /** fileId -> upload token, only for the files the peer accepted. */
    val files: Map<String, String>,
)

/**
 * Client side of the protocol: register/info for discovery, and the send
 * flow (prepare-upload → upload per file → optional cancel).
 *
 * The engine is OkHttp/JSSE on purpose: official peers use **mutual TLS** —
 * their server requests a client certificate and treats its SHA-256 as our
 * identity. JSSE can present our persisted self-signed certificate; Ktor's
 * CIO client cannot present certificates at all (and its TLS stack choked
 * on the official server's CertificateRequest — "Invalid TLS record type
 * code" errors). Server certificates are verified by fingerprint against
 * the value announced in discovery ([FingerprintTrustManager]), which is
 * the LocalSend trust model; hostname verification is therefore disabled.
 */
class SendClient(
    private val trustManager: FingerprintTrustManager,
    private val keyStoreProvider: () -> KeyStore?,
) {
    private val clientDelegate = lazy {
        val keyStore = keyStoreProvider()
        val keyManagers = keyStore?.let { store ->
            KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).apply {
                init(store, CertManager.PASSWORD.toCharArray())
            }.keyManagers
        }
        val sslContext = SSLContext.getInstance("TLS").apply {
            init(keyManagers, arrayOf(trustManager), SecureRandom())
        }
        HttpClient(OkHttp) {
            install(ContentNegotiation) { json(LocalSendJson) }
            engine {
                config {
                    sslSocketFactory(sslContext.socketFactory, trustManager)
                    hostnameVerifier { _, _ -> true }
                }
            }
        }
    }

    private val client: HttpClient get() = clientDelegate.value

    private fun base(peer: DiscoveredDevice): String {
        val scheme = if (peer.info.protocol == "http") "http" else "https"
        return "$scheme://${peer.ip}:${peer.info.port}"
    }

    private fun pinFingerprint(peer: DiscoveredDevice) {
        // An empty fingerprint means "unknown peer" (manual IP probe):
        // accept the certificate and learn the fingerprint from /info.
        trustManager.expected =
            if (peer.fingerprint.isEmpty()) emptySet() else setOf(peer.fingerprint)
    }

    /**
     * POST /register — the peer answers with its InfoDto (two-way
     * discovery). Port/protocol are NOT part of that response; callers keep
     * the values from the announcement (or the probed port).
     */
    suspend fun register(peer: DiscoveredDevice, local: LocalDevice): InfoDto? =
        runCatching {
            pinFingerprint(peer)
            client.post("${base(peer)}${ProtocolConstants.PATH_REGISTER}") {
                contentType(ContentType.Application.Json)
                setBody(local.toRegisterDto())
            }.body<InfoDto>()
        }.getOrNull()

    /** GET /info — debugging / legacy discovery aid. */
    suspend fun fetchInfo(peer: DiscoveredDevice): InfoDto? =
        runCatching {
            pinFingerprint(peer)
            client.get("${base(peer)}${ProtocolConstants.PATH_INFO}").body<InfoDto>()
        }.getOrNull()

    /**
     * POST /prepare-upload. Throws [SendException] with the peer's status on
     * failure (401 = PIN required, 403 = declined, 409 = busy, 429 = PIN
     * cooldown, …). A 204 response means a message-only transfer was
     * accepted and nothing needs uploading.
     */
    suspend fun prepareUpload(
        peer: DiscoveredDevice,
        local: LocalDevice,
        files: Map<String, FileDto>,
        pin: String? = null,
    ): PreparedResult {
        pinFingerprint(peer)
        val response: HttpResponse =
            client.post("${base(peer)}${ProtocolConstants.PATH_PREPARE_UPLOAD}") {
                if (!pin.isNullOrEmpty()) parameter("pin", pin)
                contentType(ContentType.Application.Json)
                setBody(PrepareUploadRequestDto(info = local.toRegisterDto(), files = files))
            }
        if (response.status == HttpStatusCode.NoContent) {
            return PreparedResult(sessionId = null, files = emptyMap())
        }
        if (!response.status.isSuccess()) {
            throw SendException(response.status, "prepare-upload failed: ${response.status}")
        }
        val body = response.body<PrepareUploadResponseDto>()
        return PreparedResult(body.sessionId, body.files)
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

    fun close() {
        if (clientDelegate.isInitialized()) runCatching { clientDelegate.value.close() }
    }

    private companion object {
        const val BUFFER_SIZE = 64 * 1024
    }
}
