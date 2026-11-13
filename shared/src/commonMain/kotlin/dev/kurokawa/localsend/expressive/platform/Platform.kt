package dev.kurokawa.localsend.expressive.platform

import androidx.compose.runtime.Composable
import dev.kurokawa.localsend.expressive.protocol.DeviceType

/** The DeviceType this platform reports to peers. */
expect fun platformDeviceType(): DeviceType

/**
 * Whether this platform lets the app choose the hotspot's name,
 * password and band. Android: false — the configured LocalOnlyHotspot
 * API is a system API outside the public SDK, so the system assigns
 * the credentials (shown live once the hotspot is on). Desktop: true
 * (nmcli takes the full configuration).
 */
expect fun hotspotConfigEditable(): Boolean

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
    /** System (or updated system) app — the picker can hide these (v0.5.0). */
    val isSystem: Boolean = false,
)

/** The app's launcher icon, drawn by the platform (v0.5.0: the picker shows real icons). */
@Composable
expect fun AppIcon(packageName: String, modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier)

/** True on Android, where the receive folder is picked through SAF. */
expect fun usesSafDestination(): Boolean

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
