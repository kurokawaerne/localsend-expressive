package dev.kurokawa.localsend.expressive.platform

/**
 * Opening received files and installing received Android packages
 * (v0.5.0) — the InstallerX-style built-in installer Ernest asked for, so
 * a received .apk / split set doesn't need another app to install.
 */

/** True on Android; the desktop app can't install packages. */
expect fun installerSupported(): Boolean

/** File names the built-in installer can handle: single APKs and split sets. */
fun isInstallablePackage(fileName: String): Boolean {
    val lower = fileName.lowercase()
    return lower.endsWith(".apk") || lower.endsWith(".apks") ||
        lower.endsWith(".xapk") || lower.endsWith(".apkm")
}

/** Open a received file with the platform viewer (from History's URI). */
expect fun openReceivedFile(uri: String, fileName: String)

/**
 * Install the package at [uri] (a content:// URI or file path recorded in
 * History). Returns null when the system install prompt was launched, or
 * a human-readable explanation when it couldn't be.
 */
expect fun installPackage(uri: String, fileName: String): String?
