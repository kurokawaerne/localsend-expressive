package dev.kurokawa.localsend.expressive.platform

import java.io.File

actual fun installerSupported(): Boolean = false

actual fun openReceivedFile(uri: String, fileName: String) {
    runCatching {
        val file = File(uri)
        if (file.exists() && java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop.getDesktop().open(file)
        }
    }
}

actual fun installPackage(uri: String, fileName: String): String? =
    "Installing Android packages is only available in the Android app."
