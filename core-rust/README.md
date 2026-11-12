# lsex-core (Rust)

Native core for LocalSend Expressive (LSEx), called from Kotlin
through JNI. Package `lsex-core`, library `lsex_core`.

**What it does**

- **Chunk math for resumable sends** — `chunk_count`,
  `chunk_offset`, `chunk_length`, `resume_offset`,
  `next_chunk_index`. Files are sent in fixed-size chunks so a
  transfer interrupted by a disconnection can be retried — the
  whole send or any single file — instead of starting over.
- **SHA-256** — `sha256` / `sha256_hex`, implemented by hand from
  FIPS 180-4. LocalSend uses SHA-256 for file checksums and for
  TLS certificate fingerprints.

The default build is **std-only, zero external dependencies**, so
`cargo test` works fully offline.

**Resume semantics**

The receiver only ever confirms *whole* chunks. If a transfer
dies mid-chunk, that partial chunk counts for nothing:
`resume_offset(confirmed, chunk_size)` rounds the confirmed byte
count **down** to a chunk boundary, and `next_chunk_index` is the
chunk to resume with. A resumed send — even after the app was
closed and reopened — asks the receiver how many bytes it
confirmed, then re-sends from that offset. A half-written chunk
can therefore never be spliced into the finished file. A
`chunk_size` of 0 is treated as 1 everywhere (no div-by-zero).

**Kotlin fallback**

Every function here is mirrored by a pure-Kotlin twin with
identical semantics (`RustCore` falls back to it wherever the
native library cannot be loaded, e.g. an unsupported ABI). Both
sides are unit-tested against the same vectors — FIPS SHA-256
examples and the chunk-math edge cases — so the fallback can
never silently behave differently from the native path.

**JNI bindings**

With `--features jni` (the only feature; it pulls in the `jni`
crate), `src/jni_bindings.rs` exports the symbols for
`object RustCore` in package `dev.kurokawa.localsend.expressive`:

- `nativeChunkCount(fileSize, chunkSize)`
- `nativeChunkOffset(index, chunkSize)`
- `nativeChunkLength(index, fileSize, chunkSize)`
- `nativeResumeOffset(confirmedBytes, chunkSize)`
- `nativeSha256Hex(data: ByteArray): String` — on any JNI error
  it returns the hash of the empty input; nothing panics
  across the JNI boundary.

**How CI builds it**

CI runs `cargo test` on the default (std-only) build, then
builds the release library with `--features jni`: for Android
per ABI with cargo-ndk (arm64-v8a / armeabi-v7a / x86_64) so the
`.so` lands in each split APK's `jniLibs`, and for desktop as a
Linux shared library bundled next to the app. The Kotlin/Gradle
wiring lives outside this crate.

Run the tests locally:

```
cargo test
cargo test --features jni   # also type-checks the JNI bindings
```
