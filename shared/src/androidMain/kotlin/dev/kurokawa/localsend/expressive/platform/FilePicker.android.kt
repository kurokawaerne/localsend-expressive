package dev.kurokawa.localsend.expressive.platform

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

/** SAF picker (Storage Access Framework) — no broad storage permission needed. */
@Composable
actual fun rememberFilePickerLauncher(onPicked: (List<PickedFile>) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments(),
    ) { uris: List<Uri> ->
        val picked = uris.mapNotNull { resolvePickedFile(context, it) }
        if (picked.isNotEmpty()) onPicked(picked)
    }
    return remember(launcher) { { launcher.launch(arrayOf("*/*")) } }
}

/** Photo/video picker (the official "Media" tile). */
@Composable
actual fun rememberMediaPickerLauncher(onPicked: (List<PickedFile>) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(),
    ) { uris: List<Uri> ->
        val picked = uris.mapNotNull { resolvePickedFile(context, it) }
        if (picked.isNotEmpty()) onPicked(picked)
    }
    return remember(launcher) {
        {
            launcher.launch(
                androidx.activity.result.PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageAndVideo,
                ),
            )
        }
    }
}

/**
 * Receive-destination picker (v0.5.0): SAF document tree with a
 * persistable read/write grant, so the receive sink can write into the
 * chosen folder across restarts.
 */
@Composable
actual fun rememberDestinationPickerLauncher(onPicked: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree(),
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
            )
        }
        onPicked(uri.toString())
    }
    return remember(launcher) { { launcher.launch(null) } }
}

/**
 * Folder picker (the official "Folder" tile): walks the chosen document
 * tree and delivers every file with its path relative to the folder root,
 * prefixed with the folder's own name ("Folder/sub/file").
 */
@Composable
actual fun rememberFolderPickerLauncher(onPicked: (List<PickedFile>) -> Unit): () -> Unit {
    val context = LocalContext.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree(),
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        // Take a REAL persistable read grant (the old code passed flags=0,
        // which grants nothing — the picked files became unreadable after
        // an app restart, so outbox retries of SAF files failed).
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
        }
        // The walk runs OFF the main thread (v0.12.4): DocumentFile makes
        // an IPC call per node, so walking a tree in the result callback
        // froze the UI for the whole traversal — Ernest's "folder select
        // is super slow / hangs" report.
        scope.launch {
            val picked = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                val root = androidx.documentfile.provider.DocumentFile.fromTreeUri(context, uri)
                    ?: return@withContext emptyList()
                val rootName = root.name ?: "Folder"
                val out = mutableListOf<PickedFile>()
                fun walk(
                    dir: androidx.documentfile.provider.DocumentFile,
                    prefix: String,
                ) {
                    for (child in dir.listFiles()) {
                        if (child.isDirectory) {
                            walk(child, "$prefix/${child.name}")
                        } else if (child.isFile) {
                            val name = child.name ?: continue
                            out += PickedFile(
                                handle = child.uri.toString(),
                                name = name,
                                size = child.length(),
                                displayPath = "$prefix/$name",
                            )
                        }
                    }
                }
                walk(root, rootName)
                out
            }
            if (picked.isNotEmpty()) onPicked(picked)
        }
    }
    return remember(launcher) { { launcher.launch(null) } }
}

actual fun pickedFileFromPath(path: String): PickedFile? = null

actual fun pathIsDirectory(path: String): Boolean = false

actual fun clipboardFilePaths(): List<String> = emptyList()

actual fun clipboardTextOrNull(): String? = null

actual fun androidx.compose.ui.Modifier.fileDropTarget(
    onFiles: (List<String>) -> Unit,
): androidx.compose.ui.Modifier = this
