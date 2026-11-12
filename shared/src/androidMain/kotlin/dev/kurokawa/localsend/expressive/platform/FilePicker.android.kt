package dev.kurokawa.localsend.expressive.platform

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

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
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree(),
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching { context.contentResolver.takePersistableUriPermission(uri, 0) }
        val root = androidx.documentfile.provider.DocumentFile.fromTreeUri(context, uri)
            ?: return@rememberLauncherForActivityResult
        val rootName = root.name ?: "Folder"
        val picked = mutableListOf<PickedFile>()
        fun walk(dir: androidx.documentfile.provider.DocumentFile, prefix: String) {
            for (child in dir.listFiles()) {
                if (child.isDirectory) {
                    walk(child, "$prefix/${child.name}")
                } else if (child.isFile) {
                    val name = child.name ?: continue
                    picked += PickedFile(
                        handle = child.uri.toString(),
                        name = name,
                        size = child.length(),
                        displayPath = "$prefix/$name",
                    )
                }
            }
        }
        walk(root, rootName)
        if (picked.isNotEmpty()) onPicked(picked)
    }
    return remember(launcher) { { launcher.launch(null) } }
}
