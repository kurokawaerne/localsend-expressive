package dev.kurokawa.localsend.expressive.ui
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.outlined.*
import com.composables.icons.materialsymbols.outlinedfilled.*

import androidx.compose.ui.draw.alpha
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
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.ui.platform.LocalDensity
import dev.kurokawa.localsend.expressive.platform.BrowserItem
import dev.kurokawa.localsend.expressive.platform.BrowserThumbnail
import dev.kurokawa.localsend.expressive.platform.MediaKind
import dev.kurokawa.localsend.expressive.platform.expandBrowserFolder
import dev.kurokawa.localsend.expressive.platform.listBrowserFolder
import dev.kurokawa.localsend.expressive.platform.loadMediaItems
import dev.kurokawa.localsend.expressive.platform.platformHasSystemSplash
import dev.kurokawa.localsend.expressive.platform.prefersSwipeActions
import dev.kurokawa.localsend.expressive.platform.rememberStorageAccess
import dev.kurokawa.localsend.expressive.platform.storageRoots
import kotlin.math.roundToInt
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.AssistChip
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.material3.Tab
import androidx.compose.runtime.produceState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.draw.clip
import dev.kurokawa.localsend.expressive.platform.platformDeviceModel
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntOffset

private enum class MainTab { Home, Connect, Settings }

// ── UI strings (language setting: system / English / Español) ──────────────

private data class UiStrings(
    val send: String,
    val receive: String,
    val settings: String,
    val connect: String,
    val nearbyDevices: String,
    val selection: String,
    val readyToSend: String,
    val history: String,
    val searching: String,
    val troubleshoot: String,
)

private val EnStrings = UiStrings(
    send = "Send", receive = "Receive", settings = "Settings", connect = "Connect",
    nearbyDevices = "Nearby devices", selection = "Selection",
    readyToSend = "Ready to send", history = "History",
    searching = "Searching for devices…", troubleshoot = "Troubleshoot",
)

private val EsStrings = UiStrings(
    send = "Enviar", receive = "Recibir", settings = "Ajustes", connect = "Conectar",
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
fun AppRoot(controller: LocalSendController, onReady: () -> Unit = {}) {
    val settings by controller.settings.collectAsState()
    LocalSendExpressiveTheme(settings) {
        CompositionLocalProvider(LocalStrings provides stringsFor(settings.language)) {
            var showSplash by remember { mutableStateOf(true) }
            AppScaffold(controller, settings)
            // v0.6.0: ONE splash only. Android has the system splash
            // (held by MainActivity until the first frame), so the
            // in-app overlay only runs where no system splash exists
            // (desktop). Ernest: the double splash "is not normal".
            if (showSplash && settings.enableAnimations && !platformHasSystemSplash()) {
                SplashOverlay { showSplash = false }
            }
            LaunchedEffect(Unit) { onReady() }
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
                MaterialSymbols.Outlined.Send,
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

// ── Scaffold: floating pill nav + connection fan / wide rail ───────────────

@Composable
private fun AppScaffold(controller: LocalSendController, settings: AppSettings) {
    val strings = LocalStrings.current
    var tab by rememberSaveable { mutableStateOf(MainTab.Home) }
    val local by controller.localDevice.collectAsState()
    val localIp by controller.localIpAddress.collectAsState()
    val offer by controller.incomingOffer.collectAsState()
    // One connectivity controller for the whole shell: the fan and the
    // Connect screen must observe the same hotspot / Wi-Fi Direct state.
    val connectivity = remember { ConnectivityController() }
    var showIpDialog by remember { mutableStateOf(false) }
    var pendingScan by remember { mutableStateOf(false) }
    // Device selection is hoisted: the Connect radar can select a
    // device, then the user lands on Home to pick the content.
    var selectedFingerprints by rememberSaveable {
        mutableStateOf<Set<String>>(emptySet())
    }

    val fanActions = remember(connectivity) {
        listOf(
            FanAction("Scan QR code", MaterialSymbols.Outlined.Qr_code_scanner) {
                tab = MainTab.Connect
                pendingScan = true
            },
            FanAction("My connect code", MaterialSymbols.Outlined.Qr_code_2) {
                tab = MainTab.Connect
            },
            FanAction("Hotspot", MaterialSymbols.Outlined.Wifi_tethering) {
                connectivity.toggleHotspot()
            },
            FanAction("Wi-Fi Direct", MaterialSymbols.Outlined.Router) {
                connectivity.toggleWifiDirect()
            },
            FanAction("Wi-Fi", MaterialSymbols.Outlined.Wifi) {
                connectivity.toggleWifi()
            },
            FanAction("Add by IP", MaterialSymbols.Outlined.Lan) {
                showIpDialog = true
            },
        )
    }

    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        val wide = maxWidth >= 840.dp
        val content: @Composable () -> Unit = {
            when (tab) {
                MainTab.Home -> HomeScreen(
                    controller = controller,
                    settings = settings,
                    local = local,
                    onAddByIp = { showIpDialog = true },
                    selectedFingerprints = selectedFingerprints,
                    onSelectedChange = { selectedFingerprints = it },
                )
                MainTab.Connect -> ConnectScreen(
                    controller = controller,
                    connectivity = connectivity,
                    settings = settings,
                    local = local,
                    localIp = localIp,
                    openScanner = pendingScan,
                    onScannerConsumed = { pendingScan = false },
                    onAddByIp = { showIpDialog = true },
                    onPickDevice = { device ->
                        selectedFingerprints =
                            selectedFingerprints + device.info.fingerprint
                        tab = MainTab.Home
                    },
                )
                MainTab.Settings -> SettingsScreen(controller, settings)
            }
        }
        if (wide) {
            // ChromeOS-style shell (Ernest, for the Linux build): rail on a
            // surfaceContainerLow backdrop, content floating in a rounded
            // card. The fan floats over the card's bottom-start corner.
            Row(Modifier.fillMaxSize()) {
                Column(
                    Modifier
                        .width(96.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    LogoMark(44.dp)
                    Spacer(Modifier.height(16.dp))
                    RailNavItem(
                        icon = MaterialSymbols.Outlined.Home,
                        selectedIcon = MaterialSymbols.OutlinedFilled.Home,
                        label = "Home",
                        selected = tab == MainTab.Home,
                    ) { tab = MainTab.Home }
                    RailNavItem(
                        icon = MaterialSymbols.Outlined.Hub,
                        selectedIcon = MaterialSymbols.OutlinedFilled.Hub,
                        label = strings.connect,
                        selected = tab == MainTab.Connect,
                    ) { tab = MainTab.Connect }
                    RailNavItem(
                        icon = MaterialSymbols.Outlined.Settings,
                        selectedIcon = MaterialSymbols.OutlinedFilled.Settings,
                        label = strings.settings,
                        selected = tab == MainTab.Settings,
                    ) { tab = MainTab.Settings }
                }
                Box(Modifier.weight(1f).fillMaxHeight().padding(16.dp)) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        content()
                    }
                    ConnectionFan(
                        actions = fanActions,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp),
                    )
                }
            }
        } else {
            // Phones: content runs edge-to-edge under a floating pill.
            // The pill is an overlay — nothing paints a strip behind it
            // (v0.5.0's black band came from the Scaffold bottomBar slot).
            Box(Modifier.fillMaxSize()) {
                content()
                ConnectionFan(
                    actions = fanActions,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .navigationBarsPadding()
                        .padding(end = 16.dp, bottom = 92.dp),
                )
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 14.dp),
                ) {
                    Row(
                        Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        PillNavItem(
                            icon = MaterialSymbols.Outlined.Home,
                            selectedIcon = MaterialSymbols.OutlinedFilled.Home,
                            label = "Home",
                            selected = tab == MainTab.Home,
                        ) { tab = MainTab.Home }
                        PillNavItem(
                            icon = MaterialSymbols.Outlined.Hub,
                            selectedIcon = MaterialSymbols.OutlinedFilled.Hub,
                            label = strings.connect,
                            selected = tab == MainTab.Connect,
                        ) { tab = MainTab.Connect }
                        PillNavItem(
                            icon = MaterialSymbols.Outlined.Settings,
                            selectedIcon = MaterialSymbols.OutlinedFilled.Settings,
                            label = strings.settings,
                            selected = tab == MainTab.Settings,
                        ) { tab = MainTab.Settings }
                    }
                }
            }
        }
    }

    // Quick Settings tile: jump to Home (the browser there is the picker).
    val quickShareCount by quickShareRequests().collectAsState()
    LaunchedEffect(quickShareCount) {
        if (quickShareCount > 0) tab = MainTab.Home
    }

    offer?.let { IncomingOfferDialog(controller, it) }
    if (showIpDialog) {
        IpAddressDialog(
            onDismiss = { showIpDialog = false },
            onProbe = { address ->
                showIpDialog = false
                controller.probeDevice(address)
            },
        )
    }
}

/** One destination inside the floating pill (mirrors NavigationBarItem anatomy). */
@Composable
private fun PillNavItem(
    icon: ImageVector,
    selectedIcon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    Column(
        Modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .size(width = 56.dp, height = 32.dp)
                .clip(CircleShape)
                .background(if (selected) scheme.secondaryContainer else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            val fillAlpha by animateFloatAsState(
                targetValue = if (selected) 1f else 0f,
                animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMedium),
                label = "navFill",
            )
            Icon(
                icon,
                contentDescription = label,
                tint = if (selected) scheme.onSecondaryContainer else scheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp),
            )
            if (fillAlpha > 0.01f) {
                Icon(
                    selectedIcon,
                    contentDescription = null,
                    tint = scheme.onSecondaryContainer,
                    modifier = Modifier.size(22.dp).alpha(fillAlpha),
                )
            }
        }
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) scheme.onSurface else scheme.onSurfaceVariant,
        )
    }
}

/** One destination in the wide rail. */
@Composable
private fun RailNavItem(
    icon: ImageVector,
    selectedIcon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    Column(
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .size(width = 56.dp, height = 32.dp)
                .clip(CircleShape)
                .background(if (selected) scheme.secondaryContainer else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            val fillAlpha by animateFloatAsState(
                targetValue = if (selected) 1f else 0f,
                animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMedium),
                label = "navFill",
            )
            Icon(
                icon,
                contentDescription = label,
                tint = if (selected) scheme.onSecondaryContainer else scheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp),
            )
            if (fillAlpha > 0.01f) {
                Icon(
                    selectedIcon,
                    contentDescription = null,
                    tint = scheme.onSecondaryContainer,
                    modifier = Modifier.size(22.dp).alpha(fillAlpha),
                )
            }
        }
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) scheme.onSurface else scheme.onSurfaceVariant,
        )
    }
}

// ── Home: devices + tabbed content browsers + staged + transfers ──────────

private sealed interface StagedItem {
    data class FileItem(val file: PickedFile) : StagedItem
    data class FolderItem(val folder: BrowserItem) : StagedItem
    data class TextItem(val text: String) : StagedItem
}

private enum class ContentTab { Apps, Photos, Videos, Audio, Files, Text, Web }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HomeScreen(
    controller: LocalSendController,
    settings: AppSettings,
    local: LocalDevice,
    onAddByIp: () -> Unit,
    selectedFingerprints: Set<String>,
    onSelectedChange: (Set<String>) -> Unit,
) {
    val strings = LocalStrings.current
    val devices by controller.devices.collectAsState()
    val scanning by controller.scanning.collectAsState()
    val incomingProgress by controller.incomingProgress.collectAsState()
    val sendState by controller.sendState.collectAsState()
    val outbox by controller.outbox.collectAsState()
    val history by controller.history.collectAsState()
    val lastSavedTo by controller.lastSavedTo.collectAsState()
    val localIp by controller.localIpAddress.collectAsState()
    val scope = rememberCoroutineScope()

    var staged by remember { mutableStateOf<List<StagedItem>>(emptyList()) }
    var contentTab by rememberSaveable { mutableStateOf(ContentTab.Files) }
    var readyExpanded by rememberSaveable { mutableStateOf(true) }
    var historyExpanded by rememberSaveable { mutableStateOf(false) }
    var collapsedGroups by rememberSaveable { mutableStateOf<Set<String>>(emptySet()) }
    var infoDevice by remember { mutableStateOf<DiscoveredDevice?>(null) }
    var showTroubleshoot by remember { mutableStateOf(false) }
    var editIndex by remember { mutableStateOf(-1) }
    var pin by remember { mutableStateOf("") }
    var lastText by remember { mutableStateOf("") }
    var actionNote by remember { mutableStateOf<String?>(null) }

    // Share sheet: files handed to us by other apps join the staging area.
    val sharedFiles by sharedFilesFlow().collectAsState()
    LaunchedEffect(sharedFiles) {
        if (sharedFiles.isNotEmpty()) {
            staged = staged + sharedFiles.map { StagedItem.FileItem(it) }
            clearSharedFiles()
        }
    }
    // Quick Settings tile: land on the Files browser (the in-app picker).
    val quickShareCount by quickShareRequests().collectAsState()
    LaunchedEffect(quickShareCount) {
        if (quickShareCount > 0) {
            contentTab = ContentTab.Files
            clearQuickShareRequest()
        }
    }

    val targets = devices.filter { it.info.fingerprint in selectedFingerprints }
    val stagedIds = staged.mapNotNull { item ->
        when (item) {
            is StagedItem.FileItem -> "file:${item.file.handle}"
            is StagedItem.FolderItem -> item.folder.id
            is StagedItem.TextItem -> null
        }
    }.toSet()

    fun toggleFile(file: PickedFile) {
        val id = "file:${file.handle}"
        staged = if (stagedIds.contains(id)) {
            staged.filterNot {
                it is StagedItem.FileItem && "file:${it.file.handle}" == id
            }
        } else {
            staged + StagedItem.FileItem(file)
        }
    }

    fun toggleFolder(folder: BrowserItem) {
        staged = if (stagedIds.contains(folder.id)) {
            staged.filterNot {
                it is StagedItem.FolderItem && it.folder.id == folder.id
            }
        } else {
            staged + StagedItem.FolderItem(folder)
        }
    }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 128.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header: wordmark + own alias chip.
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LsexWordmark(Modifier.weight(1f))
                AssistChip(
                    onClick = {},
                    label = { Text(local.alias) },
                    leadingIcon = {
                        Icon(
                            deviceIcon(local.deviceType),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                    },
                )
            }
        }

        // Receive status (compact — the details live in Settings / Connect).
        item {
            GroupCard {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        if (settings.serverEnabled) {
                            "Receiving as ${local.alias}"
                        } else {
                            "Receiving is off — turn the server on in Settings"
                        },
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoChip("Port ${local.port}")
                        InfoChip(if (settings.https) "HTTPS" else "HTTP")
                        InfoChip("Protocol 2.2")
                    }
                    localIp?.let { ip ->
                        Text(
                            "This device: $ip",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    QuickSaveConnectedGroup(
                        options = listOf("Off", "Favorites", "Everyone"),
                        selectedIndex = when {
                            settings.quickSave -> 2
                            settings.autoAcceptFavorites -> 1
                            else -> 0
                        },
                        onSelect = { index ->
                            controller.updateSettings(
                                settings.copy(
                                    quickSave = index == 2,
                                    autoAcceptFavorites = index == 1,
                                ),
                            )
                        },
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Auto Finish",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                        )
                        Switch(
                            checked = settings.autoFinish,
                            onCheckedChange = {
                                controller.updateSettings(settings.copy(autoFinish = it))
                            },
                        )
                    }
                }
            }
        }

        // Incoming transfer progress.
        if (incomingProgress.isNotEmpty()) {
            item {
                GroupCard {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("Receiving…", style = MaterialTheme.typography.titleMedium)
                        incomingProgress.forEach { FileProgressRow(it) }
                    }
                }
            }
        }
        lastSavedTo?.let { saved ->
            item {
                Text(
                    "Last file saved to $saved",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Devices: favorites first, tap toggles the selection.
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionTitle(strings.nearbyDevices)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onAddByIp) {
                    Icon(MaterialSymbols.Outlined.Lan, contentDescription = "Add by IP")
                }
                IconButton(onClick = { controller.refreshDiscovery() }) {
                    Icon(MaterialSymbols.Outlined.Refresh, contentDescription = "Refresh devices")
                }
            }
            Text(
                "Tap to select one or more devices to send to.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        val sortedDevices = devices.sortedByDescending {
            it.info.fingerprint in settings.favorites
        }
        if (sortedDevices.isEmpty()) {
            item {
                Column(
                    Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (scanning || settings.serverEnabled) {
                        LoadingIndicator(Modifier.size(48.dp))
                    }
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
            items(sortedDevices, key = { it.info.fingerprint }) { device ->
                DeviceCard(
                    device = device,
                    isSelected = device.info.fingerprint in selectedFingerprints,
                    isFavorite = device.info.fingerprint in settings.favorites,
                    onClick = {
                        val fp = device.info.fingerprint
                        onSelectedChange(
                            if (fp in selectedFingerprints) {
                                selectedFingerprints - fp
                            } else {
                                selectedFingerprints + fp
                            },
                        )
                    },
                    onToggleFavorite = { controller.toggleFavorite(device.info.fingerprint) },
                    onInfo = { infoDevice = device },
                )
            }
        }

        // Content tabs: each tab SHOWS the content (Zapya / Files layout).
        item {
            Text(
                "What to send",
                style = MaterialTheme.typography.titleLarge,
            )
        }
        item {
            Column {
                ScrollableTabRow(
                    selectedTabIndex = contentTab.ordinal,
                    edgePadding = 0.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    ContentTab.entries.forEach { tab ->
                        Tab(
                            selected = contentTab == tab,
                            onClick = { contentTab = tab },
                            text = {
                                Text(
                                    when (tab) {
                                        ContentTab.Apps -> "Apps"
                                        ContentTab.Photos -> "Photos"
                                        ContentTab.Videos -> "Videos"
                                        ContentTab.Audio -> "Audio"
                                        ContentTab.Files -> "Files"
                                        ContentTab.Text -> "Text"
                                        ContentTab.Web -> "Web"
                                    },
                                    maxLines = 1,
                                )
                            },
                            icon = {
                                Icon(
                                    when (tab) {
                                        ContentTab.Apps -> MaterialSymbols.Outlined.Apps
                                        ContentTab.Photos -> MaterialSymbols.Outlined.Image
                                        ContentTab.Videos -> MaterialSymbols.Outlined.Movie
                                        ContentTab.Audio -> MaterialSymbols.Outlined.Music_note
                                        ContentTab.Files -> MaterialSymbols.Outlined.Folder
                                        ContentTab.Text -> MaterialSymbols.Outlined.Chat_bubble
                                        ContentTab.Web -> MaterialSymbols.Outlined.Link
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                )
                            },
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.fillMaxWidth().height(420.dp),
                ) {
                    Box(Modifier.padding(12.dp)) {
                        when (contentTab) {
                            ContentTab.Apps -> AppsBrowser(
                                settings = settings,
                                onToggleHideSystem = {
                                    controller.updateSettings(
                                        settings.copy(hideSystemApps = !settings.hideSystemApps),
                                    )
                                },
                                stagedIds = stagedIds,
                                onToggleApp = { app ->
                                    toggleFile(
                                        PickedFile(
                                            handle = app.apkHandle,
                                            name = "${app.label}.apk",
                                            size = app.apkSize,
                                        ),
                                    )
                                },
                            )
                            ContentTab.Photos -> MediaBrowser(
                                kind = MediaKind.PHOTOS,
                                stagedIds = stagedIds,
                                onToggleItem = { item ->
                                    toggleFile(
                                        PickedFile(item.handle, item.name, item.size),
                                    )
                                },
                            )
                            ContentTab.Videos -> MediaBrowser(
                                kind = MediaKind.VIDEOS,
                                stagedIds = stagedIds,
                                onToggleItem = { item ->
                                    toggleFile(
                                        PickedFile(item.handle, item.name, item.size),
                                    )
                                },
                            )
                            ContentTab.Audio -> MediaBrowser(
                                kind = MediaKind.AUDIO,
                                stagedIds = stagedIds,
                                onToggleItem = { item ->
                                    toggleFile(
                                        PickedFile(item.handle, item.name, item.size),
                                    )
                                },
                            )
                            ContentTab.Files -> FileBrowser(
                                stagedIds = stagedIds,
                                onToggleFile = { item ->
                                    toggleFile(
                                        PickedFile(item.handle, item.name, item.size),
                                    )
                                },
                                onToggleFolder = { toggleFolder(it) },
                                onStageFile = { file ->
                                    staged = staged + StagedItem.FileItem(file)
                                },
                            )
                            ContentTab.Text -> TextComposer(
                                onStageText = { text ->
                                    staged = staged + StagedItem.TextItem(text)
                                },
                            )
                            ContentTab.Web -> WebSharePanel(controller, settings, local, localIp)
                        }
                    }
                }
            }
        }

        // Ready to send — collapsible, swipe-to-reveal item actions.
        if (staged.isNotEmpty()) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${strings.readyToSend} · ${staged.size}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = { staged = emptyList() }) { Text("Clear all") }
                    IconButton(onClick = { readyExpanded = !readyExpanded }) {
                        Icon(
                            if (readyExpanded) {
                                MaterialSymbols.Outlined.Keyboard_arrow_up
                            } else {
                                MaterialSymbols.Outlined.Keyboard_arrow_down
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
                        onEdit = { editIndex = index },
                        onDiscard = { staged = staged.filterIndexed { i, _ -> i != index } },
                    )
                }
            }
            item {
                Button(
                    onClick = {
                        val files = staged.filterIsInstance<StagedItem.FileItem>()
                            .map { it.file }
                        val folders = staged.filterIsInstance<StagedItem.FolderItem>()
                            .map { it.folder }
                        val texts = staged.filterIsInstance<StagedItem.TextItem>()
                            .map { it.text }
                        scope.launch {
                            var allFiles = files
                            for (folder in folders) {
                                allFiles = allFiles + expandBrowserFolder(folder)
                            }
                            if (allFiles.isNotEmpty()) {
                                controller.sendFilesToTargets(targets, allFiles)
                            }
                            texts.forEach { controller.sendTextToTargets(targets, it) }
                            if (texts.isNotEmpty()) lastText = texts.last()
                            staged = emptyList()
                        }
                    },
                    enabled = targets.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(MaterialSymbols.Outlined.Send, contentDescription = null)
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

        // Text sends keep an ephemeral card (PIN retry lives here).
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
                                onClick = { controller.sendText(state.target, lastText, pin) },
                                enabled = lastText.isNotBlank(),
                                modifier = Modifier.fillMaxWidth(),
                            ) { Text("Retry with PIN") }
                        }
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

        // Outbox, grouped per recipient device.
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
                            Icon(MaterialSymbols.Outlined.Replay, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Retry all")
                        }
                    }
                }
            }
            val groups = outbox.groupBy { it.targetFingerprint }
            groups.forEach { (fingerprint, jobs) ->
                val first = jobs.first()
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                collapsedGroups = if (fingerprint in collapsedGroups) {
                                    collapsedGroups - fingerprint
                                } else {
                                    collapsedGroups + fingerprint
                                }
                            }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            deviceIcon(deviceTypeOf(first.targetType)),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                first.targetAlias,
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Text(
                                "${jobs.size} send(s) · " +
                                    formatSize(jobs.sumOf { job -> job.files.sumOf { it.size } }),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Icon(
                            if (fingerprint in collapsedGroups) {
                                MaterialSymbols.Outlined.Keyboard_arrow_down
                            } else {
                                MaterialSymbols.Outlined.Keyboard_arrow_up
                            },
                            contentDescription = null,
                        )
                    }
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    )
                }
                if (fingerprint !in collapsedGroups) {
                    items(jobs, key = { it.id }) { job ->
                        OutboxJobCard(controller, job, grouped = true)
                    }
                }
            }
        }

        // History — collapsible, with Open / Install / Resend.
        if (history.isNotEmpty()) {
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { historyExpanded = !historyExpanded },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "${strings.history} · ${history.size}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        if (historyExpanded) {
                            MaterialSymbols.Outlined.Keyboard_arrow_up
                        } else {
                            MaterialSymbols.Outlined.Keyboard_arrow_down
                        },
                        contentDescription = if (historyExpanded) "Collapse" else "Expand",
                    )
                }
            }
            if (historyExpanded) {
                items(history, key = { "${it.timeMillis}:${it.fileName}" }) { entry ->
                    HistoryRow(
                        entry = entry,
                        onOpen = { openReceivedFile(entry.uri, entry.fileName) },
                        onInstall = {
                            scope.launch {
                                val result = withContext(Dispatchers.IO) {
                                    installPackage(entry.uri, entry.fileName)
                                }
                                actionNote = result
                                    ?: "Installer opened for ${entry.fileName}"
                            }
                        },
                        onResend = {
                            staged = staged + StagedItem.FileItem(
                                PickedFile(entry.handle, entry.fileName, entry.size),
                            )
                            actionNote = "${entry.fileName} is staged — pick a device and send."
                        },
                    )
                }
            }
            actionNote?.let { note ->
                item {
                    Text(
                        note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }

    if (editIndex in staged.indices) {
        when (val item = staged[editIndex]) {
            is StagedItem.FileItem -> {
                val path = item.file.displayPath
                StagedEditDialog(
                    title = "Rename",
                    initial = path?.substringAfterLast('/') ?: item.file.name,
                    onDismiss = { editIndex = -1 },
                    onSave = { newName ->
                        val renamed = if (path != null) {
                            val parent = path.substringBeforeLast('/', "")
                            item.file.copy(
                                name = newName,
                                displayPath = if (parent.isEmpty()) {
                                    newName
                                } else {
                                    "$parent/$newName"
                                },
                            )
                        } else {
                            item.file.copy(name = newName)
                        }
                        staged = staged.toMutableList().also {
                            it[editIndex] = StagedItem.FileItem(renamed)
                        }
                        editIndex = -1
                    },
                )
            }
            is StagedItem.TextItem -> StagedEditDialog(
                title = "Edit text",
                initial = item.text,
                multiline = true,
                onDismiss = { editIndex = -1 },
                onSave = { newText ->
                    staged = staged.toMutableList().also {
                        it[editIndex] = StagedItem.TextItem(newText)
                    }
                    editIndex = -1
                },
            )
            is StagedItem.FolderItem -> Unit // folders have no edit action
        }
    }
    infoDevice?.let { device ->
        DeviceInfoDialog(
            device = device,
            isFavorite = device.info.fingerprint in settings.favorites,
            onToggleFavorite = { controller.toggleFavorite(device.info.fingerprint) },
            onDismiss = { infoDevice = null },
        )
    }
    if (showTroubleshoot) {
        TroubleshootDialog(onDismiss = { showTroubleshoot = false })
    }
}

// ── Content browsers: the tabs SHOW the content (Zapya / Files layout) ─────

/** Tiny common-code date formatter (yyyy-MM-dd) from epoch millis, UTC. */
private fun formatDate(millis: Long): String {
    if (millis <= 0) return ""
    val z = millis / 86_400_000 + 719_468
    val era = if (z >= 0) z / 146_097 else (z - 146_096) / 146_097
    val doe = z - era * 146_097
    val yoe = (doe - doe / 1460 + doe / 36_524 - doe / 146_096) / 365
    val y = yoe + era * 400
    val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
    val mp = (5 * doy + 2) / 153
    val d = doy - (153 * mp + 2) / 5 + 1
    val m = if (mp < 10) mp + 3 else mp - 9
    val year = if (m <= 2) y + 1 else y
    fun p2(v: Long): String = v.toString().padStart(2, '0')
    return "$year-${p2(m)}-${p2(d)}"
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AppsBrowser(
    settings: AppSettings,
    onToggleHideSystem: () -> Unit,
    stagedIds: Set<String>,
    onToggleApp: (InstalledApp) -> Unit,
) {
    val apps by produceState<List<InstalledApp>>(emptyList()) {
        value = withContext(Dispatchers.IO) { loadInstalledApps() }
    }
    var query by remember { mutableStateOf("") }
    val visible = apps.filter { app ->
        (!settings.hideSystemApps || !app.isSystem) &&
            (query.isBlank() || app.label.contains(query, ignoreCase = true) ||
                app.packageName.contains(query, ignoreCase = true))
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search apps") },
            leadingIcon = { Icon(MaterialSymbols.Outlined.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "My apps (${visible.size})",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f),
            )
            Text("Hide system", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.width(8.dp))
            Switch(checked = settings.hideSystemApps, onCheckedChange = { onToggleHideSystem() })
        }
        if (apps.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator(Modifier.size(40.dp))
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                items(visible, key = { it.packageName }) { app ->
                    val checked = "file:${app.apkHandle}" in stagedIds
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onToggleApp(app) }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AppIcon(app.packageName, Modifier.size(42.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(app.label, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                formatSize(app.apkSize),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Checkbox(checked = checked, onCheckedChange = { onToggleApp(app) })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MediaBrowser(
    kind: MediaKind,
    stagedIds: Set<String>,
    onToggleItem: (BrowserItem) -> Unit,
) {
    val access = rememberStorageAccess()
    if (!access.mediaGranted) {
        GrantAccessCard(
            icon = MaterialSymbols.Outlined.Photo_library,
            title = "Media access needed",
            body = "To browse your photos, videos and music here, LSEx needs " +
                "permission to read the media library.",
            actionLabel = "Grant access",
            onAction = access.requestMedia,
            onRecheck = access.recheck,
        )
        return
    }
    var items by remember(kind) { mutableStateOf<List<BrowserItem>?>(null) }
    LaunchedEffect(kind) { items = loadMediaItems(kind) }
    val loaded = items
    when {
        loaded == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LoadingIndicator(Modifier.size(44.dp))
        }
        loaded.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "Nothing here yet.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        kind == MediaKind.AUDIO -> LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            items(loaded, key = { it.id }) { item ->
                val checked = "file:${item.handle}" in stagedIds
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onToggleItem(item) }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BrowserThumbnail(
                        item,
                        Modifier.size(42.dp).clip(RoundedCornerShape(10.dp)),
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            item.name,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            formatSize(item.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Checkbox(checked = checked, onCheckedChange = { onToggleItem(item) })
                }
            }
        }
        else -> LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(loaded, key = { it.id }) { item ->
                val checked = "file:${item.handle}" in stagedIds
                Box(
                    Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onToggleItem(item) },
                ) {
                    BrowserThumbnail(item, Modifier.fillMaxSize())
                    if (checked) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                ),
                        )
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .size(22.dp),
                        ) {
                            Icon(
                                MaterialSymbols.Outlined.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(3.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FileBrowser(
    stagedIds: Set<String>,
    onToggleFile: (BrowserItem) -> Unit,
    onToggleFolder: (BrowserItem) -> Unit,
    onStageFile: (PickedFile) -> Unit,
) {
    val access = rememberStorageAccess()
    val pickFiles = rememberFilePickerLauncher { picked ->
        picked.forEach { onStageFile(it) }
    }
    if (!access.fullAccess) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            GrantAccessCard(
                icon = MaterialSymbols.Outlined.Folder,
                title = "Browse your storage",
                body = "With All files access, this tab becomes a file manager: " +
                    "drill into folders and pick anything, like Zapya. Without it, " +
                    "use the system picker.",
                actionLabel = "Grant all-files access",
                onAction = access.requestFullAccess,
                onRecheck = access.recheck,
            )
            OutlinedButton(
                onClick = pickFiles,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(MaterialSymbols.Outlined.Attach_file, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Pick files with the system picker")
            }
        }
        return
    }
    var pathStack by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    val currentPath = pathStack.lastOrNull()
    var entries by remember { mutableStateOf<List<BrowserItem>?>(null) }
    LaunchedEffect(currentPath) {
        entries = if (currentPath == null) {
            storageRoots()
        } else {
            listBrowserFolder(currentPath)
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Breadcrumb path bar: tap a crumb to jump back up.
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = { pathStack = emptyList() }) {
                Text("Storage")
            }
            pathStack.forEachIndexed { index, path ->
                Text("/", color = MaterialTheme.colorScheme.onSurfaceVariant)
                TextButton(onClick = { pathStack = pathStack.take(index + 1) }) {
                    Text(path.substringAfterLast('/').ifEmpty { path })
                }
            }
        }
        val loaded = entries
        when {
            loaded == null -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                LoadingIndicator(Modifier.size(44.dp))
            }
            loaded.isEmpty() -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "This folder is empty.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                items(loaded, key = { it.id }) { item ->
                    val fileChecked = "file:${item.handle}" in stagedIds
                    val folderChecked = item.id in stagedIds
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                if (item.isFolder && currentPath == null) {
                                    pathStack = listOf(item.handle)
                                } else if (item.isFolder) {
                                    pathStack = pathStack + item.handle
                                } else {
                                    onToggleFile(item)
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (item.isFolder) {
                            Icon(
                                MaterialSymbols.Outlined.Folder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp).padding(6.dp),
                            )
                        } else {
                            BrowserThumbnail(
                                item,
                                Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)),
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                item.name,
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                if (item.isFolder) {
                                    "${item.itemCount} items"
                                } else {
                                    listOf(
                                        formatSize(item.size),
                                        formatDate(item.dateMillis),
                                    ).filter { it.isNotEmpty() }.joinToString(" · ")
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        if (currentPath != null || !item.isFolder) {
                            Checkbox(
                                checked = if (item.isFolder) folderChecked else fileChecked,
                                onCheckedChange = {
                                    if (item.isFolder) {
                                        onToggleFolder(item)
                                    } else {
                                        onToggleFile(item)
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GrantAccessCard(
    icon: ImageVector,
    title: String,
    body: String,
    actionLabel: String,
    onAction: () -> Unit,
    onRecheck: () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(44.dp),
        )
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(6.dp))
        Text(
            body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(14.dp))
        Button(onClick = onAction) { Text(actionLabel) }
        TextButton(onClick = onRecheck) { Text("Recheck") }
    }
}

@Composable
private fun TextComposer(onStageText: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    val clipboard = rememberClipboardText()
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Write a message") },
            minLines = 4,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { clipboard()?.let { text = it } },
                modifier = Modifier.weight(1f),
            ) {
                Icon(MaterialSymbols.Outlined.Content_paste, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Paste")
            }
            Button(
                onClick = {
                    onStageText(text)
                    text = ""
                },
                enabled = text.isNotBlank(),
                modifier = Modifier.weight(1f),
            ) {
                Icon(MaterialSymbols.Outlined.Add, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Add text")
            }
        }
        Text(
            "Text is staged like a file — pick the devices above, then send to all of them.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ── Staged item cards: swipe-to-reveal actions (official list guidance) ────

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun StagedItemCard(
    item: StagedItem,
    onEdit: () -> Unit,
    onDiscard: () -> Unit,
) {
    if (!prefersSwipeActions()) {
        // Desktop (pointer): actions stay visible, like v0.5.0.
        StagedItemCardContent(item, onEdit, onDiscard, showActions = true)
        return
    }
    // Touch: Edit / Discard hide behind a left swipe and stay revealed
    // (MD3 list guidance: primary action at the very end, and a second
    // access path — a long press opens the editor directly).
    val density = LocalDensity.current
    val revealPx = with(density) { 124.dp.toPx() }
    val state = remember { AnchoredDraggableState(initialValue = 0) }
    LaunchedEffect(revealPx) {
        state.updateAnchors(DraggableAnchors {
            0 at 0f
            1 at -revealPx
        })
    }
    val scope = rememberCoroutineScope()
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        Row(
            Modifier.matchParentSize().padding(end = 10.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (item !is StagedItem.FolderItem) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    IconButton(
                        onClick = {
                            scope.launch { state.snapTo(0) }
                            onEdit()
                        },
                        modifier = Modifier.size(44.dp),
                    ) {
                        Icon(
                            MaterialSymbols.Outlined.Edit,
                            contentDescription = "Rename / edit",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
            }
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.errorContainer,
            ) {
                IconButton(
                    onClick = onDiscard,
                    modifier = Modifier.size(44.dp),
                ) {
                    Icon(
                        MaterialSymbols.Outlined.Delete,
                        contentDescription = "Discard",
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }
        }
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(state.requireOffset().roundToInt(), 0) }
                .anchoredDraggable(state, Orientation.Horizontal)
                .combinedClickable(
                    onClick = {
                        if (state.currentValue == 1) {
                            scope.launch { state.snapTo(0) }
                        }
                    },
                    onLongClick = {
                        if (item !is StagedItem.FolderItem) onEdit()
                    },
                ),
        ) {
            StagedItemCardContent(item, onEdit, onDiscard, showActions = false)
        }
    }
}

@Composable
private fun StagedItemCardContent(
    item: StagedItem,
    onEdit: () -> Unit,
    onDiscard: () -> Unit,
    showActions: Boolean,
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
                        is StagedItem.FileItem -> MaterialSymbols.Outlined.Attach_file
                        is StagedItem.FolderItem -> MaterialSymbols.Outlined.Folder
                        is StagedItem.TextItem -> MaterialSymbols.Outlined.Chat
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
                    is StagedItem.FolderItem -> item.folder.name
                    is StagedItem.TextItem -> item.text
                },
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                when (item) {
                    is StagedItem.FileItem -> formatSize(item.file.size)
                    is StagedItem.FolderItem ->
                        if (item.folder.itemCount >= 0) {
                            "Folder · ${item.folder.itemCount} items"
                        } else {
                            "Folder"
                        }
                    is StagedItem.TextItem ->
                        "Text · ${formatSize(item.text.encodeToByteArray().size.toLong())}"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (showActions) {
            if (item !is StagedItem.FolderItem) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                ) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(38.dp)) {
                        Icon(
                            MaterialSymbols.Outlined.Edit,
                            contentDescription = "Rename / edit",
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
                Spacer(Modifier.width(6.dp))
            }
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
            ) {
                IconButton(onClick = onDiscard, modifier = Modifier.size(38.dp)) {
                    Icon(
                        MaterialSymbols.Outlined.Close,
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
                    MaterialSymbols.Outlined.Check_circle,
                    contentDescription = "Selected",
                    tint = scheme.primary,
                )
            }
            IconButton(onClick = onInfo) {
                Icon(
                    MaterialSymbols.Outlined.Info,
                    contentDescription = "Device info",
                    tint = subtitleColor,
                )
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    if (isFavorite) MaterialSymbols.OutlinedFilled.Star else MaterialSymbols.Outlined.Star,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) scheme.primary else subtitleColor,
                )
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
                MaterialSymbols.Outlined.Link,
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

// ── Connect: Zapya-organised connection view ───────────────────────────────

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ConnectScreen(
    controller: LocalSendController,
    connectivity: ConnectivityController,
    settings: AppSettings,
    local: LocalDevice,
    localIp: String?,
    openScanner: Boolean,
    onScannerConsumed: () -> Unit,
    onAddByIp: () -> Unit,
    onPickDevice: (DiscoveredDevice) -> Unit,
) {
    val strings = LocalStrings.current
    val devices by controller.devices.collectAsState()
    val scanning by controller.scanning.collectAsState()
    val connState by connectivity.state.collectAsState()
    val scope = rememberCoroutineScope()
    var showScanner by remember { mutableStateOf(false) }
    var showConnectQr by remember { mutableStateOf(false) }
    var scanNote by remember { mutableStateOf<String?>(null) }
    var infoDevice by remember { mutableStateOf<DiscoveredDevice?>(null) }
    LaunchedEffect(Unit) { connectivity.refresh() }
    LaunchedEffect(openScanner) {
        if (openScanner) {
            showScanner = true
            onScannerConsumed()
        }
    }

    val hotspot = connState.hotspot
    val direct = connState.wifiDirect
    val groupActive = hotspot != null || direct != null
    // QR rule (Ernest): a code is only shown when it carries information
    // discovery can't give you — the credentials of a group WE created.
    // The plain connect code exists behind an explicit "Show QR" tap.
    val groupQr = when {
        hotspot != null -> wifiQrPayload(hotspot.ssid, hotspot.password)
        direct != null -> wifiQrPayload(direct.ssid, direct.password)
        else -> null
    }
    val groupQrCaption = when {
        hotspot != null -> "Scan to join “${hotspot.ssid}”"
        direct != null -> "Scan to join “${direct.ssid}”"
        else -> ""
    }
    val activePassword = hotspot?.password ?: direct?.password

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 128.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column {
                Text(strings.connect, style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Create a group, join one, or find devices on your network.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Current network strip (Zapya's radar header).
        item {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(MaterialSymbols.Outlined.Wifi, contentDescription = null)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        localIp?.let { "This device: $it" } ?: "Not on a network",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                    InfoChip("Alias: ${local.alias}")
                }
            }
        }

        // Create / Join — Zapya's two big actions.
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    onClick = { connectivity.toggleHotspot() },
                    colors = CardDefaults.cardColors(
                        containerColor = if (groupActive) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        },
                    ),
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier.weight(1f).height(132.dp),
                ) {
                    Column(
                        Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(52.dp),
                        ) {
                            Icon(
                                MaterialSymbols.Outlined.Group_add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(13.dp),
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            if (groupActive) "Group is on" else "Create group",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            if (groupActive) {
                                "Tap to stop the hotspot"
                            } else {
                                "Start a hotspot others join"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                Card(
                    onClick = { showScanner = true },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    ),
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier.weight(1f).height(132.dp),
                ) {
                    Column(
                        Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(52.dp),
                        ) {
                            Icon(
                                MaterialSymbols.Outlined.Qr_code_scanner,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiary,
                                modifier = Modifier.padding(13.dp),
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        Text("Join group", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Scan the host's QR code",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }

        // Group QR: only while a group is up (it carries the credentials).
        if (groupQr != null) {
            item {
                GroupCard {
                    Column(
                        Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            QrCode(groupQr)
                        }
                        Text(
                            groupQrCaption,
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
                }
            }
        }

        // Radar: discovered devices as tappable bubbles.
        item {
            SectionTitle("Nearby devices")
            RadarView(
                devices = devices,
                scanning = scanning && settings.enableAnimations,
                onDeviceTap = { infoDevice = it },
            )
            if (devices.isEmpty()) {
                Text(
                    "No devices yet — both devices need the same Wi-Fi, " +
                        "or create a group above.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    devices.forEach { device ->
                        Surface(
                            shape = MaterialTheme.shapes.large,
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    deviceIcon(device.info.deviceType),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        device.info.alias,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                    Text(
                                        device.ip,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Button(onClick = { onPickDevice(device) }) {
                                    Text("Select")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Radios: the connectivity tiles from the old card.
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionTitle("Radios")
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { connectivity.refresh() }) {
                    Icon(MaterialSymbols.Outlined.Refresh, contentDescription = "Refresh connectivity")
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ConnectivityTile(
                    icon = if (connState.wifiEnabled == false) {
                        MaterialSymbols.Outlined.Wifi_off
                    } else {
                        MaterialSymbols.Outlined.Wifi
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
                    icon = MaterialSymbols.Outlined.Wifi_tethering,
                    label = "Hotspot",
                    active = hotspot != null,
                    modifier = Modifier.weight(1f),
                ) { connectivity.toggleHotspot() }
                ConnectivityTile(
                    icon = MaterialSymbols.Outlined.Router,
                    label = "WiFi Direct",
                    active = direct != null,
                    modifier = Modifier.weight(1f),
                ) { connectivity.toggleWifiDirect() }
            }
        }
        if (connState.busy) {
            item {
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
        }
        connState.message?.let { message ->
            item {
                Text(
                    message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Scan / manual connect / on-demand connect code.
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { showScanner = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(MaterialSymbols.Outlined.Qr_code_scanner, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Scan QR — join WiFi, open a web share, connect")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onAddByIp,
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(MaterialSymbols.Outlined.Lan, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Add by IP")
                    }
                    OutlinedButton(
                        onClick = { showConnectQr = !showConnectQr },
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(MaterialSymbols.Outlined.Qr_code_2, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text(if (showConnectQr) "Hide my QR" else "Show my QR")
                    }
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
        if (showConnectQr && groupQr == null && localIp != null) {
            item {
                GroupCard {
                    Column(
                        Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            QrCode("lsex://connect?host=$localIp&port=${local.port}")
                        }
                        Text(
                            "Scan with LSEx to connect to ${local.alias}",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }

    infoDevice?.let { device ->
        DeviceInfoDialog(
            device = device,
            isFavorite = device.info.fingerprint in settings.favorites,
            onToggleFavorite = { controller.toggleFavorite(device.info.fingerprint) },
            onDismiss = { infoDevice = null },
        )
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

/** Concentric-ring radar with a slow sweep and device bubbles (Zapya layout). */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun RadarView(
    devices: List<DiscoveredDevice>,
    scanning: Boolean,
    onDeviceTap: (DiscoveredDevice) -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val sweep = rememberInfiniteTransition(label = "radar")
    val angle by sweep.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
        ),
        label = "sweep",
    )
    BoxWithConstraints(
        Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(scheme.surfaceContainerHigh),
    ) {
        val centerX = maxWidth / 2
        val centerY = maxHeight / 2
        val maxRadius = minOf(maxWidth, maxHeight) / 2 - 14.dp
        Canvas(Modifier.fillMaxSize()) {
            val c = Offset(size.width / 2, size.height / 2)
            val rMax = size.minDimension / 2 - 14.dp.toPx()
            listOf(0.33f, 0.66f, 1f).forEach { frac ->
                drawCircle(
                    color = scheme.onSurfaceVariant.copy(alpha = 0.25f),
                    radius = rMax * frac,
                    center = c,
                    style = Stroke(width = 1.dp.toPx()),
                )
            }
            if (scanning) {
                drawArc(
                    color = scheme.primary.copy(alpha = 0.28f),
                    startAngle = angle,
                    sweepAngle = 70f,
                    useCenter = true,
                    topLeft = Offset(c.x - rMax, c.y - rMax),
                    size = Size(rMax * 2, rMax * 2),
                )
            }
            drawCircle(color = scheme.primary, radius = 4.dp.toPx(), center = c)
        }
        if (devices.isEmpty() && scanning) {
            LoadingIndicator(
                Modifier
                    .align(Alignment.Center)
                    .size(44.dp),
            )
        }
        devices.forEach { device ->
            // Stable pseudo-position from the fingerprint, so bubbles
            // don't jump around between recompositions.
            val hash = device.info.fingerprint.hashCode()
            val bubbleAngle = (hash % 360).toFloat()
            val radiusFrac = 0.45f + ((hash / 360) % 40) / 100f
            val rad = bubbleAngle.toDouble() * (kotlin.math.PI / 180.0)
            val dx = centerX + (maxRadius * radiusFrac) * kotlin.math.cos(rad).toFloat() - 30.dp
            val dy = centerY + (maxRadius * radiusFrac) * kotlin.math.sin(rad).toFloat() - 30.dp
            Column(
                Modifier
                    .offset(x = dx, y = dy)
                    .width(60.dp)
                    .clip(CircleShape)
                    .clickable { onDeviceTap(device) },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Surface(
                    shape = CircleShape,
                    color = scheme.primaryContainer,
                    modifier = Modifier.size(44.dp),
                ) {
                    Icon(
                        deviceIcon(device.info.deviceType),
                        contentDescription = device.info.alias,
                        tint = scheme.onPrimaryContainer,
                        modifier = Modifier.padding(10.dp),
                    )
                }
                Text(
                    device.info.alias,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
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

// ── Settings: Android-style segmented lists, vivid icon tones ──────────────

/**
 * One vivid icon tone (Android Settings recipe, Ernest's reference
 * screenshot): a saturated light fill with a darker glyph of the same
 * hue. Fixed hues — dynamic color must not wash them out.
 */
private data class IconTone(val fill: Color, val glyph: Color)

private val ToneSky = IconTone(Color(0xFF8FD3FF), Color(0xFF0B4A6F))
private val ToneBlue = IconTone(Color(0xFFA8C7FA), Color(0xFF17418C))
private val TonePink = IconTone(Color(0xFFF8BBD0), Color(0xFF880E4F))
private val ToneMagenta = IconTone(Color(0xFFF48FB1), Color(0xFF6A0DAD))
private val ToneOrange = IconTone(Color(0xFFFFCC80), Color(0xFF7A3E00))
private val TonePeach = IconTone(Color(0xFFFFAB91), Color(0xFF7A2E0E))
private val ToneYellow = IconTone(Color(0xFFFFEE58), Color(0xFF6D5C00))
private val ToneGreen = IconTone(Color(0xFFA5D6A7), Color(0xFF1B5E20))
private val ToneTeal = IconTone(Color(0xFF80CBC4), Color(0xFF004D40))
private val TonePurple = IconTone(Color(0xFFCE93D8), Color(0xFF4A148C))
private val ToneRed = IconTone(Color(0xFFB3261E), Color(0xFFFFFFFF))
private val ToneGray = IconTone(Color(0xFFBDBDBD), Color(0xFF212121))

@Composable
private fun SettingIconChip(icon: ImageVector, tone: IconTone) {
    Surface(
        shape = CircleShape,
        color = tone.fill,
        modifier = Modifier.size(44.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                icon,
                contentDescription = null,
                tint = tone.glyph,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

/**
 * MD3E segmented list: rows are tiles separated by 2dp gaps of the
 * background — outer corners 20dp, inner corners 6dp, no dividers.
 */
@Composable
private fun SegmentedList(rows: List<@Composable () -> Unit>) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        rows.forEachIndexed { index, row ->
            val top = if (index == 0) 20.dp else 6.dp
            val bottom = if (index == rows.lastIndex) 20.dp else 6.dp
            Surface(
                shape = RoundedCornerShape(
                    topStart = top, topEnd = top,
                    bottomStart = bottom, bottomEnd = bottom,
                ),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier.fillMaxWidth(),
            ) {
                row()
            }
        }
    }
}

@Composable
private fun SwitchRow(
    icon: ImageVector,
    tone: IconTone,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingIconChip(icon, tone)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownRow(
    icon: ImageVector,
    tone: IconTone,
    title: String,
    options: List<Pair<String, String>>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        Row(
            Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SettingIconChip(icon, tone)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                Text(
                    options.firstOrNull { it.first == selected }?.second ?: selected,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                if (expanded) MaterialSymbols.Outlined.Expand_less else MaterialSymbols.Outlined.Expand_more,
                contentDescription = null,
            )
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { (value, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    trailingIcon = {
                        if (value == selected) {
                            Icon(MaterialSymbols.Outlined.Check, contentDescription = "Selected")
                        }
                    },
                    onClick = {
                        expanded = false
                        onSelect(value)
                    },
                )
            }
        }
    }
}

@Composable
private fun SettingsScreen(controller: LocalSendController, settings: AppSettings) {
    val strings = LocalStrings.current
    val local by controller.localDevice.collectAsState()
    var advanced by rememberSaveable { mutableStateOf(false) }
    val destinationPicker = rememberDestinationPickerLauncher { picked ->
        if (usesSafDestination()) {
            controller.updateSettings(settings.copy(destinationTreeUri = picked))
        } else {
            controller.updateSettings(settings.copy(destinationFolder = picked))
        }
    }
    var aliasText by remember(settings.alias) { mutableStateOf(settings.alias) }
    var portText by remember(settings.port) { mutableStateOf(settings.port.toString()) }
    var pinText by remember(settings.pin) { mutableStateOf(settings.pin) }
    var timeoutText by remember(settings.discoveryTimeoutSec) {
        mutableStateOf(settings.discoveryTimeoutSec.toString())
    }
    var multicastText by remember(settings.multicastAddress) {
        mutableStateOf(settings.multicastAddress)
    }
    var whitelistText by remember(settings.networkWhitelist) {
        mutableStateOf(settings.networkWhitelist)
    }
    var blacklistText by remember(settings.networkBlacklist) {
        mutableStateOf(settings.networkBlacklist)
    }
    var destinationText by remember(settings.destinationFolder) {
        mutableStateOf(settings.destinationFolder)
    }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 128.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        item {
            Text(strings.settings, style = MaterialTheme.typography.headlineMedium)
        }

        // ── General ──
        item { SectionTitle("General") }
        item {
            val rows = mutableListOf<@Composable () -> Unit>(
                {
                    DropdownRow(
                        icon = MaterialSymbols.OutlinedFilled.Dark_mode,
                        tone = ToneBlue,
                        title = "Theme",
                        options = listOf(
                            "system" to "System",
                            "light" to "Light",
                            "dark" to "Dark",
                        ),
                        selected = settings.themeMode,
                        onSelect = {
                            controller.updateSettings(settings.copy(themeMode = it))
                        },
                    )
                },
                {
                    DropdownRow(
                        icon = MaterialSymbols.OutlinedFilled.Palette,
                        tone = ToneMagenta,
                        title = "Colors",
                        options = listOf(
                            "dynamic" to "Wallpaper",
                            "brand" to "LSEx teal",
                        ),
                        selected = settings.colorMode,
                        onSelect = {
                            controller.updateSettings(settings.copy(colorMode = it))
                        },
                    )
                },
                {
                    DropdownRow(
                        icon = MaterialSymbols.OutlinedFilled.Language,
                        tone = ToneGray,
                        title = "Language",
                        options = listOf(
                            "system" to "System",
                            "en" to "English",
                            "es" to "Español",
                        ),
                        selected = settings.language,
                        onSelect = {
                            controller.updateSettings(settings.copy(language = it))
                        },
                    )
                },
                {
                    SwitchRow(
                        icon = MaterialSymbols.OutlinedFilled.Animation,
                        tone = ToneTeal,
                        title = "Animations",
                        subtitle = "Splash and expressive motion",
                        checked = settings.enableAnimations,
                        onCheckedChange = {
                            controller.updateSettings(settings.copy(enableAnimations = it))
                        },
                    )
                },
            )
            if (supportsQuickShareTile()) {
                rows += {
                    Row(
                        Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SettingIconChip(MaterialSymbols.OutlinedFilled.Share, ToneSky)
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Quick share tile", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "Add the LSEx tile from your Quick Settings panel " +
                                    "editor to start a share in one tap.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
            rows += {
                SwitchRow(
                    icon = MaterialSymbols.OutlinedFilled.Tune,
                    tone = ToneRed,
                    title = "Advanced settings",
                    subtitle = "Show the advanced sections, like the official app",
                    checked = advanced,
                    onCheckedChange = { advanced = it },
                )
            }
            SegmentedList(rows)
        }

        // ── Desktop (window behaviour) ──
        if (platformDeviceType() == DeviceType.desktop) {
            item { SectionTitle("Desktop") }
            item {
                SegmentedList(
                    listOf(
                        {
                            SwitchRow(
                                icon = MaterialSymbols.OutlinedFilled.Push_pin,
                                tone = ToneOrange,
                                title = "Always on top",
                                checked = settings.alwaysOnTop,
                                onCheckedChange = {
                                    controller.updateSettings(settings.copy(alwaysOnTop = it))
                                },
                            )
                        },
                        {
                            SwitchRow(
                                icon = MaterialSymbols.OutlinedFilled.Save,
                                tone = ToneGreen,
                                title = "Save window placement",
                                checked = settings.saveWindowPlacement,
                                onCheckedChange = {
                                    controller.updateSettings(
                                        settings.copy(saveWindowPlacement = it),
                                    )
                                },
                            )
                        },
                        {
                            SwitchRow(
                                icon = MaterialSymbols.OutlinedFilled.Web_asset,
                                tone = TonePurple,
                                title = "Minimize to tray",
                                checked = settings.minimizeToTray,
                                onCheckedChange = {
                                    controller.updateSettings(
                                        settings.copy(minimizeToTray = it),
                                    )
                                },
                            )
                        },
                    ),
                )
            }
        }

        // ── Receive ──
        item { SectionTitle(strings.receive) }
        item {
            val rows = mutableListOf<@Composable () -> Unit>(
                {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("Quick Save", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "Skip the accept prompt — for everyone, or only for favorites.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        QuickSaveConnectedGroup(
                            options = listOf("Off", "Favorites", "Everyone"),
                            selectedIndex = when {
                                settings.quickSave -> 2
                                settings.autoAcceptFavorites -> 1
                                else -> 0
                            },
                            onSelect = { index ->
                                controller.updateSettings(
                                    settings.copy(
                                        quickSave = index == 2,
                                        autoAcceptFavorites = index == 1,
                                    ),
                                )
                            },
                        )
                    }
                },
                {
                    SwitchRow(
                        icon = MaterialSymbols.OutlinedFilled.Pin,
                        tone = ToneYellow,
                        title = "Require PIN",
                        subtitle = if (settings.pin.isNotEmpty()) {
                            "Senders must enter your PIN (set in Network)"
                        } else {
                            "Anyone on the network can offer files"
                        },
                        checked = settings.pin.isNotEmpty(),
                        onCheckedChange = { enabled ->
                            controller.updateSettings(
                                settings.copy(
                                    pin = if (enabled) settings.pin.ifEmpty { "1234" } else "",
                                ),
                            )
                        },
                    )
                },
                {
                    SwitchRow(
                        icon = MaterialSymbols.OutlinedFilled.Photo_library,
                        tone = TonePink,
                        title = "Save to Gallery",
                        subtitle = "Photos and videos go to Pictures / Movies",
                        checked = settings.saveToGallery,
                        onCheckedChange = {
                            controller.updateSettings(settings.copy(saveToGallery = it))
                        },
                    )
                },
                {
                    SwitchRow(
                        icon = MaterialSymbols.OutlinedFilled.Done_all,
                        tone = ToneGreen,
                        title = "Auto Finish",
                        subtitle = "Close the receive progress when a transfer ends",
                        checked = settings.autoFinish,
                        onCheckedChange = {
                            controller.updateSettings(settings.copy(autoFinish = it))
                        },
                    )
                },
                {
                    SwitchRow(
                        icon = MaterialSymbols.OutlinedFilled.History,
                        tone = ToneBlue,
                        title = "Save to History",
                        checked = settings.saveToHistory,
                        onCheckedChange = {
                            controller.updateSettings(settings.copy(saveToHistory = it))
                        },
                    )
                },
            )
            if (advanced) {
                rows += {
                    SwitchRow(
                        icon = MaterialSymbols.OutlinedFilled.Verified,
                        tone = ToneTeal,
                        title = "Verify checksums",
                        subtitle = "Reject files whose SHA-256 doesn't match",
                        checked = settings.verifyChecksums,
                        onCheckedChange = {
                            controller.updateSettings(settings.copy(verifyChecksums = it))
                        },
                    )
                }
            }
            rows += {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("Destination folder", style = MaterialTheme.typography.bodyLarge)
                    if (usesSafDestination()) {
                        Text(
                            if (settings.destinationTreeUri.isEmpty()) {
                                "Downloads (default)"
                            } else {
                                "Custom folder (picked with the system picker)"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = destinationPicker) { Text("Choose folder") }
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
                    } else {
                        OutlinedTextField(
                            value = destinationText,
                            onValueChange = { destinationText = it },
                            label = { Text("Destination folder path") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = destinationPicker) { Text("Browse…") }
                            Button(
                                onClick = {
                                    controller.updateSettings(
                                        settings.copy(destinationFolder = destinationText.trim()),
                                    )
                                },
                            ) { Text("Save destination") }
                        }
                    }
                }
            }
            SegmentedList(rows)
        }

        // ── Sending (advanced) ──
        if (advanced) {
            item { SectionTitle("Sending") }
            item {
                SegmentedList(
                    listOf(
                        {
                            SwitchRow(
                                icon = MaterialSymbols.OutlinedFilled.Link,
                                tone = ToneSky,
                                title = "Auto-accept web uploads",
                                subtitle = "Receive-via-link uploads skip the prompt",
                                checked = settings.shareViaLinkAutoAccept,
                                onCheckedChange = {
                                    controller.updateSettings(
                                        settings.copy(shareViaLinkAutoAccept = it),
                                    )
                                },
                            )
                        },
                        {
                            SwitchRow(
                                icon = MaterialSymbols.OutlinedFilled.Fingerprint,
                                tone = TonePurple,
                                title = "Create checksums",
                                subtitle = "SHA-256 for every file sent (slower on huge files)",
                                checked = settings.createChecksums,
                                onCheckedChange = {
                                    controller.updateSettings(
                                        settings.copy(createChecksums = it),
                                    )
                                },
                            )
                        },
                        {
                            SwitchRow(
                                icon = MaterialSymbols.OutlinedFilled.Replay,
                                tone = ToneOrange,
                                title = "Retry automatically",
                                subtitle = "Failed sends back off and retry on their own",
                                checked = settings.autoRetry,
                                onCheckedChange = {
                                    controller.updateSettings(settings.copy(autoRetry = it))
                                },
                            )
                        },
                        {
                            SwitchRow(
                                icon = MaterialSymbols.OutlinedFilled.Bolt,
                                tone = ToneYellow,
                                title = "Chunked transfers",
                                subtitle = "Resumable 1 MiB chunks between LSEx devices",
                                checked = settings.chunkedTransfers,
                                onCheckedChange = {
                                    controller.updateSettings(
                                        settings.copy(chunkedTransfers = it),
                                    )
                                },
                            )
                        },
                    ),
                )
            }
        }

        // ── Network ──
        item { SectionTitle("Network") }
        item {
            val rows = mutableListOf<@Composable () -> Unit>(
                {
                    SwitchRow(
                        icon = MaterialSymbols.OutlinedFilled.Dns,
                        tone = ToneGreen,
                        title = "Server",
                        subtitle = "Other devices can find and send to you",
                        checked = settings.serverEnabled,
                        onCheckedChange = {
                            controller.updateSettings(settings.copy(serverEnabled = it))
                        },
                    )
                },
                {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OutlinedTextField(
                            value = aliasText,
                            onValueChange = { aliasText = it },
                            label = { Text("Device name (alias)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = portText,
                            onValueChange = { portText = it.filter { c -> c.isDigit() } },
                            label = { Text("Port") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = pinText,
                            onValueChange = { pinText = it },
                            label = { Text("PIN (empty = no PIN)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Button(
                            onClick = {
                                controller.updateSettings(
                                    settings.copy(
                                        alias = aliasText.trim(),
                                        port = portText.toIntOrNull()
                                            ?: ProtocolConstants.DEFAULT_PORT,
                                        pin = pinText.trim(),
                                    ),
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text("Save name, port and PIN") }
                    }
                },
                {
                    SwitchRow(
                        icon = MaterialSymbols.OutlinedFilled.Lock,
                        tone = ToneBlue,
                        title = "Encryption (HTTPS)",
                        subtitle = "Self-signed certificate, fingerprint-verified",
                        checked = settings.https,
                        onCheckedChange = {
                            controller.updateSettings(settings.copy(https = it))
                        },
                    )
                },
                {
                    SwitchRow(
                        icon = MaterialSymbols.OutlinedFilled.Language,
                        tone = ToneTeal,
                        title = "Download API",
                        subtitle = "Share-via-link web page and uploads",
                        checked = settings.downloadApi,
                        onCheckedChange = {
                            controller.updateSettings(settings.copy(downloadApi = it))
                        },
                    )
                },
            )
            if (advanced) {
                rows += {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        InfoRow("Device model", platformDeviceModel())
                        InfoRow("Device type", local.deviceType.name.lowercase())
                        InfoRow("Fingerprint", local.fingerprint.take(24) + "…")
                        InfoRow(
                            "Transfer core",
                            if (rustCoreActive()) "Rust (native)" else "Kotlin fallback",
                        )
                    }
                }
                rows += {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OutlinedTextField(
                            value = timeoutText,
                            onValueChange = { timeoutText = it.filter { c -> c.isDigit() } },
                            label = { Text("Discovery timeout (seconds)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = multicastText,
                            onValueChange = { multicastText = it },
                            label = { Text("Multicast group (empty = 224.0.0.167)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = whitelistText,
                            onValueChange = { whitelistText = it },
                            label = { Text("Network whitelist (IPs/CIDRs, comma-separated)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = blacklistText,
                            onValueChange = { blacklistText = it },
                            label = { Text("Network blacklist (IPs/CIDRs, comma-separated)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Button(
                            onClick = {
                                controller.updateSettings(
                                    settings.copy(
                                        discoveryTimeoutSec = timeoutText.toIntOrNull() ?: 10,
                                        multicastAddress = multicastText.trim(),
                                        networkWhitelist = whitelistText.trim(),
                                        networkBlacklist = blacklistText.trim(),
                                    ),
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text("Save advanced network settings") }
                    }
                }
            }
            SegmentedList(rows)
        }

        // ── Favorites ──
        if (settings.favorites.isNotEmpty()) {
            item { SectionTitle("Favorites") }
            item {
                SegmentedList(
                    settings.favorites.map { fingerprint ->
                        {
                            Row(
                                Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                SettingIconChip(MaterialSymbols.OutlinedFilled.Star, ToneYellow)
                                Spacer(Modifier.width(14.dp))
                                Text(
                                    fingerprint.take(20) + "…",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f),
                                )
                                IconButton(
                                    onClick = { controller.toggleFavorite(fingerprint) },
                                ) {
                                    Icon(
                                        MaterialSymbols.OutlinedFilled.Close,
                                        contentDescription = "Remove favorite",
                                    )
                                }
                            }
                        }
                    },
                )
            }
        }

        item {
            Text(
                "LSEx 0.6.0 · LocalSend Protocol 2.2 · Unofficial, credits to the LocalSend project",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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
            if (entry.incoming) MaterialSymbols.Outlined.Download else MaterialSymbols.Outlined.Send,
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
                    MaterialSymbols.Outlined.System_update,
                    contentDescription = "Install this package",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
        if (onOpen != null) {
            IconButton(onClick = onOpen) {
                Icon(
                    MaterialSymbols.Outlined.Open_in_new,
                    contentDescription = "Open file",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
        if (onResend != null) {
            IconButton(onClick = onResend) {
                Icon(
                    MaterialSymbols.Outlined.Send,
                    contentDescription = "Stage for resending",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Icon(
            if (entry.success) MaterialSymbols.Outlined.Check_circle else MaterialSymbols.Outlined.Error,
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
                            Icon(MaterialSymbols.Outlined.Replay, contentDescription = null)
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
                        MaterialSymbols.Outlined.Check_circle,
                        contentDescription = "Done",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    TransferStatus.FAILED -> if (enabled) {
                        IconButton(
                            onClick = { controller.retryOutboxFile(job.id, file.fileId) },
                        ) {
                            Icon(
                                MaterialSymbols.Outlined.Replay,
                                contentDescription = "Retry this file",
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    } else {
                        Icon(
                            MaterialSymbols.Outlined.Error,
                            contentDescription = "Failed",
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                    TransferStatus.ACTIVE -> {
                        LoadingIndicator(Modifier.size(20.dp))
                        IconButton(onClick = { controller.cancelSend() }) {
                            Icon(
                                MaterialSymbols.Outlined.Close,
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
                    MaterialSymbols.Outlined.Download,
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
                    MaterialSymbols.Outlined.Chat,
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
                    MaterialSymbols.Outlined.Lan,
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
                    MaterialSymbols.Outlined.Check_circle,
                    contentDescription = "Done",
                    tint = MaterialTheme.colorScheme.primary,
                )
                TransferStatus.FAILED, TransferStatus.CANCELLED -> Icon(
                    MaterialSymbols.Outlined.Error,
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
    DeviceType.mobile -> MaterialSymbols.Outlined.Phone_android
    DeviceType.web -> MaterialSymbols.Outlined.Language
    else -> MaterialSymbols.Outlined.Computer
}

private fun formatSize(bytes: Long): String = when {
    bytes < 1024 -> "$bytes B"
    bytes < 1024 * 1024 -> "${bytes / 1024} KB"
    bytes < 1024 * 1024 * 1024 -> "${"%.1f".format(bytes / 1024.0 / 1024.0)} MB"
    else -> "${"%.2f".format(bytes / 1024.0 / 1024.0 / 1024.0)} GB"
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
