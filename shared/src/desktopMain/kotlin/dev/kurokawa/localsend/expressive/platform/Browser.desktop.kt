package dev.kurokawa.localsend.expressive.platform

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image as SkiaImage

actual fun platformHasSystemSplash(): Boolean = false

actual fun prefersSwipeActions(): Boolean = false

private val IMAGE_EXT = setOf("jpg", "jpeg", "png", "webp", "gif", "bmp")
private val VIDEO_EXT = setOf("mp4", "mkv", "webm", "avi", "mov", "3gp")
private val AUDIO_EXT = setOf("mp3", "flac", "ogg", "opus", "m4a", "wav", "aac")

private fun homeDir(): File = File(System.getProperty("user.home"))

private fun mediaDirs(kind: MediaKind): List<File> {
    val home = homeDir()
    val names = when (kind) {
        MediaKind.PHOTOS -> listOf("Pictures", "Images")
        MediaKind.VIDEOS -> listOf("Videos", "Movies")
        MediaKind.AUDIO -> listOf("Music", "Audio")
    }
    return names.map { home.resolve(it) }.filter { it.isDirectory }
}

private fun File.toItem(): BrowserItem = BrowserItem(
    id = "file:$absolutePath",
    name = name,
    size = if (isDirectory) 0 else length(),
    isFolder = isDirectory,
    handle = absolutePath,
    itemCount = if (isDirectory) runCatching { list()?.size ?: 0 }.getOrDefault(0) else -1,
    dateMillis = lastModified(),
)

actual suspend fun loadMediaItems(kind: MediaKind): List<BrowserItem> =
    withContext(Dispatchers.IO) {
        val ext = when (kind) {
            MediaKind.PHOTOS -> IMAGE_EXT
            MediaKind.VIDEOS -> VIDEO_EXT
            MediaKind.AUDIO -> AUDIO_EXT
        }
        val found = mutableListOf<BrowserItem>()
        for (dir in mediaDirs(kind)) {
            dir.walkTopDown().maxDepth(4).forEach { file ->
                if (file.isFile && file.extension.lowercase() in ext && found.size < 3000) {
                    found += file.toItem().copy(
                        id = "media:${file.absolutePath}",
                        mimeType = when (kind) {
                            MediaKind.PHOTOS -> "image/*"
                            MediaKind.VIDEOS -> "video/*"
                            MediaKind.AUDIO -> "audio/*"
                        },
                    )
                }
            }
        }
        found.sortedByDescending { it.dateMillis }
    }

actual fun storageRoots(): List<BrowserItem> {
    val roots = mutableListOf<BrowserItem>()
    val home = homeDir()
    roots += BrowserItem(
        id = "root:${home.absolutePath}",
        name = "Home",
        isFolder = true,
        handle = home.absolutePath,
    )
    File.listRoots()?.forEach { root ->
        if (root.absolutePath != home.absolutePath) {
            roots += BrowserItem(
                id = "root:${root.absolutePath}",
                name = root.absolutePath,
                isFolder = true,
                handle = root.absolutePath,
            )
        }
    }
    return roots
}

actual suspend fun listBrowserFolder(path: String): List<BrowserItem> =
    withContext(Dispatchers.IO) {
        val dir = File(path)
        val children = runCatching { dir.listFiles() }.getOrNull() ?: return@withContext emptyList()
        children
            .filter { !it.isHidden }
            .sortedWith(compareByDescending<File> { it.isDirectory }.thenBy { it.name.lowercase() })
            .map { it.toItem() }
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

@Composable
actual fun rememberStorageAccess(): StorageAccess = StorageAccess(
    mediaGranted = true,
    fullAccess = true,
    requestMedia = {},
    requestFullAccess = {},
    recheck = {},
)

@Composable
actual fun BrowserThumbnail(item: BrowserItem, modifier: Modifier) {
    val isImage = item.name.substringAfterLast('.', "").lowercase() in IMAGE_EXT
    val bitmap by produceState<ImageBitmap?>(null, item.id) {
        value = if (isImage && !item.isFolder) {
            withContext(Dispatchers.IO) {
                runCatching {
                    val file = File(item.handle)
                    // Skip decoding huge photos for a grid cell.
                    if (file.length() in 1..12_000_000) {
                        SkiaImage.makeFromEncoded(file.readBytes()).toComposeImageBitmap()
                    } else {
                        null
                    }
                }.getOrNull()
            }
        } else {
            null
        }
    }
    val image = bitmap
    if (image != null) {
        Image(
            bitmap = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier,
        )
    } else {
        Icon(
            imageVector = when {
                item.isFolder -> Icons.Filled.Folder
                item.name.substringAfterLast('.', "").lowercase() in AUDIO_EXT -> Icons.Filled.Audiotrack
                item.name.substringAfterLast('.', "").lowercase() in VIDEO_EXT -> Icons.Filled.Movie
                else -> Icons.Filled.InsertDriveFile
            },
            contentDescription = null,
            modifier = modifier,
        )
    }
}
