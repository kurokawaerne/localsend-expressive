package dev.kurokawa.localsend.expressive.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.StateFlow

/** Credentials of a network we brought up ourselves (hotspot / Wi-Fi Direct group). */
data class HotspotInfo(
    val ssid: String,
    val password: String,
)

/** Observable state of the main-page connectivity shortcuts. */
data class ConnectivityState(
    /** Null = unknown on this platform. */
    val wifiEnabled: Boolean? = null,
    /** Our local hotspot is up with these credentials. */
    val hotspot: HotspotInfo? = null,
    /** Our Wi-Fi Direct group is up with these credentials. */
    val wifiDirect: HotspotInfo? = null,
    /** A toggle is being applied (radio changes are not instant). */
    val busy: Boolean = false,
    /** Last user-facing note (errors, hints), cleared on the next action. */
    val message: String? = null,
)

/**
 * The connectivity shortcuts on the main page: Wi-Fi, hotspot and
 * Wi-Fi Direct toggles plus the connect QR.
 *
 * Platform reality: since Android 10 apps cannot flip the Wi-Fi radio, so
 * on Android [toggleWifi] opens the system internet panel; the hotspot
 * uses the local-only hotspot API (built for exactly this — no internet
 * needed) and Wi-Fi Direct uses a P2P group, both of which yield real
 * SSID/password credentials for the connect QR. On desktop (Arch) the
 * toggles drive NetworkManager through nmcli.
 */
expect class ConnectivityController() {
    val state: StateFlow<ConnectivityState>
    fun refresh()
    fun toggleWifi()
    fun toggleHotspot()
    fun toggleWifiDirect()
    fun clearMessage()
}

/** Open a URL in the platform browser (e.g. a scanned web-share link). */
expect fun openUrl(url: String)

/**
 * Join a Wi-Fi network from a scanned `WIFI:` QR code.
 * Returns a short user-facing result ("Suggested …", "Connected …", or an error).
 */
expect fun joinWifiNetwork(ssid: String, password: String?, security: String?): String

/**
 * QR scanner surface: a live camera view on Android (CameraX + ZXing),
 * an image-file picker on desktop. Invokes [onCode] with each decoded
 * payload; the caller decides what to do with it and closes the surface.
 */
@Composable
expect fun QrScannerView(onCode: (String) -> Unit, modifier: Modifier)

/** Whether the Rust core's native library is loaded (vs the Kotlin fallback). */
expect fun rustCoreActive(): Boolean

/** Parse a `WIFI:T:WPA;S:ssid;P:pass;;` payload. Returns (ssid, password, security) or null. */
fun parseWifiQr(raw: String): Triple<String, String?, String?>? {
    if (!raw.startsWith("WIFI:", ignoreCase = true)) return null
    val fields = mutableMapOf<String, String>()
    val body = raw.substring(5)
    var i = 0
    while (i < body.length) {
        val colon = body.indexOf(':', i)
        if (colon < 0) break
        val key = body.substring(i, colon).uppercase()
        val sb = StringBuilder()
        var j = colon + 1
        while (j < body.length) {
            val c = body[j]
            if (c == '\\' && j + 1 < body.length) {
                sb.append(body[j + 1])
                j += 2
            } else if (c == ';') {
                break
            } else {
                sb.append(c)
                j++
            }
        }
        fields[key] = sb.toString()
        i = j + 1
    }
    val ssid = fields["S"]?.takeIf { it.isNotEmpty() } ?: return null
    return Triple(ssid, fields["P"], fields["T"])
}

/** Build the standard `WIFI:` payload other devices scan to join. */
fun wifiQrPayload(ssid: String, password: String): String {
    fun esc(s: String): String = s.replace("\\", "\\\\").replace(";", "\\;")
        .replace(",", "\\,").replace("\"", "\\\"").replace(":", "\\:")
    return "WIFI:T:WPA;S:${esc(ssid)};P:${esc(password)};;"
}
