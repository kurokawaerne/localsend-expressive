package dev.kurokawa.localsend.expressive.app

import dev.kurokawa.localsend.expressive.LocalDevice
import dev.kurokawa.localsend.expressive.discovery.DiscoveredDevice
import dev.kurokawa.localsend.expressive.discovery.MulticastDiscovery
import dev.kurokawa.localsend.expressive.platform.MulticastLock
import dev.kurokawa.localsend.expressive.platform.PickedFile
import dev.kurokawa.localsend.expressive.platform.openPickedInput
import dev.kurokawa.localsend.expressive.platform.platformDeviceModel
import dev.kurokawa.localsend.expressive.platform.platformDeviceType
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
import java.security.MessageDigest
import java.util.UUID

/**
 * The transfer engine: owns discovery, the receive server and the send
 * client, and exposes everything the UI needs as state flows.
 * One instance per app process, started/stopped with the UI lifecycle.
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

    private var running = false
    private var server: ReceiveServer? = null
    private var discovery: MulticastDiscovery? = null
    private var sendClient: SendClient? = null
    private var listenJob: Job? = null
    private var announceJob: Job? = null
    private var sendJob: Job? = null
    private var currentSendSession: Pair<DiscoveredDevice, String>? = null

    @Volatile
    private var offerDecision: CompletableDeferred<Set<String>?>? = null

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
        startNetworking()
    }

    override fun stop() {
        if (!running) return
        running = false
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

        override fun onFileReceived(sessionId: String, peer: RegisterDto, file: FileDto, savedToDisplay: String) {
            _lastSavedTo.value = savedToDisplay
            addHistory(peer.alias, file.fileName, file.size, incoming = true, success = true)
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
        if (s.shareViaLinkAutoAccept && offer.peer.deviceType.name == "web") return allIds
        val deferred = CompletableDeferred<Set<String>?>()
        offerDecision = deferred
        _incomingOffer.value = offer
        return try {
            deferred.await()
        } finally {
            if (offerDecision === deferred) {
                offerDecision = null
                _incomingOffer.value = null
            }
        }
    }

    override fun respondOffer(acceptedFileIds: Set<String>?) {
        offerDecision?.complete(acceptedFileIds)
    }

    // ── sending ────────────────────────────────────────────────────────────

    override fun sendFiles(target: DiscoveredDevice, files: List<PickedFile>, pin: String?) {
        val client = sendClient ?: return
        sendJob?.cancel()
        sendJob = scope.launch {
            val ids = files.map { UUID.randomUUID().toString() }
            var progresses = files.mapIndexed { i, f ->
                FileProgress(ids[i], f.name, f.size)
            }
            fun publish(active: Boolean = true, error: String? = null, needsPin: Boolean = false) {
                _sendState.value = SendState(target, progresses, active, error, needsPin)
            }
            publish()
            try {
                val dtos = LinkedHashMap<String, FileDto>()
                val withChecksums = _settings.value.createChecksums
                files.forEachIndexed { i, picked ->
                    dtos[ids[i]] = FileDto(
                        id = ids[i],
                        // Folder picks carry their relative path here; the
                        // receiver recreates the folder structure.
                        fileName = picked.displayPath ?: picked.name,
                        size = picked.size,
                        fileType = guessFileType(picked.name),
                        sha256 = if (withChecksums) sha256Of(picked) else null,
                    )
                }
                val prepared = client.prepareUpload(target, _localDevice.value, dtos, pin)
                val sessionId = prepared.sessionId
                if (sessionId == null) {
                    // 204: message-only acceptance — nothing to upload.
                    progresses = progresses.map {
                        it.copy(done = it.total, status = TransferStatus.DONE)
                    }
                    publish(active = false)
                    return@launch
                }
                currentSendSession = target to sessionId
                files.forEachIndexed { i, picked ->
                    val fileId = ids[i]
                    val token = prepared.files[fileId]
                    if (token == null) {
                        // Partial acceptance is normal protocol behavior:
                        // the peer returns tokens only for accepted files.
                        progresses = progresses.map {
                            if (it.fileId == fileId) it.copy(status = TransferStatus.FAILED) else it
                        }
                        publish()
                        return@forEachIndexed
                    }
                    progresses = progresses.map {
                        if (it.fileId == fileId) it.copy(status = TransferStatus.ACTIVE) else it
                    }
                    publish()
                    client.upload(
                        target, sessionId, fileId, token, picked.size,
                        openStream = { openPickedInput(picked.handle) },
                    ) { sent ->
                        progresses = progresses.map {
                            if (it.fileId == fileId) it.copy(done = sent) else it
                        }
                        publish()
                    }
                    progresses = progresses.map {
                        if (it.fileId == fileId) {
                            it.copy(done = picked.size, status = TransferStatus.DONE)
                        } else {
                            it
                        }
                    }
                    publish()
                    addHistory(target.info.alias, picked.name, picked.size, incoming = false, success = true)
                }
                currentSendSession = null
                publish(active = false)
            } catch (e: CancellationException) {
                progresses = progresses.map {
                    if (it.status == TransferStatus.ACTIVE || it.status == TransferStatus.WAITING) {
                        it.copy(status = TransferStatus.CANCELLED)
                    } else {
                        it
                    }
                }
                publish(active = false, error = "Cancelled")
                throw e
            } catch (e: SendException) {
                progresses = progresses.map {
                    if (it.status == TransferStatus.ACTIVE || it.status == TransferStatus.WAITING) {
                        it.copy(status = TransferStatus.FAILED)
                    } else {
                        it
                    }
                }
                publish(
                    active = false,
                    error = describeError(e),
                    needsPin = e.status == HttpStatusCode.Unauthorized,
                )
            } catch (e: Exception) {
                progresses = progresses.map {
                    if (it.status == TransferStatus.ACTIVE) it.copy(status = TransferStatus.FAILED) else it
                }
                publish(active = false, error = friendlyError(e))
            }
        }
    }

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
        sendJob?.cancel()
        val session = currentSendSession
        val client = sendClient
        if (session != null && client != null) {
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

    private fun addHistory(peerAlias: String, fileName: String, size: Long, incoming: Boolean, success: Boolean) {
        if (!_settings.value.saveToHistory) return
        val entry = HistoryEntry(System.currentTimeMillis(), peerAlias, fileName, size, incoming, success)
        _history.value = (listOf(entry) + _history.value).take(200)
        store.saveHistory(_history.value)
    }

    private companion object {
        const val ANNOUNCE_INTERVAL_MS = 10_000L
        // Official peers announce rarely (scan-time bursts), so entries
        // must live long: 20 s pruning made devices "randomly disappear"
        // (Ernest's report). Register-backs refresh lastSeen within this
        // window in normal operation.
        const val DEVICE_TTL_MS = 600_000L
    }
}
