package dev.kurokawa.localsend.expressive.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
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
import dev.kurokawa.localsend.expressive.platform.AppIcon
import dev.kurokawa.localsend.expressive.platform.ConnectivityController
import dev.kurokawa.localsend.expressive.platform.InstalledApp
import dev.kurokawa.localsend.expressive.platform.PickedFile
import dev.kurokawa.localsend.expressive.platform.QrScannerView
import dev.kurokawa.localsend.expressive.platform.installPackage
import dev.kurokawa.localsend.expressive.platform.installerSupported
import dev.kurokawa.localsend.expressive.platform.isInstallablePackage
import dev.kurokawa.localsend.expressive.platform.joinWifiNetwork
import dev.kurokawa.localsend.expressive.platform.loadInstalledApps
import dev.kurokawa.localsend.expressive.platform.openReceivedFile
import dev.kurokawa.localsend.expressive.platform.openUrl
import dev.kurokawa.localsend.expressive.platform.parseWifiQr
import dev.kurokawa.localsend.expressive.platform.clearQuickShareRequest
import dev.kurokawa.localsend.expressive.platform.clearSharedFiles
import dev.kurokawa.localsend.expressive.platform.platformDeviceType
import dev.kurokawa.localsend.expressive.platform.quickShareRequests
import dev.kurokawa.localsend.expressive.platform.rememberClipboardText
import dev.kurokawa.localsend.expressive.platform.rememberDestinationPickerLauncher
import dev.kurokawa.localsend.expressive.platform.rustCoreActive
import dev.kurokawa.localsend.expressive.platform.usesSafDestination
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

private enum class MainTab { Home, Settings }

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
 * The LSEx logo mark, drawn the same way as the launcher icon: a paper
 * plane inside an orbit ring with a satellite dot. [orbitAngle] animates
 * the dot around the ring (splash); the header uses a static angle.
 */
@Composable
private fun LogoMark(
    size: androidx.compose.ui.unit.Dp,
    orbitAngle: Float = -40f,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    Surface(
        shape = RoundedCornerShape(size * 0.28f),
        color = primary,
        modifier = modifier.size(size),
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val stroke = this.size.minDimension * 0.045f
                val radius = this.size.minDimension * 0.40f
                drawCircle(
                    color = onPrimary.copy(alpha = 0.55f),
                    radius = radius,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke),
                )
                val rad = Math.toRadians(orbitAngle.toDouble())
                val cx = center.x + radius * kotlin.math.cos(rad).toFloat()
                val cy = center.y + radius * kotlin.math.sin(rad).toFloat()
                drawCircle(
                    color = onPrimary,
                    radius = this.size.minDimension * 0.065f,
                    center = androidx.compose.ui.geometry.Offset(cx, cy),
                )
            }
            Icon(
                Icons.Filled.Send,
                contentDescription = null,
                tint = onPrimary,
                modifier = Modifier.size(size * 0.5f),
            )
        }
    }
}

/** The styled LSEx wordmark Ernest asked for: two-tone, extra bold, italic accent. */
@Composable
private fun LsexWordmark(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        LogoMark(38.dp)
        Spacer(Modifier.width(10.dp))
        Text(
            buildAnnotatedString {
                withStyle(
                    SpanStyle(fontWeight = FontWeight.ExtraBold, color = scheme.primary),
                ) { append("LS") }
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.ExtraBold,
                        fontStyle = FontStyle.Italic,
                        color = scheme.tertiary,
                    ),
                ) { append("Ex") }
            },
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}

/**
 * In-app animated splash (v0.5.0 redesign). v0.4.0's tween ran while the
 * first frames were still being skipped during startup, so Ernest only
 * ever saw a static image. This one waits for the first real frame, then
 * springs the mark in with an orbiting satellite dot, and fades out.
 */
@Composable
private fun SplashOverlay(onDone: () -> Unit) {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        withFrameNanos { }
        started = true
        delay(1600)
        onDone()
    }
    val scale by animateFloatAsState(
        targetValue = if (started) 1f else 0.55f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "splashScale",
    )
    val alpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(350),
        label = "splashAlpha",
    )
    val orbit = rememberInfiniteTransition(label = "splashOrbit")
    val angle by orbit.animateFloat(
        initialValue = -40f,
        targetValue = 320f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "splashOrbitAngle",
    )
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                },
            ) {
                LogoMark(112.dp, orbitAngle = angle)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                ),
                            ) { append("LS") }
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontStyle = FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.tertiary,
                                ),
                            ) { append("Ex") }
                        },
                        style = MaterialTheme.typography.displayMedium,
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

// ── Scaffold: floating nav bar (Google Photos style) / rail on wide screens ─

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

    var tab by remember { mutableStateOf(MainTab.Home) }

    // QS-tile quick share: jump Home and let that screen open its picker.
    val quickShareCount by quickShareRequests().collectAsState()
    LaunchedEffect(quickShareCount) {
        if (quickShareCount > 0) tab = MainTab.Home
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        if (maxWidth >= 840.dp) {
            // Wide layout (desktop / tablet): ChromeOS-flavoured chrome —
            // a rail on a soft backdrop, content floating in a rounded
            // card, like ChromeOS app windows.
            Row(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainerLow),
            ) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                ) {
                    Spacer(Modifier.height(16.dp))
                    LogoMark(44.dp)
                    Spacer(Modifier.height(28.dp))
                    NavigationRailItem(
                        selected = tab == MainTab.Home,
                        onClick = { tab = MainTab.Home },
                        icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                        label = { Text("Home") },
                    )
                    NavigationRailItem(
                        selected = tab == MainTab.Settings,
                        onClick = { tab = MainTab.Settings },
                        icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                        label = { Text(strings.settings) },
                    )
                }
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(top = 12.dp, end = 12.dp, bottom = 12.dp),
                ) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        when (tab) {
                            MainTab.Home -> HomeScreen(controller, devices, settings, local)
                            MainTab.Settings -> SettingsScreen(controller, settings, local)
                        }
                    }
                }
            }
        } else {
            Scaffold(
                bottomBar = {
                    // Floating pill navigation, Google Photos style.
                    Box(
                        Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Surface(
                            shape = RoundedCornerShape(30.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            shadowElevation = 6.dp,
                            tonalElevation = 3.dp,
                        ) {
                            Row(
                                Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                FloatingNavItem(
                                    selected = tab == MainTab.Home,
                                    icon = Icons.Filled.Home,
                                    label = "Home",
                                ) { tab = MainTab.Home }
                                FloatingNavItem(
                                    selected = tab == MainTab.Settings,
                                    icon = Icons.Filled.Settings,
                                    label = strings.settings,
                                ) { tab = MainTab.Settings }
                            }
                        }
                    }
                },
            ) { padding ->
                Box(Modifier.fillMaxSize().padding(padding)) {
                    when (tab) {
                        MainTab.Home -> HomeScreen(controller, devices, settings, local)
                        MainTab.Settings -> SettingsScreen(controller, settings, local)
                    }
                }
            }
        }
    }

    offer?.let { IncomingOfferDialog(controller, it) }
}

@Composable
private fun FloatingNavItem(
    selected: Boolean,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (selected) scheme.primaryContainer else Color.Transparent,
            modifier = Modifier.width(56.dp).height(30.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = if (selected) scheme.onPrimaryContainer else scheme.onSurfaceVariant,
                    modifier = Modifier.size(21.dp),
                )
            }
        }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) scheme.onSurface else scheme.onSurfaceVariant,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

// ── Home: Send + Receive + Connect in ONE view (v0.5.0) ────────────────────
// Layout inspired by Zapya / Google Files (content-first tabs), built
// purely from Material 3 Expressive components, per Ernest's direction:
// compose the content first, then tap as many target devices as you want.

private sealed interface StagedItem {
    data class FileItem(val file: PickedFile) : StagedItem
    data class TextItem(val text: String) : StagedItem
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HomeScreen(
    controller: LocalSendController,
    devices: List<DiscoveredDevice>,
    settings: AppSettings,
    local: LocalDevice,
) {
    val strings = LocalStrings.current
    var selectedFingerprints by remember { mutableStateOf<Set<String>>(emptySet()) }
    var staged by remember { mutableStateOf<List<StagedItem>>(emptyList()) }
    var contentTab by remember { mutableStateOf(0) }
    var textDraft by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var lastText by remember { mutableStateOf("") }
    var showIpDialog by remember { mutableStateOf(false) }
    var showAppDialog by remember { mutableStateOf(false) }
    var showTroubleshoot by remember { mutableStateOf(false) }
    var infoDevice by remember { mutableStateOf<DiscoveredDevice?>(null) }
    var pasteNote by remember { mutableStateOf<String?>(null) }
    var actionNote by remember { mutableStateOf<String?>(null) }
    var editItem by remember { mutableStateOf<StagedItem?>(null) }
    var readyExpanded by remember { mutableStateOf(true) }
    var historyExpanded by remember { mutableStateOf(false) }
    var collapsedGroups by remember { mutableStateOf<Set<String>>(emptySet()) }

    val sendState by controller.sendState.collectAsState()
    val outbox by controller.outbox.collectAsState()
    val scanning by controller.scanning.collectAsState()
    val progress by controller.incomingProgress.collectAsState()
    val history by controller.history.collectAsState()
    val lastSavedTo by controller.lastSavedTo.collectAsState()
    val localIp by controller.localIpAddress.collectAsState()
    val scope = rememberCoroutineScope()

    val picker = rememberFilePickerLauncher { files ->
        staged = staged + files.map { StagedItem.FileItem(it) }
    }
    val mediaPicker = rememberMediaPickerLauncher { files ->
        staged = staged + files.map { StagedItem.FileItem(it) }
    }
    val folderPicker = rememberFolderPickerLauncher { files ->
        staged = staged + files.map { StagedItem.FileItem(it) }
    }
    val clipboardText = rememberClipboardText()
    val isMobile = platformDeviceType() == DeviceType.mobile

    val sortedDevices = devices.sortedByDescending { it.fingerprint in settings.favorites }
    val targets = sortedDevices.filter { it.fingerprint in selectedFingerprints }
    val stagedFiles = staged.filterIsInstance<StagedItem.FileItem>().map { it.file }
    val stagedTexts = staged.filterIsInstance<StagedItem.TextItem>()
    val stagedBytes = stagedFiles.sumOf { it.size } +
        stagedTexts.sumOf { it.text.encodeToByteArray().size.toLong() }

    // Files arriving from the Android share sheet join the staging area.
    val sharedFiles by sharedFilesFlow().collectAsState()
    LaunchedEffect(sharedFiles) {
        if (sharedFiles.isNotEmpty()) {
            staged = staged + sharedFiles.map { StagedItem.FileItem(it) }
            clearSharedFiles()
        }
    }

    // Opening Home starts an active scan burst (the discovery timeout).
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
        // ── Header: wordmark + own-device chip ──
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LsexWordmark(Modifier.weight(1f))
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Text(
                        text = local.alias,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }
        }

        // ── Receive status hero ──
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
                    Modifier.padding(20.dp),
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
            GroupCard {
                QuickSaveGroup(controller, settings)
                SettingsDivider()
                SettingsSwitch(
                    icon = Icons.Filled.CheckCircle,
                    iconBg = MaterialTheme.colorScheme.tertiaryContainer,
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
            ConnectivityCard(controller, local, localIp)
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
        lastSavedTo?.let { saved ->
            item {
                Text(
                    "Last file saved to $saved",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        // ── Devices (multi-select targets) ──
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(strings.nearbyDevices, style = MaterialTheme.typography.headlineSmall)
                    Text(
                        "Tap to select one or more devices to send to.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = { showIpDialog = true }) {
                    Icon(Icons.Filled.Lan, contentDescription = "Add by IP address")
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
        if (sortedDevices.isEmpty()) {
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
                    TextButton(onClick = { showTroubleshoot = true }) {
                        Text(strings.troubleshoot)
                    }
                }
            }
        } else {
            items(sortedDevices, key = { it.fingerprint }) { device ->
                DeviceCard(
                    device = device,
                    isSelected = device.fingerprint in selectedFingerprints,
                    isFavorite = device.fingerprint in settings.favorites,
                    onClick = {
                        selectedFingerprints = if (device.fingerprint in selectedFingerprints) {
                            selectedFingerprints - device.fingerprint
                        } else {
                            selectedFingerprints + device.fingerprint
                        }
                    },
                    onToggleFavorite = { controller.toggleFavorite(device.fingerprint) },
                    onInfo = { infoDevice = device },
                )
            }
        }

        // ── Content tabs: compose FIRST, then send ──
        item {
            Text("What to send", style = MaterialTheme.typography.titleMedium)
        }
        item {
            val tabs = buildList {
                add("Files" to Icons.Filled.AttachFile)
                add("Media" to Icons.Filled.Image)
                add("Apps" to Icons.Filled.Apps)
                add("Text" to Icons.Filled.Message)
                add("Folder" to Icons.Filled.Folder)
                add("Web" to Icons.Filled.Link)
            }
            Column {
                TabRow(selectedTabIndex = contentTab) {
                    tabs.forEachIndexed { index, (label, icon) ->
                        Tab(
                            selected = contentTab == index,
                            onClick = { contentTab = index },
                            text = { Text(label) },
                            icon = { Icon(icon, contentDescription = null) },
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        when (contentTab) {
                            0 -> {
                                Button(
                                    onClick = { picker() },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Icon(Icons.Filled.AttachFile, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Choose files")
                                }
                                Text(
                                    "Any files — documents, archives, and app packages " +
                                        "(.apk, .apks, .aab) too.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            1 -> {
                                Button(
                                    onClick = { mediaPicker() },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Icon(Icons.Filled.Image, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Choose photos & videos")
                                }
                            }
                            2 -> {
                                if (isMobile) {
                                    Button(
                                        onClick = { showAppDialog = true },
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Icon(Icons.Filled.Apps, contentDescription = null)
                                        Spacer(Modifier.width(8.dp))
                                        Text("Browse installed apps")
                                    }
                                    Text(
                                        "Sends the app's APK so the other side can install it.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                } else {
                                    Text(
                                        "Installed apps can be sent from the Android app.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    OutlinedButton(
                                        onClick = { showIpDialog = true },
                                        modifier = Modifier.fillMaxWidth(),
                                    ) { Text("Add a device by IP address") }
                                }
                            }
                            3 -> {
                                OutlinedTextField(
                                    value = textDraft,
                                    onValueChange = { textDraft = it },
                                    label = { Text("Write a message") },
                                    minLines = 3,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = {
                                            val clip = clipboardText()
                                            if (clip.isNullOrBlank()) {
                                                pasteNote = "The clipboard is empty."
                                            } else {
                                                textDraft = clip
                                                pasteNote = null
                                            }
                                        },
                                    ) {
                                        Icon(Icons.Filled.ContentPaste, contentDescription = null)
                                        Spacer(Modifier.width(6.dp))
                                        Text("Paste")
                                    }
                                    Button(
                                        onClick = {
                                            if (textDraft.isNotBlank()) {
                                                staged = staged + StagedItem.TextItem(textDraft)
                                                lastText = textDraft
                                                textDraft = ""
                                            }
                                        },
                                        enabled = textDraft.isNotBlank(),
                                    ) { Text("Add text") }
                                }
                                pasteNote?.let { note ->
                                    Text(
                                        note,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                            4 -> {
                                Button(
                                    onClick = { folderPicker() },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Icon(Icons.Filled.Folder, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Choose a folder")
                                }
                                Text(
                                    "The folder structure is recreated on the other device.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            else -> {
                                WebSharePanel(controller, settings, local, localIp)
                            }
                        }
                    }
                }
            }
        }

        // ── Ready to send (expandable) ──
        if (staged.isNotEmpty()) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${strings.readyToSend} · ${staged.size} item(s) · ${formatSize(stagedBytes)}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = { staged = emptyList() }) { Text("Clear all") }
                    IconButton(onClick = { readyExpanded = !readyExpanded }) {
                        Icon(
                            if (readyExpanded) {
                                Icons.Filled.KeyboardArrowUp
                            } else {
                                Icons.Filled.KeyboardArrowDown
                            },
                            contentDescription = if (readyExpanded) "Collapse" else "Expand",
                        )
                    }
                }
            }
            if (readyExpanded) {
                items(staged.size) { index ->
                    val item = staged[index]
                    StagedItemCard(
                        item = item,
                        onEdit = { editItem = item },
                        onDiscard = { staged = staged - item },
                    )
                }
            }
            item {
                Button(
                    onClick = {
                        if (stagedFiles.isNotEmpty()) {
                            controller.sendFilesToTargets(targets, stagedFiles)
                        }
                        stagedTexts.forEach { textItem ->
                            controller.sendTextToTargets(targets, textItem.text)
                        }
                        staged = emptyList()
                    },
                    enabled = targets.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Icon(Icons.Filled.Send, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        when {
                            targets.isEmpty() -> "Select a device above"
                            targets.size == 1 -> "Send to ${targets.first().info.alias}"
                            else -> "Send to ${targets.size} devices"
                        },
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }

        // Text sends keep their own ephemeral card (PIN retry lives here);
        // file sends live in the outbox below.
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
        }

        // ── Outbox, grouped per recipient device ──
        if (outbox.isNotEmpty()) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Transfers",
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
            val groups = outbox.groupBy { it.targetFingerprint }
            groups.forEach { (fingerprint, jobs) ->
                item(key = "group-$fingerprint") {
                    val expanded = fingerprint !in collapsedGroups
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                collapsedGroups = if (expanded) {
                                    collapsedGroups + fingerprint
                                } else {
                                    collapsedGroups - fingerprint
                                }
                            }
                            .padding(vertical = 4.dp),
                    ) {
                        Icon(
                            deviceIcon(deviceTypeOf(jobs.first().targetType)),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                jobs.first().targetAlias,
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                "${jobs.size} send(s) · " +
                                    formatSize(jobs.sumOf { j -> j.files.sumOf { it.size } }),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Icon(
                            if (expanded) {
                                Icons.Filled.KeyboardArrowUp
                            } else {
                                Icons.Filled.KeyboardArrowDown
                            },
                            contentDescription = if (expanded) "Collapse" else "Expand",
                        )
                    }
                }
                if (fingerprint !in collapsedGroups) {
                    items(jobs, key = { it.id }) { job ->
                        OutboxJobCard(controller, job, grouped = true)
                    }
                }
            }
        }

        // ── History (collapsible) ──
        if (history.isNotEmpty()) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { historyExpanded = !historyExpanded },
                ) {
                    Icon(
                        Icons.Filled.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        strings.history,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        if (historyExpanded) {
                            Icons.Filled.KeyboardArrowUp
                        } else {
                            Icons.Filled.KeyboardArrowDown
                        },
                        contentDescription = if (historyExpanded) "Collapse" else "Expand",
                    )
                }
            }
            actionNote?.let { note ->
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            note,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f),
                        )
                        TextButton(onClick = { actionNote = null }) { Text("OK") }
                    }
                }
            }
            if (historyExpanded) {
                item {
                    GroupCard {
                        history.forEachIndexed { index, entry ->
                            if (index > 0) SettingsDivider()
                            HistoryRow(
                                entry = entry,
                                onOpen = if (entry.incoming && entry.success && entry.uri.isNotEmpty()) {
                                    { openReceivedFile(entry.uri, entry.fileName) }
                                } else {
                                    null
                                },
                                onInstall = if (
                                    entry.incoming && entry.success &&
                                    isInstallablePackage(entry.fileName) && installerSupported()
                                ) {
                                    {
                                        scope.launch {
                                            val result = withContext(Dispatchers.IO) {
                                                installPackage(entry.uri, entry.fileName)
                                            }
                                            actionNote = result
                                                ?: "Installer opened for ${entry.fileName}"
                                        }
                                    }
                                } else {
                                    null
                                },
                                onResend = if (!entry.incoming && entry.success && entry.handle.isNotEmpty()) {
                                    {
                                        staged = staged + StagedItem.FileItem(
                                            PickedFile(entry.handle, entry.fileName, entry.size),
                                        )
                                        actionNote =
                                            "“${entry.fileName}” is staged — pick the device(s) and send."
                                    }
                                } else {
                                    null
                                },
                            )
                        }
                    }
                }
            }
        }
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
            settings = settings,
            onToggleHideSystem = {
                controller.updateSettings(settings.copy(hideSystemApps = it))
            },
            onDismiss = { showAppDialog = false },
            onPick = { app ->
                staged = staged + StagedItem.FileItem(
                    PickedFile(
                        handle = app.apkHandle,
                        name = "${app.label}.apk",
                        size = app.apkSize,
                    ),
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
    editItem?.let { item ->
        when (item) {
            is StagedItem.FileItem -> StagedEditDialog(
                title = "Rename file",
                initial = item.file.displayPath ?: item.file.name,
                onDismiss = { editItem = null },
                onSave = { newName ->
                    val trimmed = newName.trim()
                    if (trimmed.isNotBlank()) {
                        val renamed = if (item.file.displayPath != null) {
                            val parent = item.file.displayPath.substringBeforeLast('/', "")
                            val leaf = trimmed.substringAfterLast('/')
                            item.file.copy(
                                name = leaf,
                                displayPath = if (parent.isEmpty()) leaf else "$parent/$leaf",
                            )
                        } else {
                            item.file.copy(name = trimmed)
                        }
                        staged = staged.map { if (it == item) StagedItem.FileItem(renamed) else it }
                    }
                    editItem = null
                },
            )
            is StagedItem.TextItem -> StagedEditDialog(
                title = "Edit text",
                initial = item.text,
                multiline = true,
                onDismiss = { editItem = null },
                onSave = { newText ->
                    if (newText.isNotBlank()) {
                        staged = staged.map {
                            if (it == item) StagedItem.TextItem(newText) else it
                        }
                        lastText = newText
                    }
                    editItem = null
                },
            )
        }
    }
}

@Composable
private fun StagedItemCard(
    item: StagedItem,
    onEdit: () -> Unit,
    onDiscard: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(40.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        when (item) {
                            is StagedItem.FileItem -> Icons.Filled.AttachFile
                            is StagedItem.TextItem -> Icons.Filled.Message
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    when (item) {
                        is StagedItem.FileItem -> item.file.displayPath ?: item.file.name
                        is StagedItem.TextItem -> item.text
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    when (item) {
                        is StagedItem.FileItem -> formatSize(item.file.size)
                        is StagedItem.TextItem ->
                            "Text · ${formatSize(item.text.encodeToByteArray().size.toLong())}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            // Circular action buttons, like Ernest's MD3 reference photo:
            // edit (rename) and discard are always one tap away.
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
            ) {
                IconButton(onClick = onEdit, modifier = Modifier.size(38.dp)) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Rename / edit",
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
            Spacer(Modifier.width(6.dp))
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
            ) {
                IconButton(onClick = onDiscard, modifier = Modifier.size(38.dp)) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Discard",
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun StagedEditDialog(
    title: String,
    initial: String,
    multiline: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var text by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = !multiline,
                minLines = if (multiline) 3 else 1,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            Button(
                onClick = { onSave(text) },
                enabled = text.isNotBlank(),
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
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

/** Quick Save as ONE connected button group (MD3E), as Ernest asked. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun QuickSaveGroup(controller: LocalSendController, settings: AppSettings) {
    val mode = when {
        settings.quickSave -> 2
        settings.autoAcceptFavorites -> 1
        else -> 0
    }
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Quick Save", style = MaterialTheme.typography.bodyLarge)
        Text(
            "Skip the accept prompt — for everyone, or only for favorites.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            listOf("Off", "Favorites", "Everyone").forEachIndexed { index, label ->
                SegmentedButton(
                    selected = mode == index,
                    onClick = {
                        controller.updateSettings(
                            settings.copy(
                                quickSave = index == 2,
                                autoAcceptFavorites = index == 1,
                            ),
                        )
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = 3),
                    modifier = Modifier.weight(1f),
                ) { Text(label) }
            }
        }
    }
}

/** Share-via-link panel (the "Web" content tab): URL, QR, file choice. */
@Composable
private fun WebSharePanel(
    controller: LocalSendController,
    settings: AppSettings,
    local: LocalDevice,
    localIp: String?,
) {
    val webShareFiles by controller.webShareFiles.collectAsState()
    val webSharePicker = rememberFilePickerLauncher { controller.setWebShareFiles(it) }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Link,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.width(8.dp))
            Text("Share via link", style = MaterialTheme.typography.titleSmall)
        }
        if (!settings.downloadApi) {
            Text(
                "Serve files to any browser on this network — no app needed " +
                    "on the other side.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(
                onClick = { controller.updateSettings(settings.copy(downloadApi = true)) },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Turn on web sharing") }
        } else {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Auto-accept web uploads", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "Skip the prompt for files uploaded from the web page",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = settings.shareViaLinkAutoAccept,
                    onCheckedChange = {
                        controller.updateSettings(settings.copy(shareViaLinkAutoAccept = it))
                    },
                )
            }
            OutlinedButton(
                onClick = { controller.updateSettings(settings.copy(downloadApi = false)) },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Turn web sharing off") }
        }
    }
}

// ── Settings (Android-Settings style, v0.5.0) ──────────────────────────────
// Grouped cards with slightly rounded corners, a colored-circle icon for
// every setting, dividers in the background color, generous spacing.
// Performance: this screen is a LazyColumn of independent items and does
// NOT collect the devices/outbox flows, so background transfer activity
// no longer recomposes it (the v0.4.0 "Skipped 52 frames" lag).

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
    )
}

/** A grouped settings-style card. */
@Composable
private fun GroupCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    ) {
        Column(content = content)
    }
}

/**
 * Divider in the page background color, inset to the text edge — the
 * Android Settings look Ernest asked for (not a hairline gray rule).
 */
@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 68.dp, end = 16.dp),
        thickness = 2.dp,
        color = MaterialTheme.colorScheme.surface,
    )
}

/** Leading icon in a filled colored circle, Android 17 settings style. */
@Composable
private fun SettingIconChip(icon: ImageVector, iconBg: Color) {
    Surface(shape = CircleShape, color = iconBg, modifier = Modifier.size(40.dp)) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun SettingsSwitch(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingIconChip(icon, iconBg)
        Spacer(Modifier.width(12.dp))
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

/** A settings row whose value is chosen from a dropdown menu. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingDropdown(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    options: List<Pair<String, String>>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.first == selected }?.second ?: selected
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        Row(
            Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SettingIconChip(icon, iconBg)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                Text(
                    selectedLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = "Choose",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { (value, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onSelect(value)
                        expanded = false
                    },
                    trailingIcon = if (value == selected) {
                        {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    } else {
                        null
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    controller: LocalSendController,
    settings: AppSettings,
    local: LocalDevice,
) {
    val strings = LocalStrings.current
    val scheme = MaterialTheme.colorScheme
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
    val destinationPicker = rememberDestinationPickerLauncher { picked ->
        if (usesSafDestination()) {
            controller.updateSettings(settings.copy(destinationTreeUri = picked))
        } else {
            destination = picked
            controller.updateSettings(settings.copy(destinationFolder = picked))
        }
    }

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
                SettingDropdown(
                    icon = Icons.Filled.DarkMode,
                    iconBg = scheme.primaryContainer,
                    title = "Theme",
                    options = listOf(
                        "system" to "System",
                        "light" to "Light",
                        "dark" to "Dark",
                    ),
                    selected = settings.themeMode,
                    onSelect = { controller.updateSettings(settings.copy(themeMode = it)) },
                )
                SettingsDivider()
                SettingDropdown(
                    icon = Icons.Filled.Palette,
                    iconBg = scheme.secondaryContainer,
                    title = "Colors",
                    options = listOf(
                        "dynamic" to "Wallpaper",
                        "seed" to "LSEx",
                    ),
                    selected = settings.colorMode,
                    onSelect = { controller.updateSettings(settings.copy(colorMode = it)) },
                )
                SettingsDivider()
                SettingDropdown(
                    icon = Icons.Filled.Translate,
                    iconBg = scheme.tertiaryContainer,
                    title = "Language",
                    options = listOf(
                        "system" to "System",
                        "en" to "English",
                        "es" to "Español",
                    ),
                    selected = settings.language,
                    onSelect = { controller.updateSettings(settings.copy(language = it)) },
                )
                SettingsDivider()
                SettingsSwitch(
                    icon = Icons.Filled.Animation,
                    iconBg = scheme.secondaryContainer,
                    title = "Animations",
                    subtitle = "Splash and expressive motion",
                    checked = settings.enableAnimations,
                    onCheckedChange = {
                        controller.updateSettings(settings.copy(enableAnimations = it))
                    },
                )
                if (supportsQuickShareTile()) {
                    SettingsDivider()
                    Row(
                        Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SettingIconChip(Icons.Filled.Share, scheme.tertiaryContainer)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Quick share tile", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "Add the LSEx tile from your Quick Settings panel editor " +
                                    "to start a share in one tap.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
                SettingsDivider()
                SettingsSwitch(
                    icon = Icons.Filled.Tune,
                    iconBg = scheme.errorContainer,
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
                        icon = Icons.Filled.PushPin,
                        iconBg = scheme.primaryContainer,
                        title = "Always on top",
                        subtitle = "Keep the window above other windows",
                        checked = settings.alwaysOnTop,
                        onCheckedChange = {
                            controller.updateSettings(settings.copy(alwaysOnTop = it))
                        },
                    )
                    SettingsDivider()
                    SettingsSwitch(
                        icon = Icons.Filled.Save,
                        iconBg = scheme.secondaryContainer,
                        title = "Save window placement",
                        subtitle = "Restore window position and size on launch",
                        checked = settings.saveWindowPlacement,
                        onCheckedChange = {
                            controller.updateSettings(settings.copy(saveWindowPlacement = it))
                        },
                    )
                    SettingsDivider()
                    SettingsSwitch(
                        icon = Icons.Filled.KeyboardArrowDown,
                        iconBg = scheme.tertiaryContainer,
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
                QuickSaveGroup(controller, settings)
                SettingsDivider()
                SettingsSwitch(
                    icon = Icons.Filled.Security,
                    iconBg = scheme.errorContainer,
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
                SettingsDivider()
                SettingsSwitch(
                    icon = Icons.Filled.Image,
                    iconBg = scheme.tertiaryContainer,
                    title = "Save to Gallery",
                    subtitle = "Photos and videos go to Pictures/Movies instead of Downloads",
                    checked = settings.saveToGallery,
                    onCheckedChange = {
                        controller.updateSettings(settings.copy(saveToGallery = it))
                    },
                )
                SettingsDivider()
                SettingsSwitch(
                    icon = Icons.Filled.CheckCircle,
                    iconBg = scheme.primaryContainer,
                    title = "Auto Finish",
                    subtitle = "Close a receive session as soon as it completes",
                    checked = settings.autoFinish,
                    onCheckedChange = {
                        controller.updateSettings(settings.copy(autoFinish = it))
                    },
                )
                SettingsDivider()
                SettingsSwitch(
                    icon = Icons.Filled.History,
                    iconBg = scheme.secondaryContainer,
                    title = "Save to History",
                    subtitle = "Record finished transfers in the history list",
                    checked = settings.saveToHistory,
                    onCheckedChange = {
                        controller.updateSettings(settings.copy(saveToHistory = it))
                    },
                )
                if (advanced) {
                    SettingsDivider()
                    SettingsSwitch(
                        icon = Icons.Filled.Fingerprint,
                        iconBg = scheme.tertiaryContainer,
                        title = "Verify checksums",
                        subtitle = "Reject files whose SHA-256 doesn't match the sender's",
                        checked = settings.verifyChecksums,
                        onCheckedChange = {
                            controller.updateSettings(settings.copy(verifyChecksums = it))
                        },
                    )
                }
                SettingsDivider()
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SettingIconChip(Icons.Filled.FolderOpen, scheme.primaryContainer)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Destination folder", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                when {
                                    usesSafDestination() && settings.destinationTreeUri.isNotEmpty() ->
                                        "A folder you picked (Storage Access Framework)"
                                    usesSafDestination() ->
                                        "Downloads (default)"
                                    isDesktop && settings.destinationFolder.isNotEmpty() ->
                                        settings.destinationFolder
                                    isDesktop -> "~/Downloads/LocalSend Expressive (default)"
                                    else -> "Downloads (default)"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    if (usesSafDestination()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { destinationPicker() }) {
                                Text("Choose folder")
                            }
                            if (settings.destinationTreeUri.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = {
                                        controller.updateSettings(
                                            settings.copy(destinationTreeUri = ""),
                                        )
                                    },
                                ) { Text("Use Downloads") }
                            }
                        }
                        Text(
                            "Files are written into the folder you pick — check there " +
                                "first. (MediaStore Downloads stays the fallback.)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        OutlinedTextField(
                            value = destination,
                            onValueChange = { destination = it },
                            label = { Text("Destination folder path") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { destinationPicker() }) {
                                Text("Browse…")
                            }
                            Button(
                                onClick = {
                                    controller.updateSettings(
                                        settings.copy(destinationFolder = destination.trim()),
                                    )
                                },
                            ) { Text("Save destination") }
                        }
                    }
                }
            }
        }

        if (advanced) {
            item { SectionTitle("Send") }
            item {
                GroupCard {
                    SettingsSwitch(
                        icon = Icons.Filled.Link,
                        iconBg = scheme.primaryContainer,
                        title = "Share via link: auto-accept",
                        subtitle = "Auto-accept uploads from the web page",
                        checked = settings.shareViaLinkAutoAccept,
                        onCheckedChange = {
                            controller.updateSettings(
                                settings.copy(shareViaLinkAutoAccept = it),
                            )
                        },
                    )
                    SettingsDivider()
                    SettingsSwitch(
                        icon = Icons.Filled.Fingerprint,
                        iconBg = scheme.secondaryContainer,
                        title = "Create checksums",
                        subtitle = "Attach a SHA-256 to every file you send (slower for big files)",
                        checked = settings.createChecksums,
                        onCheckedChange = {
                            controller.updateSettings(settings.copy(createChecksums = it))
                        },
                    )
                    SettingsDivider()
                    SettingsSwitch(
                        icon = Icons.Filled.Replay,
                        iconBg = scheme.tertiaryContainer,
                        title = "Auto-retry sends",
                        subtitle = "Retry a dropped send a few times before parking it in the outbox",
                        checked = settings.autoRetry,
                        onCheckedChange = {
                            controller.updateSettings(settings.copy(autoRetry = it))
                        },
                    )
                    SettingsDivider()
                    SettingsSwitch(
                        icon = Icons.Filled.Speed,
                        iconBg = scheme.primaryContainer,
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
                    icon = Icons.Filled.Router,
                    iconBg = scheme.primaryContainer,
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
                SettingsDivider()
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
                SettingsDivider()
                SettingsSwitch(
                    icon = Icons.Filled.Security,
                    iconBg = scheme.tertiaryContainer,
                    title = "Encryption (HTTPS)",
                    subtitle = "Self-signed certificate; the fingerprint is the cert SHA-256",
                    checked = settings.https,
                    onCheckedChange = {
                        controller.updateSettings(settings.copy(https = it))
                    },
                )
                SettingsDivider()
                SettingsSwitch(
                    icon = Icons.Filled.Link,
                    iconBg = scheme.secondaryContainer,
                    title = "Download API",
                    subtitle = "Share via link: serve files to browsers on the network",
                    checked = settings.downloadApi,
                    onCheckedChange = {
                        controller.updateSettings(settings.copy(downloadApi = it))
                    },
                )
                if (advanced) {
                    SettingsDivider()
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
                        if (index > 0) SettingsDivider()
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
                "LSEx 0.5.0 · LocalSend Protocol ${ProtocolConstants.VERSION} · " +
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

// ── App picker: real icons + hide-system-apps toggle (v0.5.0) ──────────────

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AppPickerDialog(
    settings: AppSettings,
    onToggleHideSystem: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onPick: (InstalledApp) -> Unit,
) {
    var apps by remember { mutableStateOf<List<InstalledApp>?>(null) }
    var query by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        apps = withContext(Dispatchers.Default) { loadInstalledApps() }
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Hide system apps",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                    Switch(
                        checked = settings.hideSystemApps,
                        onCheckedChange = onToggleHideSystem,
                    )
                }
                val list = apps
                when {
                    list == null -> Box(
                        Modifier.fillMaxWidth().height(120.dp),
                        contentAlignment = Alignment.Center,
                    ) { LoadingIndicator() }

                    list.isEmpty() -> Text("No apps could be listed on this device.")
                    else -> LazyColumn(Modifier.height(320.dp)) {
                        items(
                            list.filter { app ->
                                (!settings.hideSystemApps || !app.isSystem) &&
                                    (query.isBlank() ||
                                        app.label.contains(query, ignoreCase = true))
                            },
                            key = { it.packageName },
                        ) { app ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                AppIcon(app.packageName, Modifier.size(42.dp))
                                Spacer(Modifier.width(12.dp))
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

// ── History + outbox rows ──────────────────────────────────────────────────

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
private fun HistoryRow(
    entry: HistoryEntry,
    onOpen: (() -> Unit)? = null,
    onInstall: (() -> Unit)? = null,
    onResend: (() -> Unit)? = null,
) {
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
        if (onInstall != null) {
            IconButton(onClick = onInstall) {
                Icon(
                    Icons.Filled.SystemUpdate,
                    contentDescription = "Install this package",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
        if (onOpen != null) {
            IconButton(onClick = onOpen) {
                Icon(
                    Icons.Filled.OpenInNew,
                    contentDescription = "Open file",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
        if (onResend != null) {
            IconButton(onClick = onResend) {
                Icon(
                    Icons.Filled.Send,
                    contentDescription = "Stage for resending",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
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

private fun deviceTypeOf(name: String?): DeviceType? =
    name?.let { runCatching { DeviceType.valueOf(it) }.getOrNull() }

// ── Outbox (persistent sends: global + per-file retry) ─────────────────────

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun OutboxJobCard(
    controller: LocalSendController,
    job: OutboxJob,
    grouped: Boolean = false,
) {
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
                    if (!grouped) {
                        Text(
                            "To ${job.targetAlias}",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
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
                        Text("Cancel send")
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

/**
 * One file of a send job as its OWN card (Ernest's sending-view request):
 * progress bar plus a quick cancel while it's the file in flight.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun OutboxFileRow(
    controller: LocalSendController,
    job: OutboxJob,
    file: OutboxFile,
    enabled: Boolean,
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
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
                        IconButton(
                            onClick = { controller.retryOutboxFile(job.id, file.fileId) },
                        ) {
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
                    TransferStatus.ACTIVE -> {
                        LoadingIndicator(Modifier.size(20.dp))
                        IconButton(onClick = { controller.cancelSend() }) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Cancel this send",
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
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
