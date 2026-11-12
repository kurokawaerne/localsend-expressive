package dev.kurokawa.localsend.expressive.transfer

import dev.kurokawa.localsend.expressive.RustCore
import java.security.MessageDigest

/**
 * The math behind chunked, resumable transfers — computed by the Rust core
 * when its native library is bundled, and by this identical Kotlin twin
 * otherwise. The two implementations are locked to the same test vectors
 * (Rust: `cargo test` in core-rust/; Kotlin: TransferMathTest).
 *
 * Resume semantics: only FULLY confirmed chunks count. If the receiver
 * confirms N bytes, sending resumes at the start of the chunk containing
 * byte N — a partially received chunk is always resent whole, so the
 * receiver never has to trust a torn write.
 */
object TransferMath {
    /** Wire chunk size for the LSEx resumable-transfer extension: 1 MiB. */
    const val CHUNK_SIZE: Long = 1024L * 1024L

    /** True when the Rust core is doing the math (shown in the UI footer). */
    val rustActive: Boolean get() = RustCore.loaded

    fun chunkCount(fileSize: Long, chunkSize: Long = CHUNK_SIZE): Long {
        if (RustCore.loaded) {
            runCatching { return RustCore.nativeChunkCount(fileSize, chunkSize) }
        }
        val cs = if (chunkSize <= 0) 1 else chunkSize
        if (fileSize <= 0) return 0
        return (fileSize + cs - 1) / cs
    }

    fun chunkOffset(index: Long, chunkSize: Long = CHUNK_SIZE): Long {
        if (RustCore.loaded) {
            runCatching { return RustCore.nativeChunkOffset(index, chunkSize) }
        }
        val cs = if (chunkSize <= 0) 1 else chunkSize
        return index * cs
    }

    fun chunkLength(index: Long, fileSize: Long, chunkSize: Long = CHUNK_SIZE): Long {
        if (RustCore.loaded) {
            runCatching { return RustCore.nativeChunkLength(index, fileSize, chunkSize) }
        }
        val cs = if (chunkSize <= 0) 1 else chunkSize
        val offset = index * cs
        if (offset >= fileSize) return 0
        return minOf(cs, fileSize - offset)
    }

    /** Offset to resume sending from, given the receiver's confirmed byte count. */
    fun resumeOffset(confirmedBytes: Long, chunkSize: Long = CHUNK_SIZE): Long {
        if (RustCore.loaded) {
            runCatching { return RustCore.nativeResumeOffset(confirmedBytes, chunkSize) }
        }
        val cs = if (chunkSize <= 0) 1 else chunkSize
        if (confirmedBytes <= 0) return 0
        return (confirmedBytes / cs) * cs
    }

    /** Lowercase hex SHA-256 of an in-memory buffer (one transfer chunk). */
    fun sha256Hex(data: ByteArray): String {
        if (RustCore.loaded) {
            runCatching { return RustCore.nativeSha256Hex(data) }
        }
        return sha256Hex(MessageDigest.getInstance("SHA-256").digest(data))
    }
}
