package dev.kurokawa.localsend.expressive.platform
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.outlined.*
import com.composables.icons.materialsymbols.outlinedfilled.*

import android.content.Context
import android.net.wifi.WifiManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import dev.kurokawa.localsend.expressive.protocol.DeviceType

actual fun platformDeviceType(): DeviceType = DeviceType.mobile

actual fun hotspotConfigEditable(): Boolean = false

actual fun platformDeviceModel(): String =
    listOfNotNull(android.os.Build.MANUFACTURER, android.os.Build.MODEL)
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifEmpty { "Android" }

/**
 * Android drops multicast packets unless a WifiManager.MulticastLock is held,
 * so discovery holds this lock for the whole session (M1).
 */
actual class MulticastLock {
    private var lock: WifiManager.MulticastLock? = null

    actual fun acquire() {
        if (lock != null) return
        val context = AppContextHolder.appContext ?: return
        val wifi = context.applicationContext
            .getSystemService(Context.WIFI_SERVICE) as? WifiManager ?: return
        lock = wifi.createMulticastLock("localsend-expressive").apply {
            setReferenceCounted(false)
            acquire()
        }
    }

    actual fun release() {
        runCatching { lock?.release() }
        lock = null
    }
}

@Composable
actual fun rememberClipboardText(): () -> String? {
    val context = androidx.compose.ui.platform.LocalContext.current
    return remember(context) {
        {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE)
                as? android.content.ClipboardManager
            clipboard?.primaryClip?.takeIf { it.itemCount > 0 }
                ?.getItemAt(0)?.coerceToText(context)?.toString()
        }
    }
}

// Process-wide cache (v0.12.2 verdict #1): the full PackageManager
// scan + label resolution over ~300 apps took seconds and re-ran on
// EVERY picker open (the browser's produceState restarts when the
// tab's composition is disposed). Apps don't change mid-session in
// any way that matters here; scan once per process.
private var installedAppsCache: List<InstalledApp>? = null

actual fun loadInstalledApps(): List<InstalledApp> {
    installedAppsCache?.let { return it }
    val context = AppContextHolder.appContext ?: return emptyList()
    val pm = context.packageManager
    val scanned = pm.getInstalledApplications(android.content.pm.PackageManager.GET_META_DATA)
        .filter { it.sourceDir != null }
        .mapNotNull { app ->
            val file = java.io.File(app.sourceDir)
            InstalledApp(
                label = pm.getApplicationLabel(app).toString(),
                packageName = app.packageName,
                apkHandle = app.sourceDir,
                // Total install size: base + every split (v0.10.0
                // showed the base APK alone for split apps).
                apkSize = file.length() + (app.splitSourceDirs?.sumOf { path ->
                    runCatching { java.io.File(path).length() }.getOrDefault(0L)
                } ?: 0L),
                splitHandles = app.splitSourceDirs?.toList() ?: emptyList(),
                isSystem = (app.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0 ||
                    (app.flags and android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0,
            )
        }
        .sortedBy { it.label.lowercase() }
    installedAppsCache = scanned
    return scanned
}

@Composable
actual fun AppIcon(packageName: String, modifier: androidx.compose.ui.Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val bitmap = androidx.compose.runtime.produceState<android.graphics.Bitmap?>(null, packageName) {
        value = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            runCatching {
                val drawable = context.packageManager.getApplicationIcon(packageName)
                val bmp = android.graphics.Bitmap.createBitmap(
                    96, 96, android.graphics.Bitmap.Config.ARGB_8888,
                )
                val canvas = android.graphics.Canvas(bmp)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bmp
            }.getOrNull()
        }
    }.value
    if (bitmap != null) {
        androidx.compose.foundation.Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = modifier,
        )
    } else {
        androidx.compose.foundation.layout.Box(
            modifier = modifier,
            contentAlignment = androidx.compose.ui.Alignment.Center,
        ) {
            androidx.compose.material3.Icon(
                MaterialSymbols.Outlined.Apps,
                contentDescription = null,
            )
        }
    }
}

actual fun usesSafDestination(): Boolean = true

actual fun supportsQuickShareTile(): Boolean = true

actual fun systemLanguageCode(): String =
    java.util.Locale.getDefault().language.ifBlank { "en" }

/** Set by the QS TileService (same process) when the user taps the LSEx tile. */
object QuickShareSignal {
    val requests = kotlinx.coroutines.flow.MutableStateFlow(0)
}

actual fun quickShareRequests(): kotlinx.coroutines.flow.StateFlow<Int> = QuickShareSignal.requests

actual fun clearQuickShareRequest() {
    QuickShareSignal.requests.value = 0
}


actual fun stageAppBundle(app: InstalledApp): PickedFile? {
    val context = AppContextHolder.appContext ?: return null
    if (app.splitHandles.isEmpty()) return null
    return runCatching {
        val base = java.io.File(app.apkHandle)
        val dir = java.io.File(context.cacheDir, "appbundles").apply { mkdirs() }
        // Cache key: package + base APK mtime (an app update re-bundles).
        val out = java.io.File(dir, "${app.packageName}-${base.lastModified()}.apks")
        if (!out.exists() || out.length() == 0L) {
            val tmp = java.io.File(dir, out.name + ".tmp")
            // STORED, never deflated (Ernest, v0.12.2 verdict #6):
            // APKs are already compressed, so deflating a base +
            // splits bundle (often 100+ MB) bought ~nothing and
            // took tens of seconds on-device — the "eternity
            // loading" before a split app would select. Stored
            // entries make bundling a plain copy, and the bundle's
            // size lands within a few KB of the base + splits total
            // the browser shows (verdict #5's grid/list mismatch).
            java.util.zip.ZipOutputStream(tmp.outputStream().buffered()).use { zip ->
                fun add(file: java.io.File, entryName: String) {
                    val crc = java.util.zip.CRC32()
                    val buf = ByteArray(64 * 1024)
                    file.inputStream().buffered().use { input ->
                        while (true) {
                            val n = input.read(buf)
                            if (n < 0) break
                            crc.update(buf, 0, n)
                        }
                    }
                    val entry = java.util.zip.ZipEntry(entryName).apply {
                        method = java.util.zip.ZipEntry.STORED
                        size = file.length()
                        this.crc = crc.value
                    }
                    zip.putNextEntry(entry)
                    file.inputStream().buffered().use { it.copyTo(zip) }
                    zip.closeEntry()
                }
                add(base, "base.apk")
                app.splitHandles.forEach { path ->
                    val split = java.io.File(path)
                    if (split.exists()) add(split, split.name)
                }
            }
            tmp.renameTo(out)
        }
        PickedFile(
            handle = out.absolutePath,
            name = "${app.label}.apks",
            size = out.length(),
        )
    }.getOrNull()
}
