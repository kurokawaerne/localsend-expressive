package dev.kurokawa.localsend.expressive.discovery

import dev.kurokawa.localsend.expressive.LocalDevice
import dev.kurokawa.localsend.expressive.protocol.LocalSendJson
import dev.kurokawa.localsend.expressive.protocol.ProtocolConstants
import dev.kurokawa.localsend.expressive.protocol.RegisterDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket

/**
 * Multicast UDP discovery (Protocol v2.2 §3.1).
 *
 * Peers announce to 224.0.0.167:53317. When an announcement arrives, the caller
 * decides how to answer (POST /register to the announcer, or a UDP response
 * with announce=false as fallback) — this class only moves packets.
 */
class MulticastDiscovery(
    private val local: LocalDevice,
    private val onPeer: (dto: RegisterDto, ip: String) -> Unit,
    private val onAnnouncement: (dto: RegisterDto, ip: String) -> Unit = onPeer,
    groupAddress: String = ProtocolConstants.MULTICAST_ADDRESS,
) {
    private val group: InetAddress = InetAddress.getByName(groupAddress)

    /** Send one announcement for this device. Call periodically while discovering. */
    suspend fun announce() = withContext(Dispatchers.IO) {
        val payload = LocalSendJson.encodeToString(local.toRegisterDto(announce = true))
            .toByteArray(Charsets.UTF_8)
        MulticastSocket().use { socket ->
            socket.timeToLive = 255
            socket.send(DatagramPacket(payload, payload.size, group, local.port))
        }
    }

    /** Reply to an announcement with a UDP response (fallback when HTTP register fails). */
    suspend fun respondUdp(targetPort: Int = local.port) = withContext(Dispatchers.IO) {
        val payload = LocalSendJson.encodeToString(local.toRegisterDto(announce = false))
            .toByteArray(Charsets.UTF_8)
        MulticastSocket().use { socket ->
            socket.timeToLive = 255
            socket.send(DatagramPacket(payload, payload.size, group, targetPort))
        }
    }

    /** Listen for announcements and peer responses until the coroutine is cancelled. */
    suspend fun listen() = withContext(Dispatchers.IO) {
        // SO_REUSEADDR: the official app (or a second instance) may already
        // hold this port on the same device — without reuse the bind fails
        // and discovery silently dies.
        val socket = MulticastSocket(null as java.net.SocketAddress?)
        socket.reuseAddress = true
        socket.bind(java.net.InetSocketAddress(local.port))
        try {
            socket.joinGroup(group)
            val buffer = ByteArray(16 * 1024)
            while (isActive) {
                val packet = DatagramPacket(buffer, buffer.size)
                socket.receive(packet)
                val text = String(packet.data, packet.offset, packet.length, Charsets.UTF_8)
                val dto = runCatching { LocalSendJson.decodeFromString<RegisterDto>(text) }
                    .getOrNull() ?: continue
                if (dto.fingerprint == local.fingerprint) continue // never discover ourselves
                val ip = packet.address.hostAddress ?: continue
                if (dto.announce == true) onAnnouncement(dto, ip) else onPeer(dto, ip)
            }
        } finally {
            runCatching { socket.leaveGroup(group) }
            socket.close()
        }
    }
}
