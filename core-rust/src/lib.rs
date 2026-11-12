//! LSEx core — Rust native core for LocalSend Expressive.
//!
//! The transfer-resume maths and SHA-256 live here, in pure,
//! dependency-free Rust. A Kotlin twin (`ChunkMath` / hashing in
//! the shared module) mirrors every function in this file with
//! identical semantics, and is the fallback wherever the native
//! library cannot be loaded; both sides are unit-tested against
//! the same vectors and edge cases so they cannot drift apart.
//!
//! ## Resume semantics
//!
//! A file is sent in fixed-size chunks. The receiver only ever
//! *confirms* whole chunks: if a transfer dies mid-chunk, that
//! partial chunk counts for nothing and is resent from its start.
//! [`resume_offset`] therefore rounds the confirmed byte count
//! *down* to a chunk boundary, and [`next_chunk_index`] is where
//! a resumed send continues. This is what lets a send be retried
//! — per file or as a whole, even after the app was closed — by
//! re-asking the receiver how many bytes it confirmed and picking
//! up at the returned offset, with no risk of a spliced/corrupt
//! file from a half-written chunk.
//!
//! A `chunk_size` of 0 is treated as 1 everywhere, so no caller
//! can divide by zero through these functions.

mod sha256;

pub use crate::sha256::{sha256, sha256_hex};

#[cfg(feature = "jni")]
mod jni_bindings;

/// Effective chunk size: 0 is treated as 1 to avoid div-by-zero.
fn effective_chunk_size(chunk_size: u64) -> u64 {
    if chunk_size == 0 {
        1
    } else {
        chunk_size
    }
}

/// Number of chunks needed to send `file_size` bytes at
/// `chunk_size` bytes per chunk: `ceil(file_size / chunk_size)`.
/// 0 when `file_size` is 0.
pub fn chunk_count(file_size: u64, chunk_size: u64) -> u64 {
    if file_size == 0 {
        return 0;
    }
    let cs = effective_chunk_size(chunk_size);
    file_size / cs + u64::from(file_size % cs != 0)
}

/// Byte offset at which chunk `index` starts
/// (`index * chunk_size`, saturating on overflow).
pub fn chunk_offset(index: u64, chunk_size: u64) -> u64 {
    index.saturating_mul(effective_chunk_size(chunk_size))
}

/// Length in bytes of chunk `index` of a `file_size`-byte file.
/// The last chunk may be short; 0 if the chunk's offset is at or
/// beyond `file_size` (index out of range).
pub fn chunk_length(index: u64, file_size: u64, chunk_size: u64) -> u64 {
    let cs = effective_chunk_size(chunk_size);
    let offset = chunk_offset(index, cs);
    if offset >= file_size {
        return 0;
    }
    (file_size - offset).min(cs)
}

/// Offset a resumed transfer should continue from, given
/// `confirmed_bytes` bytes acknowledged by the receiver: the
/// confirmed count rounded down to a whole-chunk boundary.
/// A partially confirmed chunk is always resent in full.
pub fn resume_offset(confirmed_bytes: u64, chunk_size: u64) -> u64 {
    let cs = effective_chunk_size(chunk_size);
    (confirmed_bytes / cs) * cs
}

/// Index of the chunk a resumed transfer should continue with:
/// `resume_offset(confirmed_bytes, chunk_size) / chunk_size`.
pub fn next_chunk_index(confirmed_bytes: u64, chunk_size: u64) -> u64 {
    let cs = effective_chunk_size(chunk_size);
    resume_offset(confirmed_bytes, cs) / cs
}

#[cfg(test)]
mod tests {
    use super::*;

    // ---------- SHA-256 known vectors (FIPS 180-4 examples) ----------

    #[test]
    fn sha256_empty() {
        assert_eq!(
            sha256_hex(b""),
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        );
    }

    #[test]
    fn sha256_abc() {
        assert_eq!(
            sha256_hex(b"abc"),
            "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"
        );
        // The raw-digest form must agree with the hex form.
        assert_eq!(sha256(b"abc").len(), 32);
        assert_eq!(sha256(b"abc")[0], 0xba);
        assert_eq!(sha256(b"abc")[31], 0xad);
    }

    #[test]
    fn sha256_56_byte_vector() {
        // 56 bytes: the padding boundary case (length lands exactly
        // where the length field needs a second block).
        assert_eq!(
            sha256_hex(b"abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq"),
            "248d6a61d20638b8e5c026930c3e6039a33ce45964ff2167f6ecedd419db06c1"
        );
    }

    #[test]
    fn sha256_million_a() {
        let data = vec![b'a'; 1_000_000];
        assert_eq!(
            sha256_hex(&data),
            "cdc76e5c9914fb9281a1c7e284d73e67f1809a48a497200e046d39ccc7112cd0"
        );
    }

    // ---------- Chunk math ----------

    #[test]
    fn chunk_count_zero_size() {
        assert_eq!(chunk_count(0, 1024), 0);
        assert_eq!(chunk_count(0, 0), 0);
    }

    #[test]
    fn chunk_count_exact_multiple() {
        assert_eq!(chunk_count(4096, 1024), 4);
        assert_eq!(chunk_count(1024, 1024), 1);
    }

    #[test]
    fn chunk_count_remainder_rounds_up() {
        assert_eq!(chunk_count(4097, 1024), 5);
        assert_eq!(chunk_count(1, 1024), 1);
        assert_eq!(chunk_count(1023, 1024), 1);
    }

    #[test]
    fn chunk_count_zero_chunk_size_treated_as_one() {
        assert_eq!(chunk_count(7, 0), 7);
    }

    #[test]
    fn chunk_offsets() {
        assert_eq!(chunk_offset(0, 1024), 0);
        assert_eq!(chunk_offset(1, 1024), 1024);
        assert_eq!(chunk_offset(5, 1024), 5120);
        // Saturating, never wrapping.
        assert_eq!(chunk_offset(u64::MAX, 1024), u64::MAX);
    }

    #[test]
    fn chunk_lengths_full_and_last_short() {
        assert_eq!(chunk_length(0, 4096, 1024), 1024);
        assert_eq!(chunk_length(3, 4096, 1024), 1024);
        assert_eq!(chunk_length(4, 4097, 1024), 1);
        assert_eq!(chunk_length(0, 10, 1024), 10);
    }

    #[test]
    fn chunk_length_index_out_of_range() {
        assert_eq!(chunk_length(4, 4096, 1024), 0);
        assert_eq!(chunk_length(100, 4096, 1024), 0);
        assert_eq!(chunk_length(0, 0, 1024), 0);
    }

    #[test]
    fn chunks_cover_file_exactly() {
        // For a spread of sizes: chunk lengths sum to the file size
        // and chunk offsets are the running sums — the same
        // invariants the Kotlin twin is tested against.
        for &(size, cs) in &[
            (0u64, 1024u64),
            (1, 1024),
            (1024, 1024),
            (65_537, 1024),
            (1_000_003, 65_536),
            (10, 3),
        ] {
            let count = chunk_count(size, cs);
            let mut pos = 0u64;
            for i in 0..count {
                assert_eq!(chunk_offset(i, cs), pos, "size={size} cs={cs} i={i}");
                pos += chunk_length(i, size, cs);
            }
            assert_eq!(pos, size, "size={size} cs={cs}");
            assert_eq!(chunk_length(count, size, cs), 0);
        }
    }

    // ---------- Resume ----------

    #[test]
    fn resume_on_chunk_boundary() {
        assert_eq!(resume_offset(0, 1024), 0);
        assert_eq!(resume_offset(1024, 1024), 1024);
        assert_eq!(resume_offset(4096, 1024), 4096);
        assert_eq!(next_chunk_index(4096, 1024), 4);
    }

    #[test]
    fn resume_with_partial_chunk_resends_it() {
        // 1500 confirmed = 1 whole chunk + 476 bytes of the next:
        // resume must back up to 1024 and resend chunk 1 in full.
        assert_eq!(resume_offset(1500, 1024), 1024);
        assert_eq!(next_chunk_index(1500, 1024), 1);
        assert_eq!(resume_offset(1023, 1024), 0);
        assert_eq!(next_chunk_index(1023, 1024), 0);
        assert_eq!(resume_offset(1, 1024), 0);
    }

    #[test]
    fn resume_zero_chunk_size_treated_as_one() {
        assert_eq!(resume_offset(42, 0), 42);
        assert_eq!(next_chunk_index(42, 0), 42);
    }
}
