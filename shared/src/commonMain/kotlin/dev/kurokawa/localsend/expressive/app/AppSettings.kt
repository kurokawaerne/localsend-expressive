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
    /** Quick Save: skip the accept prompt for everyone (official feature). */
    val quickSave: Boolean = false,
    /** Close the receive progress automatically when a transfer finishes. */
    val autoFinish: Boolean = true,
    /** Custom receive folder; empty = platform default downloads folder. */
    val destinationFolder: String = "",
    /** UI theme: "system", "light" or "dark". */
    val themeMode: String = "system",
    /** Color source: "dynamic" (wallpaper/system on Android, seed on desktop) or "brand". */
    val colorMode: String = "dynamic",
    /** Fingerprints of favorite devices. */
    val favorites: Set<String> = emptySet(),
    /** Stable random fingerprint used in HTTP mode (persisted so favorites keep matching). */
    val httpFingerprint: String = "",
    // ── v0.4.0: full parity with the official app's settings ──
    /** Route received images/videos into the gallery (Pictures/Movies) instead of Downloads. */
    val saveToGallery: Boolean = true,
    /** Record finished transfers in the history list. */
    val saveToHistory: Boolean = true,
    /** After receiving, verify the SHA-256 the sender announced; reject mismatches. */
    val verifyChecksums: Boolean = false,
    /** Compute SHA-256 checksums for files we send (slower on huge files). */
    val createChecksums: Boolean = false,
    /** Auto-accept uploads that arrive through the receive-via-link page. */
    val shareViaLinkAutoAccept: Boolean = false,
    /** Master switch for splash/progress animations. */
    val enableAnimations: Boolean = true,
    /** Custom multicast group address; empty = protocol default 224.0.0.167. */
    val multicastAddress: String = "",
    /** Seconds the Send screen spends actively scanning after it opens / refresh is hit. */
    val discoveryTimeoutSec: Int = 10,
    /** Comma-separated IPs/CIDRs; when non-empty, only these peers may talk to us. */
    val networkWhitelist: String = "",
    /** Comma-separated IPs/CIDRs that are always ignored/rejected. */
    val networkBlacklist: String = "",
    /** UI language: "system", "en" or "es". */
    val language: String = "system",
    /** Whether the receive server runs at all (official's server on/off switch). */
    val serverEnabled: Boolean = true,
    /** Desktop: keep the window above others. */
    val alwaysOnTop: Boolean = false,
    /** Desktop: restore the window position/size on launch. */
    val saveWindowPlacement: Boolean = true,
    /** Desktop: closing the window minimizes to the tray instead of quitting. */
    val minimizeToTray: Boolean = false,
    // ── v0.4.0: resumable transfers ──
    /** Retry failed sends automatically (backoff), before parking them in the outbox. */
    val autoRetry: Boolean = true,
    /** Use the LSEx chunked/resumable extension when the peer supports it. */
    val chunkedTransfers: Boolean = true,
    // ── v0.5.0 ──
    /**
     * Android: a Storage Access Framework tree URI the user picked as the
     * receive destination. When set, received files are written into that
     * exact folder (through DocumentFile) instead of MediaStore Downloads.
     * Empty = platform default. Ignored on desktop.
     */
    val destinationTreeUri: String = "",
    /** App picker: hide system apps (they're rarely what you want to send). */
    val hideSystemApps: Boolean = true,
    // ── v0.8.0 ──
    /**
     * Avatar shown for this device (Settings → avatar picker). Empty =
     * the platform default (the device-type glyph). Otherwise an id from
     * the built-in avatar set (Material Symbols names).
     */
    val avatar: String = "",
    /** Receive queue default order: "smallest" (default), "arrival", "largest" or "name". */
    val receiveOrder: String = "smallest",
    /**
     * Persistent "send to multiple devices" mode (Share page split-button
     * menu). When on, device taps multi-select and Send targets all of
     * them. Stays as set until the user changes it in the same menu.
     */
    val multiDeviceSend: Boolean = false,
    /** Preferred hotspot SSID (desktop applies it; Android's local-only hotspot assigns its own). */
    val hotspotSsid: String = "",
    /** Preferred hotspot password; empty = open network where the platform allows it. */
    val hotspotPassword: String = "",
    /** Preferred hotspot band: "auto", "2.4" or "5" (GHz). */
    val hotspotBand: String = "auto",
    /** Wi-Fi Direct group name (Android 10+ honors it via WifiP2pConfig). */
    val p2pName: String = "",
    /** Wi-Fi Direct group passphrase; empty = system-generated. */
    val p2pPassword: String = "",
    /** Wi-Fi Direct band: "auto", "2.4" or "5" (GHz). */
    val p2pBand: String = "auto",
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
    /**
     * Where the file can be opened from: a content:// URI (MediaStore or
     * SAF) for received files on Android, an absolute path on desktop.
     * Empty for entries written before v0.5.0.
     */
    val uri: String = "",
    /**
     * For outgoing entries: the PickedFile handle (content URI on Android,
     * path on desktop) so the file can be re-sent from History. Empty when
     * unknown (text messages, pre-v0.5.0 entries).
     */
    val handle: String = "",
)
