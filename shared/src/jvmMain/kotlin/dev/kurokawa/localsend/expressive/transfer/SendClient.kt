package dev.kurokawa.localsend.expressive.transfer

import dev.kurokawa.localsend.expressive.LocalDevice
import dev.kurokawa.localsend.expressive.protocol.InfoDto
import dev.kurokawa.localsend.expressive.protocol.LocalSendJson
import dev.kurokawa.localsend.expressive.protocol.ProtocolConstants
import dev.kurokawa.localsend.expressive.protocol.RegisterDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json

/**
 * Client side of the protocol: talking to peers' receive servers.
 *
 * TODO(M1): HTTPS with the peer's certificate pinned by its announced
 * fingerprint. Plain HTTP for now, matching the protocol's HTTP mode.
 */
class SendClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) { json(LocalSendJson) }
    }

    /** POST /register — returns the peer's own device info (two-way discovery). */
    suspend fun register(ip: String, port: Int, local: LocalDevice): RegisterDto? =
        runCatching {
            client.post("http://$ip:$port${ProtocolConstants.PATH_REGISTER}") {
                contentType(ContentType.Application.Json)
                setBody(local.toRegisterDto())
            }.body<RegisterDto>()
        }.getOrNull()

    /** GET /info — debugging / legacy discovery aid. */
    suspend fun fetchInfo(ip: String, port: Int): InfoDto? =
        runCatching {
            client.get("http://$ip:$port${ProtocolConstants.PATH_INFO}").body<InfoDto>()
        }.getOrNull()

    fun close() = client.close()
}
