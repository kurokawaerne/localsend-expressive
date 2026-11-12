package dev.kurokawa.localsend.expressive.transfer

import dev.kurokawa.localsend.expressive.platform.platformDownloadsDir
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/** Desktop: a real file under the destination folder, via a `.part` temp. */
actual fun createReceiveTarget(
    relativePath: String,
    mimeType: String,
    size: Long,
    saveToGallery: Boolean,
    destinationFolder: String,
    destinationTreeUri: String,
): ReceiveTarget {
    val base = if (destinationFolder.isNotBlank()) {
        File(destinationFolder).apply { mkdirs() }
    } else {
        platformDownloadsDir()
    }
    val finalFile = File(base, relativePath)
    finalFile.parentFile?.mkdirs()
    val tempFile = File(finalFile.parentFile, finalFile.name + ".part")
    // commit() may rename to a collision-free sibling; resultUri follows it.
    var committedFile: File = finalFile
    return object : ReceiveTarget {
        override val displayName: String = finalFile.absolutePath
        override val resultUri: String get() = committedFile.absolutePath

        override fun openOutput(): OutputStream = FileOutputStream(tempFile)

        override fun commit() {
            val target = uniqueFile(finalFile)
            committedFile = target
            if (!tempFile.renameTo(target)) {
                tempFile.copyTo(target, overwrite = false)
                tempFile.delete()
            }
        }

        override fun abort() {
            tempFile.delete()
        }
    }
}

actual fun chunkStagingDir(): File =
    File(dev.kurokawa.localsend.expressive.platform.platformDataDir(), "chunks")

/** Avoid clobbering an existing file: "name (1).ext", "name (2).ext", … */
private fun uniqueFile(file: File): File {
    if (!file.exists()) return file
    val base = file.nameWithoutExtension
    val ext = file.extension
    var i = 1
    while (true) {
        val candidate = File(file.parentFile, if (ext.isEmpty()) "$base ($i)" else "$base ($i).$ext")
        if (!candidate.exists()) return candidate
        i++
    }
}
