package dev.kurokawa.localsend.expressive.protocol

import kotlinx.serialization.json.Json
import kotlin.random.Random

/** LocalSend Protocol v2.2 constants. Spec: https://github.com/localsend/protocol */
object ProtocolConstants {
    // The wire version current official builds (1.17+/1.18, Rust core) send.
    // The spec document is "Protocol v2.2"; its JSON examples still show the
    // older "2.0", but peers are matched by major version and 2.2 is current.
    const val VERSION = "2.2"
    const val MULTICAST_ADDRESS = "224.0.0.167"
    const val DEFAULT_PORT = 53317

    const val PATH_INFO = "/api/localsend/v2/info"
    const val PATH_REGISTER = "/api/localsend/v2/register"
    const val PATH_PREPARE_UPLOAD = "/api/localsend/v2/prepare-upload"
    const val PATH_UPLOAD = "/api/localsend/v2/upload"
    const val PATH_CANCEL = "/api/localsend/v2/cancel"
    const val PATH_PREPARE_DOWNLOAD = "/api/localsend/v2/prepare-download"
    const val PATH_DOWNLOAD = "/api/localsend/v2/download"
}

/**
 * Shared JSON config for protocol traffic. The official app evolves its DTOs,
 * so parsing must tolerate unknown fields and missing optional values.
 */
val LocalSendJson = Json {
    ignoreUnknownKeys = true
    // The official receiver (Rust core, serde) REQUIRES every RegisterDto
    // field — version, port, protocol, download included. Omitting defaults
    // (encodeDefaults = false) made our announcements unparseable, so peers
    // silently ignored us. Always emit the full field set, like the official
    // app does.
    encodeDefaults = true
    explicitNulls = false
}

/** HTTP-mode fingerprint: a random string. (HTTPS mode uses the TLS cert SHA-256 instead.) */
fun generateRandomFingerprint(): String {
    val alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    return (1..32).map { alphabet[Random.nextInt(alphabet.length)] }.joinToString("")
}
