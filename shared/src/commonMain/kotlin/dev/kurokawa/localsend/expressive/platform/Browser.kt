package dev.kurokawa.localsend.expressive.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * In-app content browser (v0.6.0, Ernest's Zapya-style layout request):
 * the Home tabs show the device's actual content — media library and a
 * folder tree — and items are selected in place instead of delegating
 * everything to the system picker.
 */

enum class MediaKind { PHOTOS, VIDEOS, AUDIO }

data class BrowserItem(
    /** Stable selection id ("media:…", "file:…", "root:…"). */
    val id: String,
    val name: String,
    val size: Long = 0,
    val isFolder: Boolean = false,
    /** content:// URI on Android, absolute path on desktop / for files. */
    val handle: String,
    /** Folders: number of entries inside (-1 = unknown). */
    val itemCount: Int = -1,
    val dateMillis: Long = 0,
    val mimeType: String = "",
)

/** Media library content (MediaStore on Android, the user's media folders on desktop). */
expect suspend fun loadMediaItems(kind: MediaKind): List<BrowserItem>

/** Top-level storage locations for the Files tab (internal storage, SD card, home…). */
expect fun storageRoots(): List<BrowserItem>

/** One folder level of the file tree, folders first. */
expect suspend fun listBrowserFolder(path: String): List<BrowserItem>

/** Walk a folder into sendable files, keeping the folder structure as display paths. */
expect suspend fun expandBrowserFolder(folder: BrowserItem): List<PickedFile>

/** Access state for the browser tabs, with request actions (Android runtime permissions). */
data class StorageAccess(
    /** Can read the media library (photos / videos / audio). */
    val mediaGranted: Boolean,
    /** Can walk the whole shared storage (the Files tree). */
    val fullAccess: Boolean,
    val requestMedia: () -> Unit,
    val requestFullAccess: () -> Unit,
    /** Re-read the grants (e.g. after returning from the system settings). */
    val recheck: () -> Unit,
)

@Composable
expect fun rememberStorageAccess(): StorageAccess

/** A thumbnail for a browser item (media thumbnail, image preview, or a type icon). */
@Composable
expect fun BrowserThumbnail(item: BrowserItem, modifier: Modifier = Modifier)

/** True when the platform shows a system splash before the app UI (Android 12+). */
expect fun platformHasSystemSplash(): Boolean

/** True where list actions belong behind a swipe (touch platforms); desktop keeps visible buttons. */
expect fun prefersSwipeActions(): Boolean
