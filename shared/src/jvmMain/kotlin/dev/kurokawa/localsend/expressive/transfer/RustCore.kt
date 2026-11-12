package dev.kurokawa.localsend.expressive

import java.io.File

/**
 * JNI bridge to the Rust core (`core-rust/`, crate `lsex-core`): chunk
 * planning, resume offsets and per-chunk SHA-256 for the resumable
 * transfer engine.
 *
 * The native library is OPTIONAL at runtime. CI/release builds bundle
 * `liblsex_core.so` (Android jniLibs / desktop app resources); when it is
 * absent or fails to load, [loaded] is false and [TransferMath] falls back
 * to the identical Kotlin implementation, so the app can never break
 * because of the native layer.
 */
object RustCore {
    val loaded: Boolean by lazy { loadNative() }

    private fun loadNative(): Boolean {
        // Android (jniLibs) and any -Djava.library.path setup.
        if (runCatching { System.loadLibrary("lsex_core") }.isSuccess) return true
        // Desktop packaging: the .so rides inside the app resources.
        return runCatching {
            val resource = "/native/liblsex_core.so"
            val stream = RustCore::class.java.getResourceAsStream(resource)
                ?: return@runCatching false
            val tmp = File.createTempFile("liblsex_core", ".so")
            tmp.deleteOnExit()
            stream.use { input -> tmp.outputStream().use { input.copyTo(it) } }
            System.load(tmp.absolutePath)
            true
        }.getOrDefault(false)
    }

    external fun nativeChunkCount(fileSize: Long, chunkSize: Long): Long
    external fun nativeChunkOffset(index: Long, chunkSize: Long): Long
    external fun nativeChunkLength(index: Long, fileSize: Long, chunkSize: Long): Long
    external fun nativeResumeOffset(confirmedBytes: Long, chunkSize: Long): Long
    external fun nativeSha256Hex(data: ByteArray): String
}
