package dev.kurokawa.localsend.expressive.platform

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
