package dev.kurokawa.localsend.expressive

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.window.application
import dev.kurokawa.localsend.expressive.app.LocalSendService
import dev.kurokawa.localsend.expressive.platform.platformDataDir
import dev.kurokawa.localsend.expressive.ui.AppRoot
import java.io.File
import java.util.Properties
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

fun main() = application {
    val service = remember { LocalSendService() }
    val settings by service.settings.collectAsState()
    val icon = painterResource("icon.png")

    // Window placement persistence (official desktop setting).
    val placementFile = remember { File(platformDataDir(), "window.properties") }
    val initial = remember {
        if (settings.saveWindowPlacement && placementFile.isFile) {
            runCatching {
                val props = Properties().apply { placementFile.inputStream().use { load(it) } }
                WindowPosition(
                    props.getProperty("x")?.toFloat()?.dp ?: 120.dp,
                    props.getProperty("y")?.toFloat()?.dp ?: 80.dp,
                ) to DpSize(
                    props.getProperty("w")?.toFloat()?.dp ?: 1024.dp,
                    props.getProperty("h")?.toFloat()?.dp ?: 760.dp,
                )
            }.getOrNull()
        } else {
            null
        }
    }
    val windowState = rememberWindowState(
        position = initial?.first ?: WindowPosition(120.dp, 80.dp),
        size = initial?.second ?: DpSize(1024.dp, 760.dp),
    )
    var windowVisible by remember { mutableStateOf(true) }

    if (settings.saveWindowPlacement) {
        LaunchedEffect(windowState) {
            snapshotFlow { windowState.position to windowState.size }
                .collectLatest {
                    delay(600)
                    runCatching {
                        val props = Properties().apply {
                            setProperty("x", windowState.position.x.value.toString())
                            setProperty("y", windowState.position.y.value.toString())
                            setProperty("w", windowState.size.width.value.toString())
                            setProperty("h", windowState.size.height.value.toString())
                        }
                        placementFile.outputStream().use { props.store(it, null) }
                    }
                }
        }
    }

    if (settings.minimizeToTray) {
        Tray(
            icon = icon,
            tooltip = "LSEx",
            onAction = { windowVisible = true },
        ) {
            Item("Open LSEx", onClick = { windowVisible = true })
            Separator()
            Item("Exit", onClick = ::exitApplication)
        }
    }

    Window(
        onCloseRequest = {
            if (settings.minimizeToTray) {
                windowVisible = false
            } else {
                exitApplication()
            }
        },
        state = windowState,
        visible = windowVisible,
        title = "LSEx",
        icon = icon,
        alwaysOnTop = settings.alwaysOnTop,
    ) {
        MenuBar {
            Menu("File") {
                Item("Exit", onClick = ::exitApplication)
            }
        }
        AppRoot(service)
    }
}
