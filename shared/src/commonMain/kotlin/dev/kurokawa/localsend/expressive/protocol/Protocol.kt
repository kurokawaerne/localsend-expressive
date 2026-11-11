package dev.kurokawa.localsend.expressive.protocol

import kotlinx.serialization.json.Json
import kotlin.random.Random

/** LocalSend Protocol v2.2 constants. Spec: https://github.com/localsend/protocol */
object ProtocolConstants {
    const val VERSION = "2.0"
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
    encodeDefaults = false
    explicitNulls = false
}

/** HTTP-mode fingerprint: a random string. (HTTPS mode uses the TLS cert SHA-256 instead.) */
fun generateRandomFingerprint(): String {
    val alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    return (1..32).map { alphabet[Random.nextInt(alphabet.length)] }.joinToString("")
}
