package dev.kurokawa.localsend.expressive

import dev.kurokawa.localsend.expressive.app.OutboxFile
import dev.kurokawa.localsend.expressive.app.OutboxJob
import dev.kurokawa.localsend.expressive.app.TransferStatus
import dev.kurokawa.localsend.expressive.platform.parseWifiQr
import dev.kurokawa.localsend.expressive.platform.wifiQrPayload
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** The outbox must survive a JSON round trip byte-for-byte in meaning:
 *  it is rewritten to disk on every progress change and reloaded cold. */
class OutboxTest {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @Test
    fun outboxRoundTrip() {
        val job = OutboxJob(
            id = "job-1",
            createdAtMillis = 123456789L,
            targetAlias = "Ernest@MSI",
            targetFingerprint = "ABCDEF",
            targetIp = "192.168.1.50",
            targetPort = 53317,
            targetProtocol = "https",
            targetModel = "MSI GF63",
            targetType = "desktop",
            sessionId = "session-1",
            tokens = mapOf("file-1" to "token-1"),
            chunked = true,
            files = listOf(
                OutboxFile(
                    fileId = "file-1",
                    handle = "/home/ernest/video.mkv",
                    name = "video.mkv",
                    displayPath = "Trips/video.mkv",
                    size = 5_000_000L,
                    sha256 = "deadbeef",
                    status = TransferStatus.FAILED,
                    confirmedBytes = 2_097_152L,
                    attempts = 3,
                    lastError = "Connection reset",
                ),
                OutboxFile(
                    fileId = "file-2",
                    handle = "content://media/42",
                    name = "photo.jpg",
                    size = 1200L,
                    status = TransferStatus.DONE,
                    confirmedBytes = 1200L,
                ),
            ),
        )
        val encoded = json.encodeToString(
            ListSerializer(OutboxJob.serializer()),
            listOf(job),
        )
        val decoded = json.decodeFromString(
            ListSerializer(OutboxJob.serializer()),
            encoded,
        )
        assertEquals(listOf(job), decoded)
        assertTrue(decoded.single().hasFailures)
        assertEquals(1, decoded.single().pendingCount)
    }

    @Test
    fun wifiQrRoundTrip() {
        val payload = wifiQrPayload("My;Net", "p@ss:word")
        val parsed = parseWifiQr(payload)
        assertEquals("My;Net", parsed?.first)
        assertEquals("p@ss:word", parsed?.second)
        assertEquals("WPA", parsed?.third)
    }

    @Test
    fun wifiQrRejectsOtherPayloads() {
        assertNull(parseWifiQr("https://example.com"))
        assertNull(parseWifiQr("WIFI:T:WPA;P:nossid;;"))
    }
}
