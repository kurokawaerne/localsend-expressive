package dev.kurokawa.localsend.expressive.transfer

import java.io.File

private val MIME_BY_EXTENSION = mapOf(
    "txt" to "text/plain",
    "md" to "text/markdown",
    "html" to "text/html",
    "json" to "application/json",
    "pdf" to "application/pdf",
    "zip" to "application/zip",
    "gz" to "application/gzip",
    "tar" to "application/x-tar",
    "zst" to "application/zstd",
    "apk" to "application/vnd.android.package-archive",
    "jpg" to "image/jpeg",
    "jpeg" to "image/jpeg",
    "png" to "image/png",
    "gif" to "image/gif",
    "webp" to "image/webp",
    "svg" to "image/svg+xml",
    "mp3" to "audio/mpeg",
    "ogg" to "audio/ogg",
    "wav" to "audio/wav",
    "mp4" to "video/mp4",
    "mkv" to "video/x-matroska",
    "webm" to "video/webm",
    "mov" to "video/quicktime",
)

/** Best-effort MIME type from the file extension (FileDto.fileType). */
fun guessFileType(fileName: String): String =
    MIME_BY_EXTENSION[fileName.substringAfterLast('.', "").lowercase()]
        ?: "application/octet-stream"

/** Strip any directory part a (possibly hostile) peer put in a file name. */
fun sanitizeFileName(name: String): String =
    File(name).name.ifBlank { "file" }

/**
 * Sanitize a peer-supplied relative path (folder sends put the folder
 * structure in FileDto.fileName, e.g. "Photos/2026/img 1.jpg"). Each
 * segment is cleaned, "." and ".." are dropped (no escaping the receive
 * folder, ever), and the result is joined with '/'.
 */
fun sanitizeRelativePath(name: String): String {
    val segments = name.replace('\\', '/').split('/')
        .map { it.trim() }
        .filter { it.isNotEmpty() && it != "." && it != ".." }
        .map { segment ->
            segment.replace(Regex("[\\\\/:*?\"<>|\\u0000-\\u001F]"), "_")
                .ifBlank { "_" }
        }
    return segments.joinToString("/").ifBlank { "file" }
}
