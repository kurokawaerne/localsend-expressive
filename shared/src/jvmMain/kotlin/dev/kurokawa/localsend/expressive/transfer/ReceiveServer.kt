package dev.kurokawa.localsend.expressive.transfer

import dev.kurokawa.localsend.expressive.LocalDevice
import dev.kurokawa.localsend.expressive.protocol.ProtocolConstants
import dev.kurokawa.localsend.expressive.protocol.RegisterDto
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

/**
 * The receiving HTTP(S) server.
 *
 * M0/M1: /info and /register work over plain HTTP.
 * TODO(M1): HTTPS with a generated self-signed certificate; the fingerprint
 * then becomes the SHA-256 of that certificate.
 * TODO(M2): /prepare-upload session handling, /upload, /cancel.
 */
class ReceiveServer(
    private val local: LocalDevice,
    private val onPeerRegistered: (dto: RegisterDto, ip: String) -> Unit = { _, _ -> },
) {
    private var server: EmbeddedServer<*, *>? = null

    fun start(port: Int = local.port) {
        server = embeddedServer(CIO, port = port) { module() }.start(wait = false)
    }

    fun stop() {
        server?.stop(500, 1000)
        server = null
    }

    private fun Application.module() {
        install(ContentNegotiation) {
            json(dev.kurokawa.localsend.expressive.protocol.LocalSendJson)
        }
        routing {
            get(ProtocolConstants.PATH_INFO) {
                call.respond(local.toInfoDto())
            }
            post(ProtocolConstants.PATH_REGISTER) {
                val peer = call.receive<RegisterDto>()
                val ip = call.request.local.remoteHost
                onPeerRegistered(peer, ip)
                // Two-way discovery: answer with our own device info.
                call.respond(local.toRegisterDto())
            }
            post(ProtocolConstants.PATH_PREPARE_UPLOAD) {
                // TODO(M2): session negotiation + accept dialog.
                call.respond(HttpStatusCode.NotImplemented)
            }
            post(ProtocolConstants.PATH_UPLOAD) {
                // TODO(M2): stream body to disk, verify sha256 (422 on mismatch).
                call.respond(HttpStatusCode.NotImplemented)
            }
            post(ProtocolConstants.PATH_CANCEL) {
                // TODO(M2): cancel the active session.
                call.respond(HttpStatusCode.NotImplemented)
            }
        }
    }
}
