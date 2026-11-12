package dev.kurokawa.localsend.expressive

import dev.kurokawa.localsend.expressive.transfer.TransferMath
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The Kotlin twin of the Rust core's chunk math + SHA-256, locked to the
 * same vectors as `cargo test` in core-rust/. CI runs this without the
 * native library present, so it verifies the fallback; the Rust crate's
 * own tests verify the native implementation.
 */
class TransferMathTest {
    private val mib = 1024L * 1024L

    @Test
    fun chunkCountBasics() {
        assertEquals(0, TransferMath.chunkCount(0))
        assertEquals(1, TransferMath.chunkCount(1))
        assertEquals(1, TransferMath.chunkCount(mib))
        assertEquals(2, TransferMath.chunkCount(mib + 1))
        assertEquals(3, TransferMath.chunkCount(3 * mib))
    }

    @Test
    fun chunkOffsetsAndLengths() {
        val size = 2 * mib + 500
        assertEquals(0, TransferMath.chunkOffset(0))
        assertEquals(mib, TransferMath.chunkOffset(1))
        assertEquals(2 * mib, TransferMath.chunkOffset(2))
        assertEquals(mib, TransferMath.chunkLength(0, size))
        assertEquals(mib, TransferMath.chunkLength(1, size))
        assertEquals(500, TransferMath.chunkLength(2, size))
        assertEquals(0, TransferMath.chunkLength(3, size))
    }

    @Test
    fun resumeOnlyCountsFullChunks() {
        assertEquals(0, TransferMath.resumeOffset(0))
        assertEquals(0, TransferMath.resumeOffset(mib - 1))
        assertEquals(mib, TransferMath.resumeOffset(mib))
        // A partially received chunk is resent whole.
        assertEquals(mib, TransferMath.resumeOffset(mib + 123))
        assertEquals(2 * mib, TransferMath.resumeOffset(2 * mib + mib / 2))
    }

    @Test
    fun sha256Vectors() {
        assertEquals(
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            TransferMath.sha256Hex(ByteArray(0)),
        )
        assertEquals(
            "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
            TransferMath.sha256Hex("abc".toByteArray(Charsets.UTF_8)),
        )
        assertEquals(
            "248d6a61d20638b8e5c026930c3e6039a33ce45964ff2167f6ecedd419db06c1",
            TransferMath.sha256Hex(
                "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq"
                    .toByteArray(Charsets.UTF_8),
            ),
        )
    }
}
