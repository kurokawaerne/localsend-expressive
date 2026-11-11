package dev.kurokawa.localsend.expressive.app

import dev.kurokawa.localsend.expressive.protocol.ProtocolConstants
import kotlinx.serialization.Serializable

/** User settings, persisted as JSON in the app data directory (M5). */
@Serializable
data class AppSettings(
    /** Empty = auto-generated alias. */
    val alias: String = "",
    val port: Int = ProtocolConstants.DEFAULT_PORT,
    /** HTTPS with a self-signed certificate (fingerprint = cert SHA-256). */
    val https: Boolean = true,
    /** Empty = no PIN required from senders. */
    val pin: String = "",
    /** Serve the Download API (web send) endpoints. */
    val downloadApi: Boolean = false,
    /** Skip the accept prompt for senders whose fingerprint is a favorite. */
    val autoAcceptFavorites: Boolean = false,
    /** Fingerprints of favorite devices. */
    val favorites: Set<String> = emptySet(),
    /** Stable random fingerprint used in HTTP mode (persisted so favorites keep matching). */
    val httpFingerprint: String = "",
)

/** One finished (or failed) transfer, shown in the history list. */
@Serializable
data class HistoryEntry(
    val timeMillis: Long,
    val peerAlias: String,
    val fileName: String,
    val size: Long,
    val incoming: Boolean,
    val success: Boolean,
)
