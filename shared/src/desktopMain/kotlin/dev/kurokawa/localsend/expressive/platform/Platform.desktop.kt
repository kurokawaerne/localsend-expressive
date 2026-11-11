package dev.kurokawa.localsend.expressive.platform

import dev.kurokawa.localsend.expressive.protocol.DeviceType

actual fun platformDeviceType(): DeviceType = DeviceType.desktop

/** Desktop JVM receives multicast without any lock. */
actual class MulticastLock {
    actual fun acquire() {}
    actual fun release() {}
}
