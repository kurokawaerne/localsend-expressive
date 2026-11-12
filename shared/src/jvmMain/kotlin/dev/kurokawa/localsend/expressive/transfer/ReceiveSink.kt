package dev.kurokawa.localsend.expressive.transfer

import java.io.OutputStream

/**
 * Where one incoming file lands, abstracted per platform.
 *
 * Desktop writes a real file under the destination folder (via a `.part`
 * temp + rename on commit). Android writes through MediaStore so received
 * files are VISIBLE — public Downloads, or Pictures/Movies when "Save to
 * Gallery" applies (v0.3.0 wrote to app-private storage, where received
 * files "didn't show anywhere" — Ernest's report).
 */
interface ReceiveTarget {
    /** Human-readable location shown in the UI/history (e.g. "Download/Photos/a.jpg"). */
    val displayName: String

    fun openOutput(): OutputStream

    /** Publish the file (rename temp / clear MediaStore pending flag). */
    fun commit()

    /** Discard a partial or rejected file. */
    fun abort()
}

/**
 * Create the target for one incoming file.
 *
 * @param relativePath sanitized "folder/sub/file.ext" path from the sender
 * @param mimeType the FileDto file type (may be a bare extension)
 * @param saveToGallery route images/videos to the gallery collections
 * @param destinationFolder custom receive folder ("" = platform default);
 *        on Android only a subfolder NAME under Download is honored
 *        (scoped storage), on desktop a full path
 */
expect fun createReceiveTarget(
    relativePath: String,
    mimeType: String,
    size: Long,
    saveToGallery: Boolean,
    destinationFolder: String,
): ReceiveTarget

/** A file offered through the Download API (share via link). */
class WebShareFile(
    val name: String,
    val size: Long,
    val open: () -> java.io.InputStream,
)

/**
 * On-disk staging for ONE file of a chunked (LSEx extension) receive.
 *
 * Chunks land in a random-access temp file; [contiguousLength] is the
 * byte count safely held from offset 0 — the value the sender resumes
 * from. When the file is complete, [publishTo] streams the staged bytes
 * into the real [ReceiveTarget] (MediaStore on Android, a file on
 * desktop), so chunked receives end up exactly where classic ones do.
 */
interface ChunkStaging {
    fun contiguousLength(): Long
    fun writeAt(offset: Long, data: ByteArray, dataOffset: Int, length: Int)

    /** Copy the staged bytes into [target], feeding [digest] if given; returns the byte count copied. */
    fun publishTo(target: ReceiveTarget, digest: java.security.MessageDigest? = null): Long
    fun abort()
}

/** Directory chunk staging files live in (wiped when the server starts: sessions are in-memory). */
expect fun chunkStagingDir(): java.io.File

/** Create fresh staging for one file of one session (any previous file is discarded). */
fun createChunkStaging(sessionId: String, fileId: String): ChunkStaging {
    val dir = chunkStagingDir().apply { mkdirs() }
    val safe = "$sessionId-$fileId".replace(Regex("[^A-Za-z0-9._-]"), "_")
    val file = java.io.File(dir, "$safe.part")
    file.delete()
    return FileChunkStaging(file)
}

/** Remove every staged chunk (called on server start; sessions never survive a restart). */
fun clearChunkStaging() {
    runCatching { chunkStagingDir().deleteRecursively() }
}

private class FileChunkStaging(private val file: java.io.File) : ChunkStaging {
    private val raf = java.io.RandomAccessFile(file, "rw")
    private var contiguous = 0L
    private var closed = false

    @Synchronized
    override fun contiguousLength(): Long = contiguous

    @Synchronized
    override fun writeAt(offset: Long, data: ByteArray, dataOffset: Int, length: Int) {
        if (closed) return
        raf.seek(offset)
        raf.write(data, dataOffset, length)
        // Sequential chunks extend the frontier; a resent chunk overlaps
        // it and may extend it too. Gaps (shouldn't happen with a
        // sequential sender) never advance the frontier.
        if (offset <= contiguous && offset + length > contiguous) {
            contiguous = offset + length
        }
    }

    @Synchronized
    override fun publishTo(target: ReceiveTarget, digest: java.security.MessageDigest?): Long {
        var copied = 0L
        java.io.FileInputStream(file).use { input ->
            target.openOutput().use { out ->
                val buffer = ByteArray(64 * 1024)
                while (true) {
                    val n = input.read(buffer)
                    if (n == -1) break
                    if (n > 0) {
                        out.write(buffer, 0, n)
                        digest?.update(buffer, 0, n)
                        copied += n
                    }
                }
            }
        }
        close()
        file.delete()
        return copied
    }

    @Synchronized
    override fun abort() {
        close()
        file.delete()
    }

    private fun close() {
        if (!closed) {
            closed = true
            runCatching { raf.close() }
        }
    }
}
