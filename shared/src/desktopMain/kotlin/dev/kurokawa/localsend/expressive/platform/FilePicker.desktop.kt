package dev.kurokawa.localsend.expressive.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragData
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.ui.draganddrop.dragData
import java.awt.FileDialog
import java.awt.Frame

/** AWT multi-select file dialog. */
@Composable
actual fun rememberFilePickerLauncher(onPicked: (List<PickedFile>) -> Unit): () -> Unit {
    return remember {
        {
            val dialog = FileDialog(null as Frame?, "Select files to send", FileDialog.LOAD)
            dialog.isMultipleMode = true
            dialog.isVisible = true
            val files = dialog.files?.toList().orEmpty()
            if (files.isNotEmpty()) {
                onPicked(files.map { PickedFile(it.absolutePath, it.name, it.length()) })
            }
        }
    }
}

private val MEDIA_EXTENSIONS = setOf(
    "jpg", "jpeg", "png", "gif", "webp", "svg", "bmp", "heic",
    "mp4", "mkv", "webm", "mov", "avi",
)

/** Desktop has no photo picker; the file dialog is filtered to media types. */
@Composable
actual fun rememberMediaPickerLauncher(onPicked: (List<PickedFile>) -> Unit): () -> Unit {
    return remember {
        {
            val dialog = FileDialog(null as Frame?, "Select media to send", FileDialog.LOAD)
            dialog.isMultipleMode = true
            dialog.setFilenameFilter { _, name ->
                name.substringAfterLast('.', "").lowercase() in MEDIA_EXTENSIONS
            }
            dialog.isVisible = true
            val files = dialog.files?.toList().orEmpty()
            if (files.isNotEmpty()) {
                onPicked(files.map { PickedFile(it.absolutePath, it.name, it.length()) })
            }
        }
    }
}

/** Receive-destination picker (v0.5.0): delivers the folder's absolute path. */
@Composable
actual fun rememberDestinationPickerLauncher(onPicked: (String) -> Unit): () -> Unit {
    return remember {
        {
            val chooser = javax.swing.JFileChooser()
            chooser.fileSelectionMode = javax.swing.JFileChooser.DIRECTORIES_ONLY
            chooser.dialogTitle = "Select the receive folder"
            if (chooser.showOpenDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
                onPicked(chooser.selectedFile.absolutePath)
            }
        }
    }
}

/** Folder picker: choose one directory; every file inside is picked recursively. */
@Composable
actual fun rememberFolderPickerLauncher(onPicked: (List<PickedFile>) -> Unit): () -> Unit {
    return remember {
        {
            val chooser = javax.swing.JFileChooser()
            chooser.fileSelectionMode = javax.swing.JFileChooser.DIRECTORIES_ONLY
            chooser.dialogTitle = "Select a folder to send"
            if (chooser.showOpenDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
                val root = chooser.selectedFile
                val picked = root.walkTopDown()
                    .filter { it.isFile }
                    .map { file ->
                        PickedFile(
                            handle = file.absolutePath,
                            name = file.name,
                            size = file.length(),
                            displayPath = root.name + "/" +
                                file.relativeTo(root).path.replace('\\', '/'),
                        )
                    }
                    .toList()
                if (picked.isNotEmpty()) onPicked(picked)
            }
        }
    }
}

actual fun pickedFileFromPath(path: String): PickedFile? {
    val file = java.io.File(path)
    return if (file.isFile) {
        PickedFile(file.absolutePath, file.name, file.length())
    } else {
        null
    }
}

actual fun pathIsDirectory(path: String): Boolean =
    java.io.File(path).isDirectory

actual fun clipboardFilePaths(): List<String> = runCatching {
    val contents = java.awt.Toolkit.getDefaultToolkit().systemClipboard
        .getContents(null) ?: return emptyList()
    if (!contents.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.javaFileListFlavor)) {
        return emptyList()
    }
    @Suppress("UNCHECKED_CAST")
    val files = contents.getTransferData(
        java.awt.datatransfer.DataFlavor.javaFileListFlavor,
    ) as List<java.io.File>
    files.map { it.absolutePath }
}.getOrDefault(emptyList())

actual fun clipboardTextOrNull(): String? = runCatching {
    val contents = java.awt.Toolkit.getDefaultToolkit().systemClipboard
        .getContents(null) ?: return null
    if (!contents.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.stringFlavor)) {
        null
    } else {
        contents.getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor) as? String
    }
}.getOrNull()

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.fileDropTarget(
    onFiles: (List<String>) -> Unit,
): Modifier = dragAndDropTarget(
    shouldStartDragAndDrop = { event ->
        event.dragData() is DragData.FilesList
    },
    target = object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            val data = event.dragData() as? DragData.FilesList ?: return false
            val paths = data.readFiles().mapNotNull { entry ->
                runCatching {
                    if (entry.startsWith("file:")) {
                        java.io.File(java.net.URI(entry)).path
                    } else {
                        entry
                    }
                }.getOrNull()
            }
            if (paths.isEmpty()) return false
            onFiles(paths)
            return true
        }
    },
)
