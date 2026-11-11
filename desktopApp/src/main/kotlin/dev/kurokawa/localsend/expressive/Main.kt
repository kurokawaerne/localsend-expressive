package dev.kurokawa.localsend.expressive

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.kurokawa.localsend.expressive.app.LocalSendService
import dev.kurokawa.localsend.expressive.ui.AppRoot

fun main() = application {
    val service = remember { LocalSendService() }
    Window(
        onCloseRequest = ::exitApplication,
        title = "LocalSend Expressive",
    ) {
        AppRoot(service)
    }
}
