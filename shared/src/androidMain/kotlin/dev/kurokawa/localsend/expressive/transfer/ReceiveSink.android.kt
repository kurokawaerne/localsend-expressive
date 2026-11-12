package dev.kurokawa.localsend.expressive.transfer

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import dev.kurokawa.localsend.expressive.platform.AppContextHolder
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * Android: write through MediaStore so received files are VISIBLE —
 * public Downloads by default, Pictures/Movies/Music when "Save to
 * Gallery" applies. (v0.3.0 wrote to the app-private folder, where files
 * effectively disappeared.)
 */
actual fun createReceiveTarget(
    relativePath: String,
    mimeType: String,
    size: Long,
    saveToGallery: Boolean,
    destinationFolder: String,
): ReceiveTarget {
    val context = AppContextHolder.appContext
        ?: error("AppContextHolder not initialised — MainActivity must set it first")
    val segments = relativePath.split('/').filter { it.isNotEmpty() }
    val fileName = segments.lastOrNull() ?: "file"
    val dirPart = segments.dropLast(1).joinToString("/")
    val mime = if (mimeType.contains('/')) mimeType else guessFileType(fileName)

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        // Legacy path (API 26-28): public Downloads folder, plain files.
        val base = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "LSEx",
        )
        val finalFile = File(File(base, dirPart), fileName).apply { parentFile?.mkdirs() }
        val tempFile = File(finalFile.parentFile, "$fileName.part")
        return object : ReceiveTarget {
            override val displayName: String = "Download/LSEx/" +
                (if (dirPart.isEmpty()) fileName else "$dirPart/$fileName")

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

    val resolver = context.contentResolver
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
