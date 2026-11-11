package dev.kurokawa.localsend.expressive.platform

import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

actual fun platformDataDir(): File {
    val context = AppContextHolder.appContext
        ?: error("AppContextHolder not initialised — MainActivity must set it first")
    return File(context.filesDir, "localsend-expressive").apply { mkdirs() }
}

actual fun platformDownloadsDir(): File {
    val context = AppContextHolder.appContext
        ?: error("AppContextHolder not initialised — MainActivity must set it first")
    val external = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    return (external ?: File(context.filesDir, "Download")).apply { mkdirs() }
}

actual fun openPickedInput(handle: String): InputStream {
    if (handle.startsWith("content://")) {
        val context = AppContextHolder.appContext
            ?: error("AppContextHolder not initialised — MainActivity must set it first")
        return context.contentResolver.openInputStream(Uri.parse(handle))
            ?: error("Cannot open $handle")
    }
    return FileInputStream(File(handle))
}
