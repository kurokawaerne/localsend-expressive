package dev.kurokawa.localsend.expressive

import dev.kurokawa.localsend.expressive.transfer.CertManager
import dev.kurokawa.localsend.expressive.transfer.certificateFingerprint
import java.security.cert.X509Certificate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CertManagerTest {
    @Test
    fun generatesStableSelfSignedIdentityWithSha256Fingerprint() {
        val manager = CertManager()
        manager.ensure()
        assertTrue(manager.isInitialized)

        // LocalSend fingerprints are the SHA-256 of the cert DER: 64 hex chars.
        assertEquals(64, manager.fingerprint.length)
        assertTrue(manager.fingerprint.all { it in '0'..'9' || it in 'A'..'F' })

        val cert = manager.keyStore.getCertificate(CertManager.ALIAS) as X509Certificate
        assertEquals(manager.fingerprint, certificateFingerprint(cert))

        // Second load (from the persisted store) yields the same fingerprint.
        val reloaded = CertManager()
        reloaded.ensure()
        assertEquals(manager.fingerprint, reloaded.fingerprint)
    }
}
