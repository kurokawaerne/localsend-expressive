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
