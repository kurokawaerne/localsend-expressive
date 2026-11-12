package dev.kurokawa.localsend.expressive.platform

import android.content.Context
import android.net.wifi.WifiManager
import dev.kurokawa.localsend.expressive.protocol.DeviceType

actual fun platformDeviceType(): DeviceType = DeviceType.mobile

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
