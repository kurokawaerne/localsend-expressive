package dev.kurokawa.localsend.expressive.platform

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.kurokawa.localsend.expressive.protocol.DeviceType

actual fun platformDeviceType(): DeviceType = DeviceType.desktop

actual fun platformDeviceModel(): String =
    System.getProperty("os.name")?.takeIf { it.isNotBlank() } ?: "Desktop"

/** Desktop JVM receives multicast without any lock. */
actual class MulticastLock {
    actual fun acquire() {}
    actual fun release() {}
}

@Composable
actual fun rememberClipboardText(): () -> String? {
    return remember {
        {
            runCatching {
                val clipboard = java.awt.Toolkit.getDefaultToolkit().systemClipboard
                if (clipboard.isDataFlavorAvailable(java.awt.datatransfer.DataFlavor.stringFlavor)) {
                    clipboard.getData(java.awt.datatransfer.DataFlavor.stringFlavor) as? String
                } else {
                    null
                }
            }.getOrNull()
        }
    }
}

actual fun loadInstalledApps(): List<InstalledApp> = emptyList()

@Composable
actual fun AppIcon(packageName: String, modifier: androidx.compose.ui.Modifier) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier,
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        androidx.compose.material3.Icon(
            Icons.Filled.Apps,
            contentDescription = null,
        )
    }
}

actual fun usesSafDestination(): Boolean = false

actual fun supportsQuickShareTile(): Boolean = false

actual fun systemLanguageCode(): String =
    System.getProperty("user.language")?.take(2)?.ifBlank { "en" } ?: "en"

private val noQuickShare = kotlinx.coroutines.flow.MutableStateFlow(0)

actual fun quickShareRequests(): kotlinx.coroutines.flow.StateFlow<Int> = noQuickShare

actual fun clearQuickShareRequest() = Unit
