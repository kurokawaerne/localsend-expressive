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
    // The PUBLIC Downloads folder — never the app-specific sandbox under
    // Android/data (getExternalFilesDir), which is invisible to the user
    // in their file manager (Ernest's v0.8.0 report).
    val public = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    return File(public, "LSEx").apply { mkdirs() }
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
