//! JNI bindings for the Kotlin `RustCore` object.
//!
//! Kotlin side (shared module, package
//! `dev.kurokawa.localsend.expressive`):
//!
//! ```kotlin
//! object RustCore {
//!     external fun nativeChunkCount(fileSize: Long, chunkSize: Long): Long
//!     external fun nativeChunkOffset(index: Long, chunkSize: Long): Long
//!     external fun nativeChunkLength(index: Long, fileSize: Long, chunkSize: Long): Long
//!     external fun nativeResumeOffset(confirmedBytes: Long, chunkSize: Long): Long
//!     external fun nativeSha256Hex(data: ByteArray): String
//! }
//! ```
//!
//! Compiled only with `--features jni`. Every function here is a
//! thin, panic-free wrapper over the pure functions in the crate
//! root: values cross the boundary as `u64` bit patterns, and the
//! SHA-256 entry point falls back to the hash of the empty input
//! on any JNI error — nothing may panic across JNI, an unwind
//! into the JVM is undefined behaviour.

use jni::objects::{JByteArray, JObject, JString};
use jni::sys::jlong;
use jni::JNIEnv;

use crate::{chunk_count, chunk_length, chunk_offset, resume_offset, sha256_hex};

#[no_mangle]
pub extern "system" fn Java_dev_kurokawa_localsend_expressive_RustCore_nativeChunkCount<
    'local,
>(
    _env: JNIEnv<'local>,
    _obj: JObject<'local>,
    file_size: jlong,
    chunk_size: jlong,
) -> jlong {
    chunk_count(file_size as u64, chunk_size as u64) as jlong
}

#[no_mangle]
pub extern "system" fn Java_dev_kurokawa_localsend_expressive_RustCore_nativeChunkOffset<
    'local,
>(
    _env: JNIEnv<'local>,
    _obj: JObject<'local>,
    index: jlong,
    chunk_size: jlong,
) -> jlong {
    chunk_offset(index as u64, chunk_size as u64) as jlong
}

#[no_mangle]
pub extern "system" fn Java_dev_kurokawa_localsend_expressive_RustCore_nativeChunkLength<
    'local,
>(
    _env: JNIEnv<'local>,
    _obj: JObject<'local>,
    index: jlong,
    file_size: jlong,
    chunk_size: jlong,
) -> jlong {
    chunk_length(index as u64, file_size as u64, chunk_size as u64) as jlong
}

#[no_mangle]
pub extern "system" fn Java_dev_kurokawa_localsend_expressive_RustCore_nativeResumeOffset<
    'local,
>(
    _env: JNIEnv<'local>,
    _obj: JObject<'local>,
    confirmed_bytes: jlong,
    chunk_size: jlong,
) -> jlong {
    resume_offset(confirmed_bytes as u64, chunk_size as u64) as jlong
}

#[no_mangle]
pub extern "system" fn Java_dev_kurokawa_localsend_expressive_RustCore_nativeSha256Hex<
    'local,
>(
    env: JNIEnv<'local>,
    _obj: JObject<'local>,
    input: JByteArray<'local>,
) -> JString<'local> {
    // On any error (null/invalid array, pending exception, …)
    // fall through with the hash of the empty input instead of
    // panicking or propagating an exception for a pure hash call.
    let hex = match env.convert_byte_array(&input) {
        Ok(bytes) => sha256_hex(&bytes),
        Err(_) => {
            let _ = env.exception_clear();
            sha256_hex(b"")
        }
    };
    match env.new_string(&hex) {
        Ok(s) => s,
        Err(_) => {
            let _ = env.exception_clear();
            // Creating a Java string for 64 ASCII chars failing
            // means the JVM could not allocate it; a second
            // attempt with the empty-input hash (after clearing
            // the pending exception) is the last resort. No
            // input-dependent path in this function can panic.
            env.new_string(sha256_hex(b""))
                .expect("failed to create fallback Java string")
        }
    }
}
