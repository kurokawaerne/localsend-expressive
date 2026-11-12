package dev.kurokawa.localsend.expressive.ui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
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
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.kurokawa.localsend.expressive.LocalDevice
import dev.kurokawa.localsend.expressive.app.AppSettings
import dev.kurokawa.localsend.expressive.app.FileProgress
import dev.kurokawa.localsend.expressive.app.HistoryEntry
import dev.kurokawa.localsend.expressive.app.IncomingOffer
import dev.kurokawa.localsend.expressive.app.LocalSendController
import dev.kurokawa.localsend.expressive.app.TransferStatus
import dev.kurokawa.localsend.expressive.discovery.DiscoveredDevice
import dev.kurokawa.localsend.expressive.platform.PickedFile
import dev.kurokawa.localsend.expressive.platform.clearSharedFiles
import dev.kurokawa.localsend.expressive.platform.rememberFilePickerLauncher
import dev.kurokawa.localsend.expressive.platform.sharedFilesFlow
import dev.kurokawa.localsend.expressive.protocol.DeviceType
import dev.kurokawa.localsend.expressive.protocol.ProtocolConstants
import dev.kurokawa.localsend.expressive.theme.LocalSendExpressiveTheme

private enum class Tab { Send, Receive, Settings }

@Composable
fun AppRoot(controller: LocalSendController) {
    val settings by controller.settings.collectAsState()
    LocalSendExpressiveTheme(settings) {
        AppScaffold(controller, settings)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppScaffold(controller: LocalSendController, settings: AppSettings) {
    DisposableEffect(controller) {
        controller.start()
        onDispose { controller.stop() }
    }
    val local by controller.localDevice.collectAsState()
    val devices by controller.devices.collectAsState()
    val offer by controller.incomingOffer.collectAsState()

    var tab by remember { mutableStateOf(Tab.Send) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LocalSend Expressive") },
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
                    label = { Text("Send") },
                )
                NavigationBarItem(
                    selected = tab == Tab.Receive,
                    onClick = { tab = Tab.Receive },
                    icon = { Icon(Icons.Filled.Download, contentDescription = null) },
                    label = { Text("Receive") },
                )
                NavigationBarItem(
                    selected = tab == Tab.Settings,
                    onClick = { tab = Tab.Settings },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                    label = { Text("Settings") },
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
    var selectedFingerprint by remember { mutableStateOf<String?>(null) }
    var pickedFiles by remember { mutableStateOf<List<PickedFile>>(emptyList()) }
    var pin by remember { mutableStateOf("") }
    var fabExpanded by remember { mutableStateOf(false) }
    var showTextDialog by remember { mutableStateOf(false) }
    var showIpDialog by remember { mutableStateOf(false) }
    val sendState by controller.sendState.collectAsState()
    val picker = rememberFilePickerLauncher { pickedFiles = it }
    val selected = devices.firstOrNull { it.fingerprint == selectedFingerprint }

    // Files arriving from the Android share sheet join the selection.
    val sharedFiles by sharedFilesFlow().collectAsState()
    LaunchedEffect(sharedFiles) {
        if (sharedFiles.isNotEmpty()) {
            pickedFiles = pickedFiles + sharedFiles
            clearSharedFiles()
        }
    }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Nearby devices", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        "Tap a device, then choose what to send.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (devices.isEmpty()) {
                item {
                    Column(
                        Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        LoadingIndicator(Modifier.size(64.dp))
                        Text(
                            "Searching for devices…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            "Not showing up? Add it by IP address from the + button.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
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
                    )
                }
            }

            if (pickedFiles.isNotEmpty()) {
                item {
                    Text("Ready to send", style = MaterialTheme.typography.titleMedium)
                }
                item {
                    Card(
                        Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                    ) {
                        Column {
                            pickedFiles.forEachIndexed { index, file ->
                                if (index > 0) HorizontalDivider()
                                Row(
                                    Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(file.name, style = MaterialTheme.typography.bodyLarge)
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

            val state = sendState
            if (state != null) {
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
                                        selected?.let { controller.sendFiles(it, pickedFiles, pin) }
                                    },
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
        }

        FloatingActionButtonMenu(
            expanded = fabExpanded,
            button = {
                ToggleFloatingActionButton(
                    checked = fabExpanded,
                    onCheckedChange = { fabExpanded = it },
                ) {
                    Icon(
                        if (fabExpanded) Icons.Filled.Close else Icons.Filled.Add,
                        contentDescription = "Send options",
                    )
                }
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        ) {
            FloatingActionButtonMenuItem(
                onClick = { fabExpanded = false; picker() },
                icon = { Icon(Icons.Filled.AttachFile, contentDescription = null) },
                text = { Text("Send files") },
            )
            FloatingActionButtonMenuItem(
                onClick = { fabExpanded = false; showTextDialog = true },
                icon = { Icon(Icons.Filled.Message, contentDescription = null) },
                text = { Text("Send a text") },
            )
            FloatingActionButtonMenuItem(
                onClick = { fabExpanded = false; showIpDialog = true },
                icon = { Icon(Icons.Filled.Lan, contentDescription = null) },
                text = { Text("Add IP address") },
            )
        }
    }

    if (showTextDialog) {
        TextMessageDialog(
            target = selected,
            onDismiss = { showTextDialog = false },
            onSend = { text ->
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
}

@Composable
private fun DeviceCard(
    device: DiscoveredDevice,
    isSelected: Boolean,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    Card(
        onClick = onClick,
        colors = if (isSelected) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        } else {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            )
        },
        shape = if (isSelected) {
            MaterialTheme.shapes.extraLarge
        } else {
            MaterialTheme.shapes.large
        },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = CircleShape,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                },
                modifier = Modifier.size(48.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        deviceIcon(device.info.deviceType),
                        contentDescription = null,
                        tint = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        },
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(device.info.alias, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${device.info.deviceModel ?: device.info.deviceType?.name ?: "device"} · ${device.ip}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (isSelected) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    if (isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
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
    val progress by controller.incomingProgress.collectAsState()
    val history by controller.history.collectAsState()
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
                ),
            ) {
                Column(
                    Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        "Ready to receive",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        local.alias,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoChip("Port ${local.port}")
                        InfoChip(if (local.protocol == "https") "HTTPS" else "HTTP")
                        InfoChip("Protocol ${ProtocolConstants.VERSION}")
                    }
                    Text(
                        "Fingerprint ${local.fingerprint.take(16)}…",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
        item {
            GroupCard {
                SettingsSwitch(
                    title = "Quick Save",
                    subtitle = "Accept every incoming transfer without asking",
                    checked = settings.quickSave,
                    onCheckedChange = {
                        controller.updateSettings(settings.copy(quickSave = it))
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
        if (progress.isNotEmpty()) {
            item {
                Text("Receiving", style = MaterialTheme.typography.titleMedium)
            }
            items(progress, key = { it.fileId }) { FileProgressRow(it) }
        }
        if (history.isNotEmpty()) {
            item {
                Text("History", style = MaterialTheme.typography.titleMedium)
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
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f),
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

// ── Settings ───────────────────────────────────────────────────────────────

@Composable
private fun SettingsScreen(
    controller: LocalSendController,
    settings: AppSettings,
    local: LocalDevice,
) {
    var alias by remember(settings.alias) { mutableStateOf(settings.alias) }
    var port by remember(settings.port) { mutableStateOf(settings.port.toString()) }
    var pin by remember(settings.pin) { mutableStateOf(settings.pin) }
    var destination by remember(settings.destinationFolder) {
        mutableStateOf(settings.destinationFolder)
    }
    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
    ) {
        item {
            Text("Settings", style = MaterialTheme.typography.headlineMedium)
        }

        item { SectionTitle("Device") }
        item {
            GroupCard {
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
                        onClick = {
                            controller.updateSettings(
                                settings.copy(
                                    alias = alias.trim(),
                                    port = port.toIntOrNull()
                                        ?: ProtocolConstants.DEFAULT_PORT,
                                    pin = pin,
                                ),
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text("Save name, port and PIN") }
                }
            }
        }

        item { SectionTitle("Appearance") }
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
                        listOf("dynamic" to "Wallpaper", "seed" to "LocalSend")
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
                    Text(
                        "Wallpaper colors come from your Android wallpaper; on desktop " +
                            "the LocalSend colors are used.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        item { SectionTitle("Receiving") }
        item {
            GroupCard {
                SettingsSwitch(
                    title = "Quick Save",
                    subtitle = "Accept every incoming transfer without asking",
                    checked = settings.quickSave,
                    onCheckedChange = {
                        controller.updateSettings(settings.copy(quickSave = it))
                    },
                )
                HorizontalDivider()
                SettingsSwitch(
                    title = "Auto-accept favorites",
                    subtitle = "Skip the accept prompt for favorite devices",
                    checked = settings.autoAcceptFavorites,
                    onCheckedChange = {
                        controller.updateSettings(settings.copy(autoAcceptFavorites = it))
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
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = destination,
                        onValueChange = { destination = it },
                        label = { Text("Save received files to (empty = Downloads)") },
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
                    ) { Text("Save destination folder") }
                }
            }
        }

        item { SectionTitle("Connection") }
        item {
            GroupCard {
                SettingsSwitch(
                    title = "HTTPS",
                    subtitle = "Self-signed certificate; fingerprint is the cert SHA-256",
                    checked = settings.https,
                    onCheckedChange = {
                        controller.updateSettings(settings.copy(https = it))
                    },
                )
                HorizontalDivider()
                SettingsSwitch(
                    title = "Download API",
                    subtitle = "Let browsers on the network download your received files",
                    checked = settings.downloadApi,
                    onCheckedChange = {
                        controller.updateSettings(settings.copy(downloadApi = it))
                    },
                )
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
                "Changes to port or HTTPS restart discovery and the receive server.",
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
    AlertDialog(
        onDismissRequest = { controller.respondOffer(false) },
        icon = {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                Icon(
                    Icons.Filled.Download,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                )
            }
        },
        title = { Text("${offer.peer.alias} wants to send") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                offer.files.forEach { file ->
                    Text("${file.fileName} · ${formatSize(file.size)}")
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
            Button(onClick = { controller.respondOffer(true) }) { Text("Accept") }
        },
        dismissButton = {
            TextButton(onClick = { controller.respondOffer(false) }) { Text("Decline") }
        },
    )
}

@Composable
private fun TextMessageDialog(
    target: DiscoveredDevice?,
    onDismiss: () -> Unit,
    onSend: (String) -> Unit,
) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                Icon(
                    Icons.Filled.Message,
                    contentDescription = null,
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FileProgressRow(progress: FileProgress) {
    Column(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                progress.fileName,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
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
            Text(entry.fileName, style = MaterialTheme.typography.bodyMedium)
            Text(
                "${entry.peerAlias} · ${formatSize(entry.size)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
