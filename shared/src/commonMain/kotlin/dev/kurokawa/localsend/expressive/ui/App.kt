package dev.kurokawa.localsend.expressive.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.kurokawa.localsend.expressive.LocalDevice
import dev.kurokawa.localsend.expressive.app.AppSettings
import dev.kurokawa.localsend.expressive.app.FileProgress
import dev.kurokawa.localsend.expressive.app.HistoryEntry
import dev.kurokawa.localsend.expressive.app.IncomingOffer
import dev.kurokawa.localsend.expressive.app.LocalSendController
import dev.kurokawa.localsend.expressive.app.OutboxFile
import dev.kurokawa.localsend.expressive.app.OutboxJob
import dev.kurokawa.localsend.expressive.app.TransferStatus
import dev.kurokawa.localsend.expressive.discovery.DiscoveredDevice
import dev.kurokawa.localsend.expressive.platform.ConnectivityController
import dev.kurokawa.localsend.expressive.platform.InstalledApp
import dev.kurokawa.localsend.expressive.platform.PickedFile
import dev.kurokawa.localsend.expressive.platform.QrScannerView
import dev.kurokawa.localsend.expressive.platform.joinWifiNetwork
import dev.kurokawa.localsend.expressive.platform.openUrl
import dev.kurokawa.localsend.expressive.platform.parseWifiQr
import dev.kurokawa.localsend.expressive.platform.clearQuickShareRequest
import dev.kurokawa.localsend.expressive.platform.clearSharedFiles
import dev.kurokawa.localsend.expressive.platform.loadInstalledApps
import dev.kurokawa.localsend.expressive.platform.platformDeviceType
import dev.kurokawa.localsend.expressive.platform.quickShareRequests
import dev.kurokawa.localsend.expressive.platform.rememberClipboardText
import dev.kurokawa.localsend.expressive.platform.rustCoreActive
import dev.kurokawa.localsend.expressive.platform.wifiQrPayload
import dev.kurokawa.localsend.expressive.platform.rememberFilePickerLauncher
import dev.kurokawa.localsend.expressive.platform.rememberFolderPickerLauncher
import dev.kurokawa.localsend.expressive.platform.rememberMediaPickerLauncher
import dev.kurokawa.localsend.expressive.platform.sharedFilesFlow
import dev.kurokawa.localsend.expressive.platform.supportsQuickShareTile
import dev.kurokawa.localsend.expressive.platform.systemLanguageCode
import dev.kurokawa.localsend.expressive.protocol.DeviceType
import dev.kurokawa.localsend.expressive.protocol.ProtocolConstants
import dev.kurokawa.localsend.expressive.theme.LocalSendExpressiveTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private enum class Tab { Send, Receive, Settings }

// ── UI strings (language setting: system / English / Español) ──────────────

private data class UiStrings(
    val send: String,
    val receive: String,
    val settings: String,
    val nearbyDevices: String,
    val selection: String,
    val readyToSend: String,
    val history: String,
    val searching: String,
    val troubleshoot: String,
)

private val EnStrings = UiStrings(
    send = "Send", receive = "Receive", settings = "Settings",
    nearbyDevices = "Nearby devices", selection = "Selection",
    readyToSend = "Ready to send", history = "History",
    searching = "Searching for devices…", troubleshoot = "Troubleshoot",
)

private val EsStrings = UiStrings(
    send = "Enviar", receive = "Recibir", settings = "Ajustes",
    nearbyDevices = "Dispositivos cercanos", selection = "Selección",
    readyToSend = "Listo para enviar", history = "Historial",
    searching = "Buscando dispositivos…", troubleshoot = "Solución de problemas",
)

private val LocalStrings = staticCompositionLocalOf { EnStrings }

private fun stringsFor(language: String): UiStrings = when (language) {
    "es" -> EsStrings
    "en" -> EnStrings
    else -> if (systemLanguageCode() == "es") EsStrings else EnStrings
}

// ── Root + splash ──────────────────────────────────────────────────────────

@Composable
fun AppRoot(controller: LocalSendController) {
    val settings by controller.settings.collectAsState()
    LocalSendExpressiveTheme(settings) {
        CompositionLocalProvider(LocalStrings provides stringsFor(settings.language)) {
            var showSplash by remember { mutableStateOf(true) }
            AppScaffold(controller, settings)
            if (showSplash && settings.enableAnimations) {
                SplashOverlay { showSplash = false }
            }
        }
    }
}

/**
 * In-app animated splash in the Minus pattern: the mark springs in, the
 * wordmark follows. (The system splash hands off to this; v0.3.0 relied on
 * the system splash alone, which Ernest never saw animate.)
 */
@Composable
private fun SplashOverlay(onDone: () -> Unit) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(950, easing = FastOutSlowInEasing))
        delay(300)
        onDone()
    }
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(30.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(104.dp)
                        .graphicsLayer {
                            val p = progress.value
                            scaleX = 0.55f + 0.45f * p
                            scaleY = 0.55f + 0.45f * p
                            alpha = p
                            rotationZ = -8f * (1f - p)
                        },
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.Send,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(52.dp),
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.graphicsLayer {
                        alpha = progress.value
                        translationY = 24f * (1f - progress.value)
                    },
                ) {
                    Text(
                        "LSEx",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        "LocalSend Expressive",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppScaffold(controller: LocalSendController, settings: AppSettings) {
    val strings = LocalStrings.current
    DisposableEffect(controller) {
        controller.start()
        onDispose { controller.stop() }
    }
    val local by controller.localDevice.collectAsState()
    val devices by controller.devices.collectAsState()
    val offer by controller.incomingOffer.collectAsState()

    var tab by remember { mutableStateOf(Tab.Send) }

    // QS-tile quick share: jump to Send and let that screen open its picker.
    val quickShareCount by quickShareRequests().collectAsState()
    LaunchedEffect(quickShareCount) {
        if (quickShareCount > 0) tab = Tab.Send
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LSEx", style = MaterialTheme.typography.headlineMedium) },
                actions = {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.padding(end = 12.dp),
                    ) {
                        Text(
                            text = local.alias,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tab == Tab.Send,
                    onClick = { tab = Tab.Send },
                    icon = { Icon(Icons.Filled.Send, contentDescription = null) },
                    label = { Text(strings.send) },
                )
                NavigationBarItem(
                    selected = tab == Tab.Receive,
                    onClick = { tab = Tab.Receive },
                    icon = { Icon(Icons.Filled.Download, contentDescription = null) },
                    label = { Text(strings.receive) },
                )
                NavigationBarItem(
                    selected = tab == Tab.Settings,
                    onClick = { tab = Tab.Settings },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                    label = { Text(strings.settings) },
                )
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (tab) {
                Tab.Send -> SendScreen(controller, devices, settings)
                Tab.Receive -> ReceiveScreen(controller, local, settings)
                Tab.Settings -> SettingsScreen(controller, settings, local)
            }
        }
    }

    offer?.let { IncomingOfferDialog(controller, it) }
}

// ── Send ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SendScreen(
    controller: LocalSendController,
    devices: List<DiscoveredDevice>,
    settings: AppSettings,
) {
    val strings = LocalStrings.current
    var selectedFingerprint by remember { mutableStateOf<String?>(null) }
    var pickedFiles by remember { mutableStateOf<List<PickedFile>>(emptyList()) }
    var pin by remember { mutableStateOf("") }
    var showTextDialog by remember { mutableStateOf(false) }
    var prefillText by remember { mutableStateOf("") }
    var showIpDialog by remember { mutableStateOf(false) }
    var showAppDialog by remember { mutableStateOf(false) }
    var showTroubleshoot by remember { mutableStateOf(false) }
    var infoDevice by remember { mutableStateOf<DiscoveredDevice?>(null) }
    var pasteNote by remember { mutableStateOf<String?>(null) }
    var lastText by remember { mutableStateOf("") }
    val sendState by controller.sendState.collectAsState()
    val outbox by controller.outbox.collectAsState()
    val scanning by controller.scanning.collectAsState()
    val picker = rememberFilePickerLauncher { pickedFiles = pickedFiles + it }
    val mediaPicker = rememberMediaPickerLauncher { pickedFiles = pickedFiles + it }
    val folderPicker = rememberFolderPickerLauncher { pickedFiles = pickedFiles + it }
    val clipboardText = rememberClipboardText()
    val selected = devices.firstOrNull { it.fingerprint == selectedFingerprint }
    val isMobile = platformDeviceType() == DeviceType.mobile

    // Files arriving from the Android share sheet join the selection.
    val sharedFiles by sharedFilesFlow().collectAsState()
    LaunchedEffect(sharedFiles) {
        if (sharedFiles.isNotEmpty()) {
            pickedFiles = pickedFiles + sharedFiles
            clearSharedFiles()
        }
    }

    // Opening Send starts an active scan burst (the discovery timeout).
    LaunchedEffect(Unit) { controller.refreshDiscovery() }

    // QS tile: open straight into the file picker, once per invocation.
    val quickShareCount by quickShareRequests().collectAsState()
    LaunchedEffect(quickShareCount) {
        if (quickShareCount > 0) {
            picker()
            clearQuickShareRequest()
        }
    }

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(strings.nearbyDevices, style = MaterialTheme.typography.headlineMedium)
                    Text(
                        "Tap a device, then choose what to send.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = { controller.refreshDiscovery() }) {
                    if (scanning) {
                        LoadingIndicator(Modifier.size(22.dp))
                    } else {
                        Icon(Icons.Filled.Refresh, contentDescription = "Search again")
                    }
                }
            }
        }
        if (devices.isEmpty()) {
            item {
                Column(
                    Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    LoadingIndicator(Modifier.size(56.dp))
                    Text(
                        strings.searching,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TextButton(onClick = { showIpDialog = true }) {
                        Text("Add by IP address instead")
                    }
                    TextButton(onClick = { showTroubleshoot = true }) {
                        Text(strings.troubleshoot)
                    }
                }
            }
        } else {
            items(devices, key = { it.fingerprint }) { device ->
                DeviceCard(
                    device = device,
                    isSelected = device.fingerprint == selectedFingerprint,
                    isFavorite = device.fingerprint in settings.favorites,
                    onClick = { selectedFingerprint = device.fingerprint },
                    onToggleFavorite = { controller.toggleFavorite(device.fingerprint) },
                    onInfo = { infoDevice = device },
                )
            }
        }

        item {
            Text(strings.selection, style = MaterialTheme.typography.titleMedium)
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SelectionTile(
                        icon = Icons.Filled.AttachFile, label = "File",
                        modifier = Modifier.weight(1f),
                    ) { picker() }
                    SelectionTile(
                        icon = Icons.Filled.Image, label = "Media",
                        modifier = Modifier.weight(1f),
                    ) { mediaPicker() }
                    SelectionTile(
                        icon = Icons.Filled.ContentPaste, label = "Paste",
                        modifier = Modifier.weight(1f),
                    ) {
                        val text = clipboardText()
                        when {
                            text.isNullOrBlank() -> pasteNote = "The clipboard is empty."
                            selected != null -> {
                                lastText = text
                                controller.sendText(selected, text)
                                pasteNote = "Clipboard sent to ${selected.info.alias}."
                            }
                            else -> {
                                prefillText = text
                                showTextDialog = true
                            }
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SelectionTile(
                        icon = Icons.Filled.Message, label = "Text",
                        modifier = Modifier.weight(1f),
                    ) {
                        prefillText = ""
                        showTextDialog = true
                    }
                    SelectionTile(
                        icon = Icons.Filled.Folder, label = "Folder",
                        modifier = Modifier.weight(1f),
                    ) { folderPicker() }
                    if (isMobile) {
                        SelectionTile(
                            icon = Icons.Filled.Apps, label = "App",
                            modifier = Modifier.weight(1f),
                        ) { showAppDialog = true }
                    } else {
                        SelectionTile(
                            icon = Icons.Filled.Lan, label = "IP address",
                            modifier = Modifier.weight(1f),
                        ) { showIpDialog = true }
                    }
                }
                pasteNote?.let { note ->
                    Text(
                        note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        if (pickedFiles.isNotEmpty()) {
            item {
                Text(strings.readyToSend, style = MaterialTheme.typography.titleMedium)
            }
            item {
                Card(
                    Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    ),
                ) {
                    Column {
                        pickedFiles.forEachIndexed { index, file ->
                            if (index > 0) HorizontalDivider()
                            Row(
                                Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        file.displayPath ?: file.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        formatSize(file.size),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                IconButton(onClick = { pickedFiles = pickedFiles - file }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Remove")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Text sends keep their own ephemeral card; file sends live in the
        // outbox section below (persistent, retryable per file).
        val state = sendState
        if (state != null && state.jobId == null) {
            item {
                Card(
                    Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    ),
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            "Sending to ${state.target.info.alias}",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        state.files.forEach { FileProgressRow(it) }
                        state.error?.let { error ->
                            Text(
                                error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                        if (state.needsPin) {
                            OutlinedTextField(
                                value = pin,
                                onValueChange = { pin = it },
                                label = { Text("PIN") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Button(
                                onClick = {
                                    controller.sendText(state.target, lastText, pin)
                                },
                                enabled = lastText.isNotBlank(),
                                modifier = Modifier.fillMaxWidth(),
                            ) { Text("Retry with PIN") }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (state.active) {
                                OutlinedButton(onClick = { controller.cancelSend() }) {
                                    Text("Cancel")
                                }
                            } else {
                                OutlinedButton(onClick = { controller.clearSendState() }) {
                                    Text("Dismiss")
                                }
                            }
                        }
                    }
                }
            }
        } else if (pickedFiles.isNotEmpty()) {
            item {
                Button(
                    onClick = { selected?.let { controller.sendFiles(it, pickedFiles) } },
                    enabled = selected != null,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Icon(Icons.Filled.Send, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (selected != null) {
                            "Send to ${selected.info.alias}"
                        } else {
                            "Pick a device above"
                        },
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }

        if (outbox.isNotEmpty()) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Outbox",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                    if (outbox.any { it.hasFailures }) {
                        TextButton(onClick = { controller.retryAllFailed() }) {
                            Icon(Icons.Filled.Replay, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Retry all")
                        }
                    }
                }
                Text(
                    "Sends are saved here — retry the whole job or a single file, " +
                        "even after closing the app.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            items(outbox, key = { it.id }) { job ->
                OutboxJobCard(controller, job)
            }
        }
    }

    if (showTextDialog) {
        TextMessageDialog(
            target = selected,
            initialText = prefillText,
            onDismiss = { showTextDialog = false },
            onSend = { text ->
                lastText = text
                selected?.let { controller.sendText(it, text) }
                showTextDialog = false
            },
        )
    }
    if (showIpDialog) {
        IpAddressDialog(
            onDismiss = { showIpDialog = false },
            onProbe = { ip ->
                controller.probeDevice(ip)
                showIpDialog = false
            },
        )
    }
    if (showAppDialog) {
        AppPickerDialog(
            onDismiss = { showAppDialog = false },
            onPick = { app ->
                pickedFiles = pickedFiles + PickedFile(
                    handle = app.apkHandle,
                    name = "${app.label}.apk",
                    size = app.apkSize,
                )
                showAppDialog = false
            },
        )
    }
    if (showTroubleshoot) {
        TroubleshootDialog { showTroubleshoot = false }
    }
    infoDevice?.let { device ->
        DeviceInfoDialog(
            device = device,
            isFavorite = device.fingerprint in settings.favorites,
            onToggleFavorite = { controller.toggleFavorite(device.fingerprint) },
            onDismiss = { infoDevice = null },
        )
    }
}

@Composable
private fun SelectionTile(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(88.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Column(
            Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(26.dp),
            )
            Spacer(Modifier.height(6.dp))
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun DeviceCard(
    device: DiscoveredDevice,
    isSelected: Boolean,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onInfo: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    // Inside the colored (selected) container every text/icon uses the
    // matching on-container color — v0.3.0 kept onSurfaceVariant subtitles
    // there, which washed out on the monochrome theme.
    val subtitleColor = if (isSelected) {
        scheme.onPrimaryContainer.copy(alpha = 0.78f)
    } else {
        scheme.onSurfaceVariant
    }
    Card(
        onClick = onClick,
        colors = if (isSelected) {
            CardDefaults.cardColors(
                containerColor = scheme.primaryContainer,
                contentColor = scheme.onPrimaryContainer,
            )
        } else {
            CardDefaults.cardColors(containerColor = scheme.surfaceContainerHigh)
        },
        shape = if (isSelected) MaterialTheme.shapes.extraLarge else MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = CircleShape,
                color = if (isSelected) scheme.primary else scheme.secondaryContainer,
                modifier = Modifier.size(48.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        deviceIcon(device.info.deviceType),
                        contentDescription = null,
                        tint = if (isSelected) scheme.onPrimary else scheme.onSecondaryContainer,
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    device.info.alias,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "${device.info.deviceModel ?: device.info.deviceType?.name ?: "device"} · ${device.ip}",
                    style = MaterialTheme.typography.bodySmall,
                    color = subtitleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (isSelected) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = "Selected",
                    tint = scheme.primary,
                )
            }
            IconButton(onClick = onInfo) {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = "Device info",
                    tint = subtitleColor,
                )
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    if (isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) scheme.primary else subtitleColor,
                )
            }
        }
    }
}

// ── Receive ────────────────────────────────────────────────────────────────

@Composable
private fun ReceiveScreen(
    controller: LocalSendController,
    local: LocalDevice,
    settings: AppSettings,
) {
    val strings = LocalStrings.current
    val progress by controller.incomingProgress.collectAsState()
    val history by controller.history.collectAsState()
    val lastSavedTo by controller.lastSavedTo.collectAsState()
    val localIp by controller.localIpAddress.collectAsState()
    val webShareFiles by controller.webShareFiles.collectAsState()
    val webSharePicker = rememberFilePickerLauncher { controller.setWebShareFiles(it) }

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
    ) {
        item {
            Card(
                Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            ) {
                Column(
                    Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        if (settings.serverEnabled) "Ready to receive" else "Server is off",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(local.alias, style = MaterialTheme.typography.headlineLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoChip("Port ${local.port}")
                        InfoChip(if (local.protocol == "https") "HTTPS" else "HTTP")
                        InfoChip("Protocol ${ProtocolConstants.VERSION}")
                    }
                    Text(
                        "Fingerprint ${local.fingerprint.take(16)}…",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    localIp?.let { ip ->
                        Text(
                            "This device: $ip:${local.port}",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
        item {
            ConnectivityCard(controller, local, localIp)
        }
        lastSavedTo?.let { saved ->
            item {
                Text(
                    "Last file saved to $saved",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        item {
            GroupCard {
                SettingsSwitch(
                    title = "Quick Save",
                    subtitle = "Accept every incoming transfer without asking",
                    checked = settings.quickSave,
                    onCheckedChange = {
                        controller.updateSettings(
                            settings.copy(
                                quickSave = it,
                                autoAcceptFavorites = if (it) false else settings.autoAcceptFavorites,
                            ),
                        )
                    },
                )
                HorizontalDivider()
                SettingsSwitch(
                    title = "Quick Save from Favorites",
                    subtitle = "Skip the accept prompt for favorite devices",
                    checked = settings.autoAcceptFavorites,
                    onCheckedChange = {
                        controller.updateSettings(
                            settings.copy(
                                autoAcceptFavorites = it,
                                quickSave = if (it) false else settings.quickSave,
                            ),
                        )
                    },
                )
                HorizontalDivider()
                SettingsSwitch(
                    title = "Auto Finish",
                    subtitle = "Close a receive session as soon as it completes",
                    checked = settings.autoFinish,
                    onCheckedChange = {
                        controller.updateSettings(settings.copy(autoFinish = it))
                    },
                )
            }
        }
        item {
            Text("Share via link", style = MaterialTheme.typography.titleMedium)
        }
        item {
            GroupCard {
                if (!settings.downloadApi) {
                    SettingsSwitch(
                        title = "Web sharing is off",
                        subtitle = "Serve files to browsers on this network (Download API)",
                        checked = false,
                        onCheckedChange = {
                            controller.updateSettings(settings.copy(downloadApi = true))
                        },
                    )
                } else {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        val scheme = if (settings.https) "https" else "http"
                        val url = "$scheme://${localIp ?: "this-device"}:${local.port}/"
                        Text(
                            "Browsers on your network can open:",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(url, style = MaterialTheme.typography.titleMedium)
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            QrCode(url)
                        }
                        if (webShareFiles.isEmpty()) {
                            Text(
                                "No files are being shared yet.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        } else {
                            webShareFiles.forEach { file ->
                                Text(
                                    "• ${file.displayPath ?: file.name} · ${formatSize(file.size)}",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { webSharePicker() }) { Text("Choose files") }
                            if (webShareFiles.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = { controller.setWebShareFiles(emptyList()) },
                                ) { Text("Clear") }
                            }
                        }
                    }
                    HorizontalDivider()
                    SettingsSwitch(
                        title = "Auto-accept web uploads",
                        subtitle = "Skip the prompt for files uploaded from the web page",
                        checked = settings.shareViaLinkAutoAccept,
                        onCheckedChange = {
                            controller.updateSettings(
                                settings.copy(shareViaLinkAutoAccept = it),
                            )
                        },
                    )
                    HorizontalDivider()
                    SettingsSwitch(
                        title = "Download API",
                        subtitle = "Turn web sharing off",
                        checked = true,
                        onCheckedChange = {
                            controller.updateSettings(settings.copy(downloadApi = false))
                        },
                    )
                }
            }
        }
        if (progress.isNotEmpty()) {
            item {
                Text("Receiving", style = MaterialTheme.typography.titleMedium)
            }
            item {
                GroupCard {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        progress.forEach { FileProgressRow(it) }
                    }
                }
            }
        }
        if (history.isNotEmpty()) {
            item {
                Text(strings.history, style = MaterialTheme.typography.titleMedium)
            }
            item {
                GroupCard {
                    history.forEachIndexed { index, entry ->
                        if (index > 0) HorizontalDivider()
                        HistoryRow(entry)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun QrCode(content: String) {
    val matrix = remember(content) {
        runCatching {
            com.google.zxing.qrcode.QRCodeWriter().encode(
                content, com.google.zxing.BarcodeFormat.QR_CODE, 0, 0,
            )
        }.getOrNull()
    }
    val dark = MaterialTheme.colorScheme.onSurface
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        modifier = Modifier.size(176.dp),
    ) {
        Canvas(Modifier.fillMaxSize().padding(10.dp)) {
            matrix?.let { m ->
                val cell = size.minDimension / m.width
                for (x in 0 until m.width) {
                    for (y in 0 until m.height) {
                        if (m[x, y]) {
                            drawRect(
                                color = Color.Black,
                                topLeft = androidx.compose.ui.geometry.Offset(x * cell, y * cell),
                                size = androidx.compose.ui.geometry.Size(cell, cell),
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Settings (full parity with the official app) ───────────────────────────

@Composable
private fun SettingsScreen(
    controller: LocalSendController,
    settings: AppSettings,
    local: LocalDevice,
) {
    val strings = LocalStrings.current
    var advanced by remember { mutableStateOf(false) }
    var alias by remember(settings.alias) { mutableStateOf(settings.alias) }
    var port by remember(settings.port) { mutableStateOf(settings.port.toString()) }
    var pin by remember(settings.pin) { mutableStateOf(settings.pin) }
    var destination by remember(settings.destinationFolder) {
        mutableStateOf(settings.destinationFolder)
    }
    var multicast by remember(settings.multicastAddress) {
        mutableStateOf(settings.multicastAddress)
    }
    var timeout by remember(settings.discoveryTimeoutSec) {
        mutableStateOf(settings.discoveryTimeoutSec.toString())
    }
    var whitelist by remember(settings.networkWhitelist) {
        mutableStateOf(settings.networkWhitelist)
    }
    var blacklist by remember(settings.networkBlacklist) {
        mutableStateOf(settings.networkBlacklist)
    }
    val isDesktop = platformDeviceType() == DeviceType.desktop

    fun saveNetworkBasics() {
        controller.updateSettings(
            settings.copy(
                alias = alias.trim(),
                port = port.toIntOrNull() ?: ProtocolConstants.DEFAULT_PORT,
                pin = pin,
            ),
        )
    }

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
    ) {
        item {
            Text(strings.settings, style = MaterialTheme.typography.headlineMedium)
        }

        item { SectionTitle("General") }
        item {
            GroupCard {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Text("Theme", style = MaterialTheme.typography.titleSmall)
                    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                        listOf("system" to "System", "light" to "Light", "dark" to "Dark")
                            .forEachIndexed { index, (value, label) ->
                                SegmentedButton(
                                    selected = settings.themeMode == value,
                                    onClick = {
                                        controller.updateSettings(
                                            settings.copy(themeMode = value),
                                        )
                                    },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index, count = 3,
                                    ),
                                    modifier = Modifier.weight(1f),
                                ) { Text(label) }
                            }
                    }
                    Text("Colors", style = MaterialTheme.typography.titleSmall)
                    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                        listOf("dynamic" to "Wallpaper", "seed" to "LSEx")
                            .forEachIndexed { index, (value, label) ->
                                SegmentedButton(
                                    selected = settings.colorMode == value,
                                    onClick = {
                                        controller.updateSettings(
                                            settings.copy(colorMode = value),
                                        )
                                    },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index, count = 2,
                                    ),
                                    modifier = Modifier.weight(1f),
                                ) { Text(label) }
                            }
                    }
                    Text("Language", style = MaterialTheme.typography.titleSmall)
                    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                        listOf("system" to "System", "en" to "EN", "es" to "ES")
                            .forEachIndexed { index, (value, label) ->
                                SegmentedButton(
                                    selected = settings.language == value,
                                    onClick = {
                                        controller.updateSettings(
                                            settings.copy(language = value),
                                        )
                                    },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index, count = 3,
                                    ),
                                    modifier = Modifier.weight(1f),
                                ) { Text(label) }
                            }
                    }
                }
                HorizontalDivider()
                SettingsSwitch(
                    title = "Animations",
                    subtitle = "Splash and expressive motion",
                    checked = settings.enableAnimations,
                    onCheckedChange = {
                        controller.updateSettings(settings.copy(enableAnimations = it))
                    },
                )
                if (supportsQuickShareTile()) {
                    HorizontalDivider()
                    Column(Modifier.padding(16.dp)) {
                        Text("Quick share tile", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "Add the LSEx tile from your Quick Settings panel editor " +
                                "to start a share in one tap.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                HorizontalDivider()
                SettingsSwitch(
                    title = "Advanced settings",
                    subtitle = "Show the advanced sections, like the official app",
                    checked = advanced,
                    onCheckedChange = { advanced = it },
                )
            }
        }

        if (isDesktop) {
            item { SectionTitle("Desktop") }
            item {
                GroupCard {
                    SettingsSwitch(
                        title = "Always on top",
                        subtitle = "Keep the window above other windows",
                        checked = settings.alwaysOnTop,
                        onCheckedChange = {
                            controller.updateSettings(settings.copy(alwaysOnTop = it))
                        },
                    )
                    HorizontalDivider()
                    SettingsSwitch(
                        title = "Save window placement",
                        subtitle = "Restore window position and size on launch",
                        checked = settings.saveWindowPlacement,
                        onCheckedChange = {
                            controller.updateSettings(settings.copy(saveWindowPlacement = it))
                        },
                    )
                    HorizontalDivider()
                    SettingsSwitch(
                        title = "Minimize to tray",
                        subtitle = "Closing the window keeps LSEx running in the tray",
                        checked = settings.minimizeToTray,
                        onCheckedChange = {
                            controller.updateSettings(settings.copy(minimizeToTray = it))
                        },
                    )
                }
            }
        }

        item { SectionTitle("Receive") }
        item {
            GroupCard {
                SettingsSwitch(
                    title = "Quick Save",
                    subtitle = "Accept every incoming transfer without asking",
                    checked = settings.quickSave,
                    onCheckedChange = {
                        controller.updateSettings(
                            settings.copy(
                                quickSave = it,
                                autoAcceptFavorites = if (it) false else settings.autoAcceptFavorites,
                            ),
                        )
                    },
                )
                HorizontalDivider()
                SettingsSwitch(
                    title = "Quick Save from Favorites",
                    subtitle = "Skip the accept prompt for favorite devices",
                    checked = settings.autoAcceptFavorites,
                    onCheckedChange = {
                        controller.updateSettings(
                            settings.copy(
                                autoAcceptFavorites = it,
                                quickSave = if (it) false else settings.quickSave,
                            ),
                        )
                    },
                )
                HorizontalDivider()
                SettingsSwitch(
                    title = "Require PIN",
                    subtitle = if (settings.pin.isEmpty()) {
                        "Senders must enter a PIN (set one below)"
                    } else {
                        "Senders must enter the PIN"
                    },
                    checked = settings.pin.isNotEmpty(),
                    onCheckedChange = { enabled ->
                        controller.updateSettings(
                            settings.copy(pin = if (enabled) (pin.ifEmpty { "1234" }) else ""),
                        )
                    },
                )
                HorizontalDivider()
                SettingsSwitch(
                    title = "Save to Gallery",
                    subtitle = "Photos and videos go to Pictures/Movies instead of Downloads",
                    checked = settings.saveToGallery,
                    onCheckedChange = {
                        controller.updateSettings(settings.copy(saveToGallery = it))
                    },
                )
                HorizontalDivider()
                SettingsSwitch(
                    title = "Auto Finish",
                    subtitle = "Close a receive session as soon as it completes",
                    checked = settings.autoFinish,
                    onCheckedChange = {
                        controller.updateSettings(settings.copy(autoFinish = it))
                    },
                )
                HorizontalDivider()
                SettingsSwitch(
                    title = "Save to History",
                    subtitle = "Record finished transfers in the history list",
                    checked = settings.saveToHistory,
                    onCheckedChange = {
                        controller.updateSettings(settings.copy(saveToHistory = it))
                    },
                )
                if (advanced) {
                    HorizontalDivider()
                    SettingsSwitch(
                        title = "Verify checksums",
                        subtitle = "Reject files whose SHA-256 doesn't match the sender's",
                        checked = settings.verifyChecksums,
                        onCheckedChange = {
                            controller.updateSettings(settings.copy(verifyChecksums = it))
                        },
                    )
                }
                HorizontalDivider()
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = destination,
                        onValueChange = { destination = it },
                        label = {
                            Text(
                                if (isDesktop) {
                                    "Destination folder (empty = ~/Downloads/LocalSend Expressive)"
                                } else {
                                    "Destination subfolder under Download (empty = Download)"
                                },
                            )
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedButton(
                        onClick = {
                            controller.updateSettings(
                                settings.copy(destinationFolder = destination.trim()),
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text("Save destination") }
                }
            }
        }

        if (advanced) {
            item { SectionTitle("Send") }
            item {
                GroupCard {
                    SettingsSwitch(
                        title = "Share via link: auto-accept",
                        subtitle = "Auto-accept uploads from the web page",
                        checked = settings.shareViaLinkAutoAccept,
                        onCheckedChange = {
                            controller.updateSettings(
                                settings.copy(shareViaLinkAutoAccept = it),
                            )
                        },
                    )
                    HorizontalDivider()
                    SettingsSwitch(
                        title = "Create checksums",
                        subtitle = "Attach a SHA-256 to every file you send (slower for big files)",
                        checked = settings.createChecksums,
                        onCheckedChange = {
                            controller.updateSettings(settings.copy(createChecksums = it))
                        },
                    )
                    HorizontalDivider()
                    SettingsSwitch(
                        title = "Auto-retry sends",
                        subtitle = "Retry a dropped send a few times before parking it in the outbox",
                        checked = settings.autoRetry,
                        onCheckedChange = {
                            controller.updateSettings(settings.copy(autoRetry = it))
                        },
                    )
                    HorizontalDivider()
                    SettingsSwitch(
                        title = "Resumable chunked sends",
                        subtitle = "With LSEx peers, send in 1 MiB chunks that resume after a disconnection",
                        checked = settings.chunkedTransfers,
                        onCheckedChange = {
                            controller.updateSettings(settings.copy(chunkedTransfers = it))
                        },
                    )
                }
            }
        }

        item { SectionTitle("Network") }
        item {
            GroupCard {
                SettingsSwitch(
                    title = "Server",
                    subtitle = if (settings.serverEnabled) {
                        "Receiving on port ${local.port}"
                    } else {
                        "Off — this device cannot receive"
                    },
                    checked = settings.serverEnabled,
                    onCheckedChange = {
                        controller.updateSettings(settings.copy(serverEnabled = it))
                    },
                )
                HorizontalDivider()
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedTextField(
                        value = alias,
                        onValueChange = { alias = it },
                        label = { Text("Device name (alias)") },
                        placeholder = { Text(local.alias) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = port,
                        onValueChange = { port = it.filter { c -> c.isDigit() }.take(5) },
                        label = { Text("Port") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = pin,
                        onValueChange = { pin = it.filter { c -> c.isDigit() }.take(8) },
                        label = { Text("PIN (empty = no PIN)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Button(
                        onClick = { saveNetworkBasics() },
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text("Save name, port and PIN") }
                    Text(
                        "Name, port, PIN and HTTPS changes restart the server.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                HorizontalDivider()
                SettingsSwitch(
                    title = "Encryption (HTTPS)",
                    subtitle = "Self-signed certificate; the fingerprint is the cert SHA-256",
                    checked = settings.https,
                    onCheckedChange = {
                        controller.updateSettings(settings.copy(https = it))
                    },
                )
                HorizontalDivider()
                SettingsSwitch(
                    title = "Download API",
                    subtitle = "Share via link: serve files to browsers on the network",
                    checked = settings.downloadApi,
                    onCheckedChange = {
                        controller.updateSettings(settings.copy(downloadApi = it))
                    },
                )
                if (advanced) {
                    HorizontalDivider()
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            "Device model: ${local.deviceModel ?: "—"} · Type: ${local.deviceType?.name ?: "—"}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        OutlinedTextField(
                            value = timeout,
                            onValueChange = { timeout = it.filter { c -> c.isDigit() }.take(2) },
                            label = { Text("Discovery timeout (seconds)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = multicast,
                            onValueChange = { multicast = it },
                            label = { Text("Multicast group (empty = 224.0.0.167)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = whitelist,
                            onValueChange = { whitelist = it },
                            label = { Text("Network whitelist (IPs/CIDRs, comma-separated)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = blacklist,
                            onValueChange = { blacklist = it },
                            label = { Text("Network blacklist (IPs/CIDRs, comma-separated)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedButton(
                            onClick = {
                                controller.updateSettings(
                                    settings.copy(
                                        discoveryTimeoutSec = timeout.toIntOrNull()
                                            ?.coerceIn(2, 30) ?: 10,
                                        multicastAddress = multicast.trim(),
                                        networkWhitelist = whitelist.trim(),
                                        networkBlacklist = blacklist.trim(),
                                    ),
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text("Save advanced network settings") }
                    }
                }
            }
        }

        if (settings.favorites.isNotEmpty()) {
            item { SectionTitle("Favorites") }
            item {
                GroupCard {
                    settings.favorites.toList().forEachIndexed { index, fingerprint ->
                        if (index > 0) HorizontalDivider()
                        Row(
                            Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "${fingerprint.take(16)}…",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                            )
                            IconButton(
                                onClick = { controller.toggleFavorite(fingerprint) },
                            ) {
                                Icon(Icons.Filled.Close, contentDescription = "Remove favorite")
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                "LSEx 0.4.0 · LocalSend Protocol ${ProtocolConstants.VERSION} · " +
                    (if (rustCoreActive()) {
                        "Rust core active"
                    } else {
                        "Rust core not loaded (Kotlin fallback)"
                    }) +
                    " · Unofficial Material 3 Expressive rewrite — not affiliated with the LocalSend project.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
    )
}

/** A grouped settings-style card: rows separated by dividers, large shape. */
@Composable
private fun GroupCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    ) {
        Column(content = content)
    }
}

@Composable
private fun SettingsSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

// ── Dialogs & rows ─────────────────────────────────────────────────────────

@Composable
private fun IncomingOfferDialog(controller: LocalSendController, offer: IncomingOffer) {
    var checked by remember(offer.sessionId) {
        mutableStateOf(offer.files.map { it.id }.toSet())
    }
    AlertDialog(
        onDismissRequest = { controller.respondOffer(null) },
        icon = {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                Icon(
                    Icons.Filled.Download,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp),
                )
            }
        },
        title = { Text("${offer.peer.alias} wants to send") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                offer.files.forEach { file ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = file.id in checked,
                            onCheckedChange = { on ->
                                checked = if (on) checked + file.id else checked - file.id
                            },
                        )
                        Column {
                            Text(
                                file.fileName,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                formatSize(file.size),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Total ${formatSize(offer.totalSize)} · from ${offer.peerIp}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { controller.respondOffer(checked) },
                enabled = checked.isNotEmpty(),
            ) { Text("Accept") }
        },
        dismissButton = {
            TextButton(onClick = { controller.respondOffer(null) }) { Text("Decline") }
        },
    )
}

@Composable
private fun TextMessageDialog(
    target: DiscoveredDevice?,
    initialText: String = "",
    onDismiss: () -> Unit,
    onSend: (String) -> Unit,
) {
    var text by remember { mutableStateOf(initialText) }
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                Icon(
                    Icons.Filled.Message,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp),
                )
            }
        },
        title = { Text("Send a text") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Message") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (target == null) {
                    Text(
                        "Pick a device first — the text goes to the selected device.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSend(text) },
                enabled = target != null && text.isNotBlank(),
            ) { Text("Send") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@Composable
private fun IpAddressDialog(
    onDismiss: () -> Unit,
    onProbe: (String) -> Unit,
) {
    var ip by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                Icon(
                    Icons.Filled.Lan,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp),
                )
            }
        },
        title = { Text("Add IP address") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = ip,
                    onValueChange = { ip = it },
                    label = { Text("IP address (optionally IP:port)") },
                    placeholder = { Text("192.168.1.50") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    "The device appears in the list once it answers.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onProbe(ip.trim()) },
                enabled = ip.isNotBlank(),
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@Composable
private fun AppPickerDialog(
    onDismiss: () -> Unit,
    onPick: (InstalledApp) -> Unit,
) {
    var apps by remember { mutableStateOf<List<InstalledApp>?>(null) }
    var query by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        apps = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
            loadInstalledApps()
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                Icon(
                    Icons.Filled.Apps,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp),
                )
            }
        },
        title = { Text("Send an app (APK)") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Search apps") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                val list = apps
                when {
                    list == null -> Box(
                        Modifier.fillMaxWidth().height(120.dp),
                        contentAlignment = Alignment.Center,
                    ) { LoadingIndicator() }

                    list.isEmpty() -> Text("No apps could be listed on this device.")
                    else -> LazyColumn(Modifier.height(320.dp)) {
                        items(
                            list.filter {
                                query.isBlank() || it.label.contains(query, ignoreCase = true)
                            },
                            key = { it.packageName },
                        ) { app ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        app.label,
                                        style = MaterialTheme.typography.bodyLarge,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        "${app.packageName} · ${formatSize(app.apkSize)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                                TextButton(onClick = { onPick(app) }) { Text("Add") }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
    )
}

@Composable
private fun DeviceInfoDialog(
    device: DiscoveredDevice,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onDismiss: () -> Unit,
) {
    val info = device.info
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                Icon(
                    deviceIcon(info.deviceType),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp),
                )
            }
        },
        title = { Text(info.alias) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                InfoRow("Model", info.deviceModel ?: "—")
                InfoRow("Type", info.deviceType?.name ?: "—")
                InfoRow("Address", "${device.ip}:${info.port}")
                InfoRow("Protocol", "${info.version} (${info.protocol})")
                InfoRow("Web share", if (info.download) "Available" else "Off")
                Text(
                    "Fingerprint",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    device.fingerprint.chunked(16).joinToString(" "),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Done") }
        },
        dismissButton = {
            TextButton(onClick = onToggleFavorite) {
                Text(if (isFavorite) "Unfavorite" else "Favorite")
            }
        },
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(84.dp),
        )
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun TroubleshootDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Troubleshoot discovery") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("• Both devices must be on the same Wi-Fi / LAN (not a guest network).")
                Text("• Allow LSEx through the firewall: UDP 224.0.0.167:53317 for discovery and TCP 53317 for transfers.")
                Text("• Turn off VPNs and \"AP isolation\" on the router — they block local discovery.")
                Text("• Keep the other device awake with LocalSend open, then hit the refresh button.")
                Text("• As a fallback, add the device by IP address (its address is shown on its Receive screen).")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("OK") }
        },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FileProgressRow(progress: FileProgress) {
    Column(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                progress.fileName,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            when (progress.status) {
                TransferStatus.DONE -> Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = "Done",
                    tint = MaterialTheme.colorScheme.primary,
                )
                TransferStatus.FAILED, TransferStatus.CANCELLED -> Icon(
                    Icons.Filled.Error,
                    contentDescription = "Failed",
                    tint = MaterialTheme.colorScheme.error,
                )
                else -> Text(
                    "${formatSize(progress.done)} / ${formatSize(progress.total)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (progress.status == TransferStatus.ACTIVE || progress.status == TransferStatus.WAITING) {
            LinearWavyProgressIndicator(
                progress = { progress.fraction },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun HistoryRow(entry: HistoryEntry) {
    Row(
        Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            if (entry.incoming) Icons.Filled.Download else Icons.Filled.Send,
            contentDescription = null,
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                entry.fileName,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                "${entry.peerAlias} · ${formatSize(entry.size)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Icon(
            if (entry.success) Icons.Filled.CheckCircle else Icons.Filled.Error,
            contentDescription = null,
            tint = if (entry.success) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            },
        )
    }
}

private fun deviceIcon(type: DeviceType?): ImageVector = when (type) {
    DeviceType.mobile -> Icons.Filled.PhoneAndroid
    DeviceType.web -> Icons.Filled.Language
    else -> Icons.Filled.Computer
}

private fun formatSize(bytes: Long): String = when {
    bytes < 1024 -> "$bytes B"
    bytes < 1024 * 1024 -> "${bytes / 1024} KB"
    bytes < 1024 * 1024 * 1024 -> "${"%.1f".format(bytes / 1024.0 / 1024.0)} MB"
    else -> "${"%.2f".format(bytes / 1024.0 / 1024.0 / 1024.0)} GB"
}

// ── Outbox (persistent sends: global + per-file retry) ─────────────────────

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun OutboxJobCard(controller: LocalSendController, job: OutboxJob) {
    var pin by remember(job.id) { mutableStateOf("") }
    val isActiveJob = job.files.any { it.status == TransferStatus.ACTIVE }
    val doneCount = job.files.count { it.status == TransferStatus.DONE }
    val failedCount = job.files.count { it.status == TransferStatus.FAILED }
    val summary = when {
        job.finished -> "Sent ${formatSize(job.files.sumOf { it.size })}"
        failedCount > 0 -> "$doneCount of ${job.files.size} sent · $failedCount failed"
        else -> "$doneCount of ${job.files.size} sent"
    }
    Card(
        Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "To ${job.targetAlias}",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (job.chunked) InfoChip("Resumable")
            }
            job.files.forEach { file ->
                OutboxFileRow(controller, job, file, enabled = !isActiveJob)
            }
            if (job.needsPin) {
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it },
                    label = { Text("PIN") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
                    onClick = { controller.retryOutboxJob(job.id, pin) },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Retry with PIN") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isActiveJob) {
                    OutlinedButton(onClick = { controller.cancelSend() }) {
                        Text("Cancel")
                    }
                } else {
                    if (!job.finished &&
                        job.files.any { it.status != TransferStatus.DONE }
                    ) {
                        OutlinedButton(onClick = { controller.retryOutboxJob(job.id) }) {
                            Icon(Icons.Filled.Replay, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Retry")
                        }
                    }
                    TextButton(onClick = { controller.dismissOutboxJob(job.id) }) {
                        Text(if (job.finished) "Dismiss" else "Remove")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun OutboxFileRow(
    controller: LocalSendController,
    job: OutboxJob,
    file: OutboxFile,
    enabled: Boolean,
) {
    Column(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    file.displayPath ?: file.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    when (file.status) {
                        TransferStatus.DONE -> formatSize(file.size)
                        TransferStatus.WAITING -> "Waiting · ${formatSize(file.size)}"
                        TransferStatus.CANCELLED -> "Cancelled · ${formatSize(file.size)}"
                        else ->
                            "${formatSize(file.confirmedBytes)} / ${formatSize(file.size)}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            when (file.status) {
                TransferStatus.DONE -> Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = "Done",
                    tint = MaterialTheme.colorScheme.primary,
                )
                TransferStatus.FAILED -> if (enabled) {
                    IconButton(onClick = { controller.retryOutboxFile(job.id, file.fileId) }) {
                        Icon(
                            Icons.Filled.Replay,
                            contentDescription = "Retry this file",
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                } else {
                    Icon(
                        Icons.Filled.Error,
                        contentDescription = "Failed",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
                TransferStatus.ACTIVE -> LoadingIndicator(Modifier.size(20.dp))
                else -> Unit
            }
        }
        if (file.status == TransferStatus.ACTIVE ||
            (file.confirmedBytes > 0 && file.status != TransferStatus.DONE)
        ) {
            LinearWavyProgressIndicator(
                progress = {
                    if (file.size > 0) {
                        (file.confirmedBytes.toFloat() / file.size).coerceIn(0f, 1f)
                    } else {
                        0f
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (file.status == TransferStatus.FAILED) {
            file.lastError?.let { error ->
                Text(
                    error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

// ── Connectivity shortcuts (Wi-Fi / hotspot / Wi-Fi Direct + QR) ───────────

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ConnectivityCard(
    controller: LocalSendController,
    local: LocalDevice,
    localIp: String?,
) {
    val connectivity = remember { ConnectivityController() }
    val connState by connectivity.state.collectAsState()
    val scope = rememberCoroutineScope()
    var showScanner by remember { mutableStateOf(false) }
    var scanNote by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) { connectivity.refresh() }

    val hotspot = connState.hotspot
    val direct = connState.wifiDirect
    val qrPayload = when {
        hotspot != null -> wifiQrPayload(hotspot.ssid, hotspot.password)
        direct != null -> wifiQrPayload(direct.ssid, direct.password)
        localIp != null -> "lsex://connect?host=$localIp&port=${local.port}"
        else -> null
    }
    val qrCaption = when {
        hotspot != null -> "Scan to join “${hotspot.ssid}”"
        direct != null -> "Scan to join “${direct.ssid}”"
        else -> "Scan with LSEx to connect to ${local.alias}"
    }
    val activePassword = hotspot?.password ?: direct?.password

    GroupCard {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Connectivity",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = { connectivity.refresh() }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh connectivity")
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ConnectivityTile(
                    icon = if (connState.wifiEnabled == false) {
                        Icons.Filled.WifiOff
                    } else {
                        Icons.Filled.Wifi
                    },
                    label = when (connState.wifiEnabled) {
                        true -> "WiFi on"
                        false -> "WiFi off"
                        null -> "WiFi"
                    },
                    active = connState.wifiEnabled == true,
                    modifier = Modifier.weight(1f),
                ) { connectivity.toggleWifi() }
                ConnectivityTile(
                    icon = Icons.Filled.WifiTethering,
                    label = "Hotspot",
                    active = hotspot != null,
                    modifier = Modifier.weight(1f),
                ) { connectivity.toggleHotspot() }
                ConnectivityTile(
                    icon = Icons.Filled.Router,
                    label = "WiFi Direct",
                    active = direct != null,
                    modifier = Modifier.weight(1f),
                ) { connectivity.toggleWifiDirect() }
            }
            if (connState.busy) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LoadingIndicator(Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Working…",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            qrPayload?.let { payload ->
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    QrCode(payload)
                }
                Text(
                    qrCaption,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (!activePassword.isNullOrEmpty()) {
                    Text(
                        "Password: $activePassword",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            OutlinedButton(
                onClick = { showScanner = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Filled.QrCodeScanner, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Scan QR — join WiFi, open a web share, connect")
            }
            connState.message?.let { message ->
                Text(
                    message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            scanNote?.let { note ->
                Text(
                    note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }

    if (showScanner) {
        AlertDialog(
            onDismissRequest = { showScanner = false },
            title = { Text("Scan a QR code") },
            text = {
                QrScannerView(
                    onCode = { code ->
                        showScanner = false
                        val wifi = parseWifiQr(code)
                        when {
                            wifi != null -> scope.launch {
                                scanNote = withContext(Dispatchers.IO) {
                                    joinWifiNetwork(wifi.first, wifi.second, wifi.third)
                                }
                            }
                            code.startsWith("http://") || code.startsWith("https://") -> {
                                openUrl(code)
                                scanNote = "Opened the link in your browser."
                            }
                            code.startsWith("lsex://") -> {
                                val params = code.substringAfter('?', "")
                                    .split('&')
                                    .mapNotNull { part ->
                                        val kv = part.split('=', limit = 2)
                                        if (kv.size == 2) kv[0] to kv[1] else null
                                    }
                                    .toMap()
                                val host = params["host"]
                                val port = params["port"]
                                if (host != null) {
                                    controller.probeDevice(
                                        if (port != null) "$host:$port" else host,
                                    )
                                    scanNote = "Connecting to $host… watch the device list."
                                } else {
                                    scanNote = "That LSEx code has no address in it."
                                }
                            }
                            else -> scanNote = "Scanned: ${code.take(80)}"
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(320.dp),
                )
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showScanner = false }) { Text("Close") }
            },
        )
    }
}

@Composable
private fun ConnectivityTile(
    icon: ImageVector,
    label: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    Card(
        onClick = onClick,
        modifier = modifier.height(76.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (active) scheme.primaryContainer else scheme.surfaceContainerHigh,
            contentColor = if (active) scheme.onPrimaryContainer else scheme.onSurface,
        ),
    ) {
        Column(
            Modifier.fillMaxSize().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (active) scheme.onPrimaryContainer else scheme.primary,
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.height(5.dp))
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}
