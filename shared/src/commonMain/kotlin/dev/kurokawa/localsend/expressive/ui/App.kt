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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.kurokawa.localsend.expressive.LocalDevice
import dev.kurokawa.localsend.expressive.discovery.DiscoveredDevice
import dev.kurokawa.localsend.expressive.platform.platformDeviceType
import dev.kurokawa.localsend.expressive.protocol.ProtocolConstants
import dev.kurokawa.localsend.expressive.theme.LocalSendExpressiveTheme

private enum class Tab { Send, Receive, Settings }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot(
    localDevice: LocalDevice = remember { LocalDevice.create(platformDeviceType()) },
    devices: List<DiscoveredDevice> = emptyList(),
) {
    LocalSendExpressiveTheme {
        var tab by remember { mutableStateOf(Tab.Send) }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("LocalSend Expressive") },
                    actions = {
                        Text(
                            text = localDevice.alias,
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
                    Tab.Send -> SendScreen(devices)
                    Tab.Receive -> ReceiveScreen(localDevice)
                    Tab.Settings -> SettingsScreen(localDevice)
                }
            }
        }
    }
}

@Composable
private fun SendScreen(devices: List<DiscoveredDevice>) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Nearby devices", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        if (devices.isEmpty()) {
            Column(
                Modifier.fillMaxWidth().padding(vertical = 32.dp),
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
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
            ) {
                items(devices, key = { it.fingerprint + it.ip }) { device ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(device.info.alias, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "${device.info.deviceModel ?: device.info.deviceType?.name ?: "device"} · ${device.ip}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiveScreen(localDevice: LocalDevice) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Ready to receive", style = MaterialTheme.typography.headlineSmall)
                Text("This device is visible as:", style = MaterialTheme.typography.bodyMedium)
                Text(localDevice.alias, style = MaterialTheme.typography.titleLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Port ${localDevice.port}", style = MaterialTheme.typography.bodySmall)
                    Text("·", style = MaterialTheme.typography.bodySmall)
                    Text(
                        "Protocol ${ProtocolConstants.VERSION}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(localDevice: LocalDevice) {
    var alias by remember { mutableStateOf(localDevice.alias) }
    var port by remember { mutableStateOf(localDevice.port.toString()) }
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = alias,
            onValueChange = { alias = it },
            label = { Text("Device name (alias)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = port,
            onValueChange = { port = it.filter { c -> c.isDigit() } },
            label = { Text("Port") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            "Settings are visual only in M0 — saving lands with the settings store in M5.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
