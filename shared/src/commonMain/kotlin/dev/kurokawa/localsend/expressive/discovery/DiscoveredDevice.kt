package dev.kurokawa.localsend.expressive.discovery

import dev.kurokawa.localsend.expressive.protocol.RegisterDto

/** A peer seen via multicast announcement or /register, with the IP it was seen from. */
data class DiscoveredDevice(
    val info: RegisterDto,
    val ip: String,
    val lastSeenMillis: Long,
) {
    val fingerprint: String get() = info.fingerprint
}
