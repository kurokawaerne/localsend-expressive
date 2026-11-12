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
