package dev.kurokawa.localsend.expressive.platform

import dev.kurokawa.localsend.expressive.protocol.DeviceType

/** The DeviceType this platform reports to peers. */
expect fun platformDeviceType(): DeviceType

/**
 * Holds whatever the platform needs so multicast packets actually arrive.
 * Android requires a WifiManager.MulticastLock; desktop JVM needs nothing.
 * TODO(M1): wire the real Android lock from MainActivity.
 */
expect class MulticastLock() {
    fun acquire()
    fun release()
}
