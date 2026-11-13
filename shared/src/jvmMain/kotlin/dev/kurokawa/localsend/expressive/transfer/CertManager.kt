package dev.kurokawa.localsend.expressive.transfer

import dev.kurokawa.localsend.expressive.platform.platformDataDir
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.BasicConstraints
import org.bouncycastle.asn1.x509.ExtendedKeyUsage
import org.bouncycastle.asn1.x509.GeneralName
import org.bouncycastle.asn1.x509.GeneralNames
import org.bouncycastle.asn1.x509.Extension
import org.bouncycastle.asn1.x509.KeyPurposeId
import org.bouncycastle.asn1.x509.KeyUsage
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.io.File
import java.math.BigInteger
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.Socket
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Date
import javax.net.ssl.SSLEngine
import javax.net.ssl.X509ExtendedKeyManager
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

        /** True when [cert]'s extensions make TLS stacks refuse it for client auth. */
        fun blocksClientAuth(cert: X509Certificate): Boolean {
            val eku = runCatching { cert.extendedKeyUsage }.getOrNull()
            if (eku != null &&
                KeyPurposeId.id_kp_clientAuth.id !in eku &&
                KeyPurposeId.anyExtendedKeyUsage.id !in eku
            ) return true
            val ku = cert.keyUsage
            if (ku != null && !ku[0]) return true // digitalSignature not set
            return false
        }

        /**
         * True when a web browser would abort the TLS handshake on this
         * certificate: no SubjectAltName at all (Chrome has ignored the
         * CN for host matching since 2017 and sends
         * SSLV3_ALERT_CERTIFICATE_UNKNOWN — the v0.8.0 blank via-link
         * page), or the leaf claims to be a CA.
         */
        fun browserUnusable(cert: X509Certificate): Boolean {
            val sans = runCatching { cert.subjectAlternativeNames }.getOrNull()
            if (sans.isNullOrEmpty()) return true
            if (cert.basicConstraints != -1) return true // leaf must not be a CA
            return false
        }

        /**
         * SAN entries for generated certificates: the names/IPs a browser
         * on the local network can plausibly use to reach the via-link
         * page — loopback, the well-known hotspot / Wi-Fi Direct gateway
         * addresses (192.168.49.1 is Android's P2P group-owner address,
         * the exact URL that failed in v0.8.0), and every address this
         * machine currently owns.
         */
        fun subjectAltNames(): GeneralNames {
            val names = mutableListOf<GeneralName>()
            names += GeneralName(GeneralName.dNSName, "localhost")
            names += GeneralName(GeneralName.dNSName, "localsend")
            names += GeneralName(GeneralName.iPAddress, "127.0.0.1")
            names += GeneralName(GeneralName.iPAddress, "::1")
            names += GeneralName(GeneralName.iPAddress, "192.168.49.1")
            names += GeneralName(GeneralName.iPAddress, "192.168.43.1")
            names += GeneralName(GeneralName.iPAddress, "192.168.137.1")
            runCatching {
                NetworkInterface.getNetworkInterfaces().toList().forEach { iface ->
                    iface.inetAddresses.toList().forEach { addr: InetAddress ->
                        addr.hostAddress?.let { names += GeneralName(GeneralName.iPAddress, it) }
                    }
                }
            }
            return GeneralNames(names.toTypedArray())
        }
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
        var regenerate = !file.isFile
        if (file.isFile) {
            file.inputStream().use { store.load(it, PASSWORD.toCharArray()) }
            // A stored certificate whose extensions forbid client
            // authentication can never be presented to official peers
            // (their servers require mutual TLS) — replace it once.
            val existing = store.getCertificate(ALIAS) as? X509Certificate
            if (existing == null || blocksClientAuth(existing) || browserUnusable(existing)) {
                regenerate = true
            }
        }
        if (regenerate) {
            store.load(null, PASSWORD.toCharArray())
            val keyPair = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.genKeyPair()
            val now = Date()
            val until = Date(now.time + 3650L * 24 * 3600 * 1000)
            val name = X500Name("CN=LocalSend Expressive")
            val serial = BigInteger(64, SecureRandom())
            val builder = JcaX509v3CertificateBuilder(name, serial, now, until, name, keyPair.public)
            // Usable for BOTH roles: official peers demand a client
            // certificate (clientAuth + digitalSignature), and our own
            // server presents the same certificate (serverAuth).
            builder.addExtension(
                Extension.keyUsage, true,
                KeyUsage(KeyUsage.digitalSignature or KeyUsage.keyEncipherment),
            )
            builder.addExtension(
                Extension.extendedKeyUsage, false,
                ExtendedKeyUsage(arrayOf(KeyPurposeId.id_kp_serverAuth, KeyPurposeId.id_kp_clientAuth)),
            )
            builder.addExtension(Extension.basicConstraints, true, BasicConstraints(false))
            builder.addExtension(Extension.subjectAlternativeName, false, subjectAltNames())
            builder.addExtension(
                Extension.subjectKeyIdentifier, false,
                JcaX509ExtensionUtils().createSubjectKeyIdentifier(keyPair.public),
            )
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

    /**
     * Our TLS identity for client connections: the private key re-imported
     * through the platform [KeyFactory] (so the TLS provider receives a key
     * object it natively owns — keys surfaced by foreign PKCS12 providers
     * can be unusable to Conscrypt's engine) plus the certificate chain.
     */
    fun clientIdentity(): Pair<PrivateKey, Array<X509Certificate>> {
        check(isInitialized) { "CertManager.ensure() must run first" }
        val stored = keyStore.getKey(ALIAS, PASSWORD.toCharArray()) as PrivateKey
        val key = runCatching {
            val encoded = stored.encoded ?: return@runCatching stored
            KeyFactory.getInstance(stored.algorithm).generatePrivate(PKCS8EncodedKeySpec(encoded))
        }.getOrDefault(stored)
        val chain = keyStore.getCertificateChain(ALIAS)
            .filterIsInstance<X509Certificate>()
            .toTypedArray()
        return key to chain
    }
}

/**
 * An [X509ExtendedKeyManager] that always offers our single identity.
 *
 * The platform KeyManagerFactory's alias *selection* is provider-specific
 * filtering (key types, issuers, EKU/KU rules) — with exactly one identity
 * there is nothing to select, and filtering can only hurt: when it misfires
 * the TLS stack sends an EMPTY Certificate message and official peers abort
 * with `TLSV1_ALERT_CERTIFICATE_REQUIRED` (the v0.3.0 send failure). This
 * manager removes that whole class of failure.
 */
class AlwaysOfferKeyManager(
    private val key: PrivateKey,
    private val chain: Array<X509Certificate>,
) : X509ExtendedKeyManager() {
    override fun getClientAliases(keyType: String?, issuers: Array<out java.security.Principal>?) =
        arrayOf(CertManager.ALIAS)

    override fun chooseClientAlias(
        keyType: Array<out String>?,
        issuers: Array<out java.security.Principal>?,
        socket: Socket?,
    ): String = CertManager.ALIAS

    override fun chooseEngineClientAlias(
        keyType: Array<out String>?,
        issuers: Array<out java.security.Principal>?,
        engine: SSLEngine?,
    ): String = CertManager.ALIAS

    override fun getServerAliases(keyType: String?, issuers: Array<out java.security.Principal>?) = null

    override fun chooseServerAlias(
        keyType: String?,
        issuers: Array<out java.security.Principal>?,
        socket: Socket?,
    ): String? = null

    override fun getCertificateChain(alias: String?): Array<X509Certificate> = chain

    override fun getPrivateKey(alias: String?): PrivateKey = key
}
