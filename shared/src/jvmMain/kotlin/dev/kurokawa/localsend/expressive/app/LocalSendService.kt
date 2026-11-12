package dev.kurokawa.localsend.expressive.app

import dev.kurokawa.localsend.expressive.LocalDevice
import dev.kurokawa.localsend.expressive.discovery.DiscoveredDevice
import dev.kurokawa.localsend.expressive.discovery.MulticastDiscovery
import dev.kurokawa.localsend.expressive.platform.MulticastLock
import dev.kurokawa.localsend.expressive.platform.PickedFile
import dev.kurokawa.localsend.expressive.platform.openPickedInput
import dev.kurokawa.localsend.expressive.platform.platformDeviceModel
import dev.kurokawa.localsend.expressive.platform.platformDeviceType
import dev.kurokawa.localsend.expressive.protocol.DeviceType
import dev.kurokawa.localsend.expressive.protocol.FileDto
import dev.kurokawa.localsend.expressive.protocol.ProtocolConstants
import dev.kurokawa.localsend.expressive.protocol.RegisterDto
import dev.kurokawa.localsend.expressive.protocol.generateRandomFingerprint
import dev.kurokawa.localsend.expressive.randomAlias
import dev.kurokawa.localsend.expressive.transfer.CertManager
import dev.kurokawa.localsend.expressive.transfer.FingerprintTrustManager
import dev.kurokawa.localsend.expressive.transfer.ReceiveServer
import dev.kurokawa.localsend.expressive.transfer.SendClient
import dev.kurokawa.localsend.expressive.transfer.SendException
import dev.kurokawa.localsend.expressive.transfer.TransferMath
import dev.kurokawa.localsend.expressive.transfer.guessFileType
import dev.kurokawa.localsend.expressive.transfer.sha256Hex
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * The transfer engine: owns discovery, the receive server and the send
 * client, and exposes everything the UI needs as state flows.
 * One instance per app process, started/stopped with the UI lifecycle.
 *
 * Sends run through the persistent OUTBOX: a send becomes an [OutboxJob]
 * on disk before it starts, so it can be retried — as a whole or per
 * file — after failures, disconnections, or an app restart. Against LSEx
 * peers the job moves files in 1 MiB chunks (see [TransferMath]) and a
 * retry resumes from the receiver's confirmed byte count; against
 * official peers it falls back to the classic whole-file upload.
 */
class LocalSendService : LocalSendController {
    private val store = SettingsStore()
    private val certManager = CertManager()
    private val trustManager = FingerprintTrustManager()
    private val multicastLock = MulticastLock()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _settings = MutableStateFlow(store.loadSettings())
    override val settings: StateFlow<AppSettings> = _settings

    private val _localDevice = MutableStateFlow(buildLocal())
    override val localDevice: StateFlow<LocalDevice> = _localDevice

    private val _devices = MutableStateFlow<List<DiscoveredDevice>>(emptyList())
    override val devices: StateFlow<List<DiscoveredDevice>> = _devices

    private val _incomingOffer = MutableStateFlow<IncomingOffer?>(null)
    override val incomingOffer: StateFlow<IncomingOffer?> = _incomingOffer

    private val _incomingProgress = MutableStateFlow<List<FileProgress>>(emptyList())
    override val incomingProgress: StateFlow<List<FileProgress>> = _incomingProgress

    private val _sendState = MutableStateFlow<SendState?>(null)
    override val sendState: StateFlow<SendState?> = _sendState

    private val _history = MutableStateFlow(store.loadHistory())
    override val history: StateFlow<List<HistoryEntry>> = _history

    private val _scanning = MutableStateFlow(false)
    override val scanning: StateFlow<Boolean> = _scanning

    private val _webShareFiles = MutableStateFlow<List<PickedFile>>(emptyList())
    override val webShareFiles: StateFlow<List<PickedFile>> = _webShareFiles

    private val _lastSavedTo = MutableStateFlow<String?>(null)
    override val lastSavedTo: StateFlow<String?> = _lastSavedTo

    private val _localIpAddress = MutableStateFlow<String?>(null)
    override val localIpAddress: StateFlow<String?> = _localIpAddress

    private val _outbox = MutableStateFlow<List<OutboxJob>>(emptyList())
    override val outbox: StateFlow<List<OutboxJob>> = _outbox

    private var running = false
    private var server: ReceiveServer? = null
    private var discovery: MulticastDiscovery? = null
    private var sendClient: SendClient? = null
    private var listenJob: Job? = null
    private var announceJob: Job? = null
    private var sendJob: Job? = null
    private var currentSendSession: Pair<DiscoveredDevice, String>? = null

    // ── outbox runner state (transient; the jobs themselves persist) ──
    private val outboxLock = Any()

    /** fingerprint (or IP) -> peer speaks the LSEx chunked extension. */
    private val chunkedCapabilityCache = java.util.concurrent.ConcurrentHashMap<String, Boolean>()
    private val wantedJobIds = mutableSetOf<String>()
    private val pendingPins = mutableMapOf<String, String?>()
    private var runnerJob: Job? = null

    @Volatile
    private var runningJobId: String? = null

    /** Live byte counts for whole-file uploads (not worth persisting per callback). */
    private val liveDone = ConcurrentHashMap<String, Long>()

    private val _queuedOfferCount = MutableStateFlow(0)
    override val queuedOfferCount: StateFlow<Int> = _queuedOfferCount

    /**
     * Incoming offers waiting for the user, FIFO (v0.8.0 receive queue).
     * The head is mirrored into [_incomingOffer] for the accept dialog;
     * offers behind it stay queued instead of overwriting the pending
     * decision (the old single-slot code silently dropped the first
     * sender's decision when a second offer arrived).
     */
    private val offerLock = Any()
    private val offerQueue = ArrayDeque<Pair<IncomingOffer, CompletableDeferred<Set<String>?>>>()

    // ── lifecycle ──────────────────────────────────────────────────────────

    override fun start() {
        if (running) return
        running = true
        // Stable identity, generated ONCE and persisted. (v0.2.x bug: the
        // random alias was regenerated on every cold start because the
        // generated value was never saved — the device "changed its name"
        // each launch, exactly as Ernest reported.)
        if (_settings.value.alias.isEmpty()) {
            _settings.value = _settings.value.copy(alias = randomAlias())
            store.saveSettings(_settings.value)
        }
        // Persist a stable HTTP-mode fingerprint once, so favorites keep working.
        if (!_settings.value.https && _settings.value.httpFingerprint.isEmpty()) {
            _settings.value = _settings.value.copy(httpFingerprint = generateRandomFingerprint())
            store.saveSettings(_settings.value)
        }
        _localDevice.value = buildLocal()
        multicastLock.acquire()
        // The TLS identity must exist even in HTTP mode: HTTPS peers use
        // mutual TLS and expect us to present this certificate.
        runCatching { certManager.ensure() }
        sendClient = SendClient(trustManager) {
            if (certManager.isInitialized) certManager else null
        }
        loadOutbox()
        startNetworking()
        // Resume any job that was mid-send when the app last died.
        pump()
    }

    override fun stop() {
        if (!running) return
        running = false
        runnerJob?.cancel()
        stopNetworking()
        sendClient?.close()
        sendClient = null
        multicastLock.release()
    }

    private fun startNetworking() {
        val local = _localDevice.value
        _localIpAddress.value = computeLocalIp()
        if (_settings.value.serverEnabled) {
            server = ReceiveServer(
                localProvider = { _localDevice.value },
                settingsProvider = { _settings.value },
                keyStoreProvider = { if (_settings.value.https) certManager.keyStore else null },
                ipAllowed = { ip -> ipAllowed(ip) },
                webShareProvider = {
                    _webShareFiles.value.map { picked ->
                        dev.kurokawa.localsend.expressive.transfer.WebShareFile(
                            name = picked.name,
                            size = picked.size,
                        ) { openPickedInput(picked.handle) }
                    }
                },
                callbacks = serverCallbacks,
            ).also { it.start() }
        }

        val disc = MulticastDiscovery(
            local = local,
            groupAddress = _settings.value.multicastAddress
                .ifBlank { ProtocolConstants.MULTICAST_ADDRESS },
            onPeer = { dto, ip -> if (ipAllowed(ip)) addDevice(dto, ip) },
            onAnnouncement = { dto, ip -> if (ipAllowed(ip)) onAnnouncement(dto, ip) },
        )
        discovery = disc
        listenJob = scope.launch {
            // The listener must survive socket errors (Wi-Fi roam, sleep):
            // restart it with a small backoff instead of dying silently —
            // a dead listener was half of the "devices randomly disappear"
            // report.
            while (isActive) {
                runCatching { disc.listen() }
                delay(2_000)
            }
        }
        announceJob = scope.launch {
            // Burst at start so peers see us immediately, then a steady
            // cadence. Official peers do not heartbeat, so our announces
            // (and their /register answers) are what keep entries fresh.
            repeat(3) {
                runCatching { disc.announce() }
                delay(400)
            }
            while (isActive) {
                runCatching { disc.announce() }
                pruneDevices()
                delay(ANNOUNCE_INTERVAL_MS)
            }
        }
    }

    override fun refreshDiscovery() {
        val disc = discovery ?: return
        scope.launch {
            _scanning.value = true
            try {
                repeat(3) {
                    runCatching { disc.announce() }
                    delay(350)
                }
                delay(_settings.value.discoveryTimeoutSec.coerceIn(2, 30) * 1000L)
            } finally {
                _scanning.value = false
            }
        }
    }

    override fun setWebShareFiles(files: List<PickedFile>) {
        _webShareFiles.value = files
    }

    /** Network allow/block lists (settings): exact IPs or CIDR ranges. */
    fun ipAllowed(ip: String): Boolean {
        val s = _settings.value
        if (ipMatchesAny(ip, s.networkBlacklist)) return false
        val whitelist = s.networkWhitelist.split(',', ' ', ';').filter { it.isNotBlank() }
        if (whitelist.isEmpty()) return true
        return ipMatchesAny(ip, s.networkWhitelist)
    }

    private fun ipMatchesAny(ip: String, list: String): Boolean =
        list.split(',', ' ', ';').filter { it.isNotBlank() }.any { entry -> ipMatches(ip, entry.trim()) }

    private fun ipMatches(ip: String, entry: String): Boolean {
        if (!entry.contains('/')) return ip == entry
        return runCatching {
            val (network, prefixText) = entry.split('/')
            val prefix = prefixText.toInt()
            val ipBytes = java.net.InetAddress.getByName(ip).address
            val netBytes = java.net.InetAddress.getByName(network).address
            if (ipBytes.size != netBytes.size) return false
            var bits = prefix
            for (i in ipBytes.indices) {
                if (bits <= 0) break
                val mask = if (bits >= 8) 0xFF else (0xFF shl (8 - bits)) and 0xFF
                if ((ipBytes[i].toInt() and mask) != (netBytes[i].toInt() and mask)) return false
                bits -= 8
            }
            true
        }.getOrDefault(false)
    }

    private fun computeLocalIp(): String? = runCatching {
        java.net.NetworkInterface.getNetworkInterfaces().asSequence()
            .filter { it.isUp && !it.isLoopback && !it.isVirtual }
            .flatMap { it.inetAddresses.asSequence() }
            .firstOrNull { it is java.net.Inet4Address && it.isSiteLocalAddress }
            ?.hostAddress
    }.getOrNull()

    private fun stopNetworking() {
        listenJob?.cancel()
        announceJob?.cancel()
        server?.stop()
        server = null
        discovery = null
    }

    private fun restartNetworking() {
        if (!running) return
        stopNetworking()
        _localDevice.value = buildLocal()
        startNetworking()
    }

    private fun buildLocal(): LocalDevice {
        val s = _settings.value
        val fingerprint = if (s.https) {
            certManager.ensure()
            certManager.fingerprint
        } else {
            s.httpFingerprint.ifEmpty { generateRandomFingerprint() }
        }
        return LocalDevice(
            alias = s.alias.ifEmpty { randomAlias() },
            deviceModel = platformDeviceModel(),
            deviceType = platformDeviceType(),
            fingerprint = fingerprint,
            port = s.port,
            protocol = if (s.https) "https" else "http",
            download = s.downloadApi,
        )
    }

    // ── discovery ──────────────────────────────────────────────────────────

    private fun addDevice(dto: RegisterDto, ip: String) {
        if (dto.fingerprint == _localDevice.value.fingerprint) return
        val now = System.currentTimeMillis()
        val current = _devices.value.toMutableList()
        val index = current.indexOfFirst { it.fingerprint == dto.fingerprint }
        val device = DiscoveredDevice(dto, ip, now)
        if (index >= 0) current[index] = device else current += device
        _devices.value = current.sortedBy { it.info.alias.lowercase() }
    }

    private fun pruneDevices() {
        val cutoff = System.currentTimeMillis() - DEVICE_TTL_MS
        _devices.value = _devices.value.filter { it.lastSeenMillis >= cutoff }
    }

    private fun onAnnouncement(dto: RegisterDto, ip: String) {
        addDevice(dto, ip)
        val client = sendClient ?: return
        val disc = discovery ?: return
        scope.launch {
            // Primary answer: POST /register (also yields the peer's info).
            val response = client.register(DiscoveredDevice(dto, ip, System.currentTimeMillis()), _localDevice.value)
            if (response != null) {
                // The register response is an InfoDto: it carries no
                // port/protocol, so keep the ones from the announcement.
                addDevice(
                    RegisterDto(
                        alias = response.alias,
                        version = response.version,
                        deviceModel = response.deviceModel,
                        deviceType = response.deviceType,
                        fingerprint = response.fingerprint,
                        port = dto.port,
                        protocol = dto.protocol,
                        download = response.download,
                    ),
                    ip,
                )
            } else {
                // Fallback: UDP response with announce=false.
                runCatching { disc.respondUdp() }
            }
        }
    }

    // ── receiving ──────────────────────────────────────────────────────────

    private val serverCallbacks = object : ReceiveServer.Callbacks {
        override fun onPeerRegistered(dto: RegisterDto, ip: String) = addDevice(dto, ip)

        override fun onMessage(peer: RegisterDto, text: String) {
            addHistory(peer.alias, text.take(120), text.toByteArray(Charsets.UTF_8).size.toLong(), incoming = true, success = true)
        }

        override suspend fun onOffer(offer: IncomingOffer): Set<String>? = awaitOfferDecision(offer)

        override fun onReceiveProgress(sessionId: String, files: List<FileProgress>) {
            _incomingProgress.value = files
        }

        override fun onFileReceived(
            sessionId: String,
            peer: RegisterDto,
            file: FileDto,
            savedToDisplay: String,
            savedUri: String?,
        ) {
            _lastSavedTo.value = savedToDisplay
            addHistory(
                peer.alias, file.fileName, file.size,
                incoming = true, success = true, uri = savedUri ?: "",
            )
        }

        override fun onSessionFinished(sessionId: String, peer: RegisterDto) = Unit
    }

    private suspend fun awaitOfferDecision(offer: IncomingOffer): Set<String>? {
        val s = _settings.value
        val allIds = offer.files.map { it.id }.toSet()
        if (s.quickSave) return allIds
        if (s.autoAcceptFavorites && offer.peer.fingerprint in s.favorites) return allIds
        // Uploads arriving through the web page (receive via link) carry
        // deviceType "web"; the official auto-accept setting covers them.
        if (s.shareViaLinkAutoAccept && offer.peer.deviceType?.name == "web") return allIds
        val deferred = CompletableDeferred<Set<String>?>()
        synchronized(offerLock) {
            offerQueue.addLast(offer to deferred)
            if (offerQueue.size == 1) {
                _incomingOffer.value = offer
            }
            _queuedOfferCount.value = (offerQueue.size - 1).coerceAtLeast(0)
        }
        return try {
            deferred.await()
        } finally {
            synchronized(offerLock) {
                offerQueue.removeAll { it.second === deferred }
                _incomingOffer.value = offerQueue.firstOrNull()?.first
                _queuedOfferCount.value = (offerQueue.size - 1).coerceAtLeast(0)
            }
        }
    }

    override fun respondOffer(acceptedFileIds: Set<String>?) {
        val head = synchronized(offerLock) { offerQueue.firstOrNull()?.second }
        head?.complete(acceptedFileIds)
    }

    // ── outbox: persistence ────────────────────────────────────────────────

    private fun loadOutbox() {
        var resumedAny = false
        val loaded = store.loadOutbox().map { job ->
            val wasMidSend = job.files.any { it.status == TransferStatus.ACTIVE }
            if (wasMidSend) resumedAny = true
            job.copy(
                needsPin = false,
                files = job.files.map { file ->
                    if (file.status == TransferStatus.ACTIVE) {
                        // The app died mid-send: park the file as failed with
                        // a clear reason; the job auto-resumes below and a
                        // chunked session may pick up mid-file.
                        file.copy(
                            status = TransferStatus.FAILED,
                            lastError = "The app closed during the send — tap retry to resume",
                        )
                    } else {
                        file
                    }
                },
            )
        }
        synchronized(outboxLock) {
            _outbox.value = loaded
            if (resumedAny) {
                loaded.filter { job ->
                    !job.finished && job.files.any { it.status == TransferStatus.FAILED }
                }.forEach { wantedJobIds += it.id }
            }
        }
        store.saveOutbox(loaded)
    }

    private fun persistOutbox() {
        store.saveOutbox(_outbox.value)
    }

    private fun jobById(jobId: String): OutboxJob? = _outbox.value.firstOrNull { it.id == jobId }

    /** Apply [transform] to one job, persist, and mirror it into sendState. */
    private fun mutateJob(jobId: String, transform: (OutboxJob) -> OutboxJob): OutboxJob? {
        val updated: OutboxJob?
        synchronized(outboxLock) {
            val current = _outbox.value
            val index = current.indexOfFirst { it.id == jobId }
            if (index < 0) return null
            val newJob = transform(current[index])
            _outbox.value = current.toMutableList().also { it[index] = newJob }
            updated = newJob
        }
        persistOutbox()
        updated?.let { publishJobState(it) }
        return updated
    }

    private fun mutateFile(
        jobId: String,
        fileId: String,
        transform: (OutboxFile) -> OutboxFile,
    ): OutboxJob? = mutateJob(jobId) { job ->
        job.copy(files = job.files.map { if (it.fileId == fileId) transform(it) else it })
    }

    private fun jobTarget(job: OutboxJob): DiscoveredDevice = DiscoveredDevice(
        RegisterDto(
            alias = job.targetAlias,
            deviceModel = job.targetModel,
            deviceType = job.targetType?.let { name ->
                DeviceType.entries.firstOrNull { it.name == name }
            },
            fingerprint = job.targetFingerprint,
            port = job.targetPort,
            protocol = job.targetProtocol,
        ),
        job.targetIp,
        System.currentTimeMillis(),
    )

    /** Mirror an outbox job into the legacy sendState flow the Send card binds to. */
    private fun publishJobState(job: OutboxJob) {
        // Only the most recent file job drives the shared send card; text
        // sends (jobId == null) are never overwritten by an older job.
        val current = _sendState.value
        if (current != null && current.jobId == null && current.active) return
        _sendState.value = SendState(
            target = jobTarget(job),
            files = job.files.map { file ->
                FileProgress(
                    file.fileId,
                    file.displayPath ?: file.name,
                    file.size,
                    liveDone[file.fileId] ?: file.confirmedBytes,
                    file.status,
                )
            },
            active = runningJobId == job.id,
            error = job.files.firstOrNull {
                it.lastError != null && it.status == TransferStatus.FAILED
            }?.lastError,
            needsPin = job.needsPin,
            jobId = job.id,
        )
    }

    // ── outbox: entry points ───────────────────────────────────────────────

    override fun sendFiles(target: DiscoveredDevice, files: List<PickedFile>, pin: String?) {
        if (files.isEmpty()) return
        val job = OutboxJob(
            id = UUID.randomUUID().toString(),
            createdAtMillis = System.currentTimeMillis(),
            targetAlias = target.info.alias,
            targetFingerprint = target.fingerprint,
            targetIp = target.ip,
            targetPort = target.info.port,
            targetProtocol = target.info.protocol,
            targetModel = target.info.deviceModel,
            targetType = target.info.deviceType?.name,
            files = files.map { picked ->
                OutboxFile(
                    fileId = UUID.randomUUID().toString(),
                    handle = picked.handle,
                    name = picked.name,
                    displayPath = picked.displayPath,
                    size = picked.size,
                )
            },
        )
        synchronized(outboxLock) {
            // Newest first; cap the queue, dropping finished jobs first.
            var jobs = listOf(job) + _outbox.value
            if (jobs.size > MAX_OUTBOX_JOBS) {
                val finished = jobs.filter { it.finished }
                val active = jobs.filter { !it.finished }
                jobs = (active + finished.take((MAX_OUTBOX_JOBS - active.size).coerceAtLeast(0)))
                    .sortedByDescending { it.createdAtMillis }
            }
            _outbox.value = jobs
            wantedJobIds += job.id
            if (pin != null) pendingPins[job.id] = pin
        }
        persistOutbox()
        publishJobState(job)
        pump()
    }

    override fun sendFilesToTargets(targets: List<DiscoveredDevice>, files: List<PickedFile>) {
        // One independent job per device: the outbox runner works through
        // them in order, and each can be retried/cancelled on its own.
        targets.forEach { sendFiles(it, files) }
    }

    override fun retryOutboxJob(jobId: String, pin: String?) {
        mutateJob(jobId) { job ->
            job.copy(
                needsPin = false,
                files = job.files.map { file ->
                    if (file.status == TransferStatus.FAILED ||
                        file.status == TransferStatus.CANCELLED
                    ) {
                        file.copy(
                            status = TransferStatus.WAITING,
                            attempts = 0,
                            lastError = null,
                        )
                    } else {
                        file
                    }
                },
            )
        }
        synchronized(outboxLock) {
            wantedJobIds += jobId
            if (pin != null) pendingPins[jobId] = pin
        }
        pump()
    }

    override fun retryOutboxFile(jobId: String, fileId: String, pin: String?) {
        mutateFile(jobId, fileId) { file ->
            file.copy(status = TransferStatus.WAITING, attempts = 0, lastError = null)
        }
        mutateJob(jobId) { it.copy(needsPin = false) }
        synchronized(outboxLock) {
            wantedJobIds += jobId
            if (pin != null) pendingPins[jobId] = pin
        }
        pump()
    }

    override fun retryAllFailed() {
        val failedJobIds = _outbox.value
            .filter { job -> job.files.any { it.status == TransferStatus.FAILED } }
            .map { it.id }
        failedJobIds.forEach { retryOutboxJob(it) }
    }

    override fun dismissOutboxJob(jobId: String) {
        if (runningJobId == jobId) runnerJob?.cancel()
        synchronized(outboxLock) {
            _outbox.value = _outbox.value.filter { it.id != jobId }
            wantedJobIds -= jobId
        }
        persistOutbox()
        if (_sendState.value?.jobId == jobId) _sendState.value = null
    }

    override fun pauseOutboxJob(jobId: String) {
        // Cancelling the runner parks the in-flight file as WAITING (the
        // sendOneFile CancellationException path); chunked files resume
        // from the receiver's confirmed offset on resume.
        if (runningJobId == jobId) runnerJob?.cancel()
        synchronized(outboxLock) { wantedJobIds -= jobId }
        mutateJob(jobId) { it.copy(paused = true) }
    }

    override fun resumeOutboxJob(jobId: String) {
        mutateJob(jobId) { it.copy(paused = false) }
        synchronized(outboxLock) { wantedJobIds += jobId }
        pump()
    }

    // ── outbox: the runner ─────────────────────────────────────────────────

    /** Start the single runner loop if it isn't already working. */
    private fun pump() {
        if (runnerJob?.isActive == true) return
        runnerJob = scope.launch {
            while (true) {
                val next = synchronized(outboxLock) {
                    _outbox.value.firstOrNull { job ->
                        job.id in wantedJobIds && !job.finished && !job.paused &&
                            job.files.any {
                                it.status == TransferStatus.WAITING ||
                                    it.status == TransferStatus.FAILED
                            }
                    }
                } ?: break
                runningJobId = next.id
                publishJobState(next)
                try {
                    val pin = synchronized(outboxLock) { pendingPins.remove(next.id) }
                    runJob(next.id, pin)
                } catch (e: CancellationException) {
                    // File handlers already parked in-flight files as
                    // WAITING. Cancellation must end the runner (a fresh
                    // pump() starts a new one for the next retry).
                    throw e
                } catch (e: Exception) {
                    // A runner crash must not take the loop down with it.
                } finally {
                    runningJobId = null
                    synchronized(outboxLock) { wantedJobIds -= next.id }
                    jobById(next.id)?.let { publishJobState(it) }
                }
            }
        }
    }

    private enum class FileOutcome { DONE, FAILED, SESSION_DEAD }

    /** Thrown inside a file send when the receive session is gone (403 from chunk routes). */
    private class SessionDeadException : Exception("receive session is gone")

    private suspend fun runJob(jobId: String, pin: String?) {
        val client = sendClient ?: return
        var sessionRestarts = 0
        while (true) {
            val job = jobById(jobId) ?: return
            val target = jobTarget(job)
            val pending = job.files.filter { it.status != TransferStatus.DONE }
            if (pending.isEmpty()) {
                mutateJob(jobId) { it.copy(finished = true, needsPin = false) }
                return
            }

            var sessionId = job.sessionId
            var tokens = job.tokens
            var chunked = job.chunked

            // Can we resume the session a previous run (or a previous app
            // process) left open on the receiver? Only chunked sessions
            // can be probed; whole-file sessions are re-prepared instead
            // (our server lets the same peer replace its own stale one).
            val liveSession = if (sessionId != null && tokens.isNotEmpty() && chunked) {
                val probe = pending.firstOrNull { it.fileId in tokens }
                probe != null && client.chunkStatus(
                    target, sessionId, probe.fileId, tokens.getValue(probe.fileId),
                ) != null
            } else {
                false
            }

            if (!liveSession) {
                sessionId = null
                tokens = emptyMap()
                // Checksums are computed once and kept with the job, so a
                // retry doesn't pay the hashing pass again.
                if (_settings.value.createChecksums) {
                    pending.filter { it.sha256 == null }.forEach { file ->
                        val picked = PickedFile(file.handle, file.name, file.size, file.displayPath)
                        sha256Of(picked)?.let { hash ->
                            mutateFile(jobId, file.fileId) { it.copy(sha256 = hash) }
                        }
                    }
                }
                val fresh = jobById(jobId) ?: return
                val dtos = fresh.files
                    .filter { it.status != TransferStatus.DONE }
                    .associate { file ->
                        file.fileId to FileDto(
                            id = file.fileId,
                            fileName = file.displayPath ?: file.name,
                            size = file.size,
                            fileType = guessFileType(file.name),
                            sha256 = if (_settings.value.createChecksums) file.sha256 else null,
                        )
                    }
                val prepared = try {
                    client.prepareUpload(target, _localDevice.value, dtos, pin)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: SendException) {
                    if (e.status == HttpStatusCode.Unauthorized) {
                        mutateJob(jobId) { it.copy(needsPin = true) }
                    } else {
                        val message = describeError(e)
                        pending.forEach { file ->
                            mutateFile(jobId, file.fileId) { it.copy(lastError = message) }
                        }
                    }
                    return
                } catch (e: Exception) {
                    val message = friendlyError(e)
                    pending.forEach { file ->
                        mutateFile(jobId, file.fileId) { it.copy(lastError = message) }
                    }
                    return
                }
                if (prepared.sessionId == null) {
                    // 204 with files present shouldn't happen; treat as accepted.
                    pending.forEach { file ->
                        mutateFile(jobId, file.fileId) {
                            it.copy(status = TransferStatus.DONE, confirmedBytes = it.size)
                        }
                    }
                    continue
                }
                sessionId = prepared.sessionId
                tokens = prepared.files
                // Capability results are cached per device: re-probing on
                // every session cost a full TLS round trip before the
                // first byte of every send (Ernest's "takes some seconds
                // to start" report).
                chunked = if (_settings.value.chunkedTransfers) {
                    val key = target.fingerprint.ifEmpty { target.ip }
                    chunkedCapabilityCache[key]
                        ?: (client.lsexCapabilities(target)?.chunked == true)
                            .also { chunkedCapabilityCache[key] = it }
                } else {
                    false
                }
                val sid = sessionId
                val toks = tokens
                val isChunked = chunked
                mutateJob(jobId) {
                    it.copy(sessionId = sid, tokens = toks, chunked = isChunked, needsPin = false)
                }
                // Partial acceptance: no token = the peer declined the file.
                pending.filter { it.fileId !in toks }.forEach { file ->
                    mutateFile(jobId, file.fileId) {
                        it.copy(
                            status = TransferStatus.FAILED,
                            lastError = "The device didn't accept this file",
                        )
                    }
                }
            }

            var sessionDied = false
            val filesNow = jobById(jobId)?.files ?: return
            for (file in filesNow) {
                if (file.status == TransferStatus.DONE) continue
                val token = tokens[file.fileId] ?: continue
                val outcome = sendOneFile(
                    client, target, sessionId!!, token, jobId, file.fileId, chunked,
                )
                if (outcome == FileOutcome.SESSION_DEAD) {
                    sessionDied = true
                    break
                }
            }
            if (sessionDied) {
                mutateJob(jobId) { it.copy(sessionId = null, tokens = emptyMap()) }
                sessionRestarts++
                if (sessionRestarts > MAX_SESSION_RESTARTS) return
                continue
            }
            return
        }
    }

    /** Send one file with the auto-retry budget; persists every state change. */
    private suspend fun sendOneFile(
        client: SendClient,
        target: DiscoveredDevice,
        sessionId: String,
        token: String,
        jobId: String,
        fileId: String,
        chunked: Boolean,
    ): FileOutcome {
        while (true) {
            val file = jobById(jobId)?.files?.firstOrNull { it.fileId == fileId }
                ?: return FileOutcome.FAILED
            if (file.status == TransferStatus.DONE) return FileOutcome.DONE
            mutateFile(jobId, fileId) {
                it.copy(
                    status = TransferStatus.ACTIVE,
                    attempts = it.attempts + 1,
                    lastError = null,
                )
            }
            try {
                if (chunked) {
                    sendFileChunked(client, target, sessionId, token, jobId, fileId)
                } else {
                    sendFileWhole(client, target, sessionId, token, jobId, fileId)
                }
                liveDone.remove(fileId)
                mutateFile(jobId, fileId) {
                    it.copy(
                        status = TransferStatus.DONE,
                        confirmedBytes = it.size,
                        lastError = null,
                    )
                }
                val done = jobById(jobId)?.files?.firstOrNull { it.fileId == fileId }
                addHistory(
                    target.info.alias,
                    done?.displayPath ?: done?.name ?: file.name,
                    file.size,
                    incoming = false,
                    success = true,
                    handle = file.handle,
                )
                return FileOutcome.DONE
            } catch (e: SessionDeadException) {
                mutateFile(jobId, fileId) {
                    it.copy(status = TransferStatus.WAITING, lastError = null)
                }
                return FileOutcome.SESSION_DEAD
            } catch (e: CancellationException) {
                mutateFile(jobId, fileId) {
                    it.copy(status = TransferStatus.WAITING, lastError = "Cancelled")
                }
                throw e
            } catch (e: SendException) {
                // Protocol rejections (declined, busy, bad checksum) are not
                // transient: park the file for a manual retry.
                if (e.status == HttpStatusCode.Forbidden) {
                    mutateFile(jobId, fileId) {
                        it.copy(status = TransferStatus.WAITING, lastError = null)
                    }
                    return FileOutcome.SESSION_DEAD
                }
                mutateFile(jobId, fileId) {
                    it.copy(status = TransferStatus.FAILED, lastError = describeError(e))
                }
                return FileOutcome.FAILED
            } catch (e: Exception) {
                // Transport failure (Wi-Fi drop, peer sleep, …): auto-retry
                // with backoff, then park as FAILED for the outbox UI.
                val attemptsNow = jobById(jobId)?.files
                    ?.firstOrNull { it.fileId == fileId }?.attempts ?: 1
                val message = friendlyError(e)
                if (_settings.value.autoRetry && attemptsNow < MAX_AUTO_ATTEMPTS) {
                    mutateFile(jobId, fileId) {
                        it.copy(status = TransferStatus.WAITING, lastError = message)
                    }
                    delay(backoffMillis(attemptsNow))
                    continue
                }
                mutateFile(jobId, fileId) {
                    it.copy(status = TransferStatus.FAILED, lastError = message)
                }
                return FileOutcome.FAILED
            }
        }
    }

    private fun backoffMillis(attempts: Int): Long =
        (2_000L shl attempts.coerceIn(0, 4)).coerceAtMost(30_000L)

    /**
     * Chunked send (LSEx extension): ask the receiver what it already has,
     * then stream only the missing chunks. Any interruption leaves the
     * receiver's staged bytes intact, so the next attempt — in seconds or
     * after an app restart — picks up at the confirmed offset.
     */
    private suspend fun sendFileChunked(
        client: SendClient,
        target: DiscoveredDevice,
        sessionId: String,
        token: String,
        jobId: String,
        fileId: String,
    ) {
        val file = jobById(jobId)?.files?.firstOrNull { it.fileId == fileId }
            ?: throw IOException("Outbox file vanished")
        val reader = ChunkReader(file.handle)
        try {
            while (true) {
                val confirmed = client.chunkStatus(target, sessionId, fileId, token)
                    ?: throw SessionDeadException()
                val resumeAt = TransferMath.resumeOffset(confirmed)
                mutateFile(jobId, fileId) { it.copy(confirmedBytes = confirmed) }
                if (resumeAt >= file.size) break
                val index = resumeAt / TransferMath.CHUNK_SIZE
                val length = TransferMath.chunkLength(index, file.size).toInt()
                val bytes = reader.readChunk(resumeAt, length)
                val newConfirmed = try {
                    client.uploadChunk(
                        target, sessionId, fileId, token, resumeAt, bytes,
                        TransferMath.sha256Hex(bytes),
                    )
                } catch (e: SendException) {
                    if (e.status == HttpStatusCode.Forbidden) throw SessionDeadException()
                    throw e
                }
                mutateFile(jobId, fileId) { it.copy(confirmedBytes = newConfirmed) }
            }
        } finally {
            reader.close()
        }
        try {
            client.finishChunkedFile(target, sessionId, fileId, token)
        } catch (e: SendException) {
            if (e.status == HttpStatusCode.Forbidden) throw SessionDeadException()
            throw e
        }
    }

    /** Classic whole-file upload (official peers, or chunked mode off). */
    private suspend fun sendFileWhole(
        client: SendClient,
        target: DiscoveredDevice,
        sessionId: String,
        token: String,
        jobId: String,
        fileId: String,
    ) {
        val file = jobById(jobId)?.files?.firstOrNull { it.fileId == fileId }
            ?: throw IOException("Outbox file vanished")
        client.upload(
            target, sessionId, fileId, token, file.size,
            openStream = { openPickedInput(file.handle) },
        ) { sent ->
            liveDone[fileId] = sent
            jobById(jobId)?.let { publishJobState(it) }
        }
    }

    /**
     * Sequential reader over a picked file for chunked sends: keeps one
     * stream open and reads forward; reopens + skips when a resume point
     * sits behind the current position.
     */
    private class ChunkReader(private val handle: String) {
        private var stream: InputStream? = null
        private var position = 0L

        fun readChunk(offset: Long, length: Int): ByteArray {
            when {
                stream == null || position > offset -> reopen(offset)
                position < offset -> skipFully(offset - position)
            }
            val bytes = ByteArray(length)
            var read = 0
            val input = stream!!
            while (read < length) {
                val n = input.read(bytes, read, length - read)
                if (n == -1) {
                    throw IOException(
                        "The file ended early — it may have changed since it was picked",
                    )
                }
                read += n
            }
            position = offset + length
            return bytes
        }

        private fun reopen(offset: Long) {
            close()
            stream = openPickedInput(handle)
            position = 0
            if (offset > 0) skipFully(offset)
        }

        private fun skipFully(bytes: Long) {
            var remaining = bytes
            val input = stream!!
            while (remaining > 0) {
                val skipped = input.skip(remaining)
                if (skipped > 0) {
                    remaining -= skipped
                    position += skipped
                } else {
                    if (input.read() == -1) throw IOException("The file ended early")
                    remaining--
                    position++
                }
            }
        }

        fun close() {
            runCatching { stream?.close() }
            stream = null
            position = 0
        }
    }

    // ── text messages (ephemeral — not outbox jobs) ────────────────────────

    override fun sendText(target: DiscoveredDevice, text: String, pin: String?) {
        val client = sendClient ?: return
        sendJob?.cancel()
        sendJob = scope.launch {
            val id = UUID.randomUUID().toString()
            val bytes = text.toByteArray(Charsets.UTF_8)
            var progress = FileProgress(id, "Text message", bytes.size.toLong())
            fun publish(active: Boolean = true, error: String? = null, needsPin: Boolean = false) {
                _sendState.value = SendState(target, listOf(progress), active, error, needsPin)
            }
            publish()
            try {
                val dto = FileDto(
                    id = id,
                    fileName = "Text.txt",
                    size = bytes.size.toLong(),
                    fileType = "text/plain",
                    preview = text,
                )
                val prepared = client.prepareUpload(target, _localDevice.value, mapOf(id to dto), pin)
                val sessionId = prepared.sessionId
                if (sessionId == null) {
                    // 204 — the message itself rode inside prepare-upload.
                    progress = progress.copy(done = bytes.size.toLong(), status = TransferStatus.DONE)
                    addHistory(target.info.alias, text.take(120), bytes.size.toLong(), incoming = false, success = true)
                    publish(active = false)
                    return@launch
                }
                val token = prepared.files[id]
                    ?: throw SendException(HttpStatusCode.Forbidden, "The device did not accept the message")
                currentSendSession = target to sessionId
                progress = progress.copy(status = TransferStatus.ACTIVE)
                publish()
                client.upload(
                    target, sessionId, id, token, bytes.size.toLong(),
                    openStream = { bytes.inputStream() },
                ) { sent ->
                    progress = progress.copy(done = sent)
                    publish()
                }
                progress = progress.copy(done = bytes.size.toLong(), status = TransferStatus.DONE)
                currentSendSession = null
                addHistory(target.info.alias, text.take(120), bytes.size.toLong(), incoming = false, success = true)
                publish(active = false)
            } catch (e: CancellationException) {
                progress = progress.copy(status = TransferStatus.CANCELLED)
                publish(active = false, error = "Cancelled")
                throw e
            } catch (e: SendException) {
                progress = progress.copy(status = TransferStatus.FAILED)
                publish(active = false, error = describeError(e), needsPin = e.status == HttpStatusCode.Unauthorized)
            } catch (e: Exception) {
                progress = progress.copy(status = TransferStatus.FAILED)
                publish(active = false, error = friendlyError(e))
            }
        }
    }

    override fun sendTextToTargets(targets: List<DiscoveredDevice>, text: String) {
        val client = sendClient ?: return
        if (targets.isEmpty() || text.isBlank()) return
        sendJob?.cancel()
        sendJob = scope.launch {
            val bytes = text.toByteArray(Charsets.UTF_8)
            for (target in targets) {
                val id = UUID.randomUUID().toString()
                var progress = FileProgress(id, "Text message", bytes.size.toLong())
                fun publish(active: Boolean = true, error: String? = null, needsPin: Boolean = false) {
                    _sendState.value = SendState(target, listOf(progress), active, error, needsPin)
                }
                publish()
                try {
                    val dto = FileDto(
                        id = id,
                        fileName = "Text.txt",
                        size = bytes.size.toLong(),
                        fileType = "text/plain",
                        preview = text,
                    )
                    val prepared = client.prepareUpload(target, _localDevice.value, mapOf(id to dto), null)
                    val sessionId = prepared.sessionId
                    if (sessionId == null) {
                        progress = progress.copy(done = bytes.size.toLong(), status = TransferStatus.DONE)
                        addHistory(target.info.alias, text.take(120), bytes.size.toLong(), incoming = false, success = true)
                        publish(active = false)
                        continue
                    }
                    val token = prepared.files[id]
                        ?: throw SendException(HttpStatusCode.Forbidden, "The device did not accept the message")
                    currentSendSession = target to sessionId
                    progress = progress.copy(status = TransferStatus.ACTIVE)
                    publish()
                    client.upload(
                        target, sessionId, id, token, bytes.size.toLong(),
                        openStream = { bytes.inputStream() },
                    ) { sent ->
                        progress = progress.copy(done = sent)
                        publish()
                    }
                    progress = progress.copy(done = bytes.size.toLong(), status = TransferStatus.DONE)
                    currentSendSession = null
                    addHistory(target.info.alias, text.take(120), bytes.size.toLong(), incoming = false, success = true)
                    publish(active = false)
                } catch (e: CancellationException) {
                    progress = progress.copy(status = TransferStatus.CANCELLED)
                    publish(active = false, error = "Cancelled")
                    throw e
                } catch (e: SendException) {
                    progress = progress.copy(status = TransferStatus.FAILED)
                    publish(active = false, error = describeError(e), needsPin = e.status == HttpStatusCode.Unauthorized)
                } catch (e: Exception) {
                    progress = progress.copy(status = TransferStatus.FAILED)
                    publish(active = false, error = friendlyError(e))
                }
            }
        }
    }

    override fun probeDevice(ip: String) {
        val client = sendClient ?: return
        scope.launch {
            // Accept "host" or "host:port"; default to our own listen port.
            val host = ip.substringBefore(':').trim()
            val port = ip.substringAfter(':', "").trim().toIntOrNull()
                ?: _settings.value.port
            // Unknown peer: /info first (no fingerprint to pin yet), then
            // /register with the learned identity.
            val probe = DiscoveredDevice(
                RegisterDto(alias = "", fingerprint = "", port = port, protocol = _localDevice.value.protocol),
                host, System.currentTimeMillis(),
            )
            val info = client.fetchInfo(probe) ?: return@launch
            val known = DiscoveredDevice(
                RegisterDto(
                    alias = info.alias,
                    version = info.version,
                    deviceModel = info.deviceModel,
                    deviceType = info.deviceType,
                    fingerprint = info.fingerprint,
                    port = port,
                    protocol = _localDevice.value.protocol,
                    download = info.download,
                ),
                host, System.currentTimeMillis(),
            )
            addDevice(known.info, host)
            client.register(known, _localDevice.value)
        }
    }

    private fun describeError(e: SendException): String = when (e.status) {
        HttpStatusCode.Unauthorized -> "The device asked for a PIN"
        HttpStatusCode.Forbidden -> "The device declined the transfer"
        HttpStatusCode.Conflict -> "The device is busy with another transfer"
        HttpStatusCode.UnprocessableEntity -> "Hash mismatch reported by the device"
        HttpStatusCode.TooManyRequests -> "Too many wrong PIN attempts — wait a minute and retry"
        else -> "Failed: ${e.status}"
    }

    /** Translate raw TLS/stack errors into something a human can act on. */
    private fun friendlyError(e: Exception): String {
        val msg = e.message ?: return "Send failed"
        return when {
            msg.contains("CERTIFICATE_REQUIRED", ignoreCase = true) ||
                msg.contains("certificate_required", ignoreCase = true) ->
                "The other device didn't accept this device's certificate. " +
                    "Make sure the other device runs a current LocalSend and try again."
            msg.contains("SSL", ignoreCase = true) || msg.contains("TLS", ignoreCase = true) ->
                "The secure connection failed. Check that both devices use the same HTTPS setting."
            msg.contains("fingerprint mismatch", ignoreCase = true) ->
                "The other device's identity changed — remove it from favorites and let it be discovered again."
            else -> msg
        }
    }

    override fun cancelSend() {
        runnerJob?.cancel()
        sendJob?.cancel()
        val client = sendClient
        // Cancelling an outbox job also cancels its receive session: a
        // deliberate stop, unlike a disconnection (which leaves the
        // session resumable). The job stays in the outbox for a retry.
        val stateJobId = _sendState.value?.jobId
        val job = stateJobId?.let { jobById(it) } ?: _outbox.value.firstOrNull {
            it.id == runningJobId
        }
        val session = currentSendSession
        if (client != null && job?.sessionId != null) {
            val target = jobTarget(job)
            val sid = job.sessionId
            scope.launch { client.cancel(target, sid) }
            mutateJob(job.id) { it.copy(sessionId = null, tokens = emptyMap()) }
        } else if (session != null && client != null) {
            scope.launch { client.cancel(session.first, session.second) }
        }
        currentSendSession = null
    }

    override fun clearSendState() {
        _sendState.value = null
    }

    private fun sha256Of(picked: PickedFile): String? = runCatching {
        val digest = MessageDigest.getInstance("SHA-256")
        openPickedInput(picked.handle).use { input ->
            val buffer = ByteArray(64 * 1024)
            while (true) {
                val n = input.read(buffer)
                if (n == -1) break
                if (n > 0) digest.update(buffer, 0, n)
            }
        }
        sha256Hex(digest.digest())
    }.getOrNull()

    // ── settings / favorites / history ─────────────────────────────────────

    override fun updateSettings(settings: AppSettings) {
        val old = _settings.value
        _settings.value = settings
        store.saveSettings(settings)
        if (old.alias != settings.alias || old.port != settings.port ||
            old.https != settings.https || old.downloadApi != settings.downloadApi ||
            old.serverEnabled != settings.serverEnabled ||
            old.multicastAddress != settings.multicastAddress
        ) {
            restartNetworking()
        }
    }

    override fun toggleFavorite(fingerprint: String) {
        val s = _settings.value
        val favorites = if (fingerprint in s.favorites) {
            s.favorites - fingerprint
        } else {
            s.favorites + fingerprint
        }
        updateSettings(s.copy(favorites = favorites))
    }

    private fun addHistory(
        peerAlias: String,
        fileName: String,
        size: Long,
        incoming: Boolean,
        success: Boolean,
        uri: String = "",
        handle: String = "",
    ) {
        if (!_settings.value.saveToHistory) return
        val entry = HistoryEntry(
            timeMillis = System.currentTimeMillis(),
            peerAlias = peerAlias,
            fileName = fileName,
            size = size,
            incoming = incoming,
            success = success,
            uri = uri,
            handle = handle,
        )
        _history.value = (listOf(entry) + _history.value).take(200)
        store.saveHistory(_history.value)
    }

    override fun removeHistoryEntry(entry: HistoryEntry) {
        _history.value = _history.value.filter { it != entry }
        store.saveHistory(_history.value)
    }

    override fun clearHistory() {
        _history.value = emptyList()
        store.saveHistory(_history.value)
    }

    private companion object {
        const val ANNOUNCE_INTERVAL_MS = 10_000L
        // Official peers announce rarely (scan-time bursts), so entries
        // must live long: 20 s pruning made devices "randomly disappear"
        // (Ernest's report). Register-backs refresh lastSeen within this
        // window in normal operation.
        const val DEVICE_TTL_MS = 600_000L
        /** Outbox jobs kept on disk (finished ones are dropped first). */
        const val MAX_OUTBOX_JOBS = 25
        /** Automatic attempts per file within one run before parking it as FAILED. */
        const val MAX_AUTO_ATTEMPTS = 5
        /** Fresh prepare-upload rounds per run when the receive session keeps dying. */
        const val MAX_SESSION_RESTARTS = 2
    }
}
