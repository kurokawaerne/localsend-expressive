package dev.kurokawa.localsend.expressive.discovery

import dev.kurokawa.localsend.expressive.protocol.RegisterDto

/** A peer seen via multicast announcement or /register, with the IP it was seen from. */
data class DiscoveredDevice(
    val info: RegisterDto,
    val ip: String,
    val lastSeenMillis: Long,
) {
    val fingerprint: String get() = info.fingerprint

    /**
     * Who this device IS, across renames: LSEx peers announce a stable
     * deviceId that survives alias/model/type edits; official peers have
     * none, so their fingerprint is the identity. Favorites, per-device
     * transfer data and dedupe all key on this.
     */
    val identityKey: String get() = info.deviceId ?: info.fingerprint

    /** The peer's chosen avatar id (LSEx peers only). */
    val avatar: String? get() = info.avatar
}
