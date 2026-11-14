package dev.kurokawa.localsend.expressive.platform

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageInstaller
import android.net.Uri
import android.provider.Settings
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

actual fun installerSupported(): Boolean = true

actual fun deleteReceivedFile(uri: String): Boolean {
    val context = AppContextHolder.appContext ?: return false
    return runCatching {
        val parsed = Uri.parse(uri)
        if (parsed.scheme == "content") {
            context.contentResolver.delete(parsed, null, null) > 0
        } else {
            File(uri).delete()
        }
    }.getOrDefault(false)
}

actual fun openReceivedFile(uri: String, fileName: String) {
    val context = AppContextHolder.appContext ?: return
    runCatching {
        val parsed = Uri.parse(uri)
        val intent = if (parsed.scheme == "content") {
            val type = runCatching { context.contentResolver.getType(parsed) }.getOrNull()
                ?: guessMime(fileName)
            Intent(Intent.ACTION_VIEW).setDataAndType(parsed, type)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            // Legacy absolute path (pre-MediaStore entries).
            val type = guessMime(fileName)
            Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(File(uri)), type)
        }
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}

private fun guessMime(fileName: String): String =
    android.webkit.MimeTypeMap.getSingleton()
        .getMimeTypeFromExtension(fileName.substringAfterLast('.', "").lowercase())
        ?: "*/*"

private fun openStream(context: android.content.Context, uri: String): InputStream? =
    if (uri.startsWith("content://")) {
        context.contentResolver.openInputStream(Uri.parse(uri))
    } else {
        runCatching { FileInputStream(File(uri)) }.getOrNull()
    }

/**
 * PackageInstaller session install, InstallerX-style:
 * - `.apk` → the file itself is the session's single entry.
 * - `.apks` / `.xapk` / `.apkm` → every `.apk` inside the zip becomes an
 *   entry of the same session (base + config splits install atomically).
 * - `.aab` → refused with an explanation: an AAB is a publishing bundle,
 *   not an installable artifact; it needs conversion (bundletool) first.
 *
 * The user still confirms in the system installer UI (the session's
 * user-action prompt is routed through InstallReceiver).
 */
actual fun installPackage(uri: String, fileName: String): String? {
    val context = AppContextHolder.appContext
        ?: return "Installer isn't ready yet."
    val lower = fileName.lowercase()
    if (lower.endsWith(".aab")) {
        return "“.aab” bundles can't be installed directly — they're publishing " +
            "bundles that need conversion to APK splits first. Ask the sender " +
            "for the .apks or .apk instead."
    }
    if (!context.packageManager.canRequestPackageInstalls()) {
        runCatching {
            context.startActivity(
                Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:${context.packageName}"),
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
        }
        return "Allow LSEx to install apps (opened the setting), then tap Install again."
    }
    val source = openStream(context, uri) ?: return "Couldn't read the package file."
    val stagingDir = File(context.cacheDir, "installer").apply { mkdirs() }
    // stage name -> staged file
    val staged = mutableListOf<Pair<String, File>>()
    try {
        if (lower.endsWith(".apk")) {
            val out = File(stagingDir, "base.apk")
            source.use { input -> out.outputStream().use { input.copyTo(it) } }
            staged += "base.apk" to out
        } else {
            var found = 0
            ZipInputStream(source).use { zip ->
                while (true) {
                    val entry = zip.nextEntry ?: break
                    if (!entry.isDirectory && entry.name.lowercase().endsWith(".apk")) {
                        val safeName = entry.name.substringAfterLast('/')
                            .ifBlank { "split_$found.apk" }
                        if (staged.none { it.first == safeName }) {
                            val out = File(stagingDir, safeName)
                            out.outputStream().use { zip.copyTo(it) }
                            staged += safeName to out
                        }
                        found++
                    }
                    zip.closeEntry()
                }
            }
            if (staged.isEmpty()) return "No APK files inside this package set."
        }
        // InstallerX's core lesson: a bundle carries splits for
        // EVERY device — installing all of them fails or bloats.
        // Only the splits matching THIS device go into the session.
        val selected = selectDeviceSplits(context, staged)
        if (selected.isEmpty()) return "No splits in this set match this device."
        val installer = context.packageManager.packageInstaller
        val params = PackageInstaller.SessionParams(
            PackageInstaller.SessionParams.MODE_FULL_INSTALL,
        )
        val sessionId = installer.createSession(params)
        val session = installer.openSession(sessionId)
        try {
            for ((name, file) in selected) {
                session.openWrite(name, 0, file.length()).use { out ->
                    FileInputStream(file).use { it.copyTo(out) }
                    session.fsync(out)
                }
            }
            val receiverIntent = Intent(ACTION_INSTALL_RESULT).apply {
                setClassName(
                    context.packageName,
                    "dev.kurokawa.localsend.expressive.InstallReceiver",
                )
                putExtra(EXTRA_PACKAGE_LABEL, fileName)
            }
            val pending = PendingIntent.getBroadcast(
                context,
                sessionId,
                receiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
            )
            session.commit(pending.intentSender)
        } finally {
            session.close()
        }
        return null
    } catch (e: Exception) {
        return "Install failed: ${e.message}"
    } finally {
        staged.forEach { runCatching { it.second.delete() } }
    }
}

const val ACTION_INSTALL_RESULT = "dev.kurokawa.localsend.expressive.INSTALL_RESULT"
const val EXTRA_PACKAGE_LABEL = "package_label"


/**
 * Device-matched split selection, aligned with InstallerX Revived:
 * bundles (.apks/.xapk/.apkm) ship config splits for every ABI,
 * density and language. Keep the base + feature splits, only this
 * device's ABI splits, the single best density split, and this
 * device's language splits. Classification is by the conventional
 * split file names (config.<x>.apk / split_config.<x>.apk).
 */
private fun selectDeviceSplits(
    context: android.content.Context,
    staged: List<Pair<String, File>>,
): List<Pair<String, File>> {
    val knownAbis = listOf("arm64_v8a", "armeabi_v7a", "armeabi", "x86_64", "x86")
    val densityDpis = mapOf(
        "ldpi" to 120, "mdpi" to 160, "tvdpi" to 213, "hdpi" to 240,
        "xhdpi" to 320, "xxhdpi" to 480, "xxxhdpi" to 640,
        "120" to 120, "160" to 160, "213" to 213, "240" to 240,
        "320" to 320, "480" to 480, "640" to 640,
    )
    fun abiOf(name: String): String? = knownAbis.firstOrNull { name.contains(it) }
    fun dpiOf(name: String): Int? =
        densityDpis.entries.firstOrNull { name.contains(it.key) }?.value
    fun langOf(name: String): String? {
        if (abiOf(name) != null || dpiOf(name) != null) return null
        val core = name.removeSuffix(".apk")
            .removePrefix("split_")
            .removePrefix("config.")
            .substringAfter("config.")
        return core.takeIf {
            it.matches(Regex("[a-z]{2,3}(_[a-zA-Z]{2})?"))
        }
    }
    fun isConfig(name: String): Boolean =
        name.startsWith("config.") || name.startsWith("split_config.") ||
            name.contains(".config.")

    val deviceAbis = android.os.Build.SUPPORTED_ABIS.map { it.replace('-', '_') }
    val deviceDpi = context.resources.displayMetrics.densityDpi
    val deviceLangs = (0 until context.resources.configuration.locales.size())
        .map { context.resources.configuration.locales.get(it).language }

    val configs = staged.filter { isConfig(it.first.lowercase()) }
    val abiSplits = configs.filter { abiOf(it.first.lowercase()) != null }
    val chosenAbis = abiSplits.filter {
        abiOf(it.first.lowercase()) in deviceAbis
    }.ifEmpty { abiSplits }
    val densitySplits = configs.filter {
        abiOf(it.first.lowercase()) == null && dpiOf(it.first.lowercase()) != null
    }
    val chosenDensity = densitySplits
        .sortedWith(
            compareBy(
                { dpiOf(it.first.lowercase())!! < deviceDpi },
                { kotlin.math.abs(dpiOf(it.first.lowercase())!! - deviceDpi) },
            ),
        )
        .firstOrNull()
    val langSplits = configs.filter { langOf(it.first.lowercase()) != null }
    val chosenLangs = langSplits.filter {
        langOf(it.first.lowercase())!!.substringBefore('_') in deviceLangs
    }
    val chosen = chosenAbis + listOfNotNull(chosenDensity) + chosenLangs
    return staged.filter { entry ->
        val name = entry.first.lowercase()
        !isConfig(name) || entry in chosen ||
            (abiOf(name) == null && dpiOf(name) == null && langOf(name) == null)
    }
}
