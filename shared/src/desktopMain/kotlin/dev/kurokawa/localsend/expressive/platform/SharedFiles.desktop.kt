package dev.kurokawa.localsend.expressive.platform

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private val emptyShared = MutableStateFlow<List<PickedFile>>(emptyList())

/** Desktop has no share sheet; nothing ever arrives here. */
actual fun sharedFilesFlow(): StateFlow<List<PickedFile>> = emptyShared

actual fun clearSharedFiles() = Unit
