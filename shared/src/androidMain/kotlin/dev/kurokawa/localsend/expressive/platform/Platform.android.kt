package dev.kurokawa.localsend.expressive.platform

import dev.kurokawa.localsend.expressive.protocol.DeviceType

actual fun platformDeviceType(): DeviceType = DeviceType.mobile

/**
 * M0 stub. Android drops multicast packets unless a WifiManager.MulticastLock
 * is held, so M1 wires a real lock in from MainActivity (it needs a Context).
 */
actual class MulticastLock {
    actual fun acquire() {}
    actual fun release() {}
}
