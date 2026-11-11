package dev.kurokawa.localsend.expressive.platform

import java.io.File
import java.io.FileInputStream
import java.io.InputStream

actual fun platformDataDir(): File =
    File(System.getProperty("user.home"), ".local/share/localsend-expressive").apply { mkdirs() }

actual fun platformDownloadsDir(): File =
    File(System.getProperty("user.home"), "Downloads/LocalSend Expressive").apply { mkdirs() }

actual fun openPickedInput(handle: String): InputStream = FileInputStream(File(handle))
