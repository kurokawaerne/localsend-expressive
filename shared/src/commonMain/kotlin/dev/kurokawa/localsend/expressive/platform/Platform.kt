package dev.kurokawa.localsend.expressive.platform

import androidx.compose.runtime.Composable
import dev.kurokawa.localsend.expressive.protocol.DeviceType

/** The DeviceType this platform reports to peers. */
expect fun platformDeviceType(): DeviceType

/** Human-readable device model sent in announcements, e.g. "Xiaomi 2201117TG". */
expect fun platformDeviceModel(): String

/**
 * Holds whatever the platform needs so multicast packets actually arrive.
 * Android requires a WifiManager.MulticastLock; desktop JVM needs nothing.
 * TODO(M1): wire the real Android lock from MainActivity.
 */
expect class MulticastLock() {
    fun acquire()
    fun release()
}

/** Text currently on the system clipboard (null when empty/non-text). Used by the "Paste" tile. */
@Composable
expect fun rememberClipboardText(): () -> String?

/** An installed app that can be sent as its APK (the official "App" tile, Android only). */
data class InstalledApp(
    val label: String,
    val packageName: String,
    /** Handle understood by openPickedInput (APK file path on Android). */
    val apkHandle: String,
    val apkSize: Long,
)

/** Installed apps available for sending; empty on platforms without an app store model. */
expect fun loadInstalledApps(): List<InstalledApp>

/** Whether this platform has a QS-style quick tile / quick-share entry (Android). */
expect fun supportsQuickShareTile(): Boolean

/** ISO 639-1 code of the OS language ("en", "es", …), for the language setting. */
expect fun systemLanguageCode(): String

/**
 * Quick-share invocations from outside the UI (Android QS tile): each
 * event asks the Send screen to open with the file picker ready.
 * Always silent on desktop.
 */
expect fun quickShareRequests(): kotlinx.coroutines.flow.StateFlow<Int>

expect fun clearQuickShareRequest()
