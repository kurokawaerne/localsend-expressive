package dev.kurokawa.localsend.expressive

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.kurokawa.localsend.expressive.ui.AppRoot

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "LocalSend Expressive",
    ) {
        AppRoot()
    }
}
