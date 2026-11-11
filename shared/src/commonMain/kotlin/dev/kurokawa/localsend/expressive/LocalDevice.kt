package dev.kurokawa.localsend.expressive

import dev.kurokawa.localsend.expressive.protocol.DeviceType
import dev.kurokawa.localsend.expressive.protocol.InfoDto
import dev.kurokawa.localsend.expressive.protocol.ProtocolConstants
import dev.kurokawa.localsend.expressive.protocol.RegisterDto
import dev.kurokawa.localsend.expressive.protocol.generateRandomFingerprint
import kotlin.random.Random

/** This device, as presented to peers. */
data class LocalDevice(
    val alias: String,
    val deviceModel: String? = null,
    val deviceType: DeviceType,
    val fingerprint: String,
    val port: Int = ProtocolConstants.DEFAULT_PORT,
    val protocol: String = "https",
    val download: Boolean = false,
) {
    fun toRegisterDto(announce: Boolean? = null): RegisterDto = RegisterDto(
        alias = alias,
        deviceModel = deviceModel,
        deviceType = deviceType,
        fingerprint = fingerprint,
        port = port,
        protocol = protocol,
        download = download,
        announce = announce,
    )

    fun toInfoDto(): InfoDto = InfoDto(
        alias = alias,
        deviceModel = deviceModel,
        deviceType = deviceType,
        fingerprint = fingerprint,
        download = download,
    )

    companion object {
        /**
         * Fallback identity with a random (HTTP-style) fingerprint.
         * The running app builds its LocalDevice in LocalSendService instead,
         * where the HTTPS fingerprint is the TLS certificate's SHA-256.
         */
        fun create(deviceType: DeviceType, deviceModel: String? = null): LocalDevice = LocalDevice(
            alias = randomAlias(),
            deviceModel = deviceModel,
            deviceType = deviceType,
            fingerprint = generateRandomFingerprint(),
        )
    }
}

private val ALIAS_ADJECTIVES = listOf(
    "Nice", "Secret", "Happy", "Brave", "Calm", "Swift", "Bright", "Gentle", "Proud", "Silent",
)
private val ALIAS_NOUNS = listOf(
    "Orange", "Banana", "Tiger", "Falcon", "Willow", "Comet", "Harbor", "Meadow", "Quartz", "Sparrow",
)

/** Friendly two-word alias in the official app's style, e.g. "Nice Orange". */
fun randomAlias(): String =
    ALIAS_ADJECTIVES[Random.nextInt(ALIAS_ADJECTIVES.size)] + " " +
        ALIAS_NOUNS[Random.nextInt(ALIAS_NOUNS.size)]
