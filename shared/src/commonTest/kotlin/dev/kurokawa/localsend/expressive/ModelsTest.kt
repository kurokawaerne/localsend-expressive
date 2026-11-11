package dev.kurokawa.localsend.expressive

import dev.kurokawa.localsend.expressive.protocol.DeviceType
import dev.kurokawa.localsend.expressive.protocol.FileDto
import dev.kurokawa.localsend.expressive.protocol.LocalSendJson
import dev.kurokawa.localsend.expressive.protocol.PrepareUploadRequestDto
import dev.kurokawa.localsend.expressive.protocol.RegisterDto
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ModelsTest {

    @Test
    fun parsesOfficialAnnounceSample() {
        // Sample announcement straight from the Protocol v2.2 spec (§3.1).
        val json = """
            {
              "alias": "Nice Orange",
              "version": "2.0",
              "deviceModel": "Samsung",
              "deviceType": "mobile",
              "fingerprint": "random string",
              "port": 53317,
              "protocol": "https",
              "download": true,
              "announce": true
            }
        """.trimIndent()
        val dto = LocalSendJson.decodeFromString<RegisterDto>(json)
        assertEquals("Nice Orange", dto.alias)
        assertEquals("2.0", dto.version)
        assertEquals("Samsung", dto.deviceModel)
        assertEquals(DeviceType.mobile, dto.deviceType)
        assertEquals(53317, dto.port)
        assertEquals(true, dto.announce)
    }

    @Test
    fun prepareUploadRoundTrips() {
        val request = PrepareUploadRequestDto(
            info = RegisterDto(
                alias = "Secret Banana",
                deviceModel = "Windows",
                deviceType = DeviceType.desktop,
                fingerprint = "abc123",
            ),
            files = mapOf(
                "file1" to FileDto(
                    id = "file1",
                    fileName = "my image.png",
                    size = 324242,
                    fileType = "image/png",
                    sha256 = "deadbeef",
                ),
            ),
        )
        val encoded = LocalSendJson.encodeToString(request)
        val decoded = LocalSendJson.decodeFromString<PrepareUploadRequestDto>(encoded)
        assertEquals(request, decoded)
    }

    @Test
    fun unknownDeviceTypeFallsBackToDesktop() {
        val json = """{"alias":"X","fingerprint":"f","deviceType":"toaster"}"""
        val dto = LocalSendJson.decodeFromString<RegisterDto>(json)
        assertEquals(DeviceType.desktop, dto.deviceType)
    }

    @Test
    fun unknownFieldsAreIgnored() {
        val json = """{"alias":"X","fingerprint":"f","someFutureField":{"nested":1}}"""
        val dto = LocalSendJson.decodeFromString<RegisterDto>(json)
        assertEquals("X", dto.alias)
        assertNull(dto.deviceModel)
    }
}
