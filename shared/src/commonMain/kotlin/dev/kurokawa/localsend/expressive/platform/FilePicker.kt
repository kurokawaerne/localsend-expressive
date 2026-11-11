package dev.kurokawa.localsend.expressive.platform

import androidx.compose.runtime.Composable

/**
 * A file the user picked to send.
 * [handle] is platform-specific: an absolute path on desktop,
 * a content:// URI string on Android. The transfer layer opens it
 * through the platform input-stream hook (jvmMain).
 */
data class PickedFile(
    val handle: String,
    val name: String,
    val size: Long,
)

/**
 * Returns a launcher lambda; calling it opens the platform file picker
 * (SAF on Android, AWT FileDialog on desktop) and delivers the selection.
 */
@Composable
expect fun rememberFilePickerLauncher(onPicked: (List<PickedFile>) -> Unit): () -> Unit
