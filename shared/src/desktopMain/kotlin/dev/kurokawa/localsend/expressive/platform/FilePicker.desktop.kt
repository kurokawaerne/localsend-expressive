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
