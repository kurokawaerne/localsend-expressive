package dev.kurokawa.localsend.expressive.app

import dev.kurokawa.localsend.expressive.LocalDevice
import dev.kurokawa.localsend.expressive.discovery.DiscoveredDevice
import dev.kurokawa.localsend.expressive.platform.PickedFile
import dev.kurokawa.localsend.expressive.protocol.FileDto
import dev.kurokawa.localsend.expressive.protocol.RegisterDto
import kotlinx.coroutines.flow.StateFlow

enum class TransferStatus { WAITING, ACTIVE, DONE, FAILED, CANCELLED }

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
)

/**
 * The contract between the shared UI and the transfer engine.
 * Implemented by LocalSendService (jvmMain) for both Android and desktop.
 */
interface LocalSendController {
    val localDevice: StateFlow<LocalDevice>
    val settings: StateFlow<AppSettings>
    val devices: StateFlow<List<DiscoveredDevice>>
    val incomingOffer: StateFlow<IncomingOffer?>
    val incomingProgress: StateFlow<List<FileProgress>>
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

    fun start()
    fun stop()

    /**
     * Answer a pending [incomingOffer] with the accepted file ids
     * (per-file selection), or null to decline the whole offer.
     */
    fun respondOffer(acceptedFileIds: Set<String>?)

    /** Re-announce in a burst and mark scanning active for the discovery timeout. */
    fun refreshDiscovery()

    /** Choose which picked files share-via-link serves. */
    fun setWebShareFiles(files: List<PickedFile>)

    /** Send [files] to [target]; pass [pin] when the peer asked for one (401). */
    fun sendFiles(target: DiscoveredDevice, files: List<PickedFile>, pin: String? = null)

    /** Send a plain text message (a first-class transfer in the protocol). */
    fun sendText(target: DiscoveredDevice, text: String, pin: String? = null)

    /** Probe a manually entered IP (legacy discovery fallback / direct connect). */
    fun probeDevice(ip: String)

    fun cancelSend()
    fun clearSendState()

    fun updateSettings(settings: AppSettings)
    fun toggleFavorite(fingerprint: String)
}
