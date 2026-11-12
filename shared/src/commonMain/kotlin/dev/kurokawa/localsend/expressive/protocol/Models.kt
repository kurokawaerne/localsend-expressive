package dev.kurokawa.localsend.expressive.protocol

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonPrimitive

/** Device types from the spec. Unknown values fall back to [desktop], like the official app. */
@Serializable(with = DeviceTypeSerializer::class)
enum class DeviceType {
    mobile, desktop, web, headless, server
}

object DeviceTypeSerializer : KSerializer<DeviceType> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("DeviceType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: DeviceType) {
        encoder.encodeString(value.name)
    }

    override fun deserialize(decoder: Decoder): DeviceType {
        val raw = if (decoder is JsonDecoder) {
            decoder.decodeJsonElement().jsonPrimitive.content
        } else {
            decoder.decodeString()
        }
        return DeviceType.entries.firstOrNull { it.name == raw } ?: DeviceType.desktop
    }
}

/** Device announcement / registration payload (multicast announce + POST /register body). */
@Serializable
data class RegisterDto(
    val alias: String,
    val version: String = ProtocolConstants.VERSION,
    val deviceModel: String? = null,
    val deviceType: DeviceType? = null,
    val fingerprint: String,
    val port: Int = ProtocolConstants.DEFAULT_PORT,
    val protocol: String = "https",
    val download: Boolean = false,
    /** Only meaningful in multicast messages: true = announcement, false = response. */
    val announce: Boolean? = null,
)

/** Response of GET /info. */
@Serializable
data class InfoDto(
    val alias: String,
    val version: String = ProtocolConstants.VERSION,
    val deviceModel: String? = null,
    val deviceType: DeviceType? = null,
    val fingerprint: String,
    val download: Boolean = false,
)

@Serializable
data class FileMetadata(
    val modified: String? = null,
    val accessed: String? = null,
)

@Serializable
data class FileDto(
    val id: String,
    val fileName: String,
    val size: Long,
    /** MIME type (legacy implementations sometimes send a plain extension). */
    val fileType: String,
    val sha256: String? = null,
    val preview: String? = null,
    val metadata: FileMetadata? = null,
)

@Serializable
data class PrepareUploadRequestDto(
    val info: RegisterDto,
    val files: Map<String, FileDto>,
)

@Serializable
data class PrepareUploadResponseDto(
    val sessionId: String,
    /** fileId -> upload token */
    val files: Map<String, String>,
)

@Serializable
data class PrepareDownloadResponseDto(
    val info: RegisterDto,
    val sessionId: String,
    val files: Map<String, FileDto>,
)

/** LSEx extension: answer to GET /api/lsex/v1/capabilities. */
@Serializable
data class LsexCapabilitiesDto(
    /** Chunked resumable uploads are available on this peer. */
    val chunked: Boolean = true,
    /** Chunk size the receiver prefers (bytes). */
    val chunkSize: Long = 1024L * 1024L,
    /** Extension version. */
    val version: String = "1",
)

/** LSEx extension: how much of one file the receiver has safely staged. */
@Serializable
data class ChunkStatusDto(
    val fileId: String,
    /** Contiguous bytes staged on the receiver, from offset 0. */
    val received: Long,
    /** Total file size the receiver expects. */
    val size: Long,
)
