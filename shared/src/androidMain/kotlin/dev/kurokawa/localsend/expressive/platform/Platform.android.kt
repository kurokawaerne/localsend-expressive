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

actual fun loadInstalledApps(): List<InstalledApp> {
    val context = AppContextHolder.appContext ?: return emptyList()
    val pm = context.packageManager
    return pm.getInstalledApplications(android.content.pm.PackageManager.GET_META_DATA)
        .filter { it.sourceDir != null }
        .mapNotNull { app ->
            val file = java.io.File(app.sourceDir)
            InstalledApp(
                label = pm.getApplicationLabel(app).toString(),
                packageName = app.packageName,
                apkHandle = app.sourceDir,
                apkSize = file.length(),
                isSystem = (app.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0 ||
                    (app.flags and android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0,
            )
        }
        .sortedBy { it.label.lowercase() }
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
