package dev.kurokawa.localsend.expressive.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Backing store for files arriving via ACTION_SEND / ACTION_SEND_MULTIPLE. */
object SharedFilesStore {
    val pending = MutableStateFlow<List<PickedFile>>(emptyList())
}

actual fun sharedFilesFlow(): StateFlow<List<PickedFile>> = SharedFilesStore.pending

actual fun clearSharedFiles() {
    SharedFilesStore.pending.value = emptyList()
}

/** Resolve share-intent URIs to [PickedFile]s and queue them for the UI. */
fun offerSharedUris(context: Context, uris: List<Uri>) {
    val picked = uris.mapNotNull { resolvePickedFile(context, it) }
    if (picked.isNotEmpty()) {
        SharedFilesStore.pending.value = SharedFilesStore.pending.value + picked
    }
}

/** Turn a content:// URI into a [PickedFile] (display name + size via SAF). */
fun resolvePickedFile(context: Context, uri: Uri): PickedFile? {
    runCatching {
        context.contentResolver.takePersistableUriPermission(
            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION,
        )
    }
    val nameAndSize = runCatching {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (!cursor.moveToFirst()) return@use null
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            val name = if (nameIndex >= 0) cursor.getString(nameIndex) else null
            val size = if (sizeIndex >= 0) cursor.getLong(sizeIndex) else 0L
            if (name != null) name to size else null
        }
    }.getOrNull()
    if (nameAndSize != null) {
        return PickedFile(handle = uri.toString(), name = nameAndSize.first, size = nameAndSize.second)
    }
    // Fallback (v0.12.4): some providers (photo-picker cloud items) answer
    // no DISPLAY_NAME row — the old code dropped those files entirely
    // ("media picks lose files / wrong name"). Derive a name from the URI
    // and the size from the asset descriptor instead of dropping the pick.
    val fallbackName = uri.lastPathSegment
        ?.substringAfterLast('/')
        ?.takeIf { it.isNotBlank() }
        ?: "file"
    val fallbackSize = runCatching {
        context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length }
    }.getOrNull()?.takeIf { it > 0 } ?: 0L
    return PickedFile(handle = uri.toString(), name = fallbackName, size = fallbackSize)
}
