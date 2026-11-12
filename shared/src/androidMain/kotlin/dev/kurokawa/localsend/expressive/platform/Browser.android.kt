package dev.kurokawa.localsend.expressive.platform

import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.LruCache
import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.core.content.ContextCompat
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual fun platformHasSystemSplash(): Boolean = true

actual fun prefersSwipeActions(): Boolean = true

// ── Media library (MediaStore) ────────────────────────────────────────────

actual suspend fun loadMediaItems(kind: MediaKind): List<BrowserItem> =
    withContext(Dispatchers.IO) {
        val context = AppContextHolder.appContext
        val (collection, projection) = when (kind) {
            MediaKind.PHOTOS -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI to arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE,
            )
            MediaKind.VIDEOS -> MediaStore.Videos.Media.EXTERNAL_CONTENT_URI to arrayOf(
                MediaStore.Videos.Media._ID,
                MediaStore.Videos.Media.DISPLAY_NAME,
                MediaStore.Videos.Media.SIZE,
                MediaStore.Videos.Media.DATE_ADDED,
                MediaStore.Videos.Media.MIME_TYPE,
            )
            MediaKind.AUDIO -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI to arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.MIME_TYPE,
            )
        }
        val dateColumn = projection[3]
        val items = mutableListOf<BrowserItem>()
        runCatching {
            context.contentResolver.query(
                collection, projection, null, null,
                "$dateColumn DESC",
            )?.use { cursor ->
                val idIdx = cursor.getColumnIndexOrThrow(projection[0])
                val nameIdx = cursor.getColumnIndexOrThrow(projection[1])
                val sizeIdx = cursor.getColumnIndexOrThrow(projection[2])
                val dateIdx = cursor.getColumnIndexOrThrow(projection[3])
                val mimeIdx = cursor.getColumnIndexOrThrow(projection[4])
                while (cursor.moveToNext() && items.size < 3000) {
                    val id = cursor.getLong(idIdx)
                    val uri = ContentUris.withAppendedId(collection, id)
                    items += BrowserItem(
                        id = "media:$uri",
                        name = cursor.getString(nameIdx) ?: "media-$id",
                        size = cursor.getLong(sizeIdx),
                        handle = uri.toString(),
                        dateMillis = cursor.getLong(dateIdx) * 1000,
                        mimeType = cursor.getString(mimeIdx) ?: "",
                    )
                }
            }
        }
        items
    }

// ── File tree ─────────────────────────────────────────────────────────────

actual fun storageRoots(): List<BrowserItem> {
    val context = AppContextHolder.appContext
    val roots = mutableListOf<BrowserItem>()
    val primary = Environment.getExternalStorageDirectory()
    roots += BrowserItem(
        id = "root:${primary.absolutePath}",
        name = "Internal storage",
        isFolder = true,
        handle = primary.absolutePath,
    )
    runCatching {
        context.getExternalFilesDirs(null).drop(1).forEach { dir ->
            // /storage/<volume-id>/Android/data/<pkg>/files → /storage/<volume-id>
            val volumeRoot = dir.absolutePath.split("/").take(3).joinToString("/")
            if (volumeRoot.isNotBlank() && roots.none { it.handle == volumeRoot }) {
                roots += BrowserItem(
                    id = "root:$volumeRoot",
                    name = "SD card",
                    isFolder = true,
                    handle = volumeRoot,
                )
            }
        }
    }
    return roots
}

private fun File.toBrowserItem(): BrowserItem = BrowserItem(
    id = "file:$absolutePath",
    name = name,
    size = if (isDirectory) 0 else length(),
    isFolder = isDirectory,
    handle = absolutePath,
    itemCount = if (isDirectory) runCatching { list()?.size ?: 0 }.getOrDefault(0) else -1,
    dateMillis = lastModified(),
)

actual suspend fun listBrowserFolder(path: String): List<BrowserItem> =
    withContext(Dispatchers.IO) {
        val dir = File(path)
        val children = runCatching { dir.listFiles() }.getOrNull() ?: return@withContext emptyList()
        children
            .filter { !it.isHidden }
            .sortedWith(compareByDescending<File> { it.isDirectory }.thenBy { it.name.lowercase() })
            .map { it.toBrowserItem() }
    }

actual suspend fun expandBrowserFolder(folder: BrowserItem): List<PickedFile> =
    withContext(Dispatchers.IO) {
        val root = File(folder.handle)
        if (!root.isDirectory) return@withContext emptyList()
        val picked = mutableListOf<PickedFile>()
        root.walkTopDown().forEach { file ->
            if (file.isFile) {
                val relative = file.relativeTo(root).path.replace(File.separatorChar, '/')
                picked += PickedFile(
                    handle = file.absolutePath,
                    name = file.name,
                    size = file.length(),
                    displayPath = "${folder.name}/$relative",
                )
            }
        }
        picked
    }

// ── Permissions / access ─────────────────────────────────────────────────

private fun mediaPermissions(): Array<String> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_VIDEO,
            android.Manifest.permission.READ_MEDIA_AUDIO,
        )
    } else {
        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

private fun mediaGrantedNow(): Boolean {
    val context = AppContextHolder.appContext
    return mediaPermissions().all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}

private fun fullAccessNow(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        mediaGrantedNow()
    }

@Composable
actual fun rememberStorageAccess(): StorageAccess {
    var tick by remember { mutableStateOf(0) }
    // Reading the grants is keyed by tick; recheck() bumps it after the
    // user returns from a permission dialog / the settings screen.
    val media = remember(tick) { mediaGrantedNow() }
    val full = remember(tick) { fullAccessNow() }
    return StorageAccess(
        mediaGranted = media,
        fullAccess = full,
        requestMedia = {
            PermissionGate.request(mediaPermissions()) { tick++ }
        },
        requestFullAccess = {
            val context = AppContextHolder.appContext
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                runCatching {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                            Uri.parse("package:${context.packageName}"),
                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    )
                }
            } else {
                PermissionGate.request(mediaPermissions()) { tick++ }
            }
        },
        recheck = { tick++ },
    )
}

// ── Thumbnails ────────────────────────────────────────────────────────────

private val thumbCache = object : LruCache<String, Bitmap>(24 * 1024) {
    override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount / 1024
}

@Composable
actual fun BrowserThumbnail(item: BrowserItem, modifier: Modifier) {
    val context = AppContextHolder.appContext
    val bitmap by produceState<Bitmap?>(null, item.id) {
        value = thumbCache.get(item.id) ?: withContext(Dispatchers.IO) {
            runCatching {
                when {
                    item.handle.startsWith("content://") && Build.VERSION.SDK_INT >= 29 ->
                        context.contentResolver.loadThumbnail(
                            Uri.parse(item.handle), Size(256, 256), null,
                        )
                    item.mimeType.startsWith("image/") || item.name.matches(
                        Regex(".*\\.(jpe?g|png|webp|gif|bmp)$", RegexOption.IGNORE_CASE),
                    ) -> {
                        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                        BitmapFactory.decodeFile(item.handle, bounds)
                        val sample = (maxOf(bounds.outWidth, bounds.outHeight) / 256)
                            .coerceAtLeast(1)
                        BitmapFactory.decodeFile(
                            item.handle,
                            BitmapFactory.Options().apply { inSampleSize = sample },
                        )
                    }
                    else -> null
                }
            }.getOrNull()?.also { thumbCache.put(item.id, it) }
        }
    }
    val image = bitmap
    if (image != null) {
        Image(
            bitmap = image.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier,
        )
    } else {
        Icon(
            imageVector = when {
                item.isFolder -> Icons.Filled.Folder
                item.mimeType.startsWith("audio/") -> Icons.Filled.Audiotrack
                item.mimeType.startsWith("video/") -> Icons.Filled.Movie
                else -> Icons.Filled.InsertDriveFile
            },
            contentDescription = null,
            modifier = modifier,
        )
    }
}
