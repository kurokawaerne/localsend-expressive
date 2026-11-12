package dev.kurokawa.localsend.expressive.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
