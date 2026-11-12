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
import java.net.DatagramSocket
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.MulticastSocket
import java.net.NetworkInterface

/**
 * Multicast UDP discovery (Protocol v2.2 §3.1).
 *
 * Peers announce to 224.0.0.167:53317. When an announcement arrives, the caller
 * decides how to answer (POST /register to the announcer, or a UDP response
 * with announce=false as fallback) — this class only moves packets.
 *
 * v0.5.0: announcements go out on EVERY usable LAN interface, and the group
 * is joined on every one too. The old code used the OS-default interface
 * only; on machines with VPNs (Tailscale), virtual bridges or a second NIC
 * the default is often not the Wi-Fi LAN, so announcements never reached
 * phones — Ernest's "the PC only appears after I refresh on the PC" bug
 * (the refresh forced a unicast /register, which always works). Each
 * announcement is also copied to every interface's subnet broadcast
 * address: Android Wi-Fi drivers deliver broadcast far more reliably
 * than multicast.
 */
class MulticastDiscovery(
    private val local: LocalDevice,
    private val onPeer: (dto: RegisterDto, ip: String) -> Unit,
    private val onAnnouncement: (dto: RegisterDto, ip: String) -> Unit = onPeer,
    groupAddress: String = ProtocolConstants.MULTICAST_ADDRESS,
) {
    private val group: InetAddress = InetAddress.getByName(groupAddress)

    /** Up, non-loopback, multicast-capable interfaces that carry an IPv4 address. */
    private fun lanInterfaces(): List<NetworkInterface> = runCatching {
        val all = NetworkInterface.getNetworkInterfaces()?.toList().orEmpty()
        all.filter { ni ->
            runCatching {
                ni.isUp && !ni.isLoopback && ni.supportsMulticast() &&
                    ni.interfaceAddresses.any { it.address is Inet4Address }
            }.getOrDefault(false)
        }
    }.getOrDefault(emptyList())

    private fun payload(announce: Boolean): ByteArray =
        LocalSendJson.encodeToString(local.toRegisterDto(announce = announce))
            .toByteArray(Charsets.UTF_8)

    /** Send one announcement for this device. Call periodically while discovering. */
    suspend fun announce() = withContext(Dispatchers.IO) {
        val bytes = payload(announce = true)
        val ifaces = lanInterfaces()
        if (ifaces.isEmpty()) {
            // No enumerable LAN interface (restricted sandbox): old behaviour.
            runCatching {
                MulticastSocket().use { socket ->
                    socket.timeToLive = 255
                    socket.send(DatagramPacket(bytes, bytes.size, group, local.port))
                }
            }
            return@withContext
        }
        for (ni in ifaces) {
            runCatching {
                MulticastSocket().use { socket ->
                    socket.networkInterface = ni
                    socket.timeToLive = 255
                    socket.send(DatagramPacket(bytes, bytes.size, group, local.port))
                }
            }
            for (ia in ni.interfaceAddresses) {
                val broadcast = ia.broadcast ?: continue
                runCatching {
                    DatagramSocket().use { socket ->
                        socket.broadcast = true
                        socket.send(DatagramPacket(bytes, bytes.size, broadcast, local.port))
                    }
                }
            }
        }
    }

    /** Reply to an announcement with a UDP response (fallback when HTTP register fails). */
    suspend fun respondUdp(targetPort: Int = local.port) = withContext(Dispatchers.IO) {
        val bytes = payload(announce = false)
        val ifaces = lanInterfaces()
        if (ifaces.isEmpty()) {
            runCatching {
                MulticastSocket().use { socket ->
                    socket.timeToLive = 255
                    socket.send(DatagramPacket(bytes, bytes.size, group, targetPort))
                }
            }
            return@withContext
        }
        for (ni in ifaces) {
            runCatching {
                MulticastSocket().use { socket ->
                    socket.networkInterface = ni
                    socket.timeToLive = 255
                    socket.send(DatagramPacket(bytes, bytes.size, group, targetPort))
                }
            }
        }
    }

    /** Listen for announcements and peer responses until the coroutine is cancelled. */
    suspend fun listen() = withContext(Dispatchers.IO) {
        // SO_REUSEADDR: the official app (or a second instance) may already
        // hold this port on the same device — without reuse the bind fails
        // and discovery silently dies.
        val socket = MulticastSocket(null as java.net.SocketAddress?)
        socket.reuseAddress = true
        socket.bind(InetSocketAddress(local.port))
        try {
            val ifaces = lanInterfaces()
            if (ifaces.isEmpty()) {
                socket.joinGroup(group)
            } else {
                // Join on every interface: joining only the default one
                // misses announcements that arrive on the other NICs.
                var joined = 0
                for (ni in ifaces) {
                    runCatching {
                        socket.joinGroup(InetSocketAddress(group, local.port), ni)
                        joined++
                    }
                }
                if (joined == 0) runCatching { socket.joinGroup(group) }
            }
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
