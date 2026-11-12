package dev.kurokawa.localsend.expressive.platform

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.zxing.BinaryBitmap
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.common.HybridBinarizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.Desktop
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.net.URI
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

/**
 * Desktop connectivity: drives NetworkManager through nmcli (Ernest's
 * Arch setup). Wi-Fi Direct has no sane nmcli path — the hotspot covers
 * the same use, and the tile says so.
 */
actual class ConnectivityController {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _state = MutableStateFlow(ConnectivityState())
    actual val state: StateFlow<ConnectivityState> = _state

    private var hotspotInfo: HotspotInfo? = null

    init {
        refresh()
    }

    private fun run(vararg command: String): Pair<Int, String> = runCatching {
        val process = ProcessBuilder(*command)
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText()
        process.waitFor(15, TimeUnit.SECONDS)
        process.exitValue() to output.trim()
    }.getOrElse { -1 to (it.message ?: "command failed") }

    actual fun refresh() {
        scope.launch {
            val (_, radio) = run("nmcli", "radio", "wifi")
            val wifiOn = when {
                radio.startsWith("enabled") -> true
                radio.startsWith("disabled") -> false
                else -> null
            }
            val (_, active) = run("nmcli", "-t", "-f", "NAME", "connection", "show", "--active")
            val hotspotActive = active.lines().any { it.trim() == HOTSPOT_CONNECTION }
            val info = when {
                hotspotActive && hotspotInfo != null -> hotspotInfo
                hotspotActive -> readHotspotFromNmcli()
                else -> null
            }
            if (!hotspotActive) hotspotInfo = null
            _state.value = _state.value.copy(
                wifiEnabled = wifiOn,
                hotspot = info,
            )
        }
    }

    private fun readHotspotFromNmcli(): HotspotInfo? {
        val (_, ssidOut) = run(
            "nmcli", "-s", "-g", "802-11-wireless.ssid",
            "connection", "show", HOTSPOT_CONNECTION,
        )
        val (_, pskOut) = run(
            "nmcli", "-s", "-g", "802-11-wireless-security.psk",
            "connection", "show", HOTSPOT_CONNECTION,
        )
        val ssid = ssidOut.lineSequence().firstOrNull()?.trim().orEmpty()
        if (ssid.isEmpty()) return null
        return HotspotInfo(ssid, pskOut.lineSequence().firstOrNull()?.trim().orEmpty())
            .also { hotspotInfo = it }
    }

    actual fun clearMessage() {
        _state.value = _state.value.copy(message = null)
    }

    actual fun toggleWifi() {
        scope.launch {
            val turningOn = _state.value.wifiEnabled != true
            _state.value = _state.value.copy(busy = true, message = null)
            val (code, out) = run("nmcli", "radio", "wifi", if (turningOn) "on" else "off")
            _state.value = _state.value.copy(
                busy = false,
                message = if (code != 0) "nmcli: $out" else null,
            )
            refresh()
        }
    }

    actual fun toggleHotspot() {
        scope.launch {
            _state.value = _state.value.copy(busy = true, message = null)
            if (_state.value.hotspot != null) {
                val (code, out) = run("nmcli", "connection", "down", HOTSPOT_CONNECTION)
                hotspotInfo = null
                _state.value = _state.value.copy(
                    busy = false,
                    hotspot = null,
                    message = if (code != 0) "nmcli: $out" else null,
                )
                return@launch
            }
            val (_, devices) = run("nmcli", "-t", "-f", "DEVICE,TYPE", "device", "status")
            val iface = devices.lineSequence()
                .map { it.split(":", limit = 2) }
                .firstOrNull { it.size == 2 && it[1].trim() == "wifi" }
                ?.get(0)?.trim()
            if (iface.isNullOrEmpty()) {
                _state.value = _state.value.copy(
                    busy = false,
                    message = "No Wi-Fi device found (is NetworkManager running?)",
                )
                return@launch
            }
            // User preferences (Settings → Connection) apply on desktop:
            // custom SSID, custom password (blank = generate one and
            // show it), and band ("bg" = 2.4 GHz, "a" = 5 GHz in nmcli).
            val prefs = ConnectivityPrefsHolder.current
            val ssid = prefs.hotspotSsid.ifBlank { "LSEx-Hotspot" }
            val password = prefs.hotspotPassword.ifBlank { randomPassword() }
            val command = mutableListOf(
                "nmcli", "device", "wifi", "hotspot",
                "ifname", iface,
                "con-name", HOTSPOT_CONNECTION,
                "ssid", ssid,
                "password", password,
            )
            when (prefs.hotspotBand) {
                "2.4" -> command += listOf("band", "bg")
                "5" -> command += listOf("band", "a")
            }
            val (code, out) = run(*command.toTypedArray())
            if (code == 0) {
                hotspotInfo = HotspotInfo(ssid, password)
                _state.value = _state.value.copy(
                    busy = false,
                    hotspot = hotspotInfo,
                )
            } else {
                _state.value = _state.value.copy(
                    busy = false,
                    message = "Hotspot failed: $out",
                )
            }
            refresh()
        }
    }

    actual fun toggleWifiDirect() {
        _state.value = _state.value.copy(
            message = "Wi-Fi Direct isn't available on desktop — " +
                "the hotspot does the same job here.",
        )
    }

    private fun randomPassword(): String {
        val alphabet = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789"
        return (1..10).map { alphabet.random() }.joinToString("")
    }

    private companion object {
        const val HOTSPOT_CONNECTION = "LSEx"
    }
}

actual fun openUrl(url: String) {
    runCatching {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI(url))
        } else {
            ProcessBuilder("xdg-open", url).start()
        }
    }
}

actual fun joinWifiNetwork(ssid: String, password: String?, security: String?): String {
    val command = mutableListOf("nmcli", "device", "wifi", "connect", ssid)
    if (!password.isNullOrEmpty() &&
        !security.equals("nopass", ignoreCase = true)
    ) {
        command += listOf("password", password)
    }
    val process = runCatching {
        ProcessBuilder(command).redirectErrorStream(true).start()
    }.getOrElse { return "Couldn't run nmcli: ${it.message}" }
    val output = process.inputStream.bufferedReader().readText().trim()
    process.waitFor(30, TimeUnit.SECONDS)
    return if (process.exitValue() == 0) {
        "Connected to “$ssid”."
    } else {
        "nmcli: $output"
    }
}

// ── QR scanner (desktop): decode a code from an image file ─────────────────

private class BufferedImageLuminanceSource(
    private val image: java.awt.image.BufferedImage,
) : LuminanceSource(image.width, image.height) {
    override fun getRow(y: Int, row: ByteArray?): ByteArray {
        val result = if (row != null && row.size >= width) row else ByteArray(width)
        for (x in 0 until width) {
            val rgb = image.getRGB(x, y)
            val r = (rgb shr 16) and 0xFF
            val g = (rgb shr 8) and 0xFF
            val b = rgb and 0xFF
            result[x] = ((r * 299 + g * 587 + b * 114) / 1000).toByte()
        }
        return result
    }

    override fun getMatrix(): ByteArray {
        val result = ByteArray(width * height)
        for (y in 0 until height) {
            val row = getRow(y, null)
            System.arraycopy(row, 0, result, y * width, width)
        }
        return result
    }
}

@Composable
actual fun QrScannerView(onCode: (String) -> Unit, modifier: Modifier) {
    val scope = rememberCoroutineScope()
    var note by remember { mutableStateOf<String?>(null) }
    Column(
        modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "On desktop, scan by picking an image that contains the QR code " +
                "(a screenshot or photo of the other device's code).",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            scope.launch {
                val text = withContext(Dispatchers.IO) {
                    val dialog = FileDialog(null as Frame?, "Choose a QR code image", FileDialog.LOAD)
                    dialog.isVisible = true
                    val file = dialog.files?.firstOrNull() ?: return@withContext null
                    runCatching {
                        val image = ImageIO.read(File(file.absolutePath))
                            ?: return@runCatching null
                        val bitmap = BinaryBitmap(
                            HybridBinarizer(BufferedImageLuminanceSource(image)),
                        )
                        MultiFormatReader().decode(bitmap).text
                    }.getOrNull()
                }
                if (text != null) {
                    onCode(text)
                } else {
                    note = "No QR code found in that image."
                }
            }
        }) { Text("Choose an image…") }
        note?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, style = MaterialTheme.typography.bodySmall)
        }
    }
}
