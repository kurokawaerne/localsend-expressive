package dev.kurokawa.localsend.expressive.platform

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.wifi.WifiConfiguration
import android.net.wifi.SoftApConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.net.wifi.p2p.WifiP2pGroup
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Runtime-permission bridge: MainActivity registers the system launcher
 * and stores it here, so platform code deep in the shared module can ask
 * for permissions (hotspot / Wi-Fi Direct / camera alternatives) without
 * holding an Activity reference.
 */
object PermissionGate {
    @Volatile
    var launcher: ((Array<String>) -> Unit)? = null

    @Volatile
    private var pendingResult: ((Boolean) -> Unit)? = null

    fun request(permissions: Array<String>, onResult: (Boolean) -> Unit) {
        val active = launcher
        if (active == null) {
            onResult(false)
            return
        }
        pendingResult = onResult
        active(permissions)
    }

    fun onResult(grants: Map<String, Boolean>) {
        val callback = pendingResult
        pendingResult = null
        callback?.invoke(grants.values.all { it })
    }
}

/** Permissions the hotspot / Wi-Fi Direct APIs demand on this Android version. */
private fun nearbyWifiPermissions(): Array<String> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.NEARBY_WIFI_DEVICES)
    } else {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }

private fun hasPermissions(context: Context, permissions: Array<String>): Boolean =
    permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

actual class ConnectivityController {
    private val context: Context? = AppContextHolder.appContext
    private val wifiManager: WifiManager? = context?.applicationContext
        ?.getSystemService(Context.WIFI_SERVICE) as? WifiManager
    private val p2pManager: WifiP2pManager? = context
        ?.getSystemService(Context.WIFI_P2P_SERVICE) as? WifiP2pManager
    private var p2pChannel: WifiP2pManager.Channel? = null
    private var hotspotReservation: WifiManager.LocalOnlyHotspotReservation? = null
    private var p2pGroupActive = false

    private val _state = MutableStateFlow(ConnectivityState())
    actual val state: StateFlow<ConnectivityState> = _state

    init {
        refresh()
        // Keep the Wi-Fi tile truthful (v0.6.0 complaint: "Wi-Fi status
        // being on doesn't change"): follow system Wi-Fi state changes
        // instead of only sampling when the screen first opens.
        val ctx = context
        if (ctx != null) {
            runCatching {
                val filter = android.content.IntentFilter().apply {
                    addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
                    addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
                }
                val receiver = object : android.content.BroadcastReceiver() {
                    override fun onReceive(c: Context, intent: android.content.Intent) {
                        refresh()
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ctx.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
                } else {
                    @Suppress("UnspecifiedRegisterReceiverFlag")
                    ctx.registerReceiver(receiver, filter)
                }
            }
        }
    }

    actual fun refresh() {
        val wifi = wifiManager
        _state.value = _state.value.copy(
            wifiEnabled = wifi?.isWifiEnabled,
        )
        if (p2pGroupActive) requestGroupInfo()
    }

    actual fun clearMessage() {
        _state.value = _state.value.copy(message = null)
    }

    // ── Wi-Fi network list (WiFi section) ─────────────────────────────

    actual fun scanWifiNetworks() {
        val ctx = context ?: return
        if (wifiManager == null) return
        val permissions = nearbyWifiPermissions()
        if (!hasPermissions(ctx, permissions)) {
            PermissionGate.request(permissions) { granted ->
                if (granted) {
                    scanWifiNetworks()
                } else {
                    _state.value = _state.value.copy(
                        message = "The network list needs the nearby-devices / location permission.",
                    )
                }
            }
            return
        }
        // Cached results show instantly; a fresh scan refreshes them.
        publishScanResults()
        _state.value = _state.value.copy(scanningNetworks = true)
        val receiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(c: Context, intent: android.content.Intent) {
                runCatching { ctx.unregisterReceiver(this) }
                publishScanResults()
                _state.value = _state.value.copy(scanningNetworks = false)
            }
        }
        val registered = runCatching {
            val filter = android.content.IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ctx.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                @Suppress("UnspecifiedRegisterReceiverFlag")
                ctx.registerReceiver(receiver, filter)
            }
            true
        }.getOrDefault(false)
        @Suppress("DEPRECATION")
        val started = runCatching { wifiManager?.startScan() }.getOrNull() == true
        if (!registered || !started) {
            // Android throttles scans (about 4 per 2 minutes per app):
            // keep the cached list and stop pretending we are scanning.
            if (registered) runCatching { ctx.unregisterReceiver(receiver) }
            _state.value = _state.value.copy(scanningNetworks = false)
        }
    }

    private fun publishScanResults() {
        val wifi = wifiManager ?: return
        val connectedSsid = runCatching {
            @Suppress("DEPRECATION")
            wifi.connectionInfo?.ssid?.trim('"')
        }.getOrNull()
        val results = runCatching {
            @Suppress("DEPRECATION")
            wifi.scanResults
        }.getOrDefault(emptyList())
        val networks = results
            .filter { !it.SSID.isNullOrEmpty() }
            .groupBy { it.SSID }
            .map { (ssid, group) ->
                val best = group.maxByOrNull { it.level } ?: group.first()
                val caps = best.capabilities.orEmpty()
                WifiNetwork(
                    ssid = ssid,
                    secured = caps.contains("WPA") || caps.contains("WEP") ||
                        caps.contains("SAE"),
                    level = best.level,
                    connected = ssid == connectedSsid,
                )
            }
            .sortedWith(
                compareByDescending<WifiNetwork> { it.connected }
                    .thenByDescending { it.level },
            )
        _state.value = _state.value.copy(wifiNetworks = networks)
    }

    actual fun toggleWifi() {
        val ctx = context ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Apps can't flip the radio since Android 10 — the system
            // internet panel is the sanctioned shortcut.
            runCatching {
                ctx.startActivity(
                    Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                )
            }
        } else {
            @Suppress("DEPRECATION")
            runCatching { wifiManager?.setWifiEnabled(wifiManager?.isWifiEnabled != true) }
            refresh()
        }
    }

    actual fun toggleHotspot() {
        if (hotspotReservation != null) {
            stopHotspot()
            return
        }
        val ctx = context ?: return
        val permissions = nearbyWifiPermissions()
        if (hasPermissions(ctx, permissions)) {
            startHotspot()
        } else {
            PermissionGate.request(permissions) { granted ->
                if (granted) {
                    startHotspot()
                } else {
                    _state.value = _state.value.copy(
                        message = "Hotspot needs the nearby-devices / location permission.",
                    )
                }
            }
        }
    }

    private fun startHotspot() {
        if (p2pGroupActive) {
            stopWifiDirect()
            _state.value = _state.value.copy(
                message = "Wi-Fi Direct stopped — it can't run together with the hotspot.",
            )
        }
        _state.value = _state.value.copy(busy = true, message = null)
        startHotspotAttempt(withConfig = true)
    }

    /**
     * The user's hotspot configuration as a [SoftApConfiguration]
     * (Android 11+): the settings are APPLIED through the configured
     * startLocalOnlyHotspot overload instead of being silently replaced
     * by a system-assigned name/password (Ernest's v0.8.0 report:
     * he set Ernest_LSEx / 5 GHz and got AndroidShare_1571).
     */
    private fun softApConfigOrNull(): SoftApConfiguration? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return null
        val prefs = ConnectivityPrefsHolder.current
        if (prefs.hotspotSsid.isBlank() && prefs.hotspotPassword.isBlank() &&
            prefs.hotspotBand == "auto"
        ) {
            return null
        }
        return runCatching {
            val builder = SoftApConfiguration.Builder()
            if (prefs.hotspotSsid.isNotBlank()) builder.setSsid(prefs.hotspotSsid)
            // A WPA2 passphrase must be 8..63 chars; an invalid one is
            // left out (the read-back shows what actually took effect).
            if (prefs.hotspotPassword.length in 8..63) {
                builder.setPassphrase(
                    prefs.hotspotPassword,
                    SoftApConfiguration.SECURITY_TYPE_WPA2_PSK,
                )
            }
            builder.setBand(
                when (prefs.hotspotBand) {
                    "2.4" -> SoftApConfiguration.BAND_2GHZ
                    "5" -> SoftApConfiguration.BAND_5GHZ
                    else -> SoftApConfiguration.BAND_ANY
                },
            )
            builder.build()
        }.getOrNull()
    }

    private fun startHotspotAttempt(withConfig: Boolean) {
        val wifi = wifiManager ?: return
        val callback = object : WifiManager.LocalOnlyHotspotCallback() {
            override fun onStarted(reservation: WifiManager.LocalOnlyHotspotReservation) {
                hotspotReservation = reservation
                val info = readHotspotInfo(reservation)
                _state.value = _state.value.copy(
                    busy = false,
                    hotspot = info,
                    message = if (info == null) {
                        "Hotspot is on (couldn't read its password)."
                    } else {
                        null
                    },
                )
            }

            override fun onStopped() {
                hotspotReservation = null
                _state.value = _state.value.copy(busy = false, hotspot = null)
            }

            override fun onFailed(reason: Int) {
                hotspotReservation = null
                if (withConfig && softApConfigOrNull() != null) {
                    // The device refused our configuration: still give
                    // the user a hotspot (system-assigned credentials)
                    // rather than nothing, and say so.
                    _state.value = _state.value.copy(
                        message = "This device refused the custom hotspot " +
                            "settings — starting with system defaults.",
                    )
                    startHotspotAttempt(withConfig = false)
                } else {
                    _state.value = _state.value.copy(
                        busy = false,
                        message = "Hotspot failed to start (reason $reason). " +
                            "Another hotspot may already be running.",
                    )
                }
            }
        }
        try {
            val config = if (withConfig) softApConfigOrNull() else null
            if (config != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val executor = Executor { runnable ->
                    Handler(Looper.getMainLooper()).post(runnable)
                }
                wifi.startLocalOnlyHotspot(config, executor, callback)
            } else {
                wifi.startLocalOnlyHotspot(callback, Handler(Looper.getMainLooper()))
            }
        } catch (e: SecurityException) {
            _state.value = _state.value.copy(
                busy = false,
                message = "Hotspot permission was denied.",
            )
        } catch (e: Exception) {
            if (withConfig) {
                startHotspotAttempt(withConfig = false)
            } else {
                _state.value = _state.value.copy(
                    busy = false,
                    message = "Hotspot failed: ${e.message}",
                )
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun readHotspotInfo(
        reservation: WifiManager.LocalOnlyHotspotReservation,
    ): HotspotInfo? = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val config = reservation.softApConfiguration
            val ssid = config.ssid ?: return@runCatching null
            HotspotInfo(ssid, config.passphrase.orEmpty())
        } else {
            val config = reservation.wifiConfiguration ?: return@runCatching null
            val ssid = config.SSID?.trim('"') ?: return@runCatching null
            HotspotInfo(ssid, config.preSharedKey?.trim('"').orEmpty())
        }
    }.getOrNull()

    private fun stopHotspot() {
        runCatching { hotspotReservation?.close() }
        hotspotReservation = null
        _state.value = _state.value.copy(hotspot = null, message = null)
    }

    actual fun toggleWifiDirect() {
        if (p2pGroupActive) {
            stopWifiDirect()
            return
        }
        val ctx = context ?: return
        val permissions = nearbyWifiPermissions()
        if (hasPermissions(ctx, permissions)) {
            startWifiDirect()
        } else {
            PermissionGate.request(permissions) { granted ->
                if (granted) {
                    startWifiDirect()
                } else {
                    _state.value = _state.value.copy(
                        message = "Wi-Fi Direct needs the nearby-devices / location permission.",
                    )
                }
            }
        }
    }

    private fun channel(): WifiP2pManager.Channel? {
        val manager = p2pManager ?: return null
        val ctx = context ?: return null
        if (p2pChannel == null) {
            p2pChannel = manager.initialize(ctx, Looper.getMainLooper()) {
                // The framework dropped the channel (P2P service
                // restarted): forget it so the next call re-initializes.
                // Reusing a dead channel makes EVERY P2P operation fail
                // with a generic ERROR until the app restarts — a prime
                // suspect for the persistent "internal error" reports.
                p2pChannel = null
            }
        }
        return p2pChannel
    }

    private fun startWifiDirect() {
        val manager = p2pManager ?: run {
            _state.value = _state.value.copy(message = "Wi-Fi Direct isn't available on this device.")
            return
        }
        val channel = channel() ?: return
        // Preconditions the old code ignored, producing Ernest's useless
        // "an internal error occurred": Wi-Fi off, or our own hotspot
        // still running (the two radios conflict on most devices).
        if (wifiManager?.isWifiEnabled != true) {
            _state.value = _state.value.copy(
                message = "Wi-Fi Direct needs Wi-Fi on — turn Wi-Fi on first.",
            )
            return
        }
        if (hotspotReservation != null) {
            stopHotspot()
            _state.value = _state.value.copy(
                message = "Hotspot stopped — hotspot and Wi-Fi Direct can't run together.",
            )
        }
        _state.value = _state.value.copy(busy = true, message = null)
        createGroupAttempt(manager, channel, attempt = 0)
    }

    /** The user's Wi-Fi Direct group config, or null for system defaults. */
    private fun p2pConfigOrNull(): android.net.wifi.p2p.WifiP2pConfig? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return null
        val prefs = ConnectivityPrefsHolder.current
        if (prefs.p2pName.isBlank() && prefs.p2pPassword.isBlank() && prefs.p2pBand == "auto") {
            return null
        }
        // A passphrase outside 8..63 chars makes the whole config invalid.
        if (prefs.p2pPassword.isNotEmpty() && prefs.p2pPassword.length !in 8..63) return null
        return runCatching {
            android.net.wifi.p2p.WifiP2pConfig.Builder()
                .apply {
                    if (prefs.p2pName.isNotBlank()) setNetworkName(prefs.p2pName)
                    if (prefs.p2pPassword.isNotBlank()) setPassphrase(prefs.p2pPassword)
                    setGroupOperatingBand(
                        when (prefs.p2pBand) {
                            "2.4" -> android.net.wifi.p2p.WifiP2pConfig.GROUP_OWNER_BAND_2GHZ
                            "5" -> android.net.wifi.p2p.WifiP2pConfig.GROUP_OWNER_BAND_5GHZ
                            else -> android.net.wifi.p2p.WifiP2pConfig.GROUP_OWNER_BAND_AUTO
                        },
                    )
                }
                .build()
        }.getOrNull()
    }

    private fun noopP2pListener() = object : WifiP2pManager.ActionListener {
        override fun onSuccess() = Unit
        override fun onFailure(reason: Int) = Unit
    }

    /**
     * Create the P2P group in staged attempts (v0.9.0 hardening — v0.8.0
     * still died with a generic internal ERROR on Ernest's LineageOS):
     *
     * - Peer discovery running on the channel makes createGroup fail
     *   with ERROR on many devices, so discovery is stopped first.
     * - Attempt 0 uses the user's config (name / passphrase / band);
     *   later attempts use system defaults.
     * - On ERROR, any zombie group is removed and the retry WAITS for
     *   the removal to settle (the old code retried 600 ms after a
     *   fire-and-forget removal, straight into the same ERROR).
     * - BUSY means a group is already up: adopt it via requestGroupInfo.
     * - After the last attempt the cached channel is dropped, so the
     *   user's next tap re-initializes P2P from scratch.
     */
    private fun createGroupAttempt(
        manager: WifiP2pManager,
        channel: WifiP2pManager.Channel,
        attempt: Int,
    ) {
        runCatching { manager.stopPeerDiscovery(channel, noopP2pListener()) }
        val listener = object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                p2pGroupActive = true
                requestGroupInfo()
            }

            override fun onFailure(reason: Int) {
                when {
                    reason == WifiP2pManager.BUSY -> {
                        // A group from an earlier session is still up: adopt it.
                        p2pGroupActive = true
                        requestGroupInfo()
                    }
                    reason == WifiP2pManager.ERROR && attempt < 2 ->
                        cleanupThenRetry(manager, channel, attempt)
                    else -> {
                        p2pChannel = null
                        _state.value = _state.value.copy(
                            busy = false,
                            message = "Wi-Fi Direct couldn't start: ${p2pReasonText(reason)}",
                        )
                    }
                }
            }
        }
        try {
            val config = if (attempt == 0) p2pConfigOrNull() else null
            if (config != null) {
                manager.createGroup(channel, config, listener)
            } else {
                manager.createGroup(channel, listener)
            }
        } catch (e: SecurityException) {
            _state.value = _state.value.copy(
                busy = false,
                message = "Wi-Fi Direct permission was denied.",
            )
        } catch (e: Exception) {
            if (attempt < 2) {
                cleanupThenRetry(manager, channel, attempt)
            } else {
                p2pChannel = null
                _state.value = _state.value.copy(
                    busy = false,
                    message = "Wi-Fi Direct couldn't start: ${e.message}",
                )
            }
        }
    }

    /** Remove any zombie group, WAIT for the removal, then retry. */
    private fun cleanupThenRetry(
        manager: WifiP2pManager,
        channel: WifiP2pManager.Channel,
        attempt: Int,
    ) {
        val proceed = {
            Handler(Looper.getMainLooper()).postDelayed({
                // The channel may have been dropped by a disconnect in
                // the meantime — re-fetch rather than reuse a dead one.
                val fresh = channel()
                if (fresh != null) {
                    createGroupAttempt(manager, fresh, attempt + 1)
                } else {
                    _state.value = _state.value.copy(
                        busy = false,
                        message = "Wi-Fi Direct couldn't start: an internal error occurred.",
                    )
                }
            }, 900L * (attempt + 1))
        }
        try {
            manager.removeGroup(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    proceed()
                }
                override fun onFailure(reason: Int) {
                    proceed()
                }
            })
        } catch (e: Exception) {
            proceed()
        }
    }

    private fun requestGroupInfo() {
        val manager = p2pManager ?: return
        val channel = channel() ?: return
        try {
            manager.requestGroupInfo(channel) { group: WifiP2pGroup? ->
                if (group != null && group.isGroupOwner) {
                    p2pGroupActive = true
                    _state.value = _state.value.copy(
                        busy = false,
                        wifiDirect = HotspotInfo(
                            group.networkName,
                            group.passphrase.orEmpty(),
                        ),
                    )
                } else if (!p2pGroupActive) {
                    _state.value = _state.value.copy(
                        busy = false,
                        wifiDirect = null,
                        message = "Wi-Fi Direct group isn't active.",
                    )
                }
            }
        } catch (e: SecurityException) {
            _state.value = _state.value.copy(busy = false)
        }
    }

    private fun stopWifiDirect() {
        val manager = p2pManager ?: return
        val channel = channel() ?: return
        try {
            manager.removeGroup(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    p2pGroupActive = false
                    _state.value = _state.value.copy(wifiDirect = null, message = null)
                }

                override fun onFailure(reason: Int) {
                    p2pGroupActive = false
                    _state.value = _state.value.copy(
                        wifiDirect = null,
                        message = "Wi-Fi Direct stop: ${p2pReasonText(reason)}",
                    )
                }
            })
        } catch (e: SecurityException) {
            p2pGroupActive = false
            _state.value = _state.value.copy(wifiDirect = null)
        }
    }
}

actual fun openUrl(url: String) {
    val context = AppContextHolder.appContext ?: return
    runCatching {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
    }
}

actual fun joinWifiNetwork(ssid: String, password: String?, security: String?): String {
    val context = AppContextHolder.appContext ?: return "Wi-Fi isn't available right now."
    val wifi = context.applicationContext
        .getSystemService(Context.WIFI_SERVICE) as? WifiManager
        ?: return "Wi-Fi isn't available right now."
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val builder = WifiNetworkSuggestion.Builder().setSsid(ssid)
        if (!password.isNullOrEmpty() &&
            !security.equals("nopass", ignoreCase = true)
        ) {
            builder.setWpa2Passphrase(password)
        }
        val status = runCatching {
            wifi.addNetworkSuggestions(listOf(builder.build()))
        }.getOrElse { return "Couldn't suggest the network: ${it.message}" }
        if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
            "“$ssid” suggested — if Android asks, confirm it, then the device joins."
        } else {
            "Android didn't accept the suggestion for “$ssid”."
        }
    } else {
        @Suppress("DEPRECATION")
        runCatching {
            val config = WifiConfiguration().apply {
                SSID = "\"$ssid\""
                if (!password.isNullOrEmpty()) {
                    preSharedKey = "\"$password\""
                } else {
                    allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                }
            }
            val netId = wifi.addNetwork(config)
            if (netId < 0) return "Couldn't add the network."
            wifi.enableNetwork(netId, true)
            "Connecting to “$ssid”…"
        }.getOrElse { "Couldn't join: ${it.message}" }
    }
}

/** WifiP2pManager failure reasons in words a human can act on. */
private fun p2pReasonText(reason: Int): String = when (reason) {
    WifiP2pManager.P2P_UNSUPPORTED -> "this device doesn't support Wi-Fi Direct."
    WifiP2pManager.BUSY -> "Wi-Fi Direct is busy — wait a few seconds and try again."
    WifiP2pManager.ERROR -> "an internal error occurred. Toggling Wi-Fi off and on usually clears it."
    else -> "unknown error ($reason)."
}

/**
 * Unwrap a Compose [android.content.Context] down to its ComponentActivity.
 *
 * v0.4.0 cast `LocalContext.current` directly; inside dialogs the context
 * is a ContextThemeWrapper around the activity, the cast returned null and
 * the scanner showed "Scanner unavailable." (Ernest's screenshot).
 */
private fun android.content.Context.findActivity(): androidx.activity.ComponentActivity? {
    var ctx: android.content.Context? = this
    while (ctx is android.content.ContextWrapper) {
        if (ctx is androidx.activity.ComponentActivity) return ctx
        ctx = ctx.baseContext
    }
    return ctx as? androidx.activity.ComponentActivity
}

// ── QR scanner (CameraX + ZXing) ───────────────────────────────────────────

private class QrAnalyzer(private val onCode: (String) -> Unit) : ImageAnalysis.Analyzer {
    private val reader = MultiFormatReader().apply {
        setHints(
            mapOf(
                DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE),
                DecodeHintType.TRY_HARDER to true,
            ),
        )
    }
    private var lastText: String? = null
    private var lastAtMillis = 0L

    override fun analyze(image: ImageProxy) {
        try {
            val plane = image.planes.firstOrNull() ?: return
            val buffer = plane.buffer
            val bytes = ByteArray(buffer.remaining()).also { buffer.get(it) }
            val source = PlanarYUVLuminanceSource(
                bytes, image.width, image.height, 0, 0, image.width, image.height, false,
            )
            val bitmap = BinaryBitmap(HybridBinarizer(source))
            val text = runCatching { reader.decodeWithState(bitmap).text }.getOrNull()
            if (text != null) {
                val now = System.currentTimeMillis()
                // One callback per code per 3 s — the dialog consumes the
                // first and closes, this just avoids callback spam.
                if (text != lastText || now - lastAtMillis > 3_000) {
                    lastText = text
                    lastAtMillis = now
                    onCode(text)
                }
            }
        } catch (e: Exception) {
            // A frame that can't be decoded is normal; keep scanning.
        } finally {
            image.close()
        }
    }
}

@Composable
actual fun QrScannerView(onCode: (String) -> Unit, modifier: Modifier) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted -> hasPermission = granted }
    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }
    if (!hasPermission) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Text("Camera permission is needed to scan QR codes.")
        }
        return
    }
    val activity = context.findActivity()
    if (activity == null) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Text("Scanner unavailable.")
        }
        return
    }
    val executor = remember { Executors.newSingleThreadExecutor() }
    DisposableEffect(Unit) {
        onDispose { executor.shutdown() }
    }
    AndroidView(
        factory = { viewContext ->
            val previewView = PreviewView(viewContext)
            val providerFuture = ProcessCameraProvider.getInstance(viewContext)
            providerFuture.addListener(
                {
                    val provider = runCatching { providerFuture.get() }.getOrNull()
                        ?: return@addListener
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val analysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { it.setAnalyzer(executor, QrAnalyzer(onCode)) }
                    runCatching {
                        provider.unbindAll()
                        provider.bindToLifecycle(
                            activity,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            analysis,
                        )
                    }
                },
                ContextCompat.getMainExecutor(viewContext),
            )
            previewView
        },
        modifier = modifier,
    )
}
