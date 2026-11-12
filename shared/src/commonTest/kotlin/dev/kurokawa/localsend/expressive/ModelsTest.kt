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
    fun announcementContainsEveryFieldTheOfficialAppRequires() {
        // Regression test for the v0.2.x discovery failure: the official
        // receiver's parser requires ALL RegisterDto fields. Our JSON used
        // to omit fields equal to their defaults (no version/port/protocol),
        // and official devices silently discarded our announcements.
        val dto = RegisterDto(
            alias = "Calm Harbor",
            deviceModel = "Xiaomi 2201117TG",
            deviceType = DeviceType.mobile,
            fingerprint = "ABCDEF",
            announce = true,
        )
        val element = kotlinx.serialization.json.Json.parseToJsonElement(
            LocalSendJson.encodeToString(dto),
        ) as kotlinx.serialization.json.JsonObject
        for (key in listOf(
            "alias", "version", "deviceModel", "deviceType",
            "fingerprint", "port", "protocol", "download", "announce",
        )) {
            kotlin.test.assertTrue(key in element, "announcement is missing '$key': $element")
        }
        assertEquals("2.2", element.getValue("version").let {
            (it as kotlinx.serialization.json.JsonPrimitive).content
        })
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
