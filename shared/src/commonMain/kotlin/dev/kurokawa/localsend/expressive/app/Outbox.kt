package dev.kurokawa.localsend.expressive.app

import kotlinx.serialization.Serializable

/**
 * The persistent send outbox: every file send becomes a job on disk
 * (outbox.json) BEFORE it starts, and its state is rewritten as it
 * progresses. Kill the app, lose power, reboot — the job is still here on
 * the next launch, with per-file state and the receiver-confirmed byte
 * count, ready to retry as a whole or per file. Chunked (LSEx) jobs even
 * resume mid-file when the receive session is still alive.
 *
 * The target snapshot (alias/IP/port/fingerprint) is stored with the job:
 * a retry doesn't need the device to be rediscovered first — it talks to
 * the same endpoint, and discovery refreshes the entry independently.
 */
@Serializable
data class OutboxFile(
    /** Stable id used as the FileDto id for this file across all retries. */
    val fileId: String,
    /** Platform handle understood by openPickedInput (path or content:// URI). */
    val handle: String,
    val name: String,
    /** Relative path for folder sends ("Folder/sub/file.txt"), used as FileDto.fileName. */
    val displayPath: String? = null,
    val size: Long,
    /** SHA-256 announced to the peer (only when "create checksums" is on). */
    val sha256: String? = null,
    val status: TransferStatus = TransferStatus.WAITING,
    /** Bytes the receiver has confirmed — the resume point for chunked sends. */
    val confirmedBytes: Long = 0,
    /** Send attempts made (auto-retry budget counts these). */
    val attempts: Int = 0,
    val lastError: String? = null,
) {
    fun toProgress(): FileProgress =
        FileProgress(fileId, displayPath ?: name, size, confirmedBytes, status)
}

@Serializable
data class OutboxJob(
    val id: String,
    val createdAtMillis: Long,
    val targetAlias: String,
    val targetFingerprint: String,
    val targetIp: String,
    val targetPort: Int,
    /** "https" or "http" — the scheme the target was reached with. */
    val targetProtocol: String,
    val targetModel: String? = null,
    /** DeviceType name ("mobile", "desktop", …), for the target icon. */
    val targetType: String? = null,
    /** Live receive-session id, when one is open (resume hint). */
    val sessionId: String? = null,
    /** fileId -> upload token for [sessionId]; lets a restarted app resume a live session. */
    val tokens: Map<String, String> = emptyMap(),
    /** True when this session moves files in chunks (LSEx extension). */
    val chunked: Boolean = false,
    val files: List<OutboxFile> = emptyList(),
    /** The peer asked for a PIN (401) — the job waits for the user. */
    val needsPin: Boolean = false,
    /** Every file is DONE: the job is complete and can be dismissed. */
    val finished: Boolean = false,
) {
    val hasFailures: Boolean get() = files.any { it.status == TransferStatus.FAILED }
    val pendingCount: Int get() = files.count { it.status != TransferStatus.DONE }
}
