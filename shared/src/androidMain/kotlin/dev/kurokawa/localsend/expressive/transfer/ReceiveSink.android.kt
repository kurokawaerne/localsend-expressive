package dev.kurokawa.localsend.expressive.transfer

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.documentfile.provider.DocumentFile
import dev.kurokawa.localsend.expressive.platform.AppContextHolder
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

actual fun chunkStagingDir(): File {
    val context = AppContextHolder.appContext
        ?: error("AppContextHolder not initialised — MainActivity must set it first")
    // Cache dir: staging is transient by design (sessions are in-memory);
    // finished files are published into MediaStore by publishTo.
    return File(context.cacheDir, "lsex-chunks")
}

/**
 * Android receive destinations, in priority order (v0.5.0):
 *
 * 1. A folder the user picked with the Storage Access Framework
 *    (settings.destinationTreeUri) — files land in exactly that folder,
 *    written through DocumentFile. This is the answer to two releases of
 *    "my received files aren't in Downloads": the destination is a real,
 *    user-chosen place, and History keeps the document URI so the file
 *    can be opened from inside the app regardless.
 * 2. Legacy devices (API 26-28): plain files under public Download/LSEx.
 * 3. MediaStore: public Downloads by default, Pictures/Movies/Music when
 *    "Save to Gallery" applies — visible in Files/Gallery apps.
 */
actual fun createReceiveTarget(
    relativePath: String,
    mimeType: String,
    size: Long,
    saveToGallery: Boolean,
    destinationFolder: String,
    destinationTreeUri: String,
): ReceiveTarget {
    val context = AppContextHolder.appContext
        ?: error("AppContextHolder not initialised — MainActivity must set it first")
    val segments = relativePath.split('/').filter { it.isNotEmpty() }
    val fileName = segments.lastOrNull() ?: "file"
    val dirPart = segments.dropLast(1).joinToString("/")
    val mime = if (mimeType.contains('/')) mimeType else guessFileType(fileName)
    val resolver = context.contentResolver

    // ── 1. SAF folder picked by the user ────────────────────────────────
    if (destinationTreeUri.isNotEmpty()) {
        val tree = runCatching {
            DocumentFile.fromTreeUri(context, Uri.parse(destinationTreeUri))
        }.getOrNull()
        if (tree != null && tree.exists() && tree.canWrite()) {
            var dir: DocumentFile = tree
            var failed = false
            for (segment in segments.dropLast(1)) {
                val next = dir.findFile(segment) ?: dir.createDirectory(segment)
                if (next == null) {
                    failed = true
                    break
                }
                dir = next
            }
            val doc = if (failed) null else dir.createFile(mime, fileName)
            if (doc != null) {
                return object : ReceiveTarget {
                    override val displayName: String =
                        "Folder/" + segments.joinToString("/")
                    override val resultUri: String = doc.uri.toString()

                    override fun openOutput(): OutputStream =
                        resolver.openOutputStream(doc.uri, "w")
                            ?: error("Cannot open SAF stream for ${doc.uri}")

                    override fun commit() = Unit // DocumentFile writes are final

                    override fun abort() {
                        runCatching { doc.delete() }
                    }
                }
            }
        }
        // Tree revoked/unavailable: fall through to MediaStore rather than
        // losing the file.
    }

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        // ── 2. Legacy path (API 26-28): public Downloads folder ─────────
        val base = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "LSEx",
        )
        val finalFile = File(File(base, dirPart), fileName).apply { parentFile?.mkdirs() }
        val tempFile = File(finalFile.parentFile, "$fileName.part")
        return object : ReceiveTarget {
            override val displayName: String = "Download/LSEx/" +
                (if (dirPart.isEmpty()) fileName else "$dirPart/$fileName")
            override val resultUri: String = finalFile.absolutePath

            override fun openOutput(): OutputStream = FileOutputStream(tempFile)

            override fun commit() {
                if (!tempFile.renameTo(finalFile)) {
                    tempFile.copyTo(finalFile, overwrite = true)
                    tempFile.delete()
                }
            }

            override fun abort() {
                tempFile.delete()
            }
        }
    }

    // ── 3. MediaStore (API 29+) ──────────────────────────────────────────
    val customFolder = destinationFolder.trim().trim('/')
    val (collection, relativeBase) = when {
        customFolder.isNotEmpty() ->
            MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) to
                "Download/" + sanitizeFileName(customFolder)
        saveToGallery && mime.startsWith("image/") ->
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) to "Pictures"
        saveToGallery && mime.startsWith("video/") ->
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) to "Movies"
        saveToGallery && mime.startsWith("audio/") ->
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) to "Music"
        else ->
            MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) to "Download"
    }
    val relativePathValue = if (dirPart.isEmpty()) relativeBase else "$relativeBase/$dirPart"

    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, mime)
        put(MediaStore.MediaColumns.RELATIVE_PATH, relativePathValue)
        put(MediaStore.MediaColumns.IS_PENDING, 1)
    }
    val uri: Uri = resolver.insert(collection, values)
        ?: error("MediaStore refused to create $relativePathValue/$fileName")

    return object : ReceiveTarget {
        override val displayName: String = "$relativePathValue/$fileName"
        override val resultUri: String = uri.toString()

        override fun openOutput(): OutputStream =
            resolver.openOutputStream(uri, "w") ?: error("Cannot open MediaStore stream for $uri")

        override fun commit() {
            val done = ContentValues().apply { put(MediaStore.MediaColumns.IS_PENDING, 0) }
            resolver.update(uri, done, null, null)
        }

        override fun abort() {
            runCatching { resolver.delete(uri, null, null) }
        }
    }
}
