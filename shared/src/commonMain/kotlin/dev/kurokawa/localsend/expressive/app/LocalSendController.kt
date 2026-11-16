package dev.kurokawa.localsend.expressive.app

import dev.kurokawa.localsend.expressive.LocalDevice
import dev.kurokawa.localsend.expressive.discovery.DiscoveredDevice
import dev.kurokawa.localsend.expressive.platform.PickedFile
import dev.kurokawa.localsend.expressive.protocol.FileDto
import dev.kurokawa.localsend.expressive.protocol.RegisterDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class TransferStatus { WAITING, ACTIVE, VERIFYING, DONE, FAILED, CANCELLED }

/** Progress of a single file within a transfer session. */
data class FileProgress(
    val fileId: String,
    val fileName: String,
    val total: Long,
    val done: Long = 0,
    val status: TransferStatus = TransferStatus.WAITING,
) {
    val fraction: Float get() = if (total > 0) (done.toFloat() / total).coerceIn(0f, 1f) else 0f
}

/** An incoming prepare-upload waiting for the user's decision (M2). */
/**
 * A browser asking to download the files shared via link (the
 * official web-send flow): POST /prepare-download pends until the
 * user answers, unless the share-via-link auto-accept setting is on.
 */
/** A received text message, shown as a popup on arrival (v0.12.0). */
data class IncomingText(
    val id: String,
    val peerAlias: String,
    val text: String,
    val timeMillis: Long,
)

data class WebDownloadRequest(
    val id: String,
    /** The browser's IP — also its download session id (upstream model). */
    val ip: String,
    /** Human-readable client, parsed from the User-Agent ("Chrome (Android)"). */
    val deviceInfo: String,
)

data class IncomingOffer(
    val sessionId: String,
    val peer: RegisterDto,
    val peerIp: String,
    val files: List<FileDto>,
) {
    val totalSize: Long get() = files.sumOf { it.size }
}

/** Observable state of an outgoing send session (M3). */
data class SendState(
    val target: DiscoveredDevice,
    val files: List<FileProgress>,
    val active: Boolean = true,
    val error: String? = null,
    val needsPin: Boolean = false,
    /** The outbox job this state mirrors (null for ephemeral text sends). */
    val jobId: String? = null,
)

/**
 * The contract between the shared UI and the transfer engine.
 * Implemented by LocalSendService (jvmMain) for both Android and desktop.
 */
interface LocalSendController {
    val localDevice: StateFlow<LocalDevice>
    val settings: StateFlow<AppSettings>

    /**
     * Incremented when the platform asks the UI to open the unified
     * transfers sheet (e.g. a tap on the transfer Live Update). The
     * service overrides this with its own flow; other implementations
     * share a never-firing default.
     */
    val openTransfersSignal: StateFlow<Int>
        get() = DefaultOpenTransfersSignal
    val devices: StateFlow<List<DiscoveredDevice>>
    val incomingOffer: StateFlow<IncomingOffer?>
    val incomingProgress: StateFlow<List<FileProgress>>
    /** Fingerprint of the peer an active incoming transfer belongs to. */
    val incomingPeer: StateFlow<String?>
    val sendState: StateFlow<SendState?>
    val history: StateFlow<List<HistoryEntry>>

    /** True while an active discovery scan burst is running. */
    val scanning: StateFlow<Boolean>

    /** Files currently offered through share-via-link (Download API). */
    val webShareFiles: StateFlow<List<PickedFile>>

    /** Display path of the most recently received file (e.g. "Download/Photos/a.jpg"). */
    val lastSavedTo: StateFlow<String?>

    /** This device's LAN IPv4 address (for the share-via-link URL), if any. */
    val localIpAddress: StateFlow<String?>

    /** Persistent send queue: survives restarts; retry whole jobs or single files. */
    val outbox: StateFlow<List<OutboxJob>>

    fun start()
    fun stop()

    /**
     * Answer a pending [incomingOffer] with the accepted file ids
     * (per-file selection), or null to decline the whole offer.
     */
    fun respondOffer(acceptedFileIds: Set<String>?)

    /** Pending browser download requests (share via link), oldest first. */
    val webDownloadRequests: StateFlow<List<WebDownloadRequest>>

    /** Answer a pending [webDownloadRequests] entry. */
    fun answerWebDownload(id: String, accept: Boolean)

    /** The received text message to pop up, if any (queue head). */
    val incomingText: StateFlow<IncomingText?>

    /** Dismiss the current [incomingText] popup (shows the next, if any). */
    fun dismissIncomingText()

    /** Re-announce in a burst and mark scanning active for the discovery timeout. */
    fun refreshDiscovery()

    /** Choose which picked files share-via-link serves. */
    fun setWebShareFiles(files: List<PickedFile>)

    /** Send [files] to [target]; pass [pin] when the peer asked for one (401). */
    fun sendFiles(target: DiscoveredDevice, files: List<PickedFile>, pin: String? = null)

    /**
     * Send [files] to several devices at once (v0.5.0 multi-target): one
     * independent outbox job per target, so each device can accept, fail
     * and retry on its own.
     */
    fun sendFilesToTargets(targets: List<DiscoveredDevice>, files: List<PickedFile>)

    /** Send a plain text message (a first-class transfer in the protocol). */
    fun sendText(target: DiscoveredDevice, text: String, pin: String? = null)

    /** Send the same text message to several devices (v0.5.0 multi-target). */
    fun sendTextToTargets(targets: List<DiscoveredDevice>, text: String)

    /** Probe a manually entered IP (legacy discovery fallback / direct connect). */
    fun probeDevice(ip: String)

    fun cancelSend()
    fun clearSendState()

    /** Pause a job (v0.8.0): in-flight work stops, state is kept for resume. */
    fun pauseOutboxJob(jobId: String)

    /** Resume a paused job (v0.8.0): chunked files continue from their offset. */
    fun resumeOutboxJob(jobId: String)

    /** How many incoming offers are queued behind the one being shown (v0.8.0). */
    val queuedOfferCount: StateFlow<Int>

    /** Re-run a whole outbox job (every file that isn't DONE). */
    fun retryOutboxJob(jobId: String, pin: String? = null)

    /** Re-run one file of an outbox job. */
    fun retryOutboxFile(jobId: String, fileId: String, pin: String? = null)

    /** Global retry: re-run every failed/interrupted file in every job. */
    fun retryAllFailed()

    /** Remove a job from the outbox (e.g. once finished, or given up on). */
    fun dismissOutboxJob(jobId: String)

    fun updateSettings(settings: AppSettings)
    fun toggleFavorite(fingerprint: String)

    /**
     * Favorite/unfavorite a discovered device, remembering its details
     * (name, model, type, avatar) for the Settings favorites section.
     * Default: fall back to the fingerprint-only toggle.
     */
    fun toggleFavorite(device: DiscoveredDevice) {
        toggleFavorite(device.fingerprint)
    }

    /**
     * The user's drag-reorder of the incoming queue: LSEx senders fetch
     * this order between files and send in it (v0.12.4 priority sync).
     */
    fun setReceiveOrder(fileIds: List<String>) {}

    /** Restart the receive server + discovery (applies identity edits). */
    fun restartServer() {}

    /** Remove one history entry (v0.8.0). */
    fun removeHistoryEntry(entry: HistoryEntry)

    /** Clear the whole history (v0.8.0). */
    fun clearHistory()
}

/** Shared never-firing default for [LocalSendController.openTransfersSignal]. */
private val DefaultOpenTransfersSignal = MutableStateFlow(0)
