package dev.kurokawa.localsend.expressive.platform

import dev.kurokawa.localsend.expressive.protocol.DeviceType

actual fun platformDeviceType(): DeviceType = DeviceType.desktop

actual fun platformDeviceModel(): String =
    System.getProperty("os.name")?.takeIf { it.isNotBlank() } ?: "Desktop"

/** Desktop JVM receives multicast without any lock. */
actual class MulticastLock {
    actual fun acquire() {}
    actual fun release() {}
}
