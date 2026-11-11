package dev.kurokawa.localsend.expressive.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import dev.kurokawa.localsend.expressive.platform.rememberFilePickerLauncher
import dev.kurokawa.localsend.expressive.protocol.DeviceType
import dev.kurokawa.localsend.expressive.protocol.ProtocolConstants
import dev.kurokawa.localsend.expressive.theme.LocalSendExpressiveTheme

private enum class Tab { Send, Receive, Settings }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot(controller: LocalSendController) {
    LocalSendExpressiveTheme {
        DisposableEffect(controller) {
            controller.start()
            onDispose { controller.stop() }
        }
        val local by controller.localDevice.collectAsState()
        val devices by controller.devices.collectAsState()
        val settings by controller.settings.collectAsState()
        val offer by controller.incomingOffer.collectAsState()

        var tab by remember { mutableStateOf(Tab.Send) }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("LocalSend Expressive") },
                    actions = {
                        Text(
                            text = local.alias,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(end = 16.dp),
                        )
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
                    Tab.Receive -> ReceiveScreen(controller, local)
                    Tab.Settings -> SettingsScreen(controller, settings, local)
                }
            }
        }

        offer?.let { IncomingOfferDialog(controller, it) }
    }
}

// ── Send ───────────────────────────────────────────────────────────────────

@Composable
private fun SendScreen(
    controller: LocalSendController,
    devices: List<DiscoveredDevice>,
    settings: AppSettings,
) {
    var selectedFingerprint by remember { mutableStateOf<String?>(null) }
    var pickedFiles by remember { mutableStateOf<List<PickedFile>>(emptyList()) }
    var pin by remember { mutableStateOf("") }
    val sendState by controller.sendState.collectAsState()
    val picker = rememberFilePickerLauncher { pickedFiles = it }
    val selected = devices.firstOrNull { it.fingerprint == selectedFingerprint }

    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Nearby devices", style = MaterialTheme.typography.headlineSmall)
        }
        if (devices.isEmpty()) {
            item {
                Column(
                    Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CircularProgressIndicator()
                    Text(
                        "Searching for devices…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            items(devices, key = { it.fingerprint }) { device ->
                val isSelected = device.fingerprint == selectedFingerprint
                val isFavorite = device.fingerprint in settings.favorites
                Card(
                    onClick = { selectedFingerprint = device.fingerprint },
                    colors = if (isSelected) {
                        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    } else {
                        CardDefaults.cardColors()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(deviceIcon(device.info.deviceType), contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(device.info.alias, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "${device.info.deviceModel ?: device.info.deviceType?.name ?: "device"} · ${device.ip}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        IconButton(onClick = { controller.toggleFavorite(device.fingerprint) }) {
                            Icon(
                                if (isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                                contentDescription = "Favorite",
                            )
                        }
                    }
                }
            }
        }

        item {
            FilledTonalButton(onClick = picker, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.AttachFile, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (pickedFiles.isEmpty()) "Select files" else "Select different files")
            }
        }

        if (pickedFiles.isNotEmpty()) {
            items(pickedFiles, key = { it.handle }) { file ->
                Row(verticalAlignment = Alignment.CenterVertically) {
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

        val state = sendState
        if (state != null) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            OutlinedButton(onClick = { controller.cancelSend() }) { Text("Cancel") }
                        } else {
                            OutlinedButton(onClick = { controller.clearSendState() }) { Text("Dismiss") }
                        }
                    }
                }
            }
        } else if (pickedFiles.isNotEmpty()) {
            item {
                Button(
                    onClick = { selected?.let { controller.sendFiles(it, pickedFiles) } },
                    enabled = selected != null,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Filled.Send, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (selected != null) "Send to ${selected.info.alias}" else "Pick a device above",
                    )
                }
            }
        }
    }
}

// ── Receive ────────────────────────────────────────────────────────────────

@Composable
private fun ReceiveScreen(controller: LocalSendController, local: LocalDevice) {
    val progress by controller.incomingProgress.collectAsState()
    val history by controller.history.collectAsState()
    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ready to receive", style = MaterialTheme.typography.headlineSmall)
                    Text("This device is visible as:", style = MaterialTheme.typography.bodyMedium)
                    Text(local.alias, style = MaterialTheme.typography.titleLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Port ${local.port}", style = MaterialTheme.typography.bodySmall)
                        Text("·", style = MaterialTheme.typography.bodySmall)
                        Text(
                            if (local.protocol == "https") "HTTPS" else "HTTP",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Text("·", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "Protocol ${ProtocolConstants.VERSION}",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Text(
                        "Fingerprint: ${local.fingerprint.take(16)}…",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
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
            items(history, key = { it.timeMillis.toString() + it.fileName }) { entry ->
                HistoryRow(entry)
            }
        }
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
    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text("Settings", style = MaterialTheme.typography.headlineSmall)
        }
        item {
            OutlinedTextField(
                value = alias,
                onValueChange = { alias = it },
                label = { Text("Device name (alias)") },
                placeholder = { Text(local.alias) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            OutlinedTextField(
                value = port,
                onValueChange = { port = it.filter { c -> c.isDigit() }.take(5) },
                label = { Text("Port") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it.filter { c -> c.isDigit() }.take(8) },
                label = { Text("PIN (empty = no PIN)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            SettingsSwitch(
                title = "HTTPS",
                subtitle = "Self-signed certificate; fingerprint is the cert SHA-256",
                checked = settings.https,
                onCheckedChange = { controller.updateSettings(settings.copy(https = it)) },
            )
        }
        item {
            SettingsSwitch(
                title = "Download API",
                subtitle = "Let browsers on the network download your received files",
                checked = settings.downloadApi,
                onCheckedChange = { controller.updateSettings(settings.copy(downloadApi = it)) },
            )
        }
        item {
            SettingsSwitch(
                title = "Auto-accept favorites",
                subtitle = "Skip the accept prompt for favorite devices",
                checked = settings.autoAcceptFavorites,
                onCheckedChange = { controller.updateSettings(settings.copy(autoAcceptFavorites = it)) },
            )
        }
        item {
            Button(
                onClick = {
                    controller.updateSettings(
                        settings.copy(
                            alias = alias.trim(),
                            port = port.toIntOrNull() ?: ProtocolConstants.DEFAULT_PORT,
                            pin = pin,
                        ),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Save name, port and PIN") }
        }
        if (settings.favorites.isNotEmpty()) {
            item {
                Text("Favorites", style = MaterialTheme.typography.titleMedium)
            }
            items(settings.favorites.toList()) { fingerprint ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${fingerprint.take(16)}…",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = { controller.toggleFavorite(fingerprint) }) {
                        Icon(Icons.Filled.Close, contentDescription = "Remove favorite")
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
private fun SettingsSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
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
            LinearProgressIndicator(
                progress = { progress.fraction },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun HistoryRow(entry: HistoryEntry) {
    Row(verticalAlignment = Alignment.CenterVertically) {
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
