package dev.kurokawa.localsend.expressive.transfer

import dev.kurokawa.localsend.expressive.platform.platformDataDir
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.io.File
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.Date
import javax.net.ssl.X509TrustManager

/** SHA-256 of [bytes] as uppercase hex — the LocalSend fingerprint format. */
fun sha256Hex(bytes: ByteArray): String =
    MessageDigest.getInstance("SHA-256").digest(bytes)
        .joinToString("") { "%02X".format(it) }

fun certificateFingerprint(cert: X509Certificate): String = sha256Hex(cert.encoded)

/**
 * Client-side TLS verification the LocalSend way: the peer's certificate is
 * trusted when its SHA-256 matches the fingerprint the peer announced during
 * discovery. With no expected fingerprint set, any certificate is accepted
 * (and its fingerprint can be read back from [lastSeenFingerprint]).
 */
class FingerprintTrustManager : X509TrustManager {
    @Volatile
    var expected: Set<String> = emptySet()

    @Volatile
    var lastSeenFingerprint: String? = null
        private set

    override fun checkServerTrusted(chain: Array<out X509Certificate>, authType: String) {
        val cert = chain.firstOrNull() ?: throw CertificateException("Empty certificate chain")
        val fingerprint = certificateFingerprint(cert)
        lastSeenFingerprint = fingerprint
        if (expected.isNotEmpty() && fingerprint !in expected) {
            throw CertificateException("Certificate fingerprint mismatch: $fingerprint")
        }
    }

    override fun checkClientTrusted(chain: Array<out X509Certificate>, authType: String) = Unit

    override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
}

/**
 * Owns this device's self-signed TLS identity (M1).
 *
 * Generates an RSA key + self-signed certificate once (BouncyCastle), persists
 * the PKCS12 key store in the app data directory, and exposes the certificate
 * SHA-256 as the device fingerprint, exactly like the official app.
 */
class CertManager {
    companion object {
        const val ALIAS = "localsend-expressive"
        const val PASSWORD = "localsend-expressive"
        private const val STORE_FILE = "tls.p12"
    }

    private val provider = BouncyCastleProvider()

    lateinit var keyStore: KeyStore
        private set

    lateinit var fingerprint: String
        private set

    val isInitialized: Boolean get() = ::keyStore.isInitialized

    /** Load the persisted identity, or generate and store a fresh one. */
    fun ensure() {
        if (isInitialized) return
        val file = File(platformDataDir(), STORE_FILE)
        val store = KeyStore.getInstance("PKCS12")
        if (file.isFile) {
            file.inputStream().use { store.load(it, PASSWORD.toCharArray()) }
        } else {
            store.load(null, PASSWORD.toCharArray())
            val keyPair = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.genKeyPair()
            val now = Date()
            val until = Date(now.time + 3650L * 24 * 3600 * 1000)
            val name = X500Name("CN=LocalSend Expressive")
            val serial = BigInteger(64, SecureRandom())
            val builder = JcaX509v3CertificateBuilder(name, serial, now, until, name, keyPair.public)
            val signer = JcaContentSignerBuilder("SHA256withRSA").setProvider(provider).build(keyPair.private)
            val cert = JcaX509CertificateConverter().setProvider(provider)
                .getCertificate(builder.build(signer))
            store.setKeyEntry(ALIAS, keyPair.private, PASSWORD.toCharArray(), arrayOf(cert))
            file.outputStream().use { store.store(it, PASSWORD.toCharArray()) }
        }
        keyStore = store
        val cert = store.getCertificate(ALIAS) as X509Certificate
        fingerprint = certificateFingerprint(cert)
    }
}
