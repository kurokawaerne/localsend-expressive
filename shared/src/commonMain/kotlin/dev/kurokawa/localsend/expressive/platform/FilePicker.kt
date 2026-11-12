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
    /**
     * Path shown to (and recreated by) the receiver: for folder picks this
     * is the relative path inside the folder ("Folder/sub/file.txt"), used
     * as FileDto.fileName. Null for plain file picks (plain [name] is used).
     */
    val displayPath: String? = null,
)

/**
 * Returns a launcher lambda; calling it opens the platform file picker
 * (SAF on Android, AWT FileDialog on desktop) and delivers the selection.
 */
@Composable
expect fun rememberFilePickerLauncher(onPicked: (List<PickedFile>) -> Unit): () -> Unit

/** Pick images/videos through the platform media picker (official's "Media" tile). */
@Composable
expect fun rememberMediaPickerLauncher(onPicked: (List<PickedFile>) -> Unit): () -> Unit

/** Pick a whole folder; every file inside is delivered with [PickedFile.displayPath] set. */
@Composable
expect fun rememberFolderPickerLauncher(onPicked: (List<PickedFile>) -> Unit): () -> Unit

/**
 * Pick the RECEIVE destination folder (v0.5.0, Ernest asked for SAF):
 * delivers a SAF tree URI string on Android (persistable read/write
 * grant taken), an absolute path on desktop.
 */
@Composable
expect fun rememberDestinationPickerLauncher(onPicked: (String) -> Unit): () -> Unit

/**
 * Files handed to the app from outside (Android share sheet). The Send
 * screen collects this flow and merges new files into its selection, then
 * calls [clearSharedFiles]. Always empty on desktop.
 */
expect fun sharedFilesFlow(): kotlinx.coroutines.flow.StateFlow<List<PickedFile>>

expect fun clearSharedFiles()
