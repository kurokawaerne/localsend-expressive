package dev.kurokawa.localsend.expressive.ui
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.outlined.*
import com.composables.icons.materialsymbols.outlinedfilled.*

import androidx.compose.ui.draw.alpha
import androidx.compose.animation.VectorConverter
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.selection.toggleable
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
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
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
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
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
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.toShape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
import dev.kurokawa.localsend.expressive.platform.WifiNetwork
import dev.kurokawa.localsend.expressive.platform.loadInstalledApps
import dev.kurokawa.localsend.expressive.platform.deleteReceivedFile
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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.Morph
import dev.kurokawa.localsend.expressive.theme.DarkScheme
import dev.kurokawa.localsend.expressive.theme.LightScheme
import dev.kurokawa.localsend.expressive.theme.MinusColorSchemeIds
import dev.kurokawa.localsend.expressive.theme.MonoDarkScheme
import dev.kurokawa.localsend.expressive.theme.MonoLightScheme
import dev.kurokawa.localsend.expressive.theme.minusColorScheme
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
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.kurokawa.localsend.expressive.platform.ConnectivityPrefs
import dev.kurokawa.localsend.expressive.platform.updateConnectivityPrefs
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
import androidx.compose.ui.draw.shadow
import dev.kurokawa.localsend.expressive.platform.platformDeviceModel
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.unit.IntOffset

private enum class MainTab { Home, Share, Settings }

// ── UI strings (language setting: system / English / Español) ──────────────

// ── Root + splash ──────────────────────────────────────────────────────────

@Composable
fun AppRoot(controller: LocalSendController, onReady: () -> Unit = {}) {
    // v0.8.0 REGRESSION FIX: the v0.6.0 rewrite of this root dropped the
    // start() call entirely — nothing ever started the receive server,
    // multicast discovery or the multicast lock, so the app discovered
    // nobody and nobody discovered it, on Android and desktop alike.
    // start() is idempotent; the engine lives for the app's lifetime.
    LaunchedEffect(controller) { controller.start() }
    val settings by controller.settings.collectAsState()
    LocalSendExpressiveTheme(settings) {
        CompositionLocalProvider(LocalStrings provides stringsFor(settings.language)) {
            var showSplash by rememberSaveable { mutableStateOf(true) }
            AppScaffold(controller, settings)
            // The animated splash runs on Android too (v0.9.0): the
            // system splash is only a static frame — no text, no corner
            // animation, no dynamic color — and Ernest's splash spec is
            // the animated one. It shows once per cold start;
            // rememberSaveable keeps rotation from replaying it.
            if (showSplash && settings.enableAnimations) {
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
/**
 * The comparison-arrows mark (Ernest's own glyph from his splash mockup):
 * two opposing arrows meeting in the middle, drawn on a pale circle.
 * Also the app icon and the splash image.
 */
@Composable
private fun ArrowsMark(size: androidx.compose.ui.unit.Dp, modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primary
    val container = MaterialTheme.colorScheme.primaryContainer
    Surface(shape = CircleShape, color = container, modifier = modifier.size(size)) {
        Canvas(Modifier.fillMaxSize().padding(size * 0.22f)) {
            val w = this.size.width
            val h = this.size.height
            val stroke = w * 0.13f
            val head = w * 0.30f
            // top-right arrow pointing down-left
            drawLine(primary, Offset(w * 0.95f, h * 0.28f), Offset(w * 0.48f, h * 0.28f), stroke)
            drawLine(primary, Offset(w * 0.72f, h * 0.05f), Offset(w * 0.48f, h * 0.28f), stroke)
            drawLine(primary, Offset(w * 0.72f, h * 0.51f), Offset(w * 0.48f, h * 0.28f), stroke)
            // bottom-left arrow pointing up-right
            drawLine(primary, Offset(w * 0.05f, h * 0.72f), Offset(w * 0.52f, h * 0.72f), stroke)
            drawLine(primary, Offset(w * 0.28f, h * 0.49f), Offset(w * 0.52f, h * 0.72f), stroke)
            drawLine(primary, Offset(w * 0.28f, h * 0.95f), Offset(w * 0.52f, h * 0.72f), stroke)
        }
    }
}

/** The wordmark from Ernest's mockups: "LocalSend" + italic "Ex", in primary. */
@Composable
private fun LsexWordmark(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Text(
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = scheme.primary)) {
                append("LocalSend")
            }
            withStyle(
                SpanStyle(
                    // The mockup's "Ex" is a serif italic flourish set
                    // against the rounded sans — Serif italic is the
                    // closest platform match.
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = scheme.primary,
                ),
            ) { append("Ex") }
        },
        style = MaterialTheme.typography.headlineMedium,
        modifier = modifier,
    )
}

/**
 * Animated splash (Ernest's exact spec): the two comparison arrows start
 * at the TOP and BOTTOM corners of the screen, sweep quickly to the
 * center along an almost-circular arc, BOUNCE when they meet, and settle
 * into the static mark (pale circle + arrows) with the wordmark at the
 * bottom. Colors come from the live color scheme, so the splash follows
 * dynamic color like the rest of the app. Runs on every platform —
 * Android's system splash (same artwork, light/dark adaptive) hands off
 * to it. Hardened: the completion callback fires from a finally block,
 * so no animation failure can trap the user on the splash.
 */
@Composable
private fun SplashOverlay(onDone: () -> Unit) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        try {
            progress.animateTo(1f, tween(durationMillis = 1150, easing = LinearEasing))
            delay(320)
        } finally {
            onDone()
        }
    }
    val primary = MaterialTheme.colorScheme.primary
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val wPx = constraints.maxWidth.toFloat()
            val hPx = constraints.maxHeight.toFloat()
            val t = progress.value
            // Ease-out for the flight itself (fast start, gentle arrival).
            val te = 1f - (1f - t).let { it * it * it }
            // Bounce: once the arrows meet (~82% in), they rebound a
            // little way back along their paths and settle again.
            val bouncePhase = ((te - 0.82f) / 0.18f).coerceIn(0f, 1f)
            val rebound = if (te > 0.82f) {
                sin(bouncePhase * PI.toFloat()) * min(wPx, hPx) * 0.028f
            } else {
                0f
            }
            // Position of one arrow: corner -> center along a quadratic
            // arc (the perpendicular bulge peaks mid-flight and curves
            // BOTH arrows in the same rotational sense — the "almost
            // circular" sweep), plus the rebound along the travel axis.
            fun arrowOffset(startX: Float, startY: Float): Offset {
                val vx = -startX
                val vy = -startY
                val len = sqrt(vx * vx + vy * vy).coerceAtLeast(1f)
                val bulge = sin(te * PI.toFloat()) * min(wPx, hPx) * 0.10f
                val along = te + rebound / len
                return Offset(
                    startX + vx * along + (-vy / len) * bulge * (1f - te),
                    startY + vy * along + (vx / len) * bulge * (1f - te),
                )
            }
            val fade = 0.3f + 0.7f * te
            // Arrow 1 (points up-right) starts at the BOTTOM-left corner.
            val o1 = arrowOffset(-wPx * 0.44f, hPx * 0.32f)
            Canvas(
                Modifier.fillMaxSize().graphicsLayer {
                    translationX = o1.x
                    translationY = o1.y
                    alpha = fade
                },
            ) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val s = size.minDimension * 0.16f
                val stroke = s * 0.26f
                drawLine(primary, Offset(cx - s, cy + s * 0.45f), Offset(cx + s * 0.05f, cy + s * 0.45f), stroke)
                drawLine(primary, Offset(cx - s * 0.35f, cy - s * 0.2f), Offset(cx + s * 0.05f, cy + s * 0.45f), stroke)
                drawLine(primary, Offset(cx - s * 0.35f, cy + s * 1.1f), Offset(cx + s * 0.05f, cy + s * 0.45f), stroke)
            }
            // Arrow 2 (points down-left) starts at the TOP-right corner.
            val o2 = arrowOffset(wPx * 0.44f, -hPx * 0.32f)
            Canvas(
                Modifier.fillMaxSize().graphicsLayer {
                    translationX = o2.x
                    translationY = o2.y
                    alpha = fade
                },
            ) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val s = size.minDimension * 0.16f
                val stroke = s * 0.26f
                drawLine(primary, Offset(cx + s, cy - s * 0.45f), Offset(cx - s * 0.05f, cy - s * 0.45f), stroke)
                drawLine(primary, Offset(cx + s * 0.35f, cy + s * 0.2f), Offset(cx - s * 0.05f, cy - s * 0.45f), stroke)
                drawLine(primary, Offset(cx + s * 0.35f, cy - s * 1.1f), Offset(cx - s * 0.05f, cy - s * 0.45f), stroke)
            }
            // The settled mark fades in as the arrows land.
            Box(
                Modifier.align(Alignment.Center)
                    .graphicsLayer { alpha = ((te - 0.6f) / 0.4f).coerceIn(0f, 1f) },
            ) {
                ArrowsMark(120.dp)
            }
            Box(
                Modifier.align(Alignment.BottomCenter).padding(bottom = 64.dp)
                    .graphicsLayer { alpha = ((te - 0.6f) / 0.4f).coerceIn(0f, 1f) },
            ) {
                LsexWordmark()
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun AppScaffold(controller: LocalSendController, settings: AppSettings) {
    val strings = LocalStrings.current
    var tab by rememberSaveable { mutableStateOf(MainTab.Home) }
    val local by controller.localDevice.collectAsState()
    val localIp by controller.localIpAddress.collectAsState()
    val offer by controller.incomingOffer.collectAsState()
    // One connectivity controller for the whole shell, so every surface
    // observes the same hotspot / Wi-Fi Direct state.
    val connectivity = remember { ConnectivityController() }
    // The user's hotspot / Wi-Fi Direct preferences (Settings) reach the
    // platform radios through this holder.
    LaunchedEffect(
        settings.hotspotSsid, settings.hotspotPassword, settings.hotspotBand,
        settings.p2pName, settings.p2pPassword, settings.p2pBand,
    ) {
        updateConnectivityPrefs(
            ConnectivityPrefs(
                hotspotSsid = settings.hotspotSsid,
                hotspotPassword = settings.hotspotPassword,
                hotspotBand = settings.hotspotBand,
                p2pName = settings.p2pName,
                p2pPassword = settings.p2pPassword,
                p2pBand = settings.p2pBand,
            ),
        )
    }

    // Staging + device selection are hoisted: the picker, the Share page
    // and share-via-link all work on the same selection.
    var staged by remember { mutableStateOf<List<StagedItem>>(emptyList()) }
    var selectedFingerprints by rememberSaveable { mutableStateOf<Set<String>>(emptySet()) }
    var showIpDialog by remember { mutableStateOf(false) }
    var showLinkPage by remember { mutableStateOf(false) }
    var showTransfers by remember { mutableStateOf(false) }

    // The global transfer pill lives in GlobalTransferPillHost below:
    // it collects the progress flows ITSELF (v0.9.0 perf pass). When the
    // scaffold collected them, every progress tick — several per second
    // during a transfer — recomposed the whole screen tree, which was
    // the "scrolling doesn't feel smooth" jank Ernest reported.

    // Share sheet: files handed to us by other apps join the staging area.
    val sharedFiles by sharedFilesFlow().collectAsState()
    LaunchedEffect(sharedFiles) {
        if (sharedFiles.isNotEmpty()) {
            staged = staged + sharedFiles.map { StagedItem.FileItem(it) }
            clearSharedFiles()
            tab = MainTab.Share
        }
    }
    // Quick Settings tile: land on Share (the picker lives there now).
    val quickShareCount by quickShareRequests().collectAsState()
    LaunchedEffect(quickShareCount) {
        if (quickShareCount > 0) {
            tab = MainTab.Share
            clearQuickShareRequest()
        }
    }

    // A root Surface, not just a background paint: the Surface is what
    // provides LocalContentColor (onBackground) to bare Texts. Without
    // it, headings outside cards fell back to the composition default —
    // BLACK — and vanished on the dark background (Ernest's monochrome
    // screenshots: "Your device" / "Setup your connection" invisible).
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize(),
    ) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wide = maxWidth >= 840.dp
        val content: @Composable () -> Unit = {
            when (tab) {
                MainTab.Home -> HomeScreen(
                    controller = controller,
                    settings = settings,
                    local = local,
                    connectivity = connectivity,
                    onReceiveViaLink = { showLinkPage = true },
                    onGoToShare = { tab = MainTab.Share },
                )
                MainTab.Share -> ShareScreen(
                    controller = controller,
                    settings = settings,
                    staged = staged,
                    onStagedChange = { staged = it },
                    selectedFingerprints = selectedFingerprints,
                    onSelectedChange = { selectedFingerprints = it },
                    onAddByIp = { showIpDialog = true },
                    onShowTransfers = { showTransfers = true },
                    onShareViaLink = {
                        controller.setWebShareFiles(stagedFiles(staged))
                        if (!settings.downloadApi) {
                            controller.updateSettings(settings.copy(downloadApi = true))
                        }
                        showLinkPage = true
                    },
                )
                MainTab.Settings -> SettingsScreen(controller, settings)
            }
        }
        if (wide) {
            // Desktop (GooglebookOS direction): a calm rail on a tinted
            // backdrop, the content floating as one large rounded sheet,
            // the transfer pill inside the sheet's bottom-end corner.
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
                    ArrowsMark(44.dp)
                    Spacer(Modifier.height(16.dp))
                    RailNavItem(
                        icon = MaterialSymbols.Outlined.Home,
                        selectedIcon = MaterialSymbols.OutlinedFilled.Home,
                        label = "Home",
                        selected = tab == MainTab.Home,
                    ) { tab = MainTab.Home }
                    RailNavItem(
                        icon = MaterialSymbols.Outlined.Compare_arrows,
                        selectedIcon = MaterialSymbols.OutlinedFilled.Compare_arrows,
                        label = strings.send,
                        selected = tab == MainTab.Share,
                    ) { tab = MainTab.Share }
                    RailNavItem(
                        icon = MaterialSymbols.Outlined.Settings,
                        selectedIcon = MaterialSymbols.OutlinedFilled.Settings,
                        label = strings.settings,
                        selected = tab == MainTab.Settings,
                    ) { tab = MainTab.Settings }
                }
                Box(Modifier.weight(1f).fillMaxHeight().padding(16.dp)) {
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLowest,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        content()
                    }
                    GlobalTransferPillHost(
                        controller = controller,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(20.dp),
                    ) { showTransfers = true }
                }
            }
        } else {
            // Phones: content edge-to-edge, but never under the status bar
            // (v0.6.0 forgot the inset and the header overlapped the clock).
            Box(Modifier.fillMaxSize()) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.statusBars),
                ) {
                    content()
                }
                Column(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    // Same width as the nav bar (Ernest: the floating
                    // transfer pill matches his mockup's bar width).
                    GlobalTransferPillHost(
                        controller = controller,
                        modifier = Modifier.fillMaxWidth(0.827f),
                    ) { showTransfers = true }
                    // The native expressive short navigation bar
                    // (Ernest's "XR nav bar"): 64dp, surfaceContainer,
                    // growing 56x32 indicator, filled selected icon,
                    // secondary active label — floated with margins and
                    // full rounding exactly like his mockup. The custom
                    // pill and its dark backdrop box are gone.
                    ShortNavigationBar(
                        modifier = Modifier
                            // Ernest: nav width EXACTLY as mocked —
                            // measured on his mockups (home + share):
                            // the bar spans x 86..913 of 1000, i.e.
                            // 82.7% of the screen width, ~8.65% margins.
                            .fillMaxWidth(0.827f)
                            .shadow(8.dp, RoundedCornerShape(36.dp))
                            .clip(RoundedCornerShape(36.dp)),
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        windowInsets = WindowInsets(0, 0, 0, 0),
                    ) {
                        ShortNavigationBarItem(
                            selected = tab == MainTab.Home,
                            onClick = { tab = MainTab.Home },
                            icon = {
                                Icon(
                                    if (tab == MainTab.Home) {
                                        MaterialSymbols.OutlinedFilled.Home
                                    } else {
                                        MaterialSymbols.Outlined.Home
                                    },
                                    contentDescription = null,
                                )
                            },
                            label = { Text("Home") },
                        )
                        ShortNavigationBarItem(
                            selected = tab == MainTab.Share,
                            onClick = { tab = MainTab.Share },
                            icon = {
                                Icon(
                                    if (tab == MainTab.Share) {
                                        MaterialSymbols.OutlinedFilled.Compare_arrows
                                    } else {
                                        MaterialSymbols.Outlined.Compare_arrows
                                    },
                                    contentDescription = null,
                                )
                            },
                            label = { Text(strings.send) },
                        )
                        ShortNavigationBarItem(
                            selected = tab == MainTab.Settings,
                            onClick = { tab = MainTab.Settings },
                            icon = {
                                Icon(
                                    if (tab == MainTab.Settings) {
                                        MaterialSymbols.OutlinedFilled.Settings
                                    } else {
                                        MaterialSymbols.Outlined.Settings
                                    },
                                    contentDescription = null,
                                )
                            },
                            label = { Text(strings.settings) },
                        )
                    }
                }
            }
        }
    }

    }
    offer?.let { IncomingOfferDialog(controller, it) }
    if (showIpDialog) {
        IpAddressDialog(
            onDismiss = { showIpDialog = false },
            onProbe = { address ->
                showIpDialog = false
                controller.probeDevice(address)
                tab = MainTab.Share
            },
        )
    }
    if (showLinkPage) {
        LinkPage(
            controller = controller,
            settings = settings,
            local = local,
            localIp = localIp,
            onDismiss = { showLinkPage = false },
        )
    }
    if (showTransfers) {
        TransferSheet(
            controller = controller,
            settings = settings,
            onDismiss = { showTransfers = false },
            onGoToShare = {
                showTransfers = false
                tab = MainTab.Share
            },
        )
    }
}

/** The PickedFiles inside the staging list (folders expanded at send time). */
private fun stagedFiles(staged: List<StagedItem>): List<PickedFile> =
    staged.mapNotNull { (it as? StagedItem.FileItem)?.file }

/**
 * One destination inside the floating pill. Ernest's icon rule: outlined
 * at rest, animating to filled when selected (the FILL axis of Material
 * Symbols, crossfaded here).
 */
@Composable
private fun PillNavItem(
    icon: ImageVector,
    selectedIcon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val fill by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(250),
        label = "navFill",
    )
    val indicator by androidx.compose.animation.core.animateValueAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            Color.Transparent
        },
        typeConverter = Color.VectorConverter(androidx.compose.ui.graphics.colorspace.ColorSpaces.Srgb),
        animationSpec = tween(250),
        label = "navIndicator",
    )
    Column(
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(shape = CircleShape, color = indicator) {
            Box(
                Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp).alpha(1f - fill),
                )
                Icon(
                    selectedIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(24.dp).alpha(fill),
                )
            }
        }
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        )
    }
}

/** Rail destination for the wide/desktop shell — same fill animation. */
@Composable
private fun RailNavItem(
    icon: ImageVector,
    selectedIcon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val fill by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(250),
        label = "railFill",
    )
    val indicator by androidx.compose.animation.core.animateValueAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            Color.Transparent
        },
        typeConverter = Color.VectorConverter(androidx.compose.ui.graphics.colorspace.ColorSpaces.Srgb),
        animationSpec = tween(250),
        label = "railIndicator",
    )
    Column(
        Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(shape = CircleShape, color = indicator) {
            Box(
                Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp).alpha(1f - fill),
                )
                Icon(
                    selectedIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(24.dp).alpha(fill),
                )
            }
        }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}

private sealed interface StagedItem {
    data class FileItem(val file: PickedFile) : StagedItem
    data class FolderItem(val folder: BrowserItem) : StagedItem
    data class TextItem(val text: String) : StagedItem
}

private enum class ContentTab { Apps, Photos, Videos, Audio, Files, Text, Web }

/**
 * The expressive connected button group (the segmented button's official
 * successor): equal widths, 2dp gaps, pill outer ends, inner corners that
 * tighten on press, the checked item filled with primary — the Quick
 * Save-style control Ernest asked for, matching his mockups.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ConnectedChoiceGroup(
    options: List<Pair<ImageVector, String>>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    onSelect: (Int) -> Unit,
) {
    ButtonGroup(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
    ) {
        options.forEachIndexed { index, (icon, label) ->
            ToggleButton(
                checked = selectedIndex == index,
                onCheckedChange = { onSelect(index) },
                shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                },
                colors = ToggleButtonDefaults.toggleButtonColors(
                    checkedContainerColor = MaterialTheme.colorScheme.primary,
                    checkedContentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
                modifier = Modifier.weight(1f),
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(label, maxLines = 1, softWrap = false)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HomeScreen(
    controller: LocalSendController,
    settings: AppSettings,
    local: LocalDevice,
    connectivity: ConnectivityController,
    onReceiveViaLink: () -> Unit,
    onGoToShare: () -> Unit,
) {
    val connState by connectivity.state.collectAsState()
    val history by controller.history.collectAsState()
    val devices by controller.devices.collectAsState()
    val outbox by controller.outbox.collectAsState()
    var historyExpanded by rememberSaveable { mutableStateOf(true) }
    var connSettingsKind by remember { mutableStateOf<String?>(null) }
    var showScanner by remember { mutableStateOf(false) }
    var joinTarget by remember { mutableStateOf<WifiNetwork?>(null) }
    var joinNote by remember { mutableStateOf<String?>(null) }
    var homeNote by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { connectivity.refresh() }

    // The connection mode follows reality: if our hotspot or P2P group is
    // up, that mode is the selected one.
    var mode by rememberSaveable { mutableStateOf(0) }
    // Scan for the WiFi section's network list whenever it is shown.
    LaunchedEffect(mode) {
        if (mode == 0) connectivity.scanWifiNetworks()
    }
    LaunchedEffect(connState.hotspot, connState.wifiDirect) {
        mode = when {
            connState.hotspot != null -> 1
            connState.wifiDirect != null -> 2
            else -> mode
        }
    }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 140.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { LsexWordmark() }

        // ── Your device ──
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Your device",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                )
                OutlinedButton(onClick = onReceiveViaLink) {
                    Icon(MaterialSymbols.Outlined.Link, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Receive via link")
                }
            }
        }
        item {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(52.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    avatarIcon(settings.avatar)
                                        ?: deviceIcon(local.deviceType),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(26.dp),
                                )
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Text(
                            settings.alias.ifEmpty { local.alias },
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoChip("Port ${local.port}")
                        if (settings.https) InfoChip("HTTPS")
                        InfoChip("Protocol 2.2")
                    }
                }
            }
        }

        // ── Setup your connection ──
        item {
            Text("Setup your connection", style = MaterialTheme.typography.titleLarge)
        }
        item {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    ConnectedChoiceGroup(
                        options = listOf(
                            MaterialSymbols.Outlined.Wifi to "WiFi",
                            MaterialSymbols.Outlined.Wifi_tethering to "Hotspot",
                            MaterialSymbols.Outlined.Router to "WDirect",
                        ),
                        selectedIndex = mode,
                        modifier = Modifier.fillMaxWidth(),
                    ) { index ->
                        mode = index
                        connectivity.clearMessage()
                    }
                    when (mode) {
                        0 -> {
                            Text("Networks", style = MaterialTheme.typography.titleSmall)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    modifier = Modifier.size(40.dp),
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            MaterialSymbols.Outlined.Wifi,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        )
                                    }
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        when (connState.wifiEnabled) {
                                            true -> "Wi-Fi is on"
                                            false -> "Wi-Fi is off"
                                            null -> "Wi-Fi"
                                        },
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                    Text(
                                        "Devices on the same network find each other automatically.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Button(onClick = { connectivity.toggleWifi() }) {
                                    Text(if (connState.wifiEnabled == true) "Settings" else "Turn on")
                                }
                            }
                            OutlinedButton(
                                onClick = { showScanner = true },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Icon(MaterialSymbols.Outlined.Qr_code_scanner, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Scan a code to join a network")
                            }
                            if (connState.wifiNetworks.isNotEmpty()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "Nearby networks",
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.weight(1f),
                                    )
                                    IconButton(onClick = { connectivity.scanWifiNetworks() }) {
                                        Icon(
                                            MaterialSymbols.Outlined.Refresh,
                                            contentDescription = "Scan again",
                                        )
                                    }
                                }
                                connState.wifiNetworks.take(8).forEach { network ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Mockup anatomy: the signal glyph sits
                                        // in a filled circle, Connect is a
                                        // filled pill button.
                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                            modifier = Modifier.size(40.dp),
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    when {
                                                        network.level >= -55 ->
                                                            MaterialSymbols.Outlined.Signal_wifi_4_bar
                                                        network.level >= -70 ->
                                                            MaterialSymbols.Outlined.Wifi_2_bar
                                                        else -> MaterialSymbols.Outlined.Wifi_1_bar
                                                    },
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(20.dp),
                                                )
                                            }
                                        }
                                        Spacer(Modifier.width(12.dp))
                                        Text(
                                            network.ssid,
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.weight(1f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        if (network.secured) {
                                            Icon(
                                                MaterialSymbols.Outlined.Lock,
                                                contentDescription = "Secured",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp),
                                            )
                                            Spacer(Modifier.width(6.dp))
                                        }
                                        if (network.connected) {
                                            Text(
                                                "Connected",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.primary,
                                            )
                                        } else {
                                            Button(onClick = {
                                                if (network.secured) {
                                                    joinTarget = network
                                                } else {
                                                    joinNote = joinWifiNetwork(
                                                        network.ssid, null, null,
                                                    )
                                                }
                                            }) { Text("Connect") }
                                        }
                                    }
                                }
                            }
                            if (joinNote != null) {
                                Text(
                                    joinNote!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        1 -> {
                            val hotspot = connState.hotspot
                            if (hotspot == null) {
                                Text(
                                    "Start a hotspot on this device — other devices join it and find you, no router needed.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { connectivity.toggleHotspot() },
                                        modifier = Modifier.weight(1f),
                                    ) { Text("Start hotspot") }
                                    OutlinedButton(onClick = { connSettingsKind = "hotspot" }) {
                                        Icon(MaterialSymbols.Outlined.Settings, contentDescription = "Hotspot settings")
                                    }
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "Hotspot is on",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.weight(1f),
                                    )
                                    IconButton(onClick = { connSettingsKind = "hotspot" }) {
                                        Icon(MaterialSymbols.Outlined.Settings, contentDescription = "Hotspot settings")
                                    }
                                }
                                Text("Network: ${hotspot.ssid}", style = MaterialTheme.typography.bodyLarge)
                                if (hotspot.password.isNotEmpty()) {
                                    Text("Password: ${hotspot.password}", style = MaterialTheme.typography.bodyLarge)
                                }
                                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    QrCode(wifiQrPayload(hotspot.ssid, hotspot.password))
                                }
                                OutlinedButton(
                                    onClick = { connectivity.toggleHotspot() },
                                    modifier = Modifier.fillMaxWidth(),
                                ) { Text("Stop hotspot") }
                            }
                        }
                        else -> {
                            val direct = connState.wifiDirect
                            if (direct == null) {
                                Text(
                                    "Wi-Fi Direct connects two devices without any network. Start a group here, or join from the other device.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { connectivity.toggleWifiDirect() },
                                        modifier = Modifier.weight(1f),
                                    ) { Text("Start Wi-Fi Direct") }
                                    OutlinedButton(onClick = { connSettingsKind = "p2p" }) {
                                        Icon(MaterialSymbols.Outlined.Settings, contentDescription = "Wi-Fi Direct settings")
                                    }
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "Wi-Fi Direct group is on",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.weight(1f),
                                    )
                                    IconButton(onClick = { connSettingsKind = "p2p" }) {
                                        Icon(MaterialSymbols.Outlined.Settings, contentDescription = "Wi-Fi Direct settings")
                                    }
                                }
                                Text("Network: ${direct.ssid}", style = MaterialTheme.typography.bodyLarge)
                                if (direct.password.isNotEmpty()) {
                                    Text("Password: ${direct.password}", style = MaterialTheme.typography.bodyLarge)
                                }
                                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    QrCode(wifiQrPayload(direct.ssid, direct.password))
                                }
                                OutlinedButton(
                                    onClick = { connectivity.toggleWifiDirect() },
                                    modifier = Modifier.fillMaxWidth(),
                                ) { Text("Stop Wi-Fi Direct") }
                            }
                        }
                    }
                    connState.message?.let { message ->
                        Text(
                            message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                    if (connState.busy) {
                        LinearWavyProgressIndicator(Modifier.fillMaxWidth())
                    }
                }
            }
        }

        // ── History ──
        item {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { historyExpanded = !historyExpanded }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(MaterialSymbols.Outlined.History, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "History",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f),
                        )
                        if (history.isNotEmpty()) {
                            TextButton(onClick = { controller.clearHistory() }) {
                                Text("Clear")
                            }
                        }
                        Icon(
                            if (historyExpanded) {
                                MaterialSymbols.Outlined.Expand_less
                            } else {
                                MaterialSymbols.Outlined.Expand_more
                            },
                            contentDescription = null,
                        )
                    }
                    if (historyExpanded) {
                        if (history.isEmpty()) {
                            Text(
                                "Nothing yet — finished transfers land here.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                            )
                        } else {
                            val shown = history.take(30)
                            Column(
                                Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(3.dp),
                            ) {
                            shown.forEachIndexed { index, entry ->
                                HistoryRow(
                                    entry = entry,
                                    shape = historyTileShape(index, shown.size),
                                    onOpen = entry.uri.takeIf { it.isNotEmpty() }
                                        ?.let { uri -> { openReceivedFile(uri, entry.fileName) } },
                                    onInstall = if (
                                        !entry.incoming && entry.uri.isEmpty()
                                    ) {
                                        null
                                    } else if (isInstallablePackage(entry.fileName) && installerSupported()) {
                                        entry.uri.takeIf { it.isNotEmpty() }
                                            ?.let { uri -> { installPackage(uri, entry.fileName) } }
                                    } else {
                                        null
                                    },
                                    onResend = if (!entry.incoming && !entry.success && entry.handle.isNotEmpty()) {
                                        {
                                            // Resume a failed send (Ernest's rule): if its
                                            // outbox job is still around, retry it; else
                                            // re-send to the device if it is nearby now.
                                            val job = outbox.firstOrNull {
                                                it.targetAlias == entry.peerAlias && it.hasFailures
                                            }
                                            val device = devices.firstOrNull {
                                                it.info.alias == entry.peerAlias
                                            }
                                            when {
                                                job != null -> controller.retryOutboxJob(job.id)
                                                device != null -> controller.sendFiles(
                                                    device,
                                                    listOf(PickedFile(entry.handle, entry.fileName, entry.size)),
                                                )
                                                else -> homeNote = "${entry.peerAlias} isn't nearby right now"
                                            }
                                            if (job != null || device != null) onGoToShare()
                                        }
                                    } else {
                                        null
                                    },
                                    onRemove = { controller.removeHistoryEntry(entry) },
                                    onDeleteFile = if (entry.incoming && entry.uri.isNotEmpty()) {
                                        {
                                            deleteReceivedFile(entry.uri)
                                            controller.removeHistoryEntry(entry)
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
        }
        homeNote?.let { note ->
            item {
                Text(
                    note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }

    connSettingsKind?.let { kind ->
        ConnectionSettingsDialog(
            controller = controller,
            settings = settings,
            kind = kind,
            onDismiss = { connSettingsKind = null },
        )
    }
    if (showScanner) {
        AlertDialog(
            onDismissRequest = { showScanner = false },
            title = { Text("Scan a code") },
            text = {
                Box(Modifier.fillMaxWidth().height(320.dp)) {
                    QrScannerView(modifier = Modifier.fillMaxSize(), onCode = { code ->
                        showScanner = false
                        val wifi = parseWifiQr(code)
                        when {
                            wifi != null -> scope.launch {
                                homeNote = withContext(Dispatchers.IO) {
                                    joinWifiNetwork(wifi.first, wifi.second, wifi.third)
                                }
                            }
                            code.startsWith("http://") || code.startsWith("https://") ->
                                openUrl(code)
                            else -> homeNote = "That code isn't a Wi-Fi or link code"
                        }
                    })
                }
            },
            confirmButton = {
                TextButton(onClick = { showScanner = false }) { Text("Close") }
            },
        )
    }
    val joining = joinTarget
    if (joining != null) {
        var password by remember(joining.ssid) { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { joinTarget = null },
            title = { Text("Join “${joining.ssid}”") },
            text = {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    joinTarget = null
                    scope.launch {
                        joinNote = withContext(Dispatchers.IO) {
                            joinWifiNetwork(joining.ssid, password, "WPA")
                        }
                    }
                }) { Text("Connect") }
            },
            dismissButton = {
                TextButton(onClick = { joinTarget = null }) { Text("Cancel") }
            },
        )
    }
}

/** Hotspot / Wi-Fi Direct preferences (also under Settings → Connection). */
@Composable
private fun ConnectionSettingsDialog(
    controller: LocalSendController,
    settings: AppSettings,
    kind: String,
    onDismiss: () -> Unit,
) {
    val isHotspot = kind == "hotspot"
    var name by remember(settings) {
        mutableStateOf(if (isHotspot) settings.hotspotSsid else settings.p2pName)
    }
    var password by remember(settings) {
        mutableStateOf(if (isHotspot) settings.hotspotPassword else settings.p2pPassword)
    }
    var band by remember(settings) {
        mutableStateOf(if (isHotspot) settings.hotspotBand else settings.p2pBand)
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isHotspot) "Hotspot settings" else "Wi-Fi Direct settings") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isHotspot) "Network name (SSID)" else "Group name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password (empty = none / system)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text("Band", style = MaterialTheme.typography.titleSmall)
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    val bands = listOf("auto" to "Auto", "2.4" to "2.4 GHz", "5" to "5 GHz")
                    bands.forEachIndexed { index, (value, label) ->
                        SegmentedButton(
                            selected = band == value,
                            onClick = { band = value },
                            shape = SegmentedButtonDefaults.itemShape(index, bands.size),
                            label = { Text(label, maxLines = 1, softWrap = false) },
                        )
                    }
                }
                Text(
                    if (isHotspot) {
                        "Applied when the hotspot starts (Android 11+). " +
                            "If a device refuses custom settings, the hotspot starts " +
                            "with system defaults instead — the running hotspot card " +
                            "always shows the name and password that actually took effect."
                            
                    } else {
                        "Wi-Fi Direct uses your name, password and band when the group is created (Android 10+)."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                controller.updateSettings(
                    if (isHotspot) {
                        settings.copy(hotspotSsid = name, hotspotPassword = password, hotspotBand = band)
                    } else {
                        settings.copy(p2pName = name, p2pPassword = password, p2pBand = band)
                    },
                )
                onDismiss()
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

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

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
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
        // The official MD3 search box (Ernest's rule: the SearchBar
        // component, not a plain text field). Used collapsed — the
        // results are the list right below it.
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = {},
                    placeholder = { Text("Search apps") },
                    leadingIcon = {
                        Icon(MaterialSymbols.Outlined.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(MaterialSymbols.Outlined.Close, contentDescription = "Clear")
                            }
                        }
                    },
                )
            },
            expanded = false,
            onExpandedChange = {},
            modifier = Modifier.fillMaxWidth(),
        ) {}
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
                                    // itemCount is -1 when unknown (storage
                                    // roots) — v0.6.0 printed it literally.
                                    if (item.itemCount >= 0) {
                                        "${item.itemCount} items"
                                    } else {
                                        "Folder"
                                    }
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
    shape: RoundedCornerShape = RoundedCornerShape(18.dp),
) {
    if (!prefersSwipeActions()) {
        // Desktop (pointer): actions stay visible.
        Surface(shape = shape, color = MaterialTheme.colorScheme.surfaceContainerLow) {
            StagedItemCardContent(item, onEdit, onDiscard, showActions = true)
        }
        return
    }
    // Touch: Edit / Discard hide behind a left swipe and stay revealed
    // (MD3 list guidance: primary action at the very end, and a second
    // access path — a long press opens the editor directly).
    //
    // v0.8.0 crash fix: v0.6.0 created the state WITHOUT anchors and set
    // them later in a LaunchedEffect, then read requireOffset() during
    // the first layout — which throws before anchors exist, i.e. the
    // app crashed the moment the first file was selected. Anchors are
    // now supplied in the constructor and the offset read is NaN-safe.
    val density = LocalDensity.current
    val revealPx = with(density) { 124.dp.toPx() }
    val state = remember {
        AnchoredDraggableState(
            initialValue = 0,
            anchors = DraggableAnchors {
                0 at 0f
                1 at -revealPx
            },
        )
    }
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
            .clip(shape)
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
                color = MaterialTheme.colorScheme.error,
            ) {
                IconButton(
                    onClick = onDiscard,
                    modifier = Modifier.size(44.dp),
                ) {
                    Icon(
                        MaterialSymbols.Outlined.Close,
                        contentDescription = "Discard",
                        tint = MaterialTheme.colorScheme.onError,
                    )
                }
            }
        }
        Surface(
            shape = shape,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    val off = state.offset
                    IntOffset(if (off.isNaN()) 0 else off.roundToInt(), 0)
                }
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
/**
 * Device row, Ernest's mockup anatomy: unselected = pale card with a
 * primary avatar circle; selected = the whole card turns into a
 * saturated full pill (background color AND shape change — no check
 * icon), with platform + IP chips and info / favorite actions.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DeviceCard(
    device: DiscoveredDevice,
    isSelected: Boolean,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onInfo: () -> Unit,
    /** Live transfer progress with this device (0..1), null when idle. */
    transferFraction: Float? = null,
) {
    val scheme = MaterialTheme.colorScheme
    val shape by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isSelected) 50.dp else 28.dp,
        animationSpec = tween(250),
        label = "deviceShape",
    )
    val container by androidx.compose.animation.core.animateValueAsState(
        targetValue = if (isSelected) scheme.primary else scheme.surfaceContainer,
        typeConverter = Color.VectorConverter(androidx.compose.ui.graphics.colorspace.ColorSpaces.Srgb),
        animationSpec = tween(250),
        label = "deviceContainer",
    )
    val content = if (isSelected) scheme.onPrimary else scheme.onSurface
    val subdued = if (isSelected) {
        scheme.onPrimary.copy(alpha = 0.85f)
    } else {
        scheme.onSurfaceVariant
    }
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = container, contentColor = content),
        shape = RoundedCornerShape(shape),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box {
            // Ernest's mockup: the transfer progress runs BEHIND the
            // device card — a soft fill to the current fraction plus the
            // wavy line along the card's bottom edge.
            if (transferFraction != null) {
                Box(
                    Modifier
                        .matchParentSize()
                        .background(
                            (if (isSelected) scheme.onPrimary else scheme.primary)
                                .copy(alpha = 0.13f),
                        ),
                ) {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(transferFraction.coerceIn(0f, 1f))
                            .background(
                                (if (isSelected) scheme.onPrimary else scheme.primary)
                                    .copy(alpha = 0.16f),
                            ),
                    )
                }
            }
            Column {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = CircleShape,
                color = if (isSelected) scheme.onPrimary else scheme.primary,
                modifier = Modifier.size(48.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        deviceIcon(device.info.deviceType),
                        contentDescription = null,
                        tint = if (isSelected) scheme.primary else scheme.onPrimary,
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    device.info.alias,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) {
                            scheme.onPrimary.copy(alpha = 0.22f)
                        } else {
                            scheme.surfaceContainerHighest
                        },
                    ) {
                        Text(
                            device.info.deviceModel ?: device.info.deviceType?.name ?: "device",
                            style = MaterialTheme.typography.labelMedium,
                            color = content,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            maxLines = 1,
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) {
                            scheme.onPrimary.copy(alpha = 0.22f)
                        } else {
                            scheme.surfaceContainerHighest
                        },
                    ) {
                        Text(
                            device.ip,
                            style = MaterialTheme.typography.labelMedium,
                            color = content,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            maxLines = 1,
                        )
                    }
                }
            }
            IconButton(onClick = onInfo) {
                Icon(
                    MaterialSymbols.Outlined.Info,
                    contentDescription = "Device info",
                    tint = subdued,
                )
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    if (isFavorite) MaterialSymbols.OutlinedFilled.Star else MaterialSymbols.Outlined.Star,
                    contentDescription = "Favorite",
                    tint = if (isFavorite && !isSelected) scheme.primary else subdued,
                )
            }
        }
            if (transferFraction != null) {
                LinearWavyProgressIndicator(
                    progress = { transferFraction.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelLargeEmphasized,
        color = MaterialTheme.colorScheme.primary,
    )
}

// ── Settings: Minus-faithful segmented lists ──────────────────────────────
//
// Copied from Minus's settings (Ernest's main MD3E reference): a group is
// a column of individual surfaceContainer tiles separated by 3dp gaps,
// position corners 16dp outer / 4dp inner, group headers in the
// emphasized label style in primary, a 40dp icon circle per row
// (surfaceContainerHighest + primary glyph; active swaps to
// onSecondaryContainer + secondaryContainer), whole-row switch targets
// with a divider and a decorative switch, and selectable rows that light
// up in secondaryContainer. Everything is scheme tokens, so monochrome
// stays legible by construction — no fixed hues anywhere.

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MinusGroupHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelLargeEmphasized,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, bottom = 10.dp),
    )
}

/**
 * One settings group: optional header + tiles spaced 3dp. Each row
 * receives its index, the row count, and its position shape.
 */
@Composable
private fun MinusGroup(
    title: String?,
    rows: List<@Composable (Int, Int, RoundedCornerShape) -> Unit>,
) {
    Column {
        if (title != null) MinusGroupHeader(title)
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            rows.forEachIndexed { index, row ->
                row(index, rows.size, historyTileShape(index, rows.size))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MinusLeadingIcon(icon: ImageVector, active: Boolean) {
    val scheme = MaterialTheme.colorScheme
    val converter = Color.VectorConverter(
        androidx.compose.ui.graphics.colorspace.ColorSpaces.Srgb,
    )
    val bg by animateValueAsState(
        targetValue = if (active) {
            scheme.onSecondaryContainer
        } else {
            scheme.surfaceContainerHighest
        },
        typeConverter = converter,
        animationSpec = tween(250),
        label = "minusIconBg",
    )
    val fg by animateValueAsState(
        targetValue = if (active) scheme.secondaryContainer else scheme.primary,
        typeConverter = converter,
        animationSpec = tween(250),
        label = "minusIconFg",
    )
    Surface(shape = CircleShape, color = bg, modifier = Modifier.size(40.dp)) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                icon,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun MinusTile(
    shape: RoundedCornerShape,
    content: @Composable () -> Unit,
) {
    Surface(
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        content()
    }
}

/**
 * Minus switch row: the WHOLE row is the switch target; when ON, the
 * tile fills with secondaryContainer and its corners morph toward
 * extraLarge (28dp), and the icon circle goes active. The Switch itself
 * is decorative (no own change handler).
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MinusSwitchRow(
    index: Int,
    count: Int,
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val frac by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "minusSwitchMorph",
    )
    val baseTop = if (index == 0) 16f else 4f
    val baseBottom = if (index == count - 1) 16f else 4f
    val shape = RoundedCornerShape(
        topStart = (baseTop + (28f - baseTop) * frac).dp,
        topEnd = (baseTop + (28f - baseTop) * frac).dp,
        bottomStart = (baseBottom + (28f - baseBottom) * frac).dp,
        bottomEnd = (baseBottom + (28f - baseBottom) * frac).dp,
    )
    val container by animateValueAsState(
        targetValue = if (checked) {
            scheme.secondaryContainer
        } else {
            scheme.surfaceContainer
        },
        typeConverter = Color.VectorConverter(
            androidx.compose.ui.graphics.colorspace.ColorSpaces.Srgb,
        ),
        animationSpec = tween(250),
        label = "minusSwitchBg",
    )
    Surface(shape = shape, color = container, modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .toggleable(
                    value = checked,
                    role = Role.Switch,
                    onValueChange = onCheckedChange,
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MinusLeadingIcon(icon, checked)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyMediumEmphasized)
                if (subtitle != null) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (checked) {
                            scheme.onSecondaryContainer
                        } else {
                            scheme.onSurfaceVariant
                        },
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Box(
                Modifier
                    .height(32.dp)
                    .width(1.dp)
                    .background(scheme.outline.copy(alpha = 0.5f)),
            )
            Spacer(Modifier.width(12.dp))
            Switch(checked = checked, onCheckedChange = null)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MinusNavRow(
    shape: RoundedCornerShape,
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    value: String? = null,
    expanded: Boolean? = null,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    Surface(
        shape = shape,
        color = scheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MinusLeadingIcon(icon, false)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyMediumEmphasized)
                if (subtitle != null) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onSurfaceVariant,
                    )
                }
            }
            when {
                expanded != null -> Icon(
                    if (expanded) {
                        MaterialSymbols.Outlined.Expand_less
                    } else {
                        MaterialSymbols.Outlined.Expand_more
                    },
                    contentDescription = null,
                    tint = scheme.onSurfaceVariant,
                )
                value != null -> Text(
                    value,
                    style = MaterialTheme.typography.labelLarge,
                    color = scheme.primary,
                )
                else -> Icon(
                    MaterialSymbols.Outlined.Chevron_right,
                    contentDescription = null,
                    tint = scheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** One option inside an expanded accordion (Minus selectable row). */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MinusSelectableRow(
    shape: RoundedCornerShape,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    Surface(
        shape = if (selected) RoundedCornerShape(16.dp) else shape,
        color = if (selected) {
            scheme.secondaryContainer
        } else {
            scheme.surfaceContainer
        },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) {
                    scheme.onSecondaryContainer
                } else {
                    scheme.onSurface
                },
                modifier = Modifier.weight(1f),
            )
            if (selected) {
                Icon(
                    MaterialSymbols.Outlined.Check,
                    contentDescription = "Selected",
                    tint = scheme.primary,
                )
            }
        }
    }
}

/**
 * Minus's circle ↔ clover morph path (its MorphPolygonShape, copied
 * from AppearanceOptionsScreen).
 */
private class MorphPolygonShape(
    private val morph: Morph,
    private val progress: Float,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline = Outline.Generic(createPath(morph, progress, size, size))

    companion object {
        fun createPath(
            morph: Morph,
            progress: Float,
            size: Size,
            containerSize: Size,
        ): Path {
            // CMP's graphics-shapes has no Morph.toPath — build the
            // path from the morph's cubics directly (same geometry).
            val path = Path()
            val cubics = morph.asCubics(progress)
            if (cubics.isNotEmpty()) {
                path.moveTo(cubics.first().anchor0X, cubics.first().anchor0Y)
                for (cubic in cubics) {
                    path.cubicTo(
                        cubic.control0X, cubic.control0Y,
                        cubic.control1X, cubic.control1Y,
                        cubic.anchor1X, cubic.anchor1Y,
                    )
                }
                path.close()
            }
            val bounds = path.getBounds()
            val maxDimension = maxOf(bounds.width, bounds.height)
            val scale = if (maxDimension > 0f) {
                size.minDimension / maxDimension
            } else {
                1f
            }
            val matrix = Matrix()
            matrix.scale(scale, scale)
            path.transform(matrix)
            val newBounds = path.getBounds()
            path.translate(containerSize.center - newBounds.center)
            return path
        }
    }
}

/**
 * Minus's color-scheme swatch, copied from its
 * AppearanceOptionsScreen: a 54dp circle that morphs into a clover
 * (spring, low-bouncy) when selected and gains a 2dp onSurface ring;
 * inside, the scheme's surfaceVariant with primary bottom-left and
 * tertiaryContainer bottom-right, clipped to the morph.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MinusSwatch(
    scheme: ColorScheme,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val morph = remember {
        Morph(MaterialShapes.Circle, MaterialShapes.Clover4Leaf)
    }
    val progress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "swatchMorph",
    )
    val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
    Box(
        modifier = Modifier
            .size(54.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val fullPath = MorphPolygonShape.createPath(
                morph, progress, size, size,
            )
            if (selected) {
                drawPath(
                    path = fullPath,
                    color = borderColor,
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
            val innerSize = Size(
                width = size.width - if (selected) 1.dp.toPx() else 0f,
                height = size.height - if (selected) 1.dp.toPx() else 0f,
            )
            val innerPath = MorphPolygonShape.createPath(
                morph, progress, innerSize, size,
            )
            clipPath(innerPath) {
                drawRect(color = scheme.surfaceVariant)
                drawRect(
                    color = scheme.primary,
                    size = size,
                    topLeft = Offset(-size.width / 2f, size.height / 2f),
                )
                drawRect(
                    color = scheme.tertiaryContainer,
                    size = size,
                    topLeft = Offset(size.width / 2f, size.height / 2f),
                )
            }
        }
    }
}

/**
 * Minus's theme-mode picker (its ThemeSection): a connected
 * ToggleButton group — System / Light / Dark — with the unchecked
 * container in surfaceVariant and the checked state on the default
 * ToggleButton colors.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MinusThemeModeGroup(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    val options = listOf(
        MaterialSymbols.OutlinedFilled.Brightness_4 to "System",
        MaterialSymbols.OutlinedFilled.Light_mode to "Light",
        MaterialSymbols.OutlinedFilled.Dark_mode to "Dark",
    )
    ButtonGroup(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            ButtonGroupDefaults.ConnectedSpaceBetween,
        ),
    ) {
        options.forEachIndexed { index, (icon, label) ->
            ToggleButton(
                checked = selectedIndex == index,
                onCheckedChange = { if (it) onSelect(index) },
                modifier = Modifier.weight(1f),
                colors = ToggleButtonDefaults.toggleButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    options.lastIndex ->
                        ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                },
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(ToggleButtonDefaults.IconSize),
                )
                Spacer(Modifier.width(ToggleButtonDefaults.IconSpacing))
                Text(label, maxLines = 1)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
    var modelText by remember(settings.deviceModel) {
        mutableStateOf(settings.deviceModel)
    }
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
    var showAvatarPicker by remember { mutableStateOf(false) }
    var connDialog by remember { mutableStateOf<String?>(null) }
    // Which inline picker/editor is expanded (accordion pattern, Minus).
    var expandedPicker by rememberSaveable { mutableStateOf<String?>(null) }
    fun togglePicker(key: String) {
        expandedPicker = if (expandedPicker == key) null else key
    }

    // Every language the official LocalSend ships (UiStrings.kt).
    val languageOptions = listOf("system" to "System") + UiLanguageOptions
    val deviceTypeOptions = listOf(
        "" to "Automatic",
        "mobile" to "Mobile",
        "desktop" to "Desktop",
        "web" to "Web",
        "headless" to "Headless",
        "server" to "Server",
    )
    val orderOptions = listOf(
        "smallest" to "Smallest files first",
        "arrival" to "Arrival order",
        "largest" to "Largest files first",
        "name" to "By name",
    )

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp, top = 16.dp, bottom = 128.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        item {
            Text(strings.settings, style = MaterialTheme.typography.headlineMedium)
        }

        // ── Profile: avatar + name + device info, edited in place ──
        item {
            MinusGroup(
                title = null,
                rows = buildList {
                    add { _, _, shape ->
                        MinusTile(shape) {
                            Row(
                                Modifier.padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .clickable { showAvatarPicker = true },
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            avatarIcon(settings.avatar)
                                                ?: deviceIcon(local.deviceType),
                                            contentDescription = "Change avatar",
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(30.dp),
                                        )
                                    }
                                }
                                Spacer(Modifier.width(16.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        settings.alias.ifEmpty { local.alias },
                                        style = MaterialTheme.typography.titleLarge,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        "${local.deviceModel ?: "This device"} · " +
                                            "tap the avatar to change it",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme
                                            .onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                    add { _, _, shape ->
                        MinusNavRow(
                            shape = shape,
                            icon = MaterialSymbols.OutlinedFilled.Badge,
                            title = "Device name",
                            value = settings.alias.ifEmpty { local.alias },
                            expanded = expandedPicker == "name",
                            onClick = { togglePicker("name") },
                        )
                    }
                    if (expandedPicker == "name") {
                        add { _, _, shape ->
                            MinusTile(shape) {
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
                                    Button(
                                        onClick = {
                                            controller.updateSettings(
                                                settings.copy(
                                                    alias = aliasText.trim(),
                                                ),
                                            )
                                            expandedPicker = null
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                    ) { Text("Save name") }
                                }
                            }
                        }
                    }
                    add { _, _, shape ->
                        MinusNavRow(
                            shape = shape,
                            icon = MaterialSymbols.OutlinedFilled.Smartphone,
                            title = "Device model",
                            value = settings.deviceModel
                                .ifEmpty { platformDeviceModel() ?: "—" },
                            expanded = expandedPicker == "model",
                            onClick = { togglePicker("model") },
                        )
                    }
                    if (expandedPicker == "model") {
                        add { _, _, shape ->
                            MinusTile(shape) {
                                Column(
                                    Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    OutlinedTextField(
                                        value = modelText,
                                        onValueChange = { modelText = it },
                                        label = {
                                            Text("Device model (empty = automatic)")
                                        },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                    Button(
                                        onClick = {
                                            controller.updateSettings(
                                                settings.copy(
                                                    deviceModel = modelText.trim(),
                                                ),
                                            )
                                            expandedPicker = null
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                    ) { Text("Save model") }
                                }
                            }
                        }
                    }
                    add { _, _, shape ->
                        MinusNavRow(
                            shape = shape,
                            icon = MaterialSymbols.OutlinedFilled.Info,
                            title = "Device type",
                            value = deviceTypeOptions
                                .firstOrNull { it.first == settings.deviceType }
                                ?.second ?: "Automatic",
                            expanded = expandedPicker == "type",
                            onClick = { togglePicker("type") },
                        )
                    }
                    if (expandedPicker == "type") {
                        deviceTypeOptions.forEach { (value, label) ->
                            add { _, _, shape ->
                                MinusSelectableRow(
                                    shape = shape,
                                    label = label,
                                    selected = settings.deviceType == value,
                                    onClick = {
                                        controller.updateSettings(
                                            settings.copy(deviceType = value),
                                        )
                                        expandedPicker = null
                                    },
                                )
                            }
                        }
                    }
                },
            )
        }

        // ── General ──
        item {
            MinusGroup(
                title = "General",
                rows = buildList {
                    add { _, _, shape ->
                        // Appearance, laid out like Minus's appearance
                        // screen: mode group, then the "Color scheme"
                        // header over the morphing swatch row.
                        MinusTile(shape) {
                            Column(
                                Modifier.padding(vertical = 16.dp),
                                verticalArrangement =
                                    Arrangement.spacedBy(16.dp),
                            ) {
                                Row(
                                    Modifier.padding(horizontal = 16.dp),
                                    verticalAlignment =
                                        Alignment.CenterVertically,
                                ) {
                                    MinusLeadingIcon(
                                        MaterialSymbols.OutlinedFilled
                                            .Palette,
                                        false,
                                    )
                                    Spacer(Modifier.width(16.dp))
                                    Text(
                                        "Appearance",
                                        style = MaterialTheme.typography
                                            .bodyMediumEmphasized,
                                    )
                                }
                                Column(
                                    Modifier.padding(horizontal = 16.dp),
                                ) {
                                    MinusThemeModeGroup(
                                        selectedIndex =
                                            when (settings.themeMode) {
                                                "light" -> 1
                                                "dark" -> 2
                                                else -> 0
                                            },
                                        onSelect = { index ->
                                            controller.updateSettings(
                                                settings.copy(
                                                    themeMode = listOf(
                                                        "system",
                                                        "light",
                                                        "dark",
                                                    )[index],
                                                ),
                                            )
                                        },
                                    )
                                }
                                Column(
                                    verticalArrangement =
                                        Arrangement.spacedBy(8.dp),
                                ) {
                                    Text(
                                        "Color scheme",
                                        style = MaterialTheme.typography
                                            .labelMedium,
                                        color = MaterialTheme.colorScheme
                                            .onSurfaceVariant,
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                        ),
                                    )
                                    val darkNow =
                                        when (settings.themeMode) {
                                            "light" -> false
                                            "dark" -> true
                                            else -> isSystemInDarkTheme()
                                        }
                                    LazyRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentPadding = PaddingValues(
                                            horizontal = 16.dp,
                                        ),
                                        horizontalArrangement =
                                            Arrangement.spacedBy(12.dp),
                                    ) {
                                        items(
                                            listOf("brand") +
                                                MinusColorSchemeIds +
                                                listOf("mono"),
                                        ) { id ->
                                            val swatchScheme = when (id) {
                                                "brand" -> if (darkNow) {
                                                    DarkScheme
                                                } else {
                                                    LightScheme
                                                }
                                                "mono" -> if (darkNow) {
                                                    MonoDarkScheme
                                                } else {
                                                    MonoLightScheme
                                                }
                                                else -> minusColorScheme(
                                                    id, darkNow,
                                                ) ?: if (darkNow) {
                                                    DarkScheme
                                                } else {
                                                    LightScheme
                                                }
                                            }
                                            MinusSwatch(
                                                scheme = swatchScheme,
                                                selected =
                                                    settings.colorMode == id ||
                                                        (id == "brand" &&
                                                            settings
                                                                .colorMode ==
                                                                "dynamic"),
                                                onClick = {
                                                    controller
                                                        .updateSettings(
                                                            settings.copy(
                                                                colorMode = id,
                                                            ),
                                                        )
                                                },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    add { index, count, _ ->
                        // Minus's Material You toggle: wallpaper colors
                        // apply to the brand scheme only.
                        MinusSwitchRow(
                            index = index,
                            count = count,
                            icon = MaterialSymbols.OutlinedFilled.Palette,
                            title = "Material You",
                            subtitle =
                                "Wallpaper colors for the brand theme",
                            checked = settings.dynamicColor ||
                                settings.colorMode == "dynamic",
                            onCheckedChange = { on ->
                                controller.updateSettings(
                                    settings.copy(
                                        dynamicColor = on,
                                        colorMode = if (!on &&
                                            settings.colorMode == "dynamic"
                                        ) {
                                            "brand"
                                        } else {
                                            settings.colorMode
                                        },
                                    ),
                                )
                            },
                        )
                    }
                    add { _, _, shape ->
                        MinusNavRow(
                            shape = shape,
                            icon = MaterialSymbols.OutlinedFilled.Language,
                            title = "Language",
                            value = languageOptions
                                .firstOrNull { it.first == settings.language }
                                ?.second ?: "System",
                            expanded = expandedPicker == "language",
                            onClick = { togglePicker("language") },
                        )
                    }
                    if (expandedPicker == "language") {
                        languageOptions.forEach { (value, label) ->
                            add { _, _, shape ->
                                MinusSelectableRow(
                                    shape = shape,
                                    label = label,
                                    selected = settings.language == value,
                                    onClick = {
                                        controller.updateSettings(
                                            settings.copy(language = value),
                                        )
                                        expandedPicker = null
                                    },
                                )
                            }
                        }
                    }
                    add { index, count, _ ->
                        MinusSwitchRow(
                            index = index,
                            count = count,
                            icon = MaterialSymbols.OutlinedFilled.Animation,
                            title = "Animations",
                            subtitle = "Splash and expressive motion",
                            checked = settings.enableAnimations,
                            onCheckedChange = {
                                controller.updateSettings(
                                    settings.copy(enableAnimations = it),
                                )
                            },
                        )
                    }
                    if (supportsQuickShareTile()) {
                        add { _, _, shape ->
                            MinusTile(shape) {
                                Row(
                                    Modifier.padding(
                                        horizontal = 16.dp, vertical = 12.dp,
                                    ),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    MinusLeadingIcon(
                                        MaterialSymbols.OutlinedFilled.Share,
                                        false,
                                    )
                                    Spacer(Modifier.width(16.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            "Quick share tile",
                                            style = MaterialTheme.typography
                                                .bodyMediumEmphasized,
                                        )
                                        Text(
                                            "Add the LSEx tile from your Quick " +
                                                "Settings panel editor to start " +
                                                "a share in one tap.",
                                            style = MaterialTheme.typography
                                                .bodySmall,
                                            color = MaterialTheme.colorScheme
                                                .onSurfaceVariant,
                                        )
                                    }
                                }
                            }
                        }
                    }
                    add { index, count, _ ->
                        MinusSwitchRow(
                            index = index,
                            count = count,
                            icon = MaterialSymbols.OutlinedFilled.Tune,
                            title = "Advanced settings",
                            subtitle = "Show the advanced sections, " +
                                "like the official app",
                            checked = advanced,
                            onCheckedChange = { advanced = it },
                        )
                    }
                },
            )
        }

        // ── Desktop (window behaviour) ──
        if (platformDeviceType() == DeviceType.desktop) {
            item {
                MinusGroup(
                    title = "Desktop",
                    rows = listOf(
                        { index: Int, count: Int, _: RoundedCornerShape ->
                            MinusSwitchRow(
                                index = index,
                                count = count,
                                icon = MaterialSymbols.OutlinedFilled.Push_pin,
                                title = "Always on top",
                                checked = settings.alwaysOnTop,
                                onCheckedChange = {
                                    controller.updateSettings(
                                        settings.copy(alwaysOnTop = it),
                                    )
                                },
                            )
                        },
                        { index: Int, count: Int, _: RoundedCornerShape ->
                            MinusSwitchRow(
                                index = index,
                                count = count,
                                icon = MaterialSymbols.OutlinedFilled.Save,
                                title = "Save window placement",
                                checked = settings.saveWindowPlacement,
                                onCheckedChange = {
                                    controller.updateSettings(
                                        settings.copy(saveWindowPlacement = it),
                                    )
                                },
                            )
                        },
                        { index: Int, count: Int, _: RoundedCornerShape ->
                            MinusSwitchRow(
                                index = index,
                                count = count,
                                icon = MaterialSymbols.OutlinedFilled.Web_asset,
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
        item {
            MinusGroup(
                title = strings.receive,
                rows = buildList {
                    add { _, _, shape ->
                        MinusTile(shape) {
                            Column(
                                Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    "Quick Save",
                                    style = MaterialTheme.typography
                                        .bodyMediumEmphasized,
                                )
                                Text(
                                    "Skip the accept prompt — for everyone, " +
                                        "or only for favorites.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme
                                        .onSurfaceVariant,
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
                        }
                    }
                    add { index, count, _ ->
                        MinusSwitchRow(
                            index = index,
                            count = count,
                            icon = MaterialSymbols.OutlinedFilled.Pin,
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
                                        pin = if (enabled) {
                                            settings.pin.ifEmpty { "1234" }
                                        } else {
                                            ""
                                        },
                                    ),
                                )
                            },
                        )
                    }
                    add { index, count, _ ->
                        MinusSwitchRow(
                            index = index,
                            count = count,
                            icon = MaterialSymbols.OutlinedFilled.Photo_library,
                            title = "Save to Gallery",
                            subtitle = "Photos and videos go to Pictures / Movies",
                            checked = settings.saveToGallery,
                            onCheckedChange = {
                                controller.updateSettings(
                                    settings.copy(saveToGallery = it),
                                )
                            },
                        )
                    }
                    add { index, count, _ ->
                        MinusSwitchRow(
                            index = index,
                            count = count,
                            icon = MaterialSymbols.OutlinedFilled.Done_all,
                            title = "Auto Finish",
                            subtitle = "Close the receive progress " +
                                "when a transfer ends",
                            checked = settings.autoFinish,
                            onCheckedChange = {
                                controller.updateSettings(
                                    settings.copy(autoFinish = it),
                                )
                            },
                        )
                    }
                    add { index, count, _ ->
                        MinusSwitchRow(
                            index = index,
                            count = count,
                            icon = MaterialSymbols.OutlinedFilled.History,
                            title = "Save to History",
                            checked = settings.saveToHistory,
                            onCheckedChange = {
                                controller.updateSettings(
                                    settings.copy(saveToHistory = it),
                                )
                            },
                        )
                    }
                    if (advanced) {
                        add { index, count, _ ->
                            MinusSwitchRow(
                                index = index,
                                count = count,
                                icon = MaterialSymbols.OutlinedFilled.Verified,
                                title = "Verify checksums",
                                subtitle = "Reject files whose SHA-256 " +
                                    "doesn't match",
                                checked = settings.verifyChecksums,
                                onCheckedChange = {
                                    controller.updateSettings(
                                        settings.copy(verifyChecksums = it),
                                    )
                                },
                            )
                        }
                    }
                    add { _, _, shape ->
                        MinusTile(shape) {
                            Column(
                                Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    "Destination folder",
                                    style = MaterialTheme.typography
                                        .bodyMediumEmphasized,
                                )
                                if (usesSafDestination()) {
                                    Text(
                                        if (settings.destinationTreeUri.isEmpty()) {
                                            "Downloads (default)"
                                        } else {
                                            "Custom folder (picked with " +
                                                "the system picker)"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme
                                            .onSurfaceVariant,
                                    )
                                    Row(
                                        horizontalArrangement =
                                            Arrangement.spacedBy(8.dp),
                                    ) {
                                        Button(onClick = destinationPicker) {
                                            Text("Choose folder")
                                        }
                                        if (settings.destinationTreeUri
                                                .isNotEmpty()
                                        ) {
                                            OutlinedButton(
                                                onClick = {
                                                    controller.updateSettings(
                                                        settings.copy(
                                                            destinationTreeUri = "",
                                                        ),
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
                                    Row(
                                        horizontalArrangement =
                                            Arrangement.spacedBy(8.dp),
                                    ) {
                                        OutlinedButton(onClick = destinationPicker) {
                                            Text("Browse…")
                                        }
                                        Button(
                                            onClick = {
                                                controller.updateSettings(
                                                    settings.copy(
                                                        destinationFolder =
                                                            destinationText.trim(),
                                                    ),
                                                )
                                            },
                                        ) { Text("Save destination") }
                                    }
                                }
                            }
                        }
                    }
                },
            )
        }

        // ── Sending (advanced) ──
        if (advanced) {
            item {
                MinusGroup(
                    title = "Sending",
                    rows = listOf(
                        { index: Int, count: Int, _: RoundedCornerShape ->
                            MinusSwitchRow(
                                index = index,
                                count = count,
                                icon = MaterialSymbols.OutlinedFilled.Link,
                                title = "Auto-accept web uploads",
                                subtitle = "Receive-via-link uploads " +
                                    "skip the prompt",
                                checked = settings.shareViaLinkAutoAccept,
                                onCheckedChange = {
                                    controller.updateSettings(
                                        settings.copy(
                                            shareViaLinkAutoAccept = it,
                                        ),
                                    )
                                },
                            )
                        },
                        { index: Int, count: Int, _: RoundedCornerShape ->
                            MinusSwitchRow(
                                index = index,
                                count = count,
                                icon = MaterialSymbols.OutlinedFilled.Fingerprint,
                                title = "Create checksums",
                                subtitle = "SHA-256 for every file sent " +
                                    "(slower on huge files)",
                                checked = settings.createChecksums,
                                onCheckedChange = {
                                    controller.updateSettings(
                                        settings.copy(createChecksums = it),
                                    )
                                },
                            )
                        },
                        { index: Int, count: Int, _: RoundedCornerShape ->
                            MinusSwitchRow(
                                index = index,
                                count = count,
                                icon = MaterialSymbols.OutlinedFilled.Replay,
                                title = "Retry automatically",
                                subtitle = "Failed sends back off and " +
                                    "retry on their own",
                                checked = settings.autoRetry,
                                onCheckedChange = {
                                    controller.updateSettings(
                                        settings.copy(autoRetry = it),
                                    )
                                },
                            )
                        },
                        { index: Int, count: Int, _: RoundedCornerShape ->
                            MinusSwitchRow(
                                index = index,
                                count = count,
                                icon = MaterialSymbols.OutlinedFilled.Bolt,
                                title = "Chunked transfers",
                                subtitle = "Resumable 1 MiB chunks " +
                                    "between LSEx devices",
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

        // ── Connection ──
        item {
            MinusGroup(
                title = "Connection",
                rows = buildList {
                    add { _, _, shape ->
                        MinusNavRow(
                            shape = shape,
                            icon = MaterialSymbols.OutlinedFilled.Wifi_tethering,
                            title = "Hotspot",
                            subtitle = "Name, password and band for your hotspot",
                            onClick = { connDialog = "hotspot" },
                        )
                    }
                    add { _, _, shape ->
                        MinusNavRow(
                            shape = shape,
                            icon = MaterialSymbols.OutlinedFilled.Router,
                            title = "Wi-Fi Direct",
                            subtitle = "Group name, password and band",
                            onClick = { connDialog = "p2p" },
                        )
                    }
                    add { _, _, shape ->
                        MinusNavRow(
                            shape = shape,
                            icon = MaterialSymbols.OutlinedFilled.Sort,
                            title = "Receive queue order",
                            value = orderOptions
                                .firstOrNull { it.first == settings.receiveOrder }
                                ?.second ?: "Smallest files first",
                            expanded = expandedPicker == "order",
                            onClick = { togglePicker("order") },
                        )
                    }
                    if (expandedPicker == "order") {
                        orderOptions.forEach { (value, label) ->
                            add { _, _, shape ->
                                MinusSelectableRow(
                                    shape = shape,
                                    label = label,
                                    selected = settings.receiveOrder == value,
                                    onClick = {
                                        controller.updateSettings(
                                            settings.copy(receiveOrder = value),
                                        )
                                        expandedPicker = null
                                    },
                                )
                            }
                        }
                    }
                },
            )
        }

        // ── Network ──
        item {
            MinusGroup(
                title = "Network",
                rows = buildList {
                    add { index, count, _ ->
                        MinusSwitchRow(
                            index = index,
                            count = count,
                            icon = MaterialSymbols.OutlinedFilled.Dns,
                            title = "Server",
                            subtitle = "Other devices can find and send to you",
                            checked = settings.serverEnabled,
                            onCheckedChange = {
                                controller.updateSettings(
                                    settings.copy(serverEnabled = it),
                                )
                            },
                        )
                    }
                    add { _, _, shape ->
                        MinusTile(shape) {
                            Column(
                                Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                OutlinedTextField(
                                    value = portText,
                                    onValueChange = {
                                        portText = it.filter { c -> c.isDigit() }
                                    },
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
                                                port = portText.toIntOrNull()
                                                    ?: ProtocolConstants
                                                        .DEFAULT_PORT,
                                                pin = pinText.trim(),
                                            ),
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                ) { Text("Save port and PIN") }
                            }
                        }
                    }
                    add { index, count, _ ->
                        MinusSwitchRow(
                            index = index,
                            count = count,
                            icon = MaterialSymbols.OutlinedFilled.Lock,
                            title = "Encryption (HTTPS)",
                            subtitle = "Self-signed certificate, " +
                                "fingerprint-verified",
                            checked = settings.https,
                            onCheckedChange = {
                                controller.updateSettings(
                                    settings.copy(https = it),
                                )
                            },
                        )
                    }
                    add { index, count, _ ->
                        MinusSwitchRow(
                            index = index,
                            count = count,
                            icon = MaterialSymbols.OutlinedFilled.Language,
                            title = "Download API",
                            subtitle = "Share-via-link web page and uploads",
                            checked = settings.downloadApi,
                            onCheckedChange = {
                                controller.updateSettings(
                                    settings.copy(downloadApi = it),
                                )
                            },
                        )
                    }
                    if (advanced) {
                        add { _, _, shape ->
                            MinusTile(shape) {
                                Column(
                                    Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    InfoRow(
                                        "Device model",
                                        local.deviceModel ?: "—",
                                    )
                                    InfoRow(
                                        "Device type",
                                        local.deviceType.name.lowercase(),
                                    )
                                    InfoRow(
                                        "Fingerprint",
                                        local.fingerprint.take(24) + "…",
                                    )
                                    InfoRow(
                                        "Transfer core",
                                        if (rustCoreActive()) {
                                            "Rust (native)"
                                        } else {
                                            "Kotlin fallback"
                                        },
                                    )
                                }
                            }
                        }
                        add { _, _, shape ->
                            MinusTile(shape) {
                                Column(
                                    Modifier.padding(16.dp),
                                    verticalArrangement =
                                        Arrangement.spacedBy(10.dp),
                                ) {
                                    OutlinedTextField(
                                        value = timeoutText,
                                        onValueChange = {
                                            timeoutText =
                                                it.filter { c -> c.isDigit() }
                                        },
                                        label = {
                                            Text("Discovery timeout (seconds)")
                                        },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                    OutlinedTextField(
                                        value = multicastText,
                                        onValueChange = { multicastText = it },
                                        label = {
                                            Text(
                                                "Multicast group " +
                                                    "(empty = 224.0.0.167)",
                                            )
                                        },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                    OutlinedTextField(
                                        value = whitelistText,
                                        onValueChange = { whitelistText = it },
                                        label = {
                                            Text(
                                                "Network whitelist " +
                                                    "(IPs/CIDRs, comma-separated)",
                                            )
                                        },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                    OutlinedTextField(
                                        value = blacklistText,
                                        onValueChange = { blacklistText = it },
                                        label = {
                                            Text(
                                                "Network blacklist " +
                                                    "(IPs/CIDRs, comma-separated)",
                                            )
                                        },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                    Button(
                                        onClick = {
                                            controller.updateSettings(
                                                settings.copy(
                                                    discoveryTimeoutSec =
                                                        timeoutText.toIntOrNull()
                                                            ?: 10,
                                                    multicastAddress =
                                                        multicastText.trim(),
                                                    networkWhitelist =
                                                        whitelistText.trim(),
                                                    networkBlacklist =
                                                        blacklistText.trim(),
                                                ),
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                    ) { Text("Save advanced network settings") }
                                }
                            }
                        }
                    }
                },
            )
        }

        // ── Favorites ──
        if (settings.favorites.isNotEmpty()) {
            item {
                MinusGroup(
                    title = "Favorites",
                    rows = settings.favorites.map { fingerprint ->
                        { _: Int, _: Int, shape: RoundedCornerShape ->
                            MinusTile(shape) {
                                Row(
                                    Modifier.padding(
                                        horizontal = 16.dp, vertical = 10.dp,
                                    ),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    MinusLeadingIcon(
                                        MaterialSymbols.OutlinedFilled.Star,
                                        false,
                                    )
                                    Spacer(Modifier.width(16.dp))
                                    Text(
                                        fingerprint.take(20) + "…",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f),
                                    )
                                    IconButton(
                                        onClick = {
                                            controller.toggleFavorite(fingerprint)
                                        },
                                    ) {
                                        Icon(
                                            MaterialSymbols.OutlinedFilled.Close,
                                            contentDescription = "Remove favorite",
                                        )
                                    }
                                }
                            }
                        }
                    },
                )
            }
        }

        // ── Donate ──
        item {
            val ltcAddress = "ltc1q2t7pg8xyyqvu2cm2jxpxtqcxr5layxztf32q5g"
            val wallets = listOf(
                "Bitcoin (BTC)" to
                    "bc1qvfr0kk56lhjn72fklaeg83flq3tjapls6gtpmy",
                "Bitcoin Cash (BCH)" to
                    "qpg572zm69ln9tskesg4lptqjdde29260uqqvg4zpk",
                "TON" to
                    "UQAOQmzcUNKFfyvKMr8z5dTK1rBdj1D4uopw0G9Lxnpp6lw3",
                "USDT (TRC20 · Tron)" to
                    "TVfwxMMa4dsAsRmd6VNkV1sUcGFK8TKwSA",
                "USDT (ERC20 · Ethereum)" to
                    "0x796759d5c38fF7CF1b452b8AB92a5ec23f16CC66",
                "Tron (TRX)" to
                    "TVfwxMMa4dsAsRmd6VNkV1sUcGFK8TKwSA",
                "Ethereum (ETH)" to
                    "0x796759d5c38fF7CF1b452b8AB92a5ec23f16CC66",
            )
            MinusGroup(
                title = "Donate",
                rows = buildList {
                    add { _, _, shape ->
                        MinusTile(shape) {
                            Column(
                                Modifier.padding(16.dp),
                                horizontalAlignment =
                                    Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Text(
                                    "😺 The QR code is my 🪙Litecoin wallet, " +
                                        "which is the preferred coin.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                )
                                QrCode(ltcAddress)
                                Text(
                                    "Litecoin (LTC)",
                                    style = MaterialTheme.typography
                                        .bodyMediumEmphasized,
                                )
                                Text(
                                    ltcAddress,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme
                                        .onSurfaceVariant,
                                    fontFamily = FontFamily.Monospace,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                    wallets.forEach { (label, address) ->
                        add { _, _, shape ->
                            MinusTile(shape) {
                                Column(
                                    Modifier.padding(
                                        horizontal = 16.dp, vertical = 12.dp,
                                    ),
                                    verticalArrangement =
                                        Arrangement.spacedBy(2.dp),
                                ) {
                                    Text(
                                        label,
                                        style = MaterialTheme.typography
                                            .bodyMediumEmphasized,
                                    )
                                    Text(
                                        address,
                                        style = MaterialTheme.typography
                                            .bodySmall,
                                        color = MaterialTheme.colorScheme
                                            .onSurfaceVariant,
                                        fontFamily = FontFamily.Monospace,
                                    )
                                }
                            }
                        }
                    }
                    add { _, _, shape ->
                        MinusTile(shape) {
                            Column(
                                Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Text(
                                    "Huge thanks for the support 🙂! ✨",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                Text(
                                    "print(\"You're helping me get done " +
                                        "with my studies, pay internet and " +
                                        "get some snacks\")",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme
                                        .onSurfaceVariant,
                                    fontFamily = FontFamily.Monospace,
                                )
                            }
                        }
                    }
                },
            )
        }

        item {
            Text(
                "LSEx 0.9.0 · LocalSend Protocol 2.2 · Unofficial, " +
                    "credits to the LocalSend project",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    if (showAvatarPicker) {
        AvatarPickerDialog(
            current = settings.avatar,
            onDismiss = { showAvatarPicker = false },
            onPick = { avatarId ->
                controller.updateSettings(settings.copy(avatar = avatarId))
                showAvatarPicker = false
            },
        )
    }
    connDialog?.let { kind ->
        ConnectionSettingsDialog(
            controller = controller,
            settings = settings,
            kind = kind,
            onDismiss = { connDialog = null },
        )
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
/** Position corners for a segmented list (Minus anatomy): 16dp outer, 4dp inner. */
private fun historyTileShape(index: Int, count: Int): RoundedCornerShape = when {
    count == 1 -> RoundedCornerShape(16.dp)
    index == 0 -> RoundedCornerShape(
        topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 4.dp,
    )
    index == count - 1 -> RoundedCornerShape(
        topStart = 4.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp,
    )
    else -> RoundedCornerShape(4.dp)
}

/** One circular action button behind a swipe-revealed row (mockup style). */
@Composable
private fun SwipeCircleButton(
    container: Color,
    content: Color,
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
) {
    Surface(shape = CircleShape, color = container) {
        IconButton(onClick = onClick, modifier = Modifier.size(44.dp)) {
            Icon(icon, contentDescription = description, tint = content)
        }
    }
}

@Composable
private fun HistoryRow(
    entry: HistoryEntry,
    shape: RoundedCornerShape,
    onOpen: (() -> Unit)? = null,
    onInstall: (() -> Unit)? = null,
    onResend: (() -> Unit)? = null,
    onRemove: () -> Unit,
    onDeleteFile: (() -> Unit)? = null,
) {
    if (!prefersSwipeActions()) {
        // Pointer platforms: actions stay visible on the tile.
        Surface(shape = shape, color = MaterialTheme.colorScheme.surfaceContainerLow) {
            HistoryRowContent(entry, onOpen, onInstall, onResend, onRemove, showActions = true)
        }
        return
    }
    // Touch: a left swipe reveals the mockup's circular action buttons —
    // remove-entry, the row's primary action (open / install / resend),
    // and for received files a red delete that removes the file itself.
    // (v0.8.0 shipped History with NO slide options at all.)
    val hasPrimary = onOpen != null || onInstall != null || onResend != null
    val actionCount = 1 + (if (hasPrimary) 1 else 0) + (if (onDeleteFile != null) 1 else 0)
    val density = LocalDensity.current
    val revealPx = with(density) { (actionCount * 52).dp.toPx() }
    val state = remember {
        AnchoredDraggableState(
            initialValue = 0,
            anchors = DraggableAnchors {
                0 at 0f
                1 at -revealPx
            },
        )
    }
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
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        Row(
            Modifier.matchParentSize().padding(end = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SwipeCircleButton(
                container = MaterialTheme.colorScheme.surfaceContainerLow,
                content = MaterialTheme.colorScheme.onSurface,
                icon = MaterialSymbols.Outlined.Close,
                description = "Remove from history",
            ) {
                scope.launch { state.snapTo(0) }
                onRemove()
            }
            if (hasPrimary) {
                Spacer(Modifier.width(8.dp))
                SwipeCircleButton(
                    container = MaterialTheme.colorScheme.secondaryContainer,
                    content = MaterialTheme.colorScheme.onSecondaryContainer,
                    icon = when {
                        onOpen != null -> MaterialSymbols.Outlined.Open_in_new
                        onInstall != null -> MaterialSymbols.Outlined.System_update
                        else -> MaterialSymbols.Outlined.Send
                    },
                    description = when {
                        onOpen != null -> "Open file"
                        onInstall != null -> "Install this package"
                        else -> "Resend"
                    },
                ) {
                    scope.launch { state.snapTo(0) }
                    (onOpen ?: onInstall ?: onResend)?.invoke()
                }
            }
            if (onDeleteFile != null) {
                Spacer(Modifier.width(8.dp))
                SwipeCircleButton(
                    container = MaterialTheme.colorScheme.error,
                    content = MaterialTheme.colorScheme.onError,
                    icon = MaterialSymbols.Outlined.Delete,
                    description = "Delete the received file",
                ) {
                    scope.launch { state.snapTo(0) }
                    onDeleteFile()
                }
            }
        }
        Surface(
            shape = shape,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    val off = state.offset
                    IntOffset(if (off.isNaN()) 0 else off.roundToInt(), 0)
                }
                .anchoredDraggable(state, Orientation.Horizontal)
                .combinedClickable(
                    onClick = {
                        if (state.currentValue == 1) {
                            scope.launch { state.snapTo(0) }
                        }
                    },
                    onLongClick = { (onOpen ?: onInstall ?: onResend)?.invoke() },
                ),
        ) {
            HistoryRowContent(entry, onOpen, onInstall, onResend, onRemove, showActions = false)
        }
    }
}

@Composable
private fun HistoryRowContent(
    entry: HistoryEntry,
    onOpen: (() -> Unit)?,
    onInstall: (() -> Unit)?,
    onResend: (() -> Unit)?,
    onRemove: () -> Unit,
    showActions: Boolean,
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
        if (showActions) {
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
            IconButton(onClick = onRemove) {
                Icon(
                    MaterialSymbols.Outlined.Close,
                    contentDescription = "Remove from history",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
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

// ── Avatars (Settings picker; default = the device-type glyph) ────────────

private fun avatarIcon(id: String): ImageVector? = when (id) {
    "person" -> MaterialSymbols.Outlined.Person
    "rocket" -> MaterialSymbols.Outlined.Rocket_launch
    "pets" -> MaterialSymbols.Outlined.Pets
    "bolt" -> MaterialSymbols.Outlined.Bolt
    "toy" -> MaterialSymbols.Outlined.Smart_toy
    "game" -> MaterialSymbols.Outlined.Sports_esports
    "music" -> MaterialSymbols.Outlined.Music_note
    "flight" -> MaterialSymbols.Outlined.Flight
    "car" -> MaterialSymbols.Outlined.Directions_car
    "coffee" -> MaterialSymbols.Outlined.Coffee
    "heart" -> MaterialSymbols.Outlined.Favorite
    "eco" -> MaterialSymbols.Outlined.Eco
    "terminal" -> MaterialSymbols.Outlined.Terminal
    "star" -> MaterialSymbols.Outlined.Star
    else -> null
}

private val AvatarOptions: List<Pair<String, String>> = listOf(
    "" to "Device icon",
    "person" to "Person",
    "rocket" to "Rocket",
    "pets" to "Pet",
    "bolt" to "Bolt",
    "toy" to "Robot",
    "game" to "Games",
    "music" to "Music",
    "flight" to "Flight",
    "car" to "Car",
    "coffee" to "Coffee",
    "heart" to "Heart",
    "eco" to "Leaf",
    "terminal" to "Terminal",
    "star" to "Star",
)

// ── Share page (Ernest's mockup): devices, split Send, picker grid ────────

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ShareScreen(
    controller: LocalSendController,
    settings: AppSettings,
    staged: List<StagedItem>,
    onStagedChange: (List<StagedItem>) -> Unit,
    selectedFingerprints: Set<String>,
    onSelectedChange: (Set<String>) -> Unit,
    onAddByIp: () -> Unit,
    onShowTransfers: () -> Unit,
    onShareViaLink: () -> Unit,
) {
    val strings = LocalStrings.current
    val devices by controller.devices.collectAsState()
    val scanning by controller.scanning.collectAsState()
    val outbox by controller.outbox.collectAsState()
    val incomingPeer by controller.incomingPeer.collectAsState()
    val incomingProgress by controller.incomingProgress.collectAsState()
    val scope = rememberCoroutineScope()
    var infoDevice by remember { mutableStateOf<DiscoveredDevice?>(null) }
    var editIndex by remember { mutableStateOf(-1) }
    var pickerTab by remember { mutableStateOf<ContentTab?>(null) }
    var showTextComposer by remember { mutableStateOf(false) }
    var selectionExpanded by rememberSaveable { mutableStateOf(true) }
    var sendMenuOpen by remember { mutableStateOf(false) }
    val clipboard = rememberClipboardText()

    LaunchedEffect(Unit) { controller.refreshDiscovery() }

    val stagedIds = staged.mapNotNull { item ->
        when (item) {
            is StagedItem.FileItem -> "file:${item.file.handle}"
            is StagedItem.FolderItem -> item.folder.id
            is StagedItem.TextItem -> null
        }
    }.toSet()

    fun toggleFile(file: PickedFile) {
        val id = "file:${file.handle}"
        onStagedChange(
            if (stagedIds.contains(id)) {
                staged.filterNot { it is StagedItem.FileItem && "file:${it.file.handle}" == id }
            } else {
                staged + StagedItem.FileItem(file)
            },
        )
    }

    fun toggleFolder(folder: BrowserItem) {
        onStagedChange(
            if (stagedIds.contains(folder.id)) {
                staged.filterNot { it is StagedItem.FolderItem && it.folder.id == folder.id }
            } else {
                staged + StagedItem.FolderItem(folder)
            },
        )
    }

    suspend fun expandStaged(): Pair<List<PickedFile>, List<String>> {
        val files = mutableListOf<PickedFile>()
        val texts = mutableListOf<String>()
        for (item in staged) {
            when (item) {
                is StagedItem.FileItem -> files += item.file
                is StagedItem.FolderItem -> files += expandBrowserFolder(item.folder)
                is StagedItem.TextItem -> texts += item.text
            }
        }
        return files to texts
    }

    val targets = devices.filter { it.info.fingerprint in selectedFingerprints }

    fun doSend() {
        if (staged.isEmpty() || targets.isEmpty()) return
        scope.launch {
            val (files, texts) = expandStaged()
            if (files.isNotEmpty()) {
                if (targets.size == 1) {
                    controller.sendFiles(targets[0], files)
                } else {
                    controller.sendFilesToTargets(targets, files)
                }
            }
            texts.forEach { text ->
                if (targets.size == 1) {
                    controller.sendText(targets[0], text)
                } else {
                    controller.sendTextToTargets(targets, text)
                }
            }
            onStagedChange(emptyList())
            onShowTransfers()
        }
    }

    val systemFilePicker = rememberFilePickerLauncher { picked ->
        picked.forEach { toggleFile(it) }
    }
    val systemFolderPicker = rememberFolderPickerLauncher { picked ->
        picked.forEach { toggleFile(it) }
    }
    val mediaPicker = rememberMediaPickerLauncher { picked ->
        picked.forEach { toggleFile(it) }
    }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 140.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { LsexWordmark() }

        // ── Nearby devices + split Send button ──
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    strings.nearbyDevices,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                )
                // The official expressive SplitButton: leading = Send,
                // trailing = a toggle whose chevron spins as its menu
                // opens and whose shape morphs to a full pill.
                Box {
                    SplitButtonLayout(
                        leadingButton = {
                            SplitButtonDefaults.LeadingButton(
                                onClick = { doSend() },
                                enabled = staged.isNotEmpty() && targets.isNotEmpty(),
                            ) {
                                Icon(MaterialSymbols.Outlined.Send, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text("Send")
                            }
                        },
                        trailingButton = {
                            SplitButtonDefaults.TrailingButton(
                                checked = sendMenuOpen,
                                onCheckedChange = { sendMenuOpen = it },
                            ) {
                                val chevronRotation by animateFloatAsState(
                                    if (sendMenuOpen) 180f else 0f,
                                )
                                Icon(
                                    MaterialSymbols.Outlined.Expand_more,
                                    contentDescription = "More send options",
                                    modifier = Modifier.graphicsLayer {
                                        rotationZ = chevronRotation
                                    },
                                )
                            }
                        },
                    )
                    DropdownMenu(
                        expanded = sendMenuOpen,
                        onDismissRequest = { sendMenuOpen = false },
                    ) {
                            DropdownMenuItem(
                                text = { Text("Add device by IP") },
                                leadingIcon = {
                                    Icon(MaterialSymbols.Outlined.Lan, contentDescription = null)
                                },
                                onClick = {
                                    sendMenuOpen = false
                                    onAddByIp()
                                },
                            )
                    }
                }
            }
        }

        if (scanning) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LoadingIndicator(Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        strings.searching,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        if (devices.isEmpty()) {
            item {
                Text(
                    "No devices yet. Both devices need the app open on the same network — or start a hotspot / Wi-Fi Direct group on the Home page.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedButton(onClick = { controller.refreshDiscovery() }) {
                    Icon(MaterialSymbols.Outlined.Refresh, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Search again")
                }
            }
        }

        val orderedDevices = devices.sortedByDescending {
            it.info.fingerprint in settings.favorites
        }
        items(orderedDevices, key = { it.info.fingerprint }) { device ->
            val isSelected = device.info.fingerprint in selectedFingerprints
            // A device counts as "in a transfer" when an outbox job for
            // it has files queued or moving (not just mid-file — between
            // files and while queued count too, or the tap falls through
            // to selection toggling: the v0.8.0 "sometimes won't open"
            // bug), or when we are receiving from it right now.
            val hasActiveTransfer = outbox.any { job ->
                job.targetFingerprint == device.info.fingerprint &&
                    job.files.any {
                        it.status == TransferStatus.ACTIVE ||
                            it.status == TransferStatus.WAITING
                    }
            } || incomingPeer == device.info.fingerprint
            val failedJob = outbox.firstOrNull { job ->
                job.targetFingerprint == device.info.fingerprint && job.hasFailures
            }
            // Live progress with THIS device, drawn behind its card
            // (Ernest's mockup): sending from the outbox job, receiving
            // from the incoming progress attributed to this peer.
            val transferFraction: Float? = when {
                hasActiveTransfer -> {
                    val job = outbox.firstOrNull {
                        it.targetFingerprint == device.info.fingerprint &&
                            it.files.any { f ->
                                f.status == TransferStatus.ACTIVE ||
                                    f.status == TransferStatus.WAITING
                            }
                    }
                    when {
                        job != null -> {
                            val total = job.files.sumOf { it.size }
                            if (total > 0) {
                                (job.files.sumOf { it.confirmedBytes }.toFloat() / total)
                                    .coerceIn(0f, 1f)
                            } else {
                                null
                            }
                        }
                        incomingPeer == device.info.fingerprint -> {
                            val total = incomingProgress.sumOf { it.total }
                            if (total > 0) {
                                (incomingProgress.sumOf { it.done }.toFloat() / total)
                                    .coerceIn(0f, 1f)
                            } else {
                                null
                            }
                        }
                        else -> null
                    }
                }
                else -> null
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                DeviceCard(
                    device = device,
                    isSelected = isSelected,
                    isFavorite = device.info.fingerprint in settings.favorites,
                    transferFraction = transferFraction,
                    onClick = {
                        when {
                            hasActiveTransfer -> {
                                // Already transferring with this device: queue the
                                // staged files to it, then open the transfer view.
                                if (staged.isNotEmpty()) {
                                    scope.launch {
                                        val (files, _) = expandStaged()
                                        if (files.isNotEmpty()) {
                                            controller.sendFiles(device, files)
                                        }
                                        onStagedChange(emptyList())
                                    }
                                }
                                onShowTransfers()
                            }
                            settings.multiDeviceSend -> onSelectedChange(
                                if (isSelected) {
                                    selectedFingerprints - device.info.fingerprint
                                } else {
                                    selectedFingerprints + device.info.fingerprint
                                },
                            )
                            else -> onSelectedChange(
                                if (isSelected) emptySet() else setOf(device.info.fingerprint),
                            )
                        }
                    },
                    onToggleFavorite = { controller.toggleFavorite(device.info.fingerprint) },
                    onInfo = { infoDevice = device },
                )
                if (failedJob != null) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onShowTransfers() }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            MaterialSymbols.Outlined.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Some files need attention — tap to review",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }

        // ── Share: picker grid (Ernest's mockup tiles) ──
        item {
            Text("Share", style = MaterialTheme.typography.titleLarge)
        }
        item {
            val tiles: List<Triple<String, ImageVector, () -> Unit>> = listOf(
                Triple("Files", MaterialSymbols.Outlined.Description) { systemFilePicker() },
                Triple("Media", MaterialSymbols.Outlined.Image) { mediaPicker() },
                Triple("Folder", MaterialSymbols.Outlined.Folder) { systemFolderPicker() },
                Triple("Browse", MaterialSymbols.Outlined.Folder_open) { pickerTab = ContentTab.Files },
                Triple("Apps", MaterialSymbols.Outlined.Apps) { pickerTab = ContentTab.Apps },
                Triple("Text", MaterialSymbols.Outlined.Chat) { showTextComposer = true },
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                tiles.chunked(3).forEach { rowTiles ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        rowTiles.forEach { (label, icon, action) ->
                            Surface(
                                shape = MaterialTheme.shapes.extraLarge,
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(92.dp)
                                    .clip(MaterialTheme.shapes.extraLarge)
                                    .clickable { action() },
                            ) {
                                Column(
                                    Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Icon(icon, contentDescription = label, modifier = Modifier.size(28.dp))
                                    Spacer(Modifier.height(6.dp))
                                    Text(label, style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                        repeat(3 - rowTiles.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
                OutlinedButton(
                    onClick = {
                        clipboard()?.takeIf { it.isNotBlank() }?.let { text ->
                            onStagedChange(staged + StagedItem.TextItem(text))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(MaterialSymbols.Outlined.Content_paste, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Paste from clipboard")
                }
            }
        }

        // ── Selection of files ──
        if (staged.isNotEmpty()) {
            item {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { selectionExpanded = !selectionExpanded }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(MaterialSymbols.Outlined.Attach_file, contentDescription = null)
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "${strings.selection} · ${staged.size}",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f),
                            )
                            TextButton(onClick = { onStagedChange(emptyList()) }) {
                                Text("Clear")
                            }
                            Icon(
                                if (selectionExpanded) {
                                    MaterialSymbols.Outlined.Expand_less
                                } else {
                                    MaterialSymbols.Outlined.Expand_more
                                },
                                contentDescription = null,
                            )
                        }
                        if (selectionExpanded) {
                            Column(
                                Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(3.dp),
                            ) {
                                staged.forEachIndexed { index, item ->
                                    StagedItemCard(
                                        item = item,
                                        shape = historyTileShape(index, staged.size),
                                        onEdit = { editIndex = index },
                                        onDiscard = {
                                            onStagedChange(
                                                staged.filterIndexed { i, _ -> i != index },
                                            )
                                        },
                                    )
                                }
                            }
                        }
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
    if (showTextComposer) {
        AlertDialog(
            onDismissRequest = { showTextComposer = false },
            title = { Text("Send a text") },
            text = {
                TextComposer(onStageText = { text ->
                    onStagedChange(staged + StagedItem.TextItem(text))
                    showTextComposer = false
                })
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showTextComposer = false }) { Text("Close") }
            },
        )
    }
    if (editIndex in staged.indices) {
        val item = staged[editIndex]
        when (item) {
            is StagedItem.FileItem -> StagedEditDialog(
                title = "Rename file",
                initial = item.file.name,
                onDismiss = { editIndex = -1 },
                onSave = { newName ->
                    onStagedChange(
                        staged.mapIndexed { i, current ->
                            if (i == editIndex && current is StagedItem.FileItem) {
                                StagedItem.FileItem(current.file.copy(name = newName))
                            } else {
                                current
                            }
                        },
                    )
                    editIndex = -1
                },
            )
            is StagedItem.TextItem -> StagedEditDialog(
                title = "Edit text",
                initial = item.text,
                multiline = true,
                onDismiss = { editIndex = -1 },
                onSave = { newText ->
                    onStagedChange(
                        staged.mapIndexed { i, current ->
                            if (i == editIndex) StagedItem.TextItem(newText) else current
                        },
                    )
                    editIndex = -1
                },
            )
            is StagedItem.FolderItem -> editIndex = -1
        }
    }
    pickerTab?.let { initialTab ->
        PickerScreen(
            initialTab = initialTab,
            controller = controller,
            settings = settings,
            stagedIds = stagedIds,
            onToggleFile = { toggleFile(it) },
            onToggleFolder = { toggleFolder(it) },
            onStageFile = { toggleFile(it) },
            stagedCount = staged.size,
            onDismiss = { pickerTab = null },
        )
    }
}

// ── In-app content picker (the tabbed browsers, full screen) ──────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PickerScreen(
    initialTab: ContentTab,
    controller: LocalSendController,
    settings: AppSettings,
    stagedIds: Set<String>,
    onToggleFile: (PickedFile) -> Unit,
    onToggleFolder: (BrowserItem) -> Unit,
    onStageFile: (PickedFile) -> Unit,
    stagedCount: Int,
    onDismiss: () -> Unit,
) {
    var tab by remember { mutableStateOf(initialTab) }
    val tabs = listOf(
        ContentTab.Apps, ContentTab.Photos, ContentTab.Videos,
        ContentTab.Audio, ContentTab.Files,
    )
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 12.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 6.dp),
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(MaterialSymbols.Outlined.Arrow_back, contentDescription = "Back")
                    }
                    Text(
                        "Add files",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f),
                    )
                    Button(onClick = onDismiss) { Text("Done · $stagedCount") }
                }
                ScrollableTabRow(
                    selectedTabIndex = tabs.indexOf(tab),
                    edgePadding = 0.dp,
                ) {
                    tabs.forEach { entry ->
                        Tab(
                            selected = tab == entry,
                            onClick = { tab = entry },
                            text = {
                                Text(
                                    when (entry) {
                                        ContentTab.Apps -> "Apps"
                                        ContentTab.Photos -> "Photos"
                                        ContentTab.Videos -> "Videos"
                                        ContentTab.Audio -> "Audio"
                                        ContentTab.Files -> "Files"
                                        else -> entry.name
                                    },
                                    maxLines = 1,
                                    softWrap = false,
                                )
                            },
                            icon = {
                                Icon(
                                    when (entry) {
                                        ContentTab.Apps -> MaterialSymbols.Outlined.Apps
                                        ContentTab.Photos -> MaterialSymbols.Outlined.Image
                                        ContentTab.Videos -> MaterialSymbols.Outlined.Movie
                                        ContentTab.Audio -> MaterialSymbols.Outlined.Music_note
                                        ContentTab.Files -> MaterialSymbols.Outlined.Folder
                                        else -> MaterialSymbols.Outlined.Description
                                    },
                                    contentDescription = null,
                                )
                            },
                        )
                    }
                }
                Box(Modifier.weight(1f).padding(top = 10.dp)) {
                    when (tab) {
                        ContentTab.Apps -> AppsBrowser(
                            settings = settings,
                            onToggleHideSystem = {
                                controller.updateSettings(
                                    settings.copy(hideSystemApps = !settings.hideSystemApps),
                                )
                            },
                            stagedIds = stagedIds,
                            onToggleApp = { app ->
                                onToggleFile(
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
                                onToggleFile(PickedFile(item.handle, item.name, item.size))
                            },
                        )
                        ContentTab.Videos -> MediaBrowser(
                            kind = MediaKind.VIDEOS,
                            stagedIds = stagedIds,
                            onToggleItem = { item ->
                                onToggleFile(PickedFile(item.handle, item.name, item.size))
                            },
                        )
                        ContentTab.Audio -> MediaBrowser(
                            kind = MediaKind.AUDIO,
                            stagedIds = stagedIds,
                            onToggleItem = { item ->
                                onToggleFile(PickedFile(item.handle, item.name, item.size))
                            },
                        )
                        ContentTab.Files -> FileBrowser(
                            stagedIds = stagedIds,
                            onToggleFile = { item ->
                                onToggleFile(PickedFile(item.handle, item.name, item.size))
                            },
                            onToggleFolder = onToggleFolder,
                            onStageFile = onStageFile,
                        )
                        else -> Unit
                    }
                }
            }
        }
    }
}

// ── Transfers: the bottom sheet + the global pill (Ernest's mockups) ──────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TransferSheet(
    controller: LocalSendController,
    settings: AppSettings,
    onDismiss: () -> Unit,
    onGoToShare: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val incoming by controller.incomingProgress.collectAsState()
    val outbox by controller.outbox.collectAsState()
    val sendState by controller.sendState.collectAsState()
    val queuedOffers by controller.queuedOfferCount.collectAsState()

    val activeJobs = outbox.filter { !it.finished }
    val finishedJobs = outbox.filter { it.finished }
    val receiving = incoming.isNotEmpty()
    val textSend = sendState?.takeIf { it.jobId == null }

    // Aggregate for the summary pill at the sheet's bottom (mockup):
    // "Transfer · N/M files · speed · ETA" over everything in flight.
    val sendFiles = activeJobs.flatMap { it.files }
    val totalFiles = sendFiles.size + incoming.size
    val doneFiles = sendFiles.count { it.status == TransferStatus.DONE } +
        incoming.count { it.status == TransferStatus.DONE }
    val totalBytes = sendFiles.sumOf { it.size } + incoming.sumOf { it.total }
    val doneBytes = sendFiles.sumOf { it.confirmedBytes } + incoming.sumOf { it.done }
    val rate = rememberTransferRate(doneBytes)
    val etaSeconds = if (rate > 1 && totalBytes > doneBytes) {
        ((totalBytes - doneBytes) / rate).toLong()
    } else {
        null
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        LazyColumn(
            Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Transfers",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f),
                    )
                    if (queuedOffers > 0) {
                        InfoChip("$queuedOffers waiting to send")
                    }
                }
            }
            if (!receiving && activeJobs.isEmpty() && textSend == null && finishedJobs.isEmpty()) {
                item {
                    Text(
                        "Nothing is transferring right now.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Button(onClick = onGoToShare) { Text("Pick files to send") }
                }
            }
            if (receiving) {
                item { SectionTitle("Receiving") }
                item {
                    ReceiveQueueList(
                        files = incoming,
                        defaultOrder = settings.receiveOrder,
                    )
                }
            }
            if (textSend != null) {
                item { SectionTitle("Sending") }
                item {
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "Text to ${textSend.target.info.alias}",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            if (textSend.active) {
                                LinearWavyProgressIndicator(Modifier.fillMaxWidth())
                            }
                            textSend.error?.let { error ->
                                Text(
                                    error,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                            Row {
                                if (textSend.active) {
                                    OutlinedButton(onClick = { controller.cancelSend() }) {
                                        Text("Cancel")
                                    }
                                } else {
                                    TextButton(onClick = { controller.clearSendState() }) {
                                        Text("Dismiss")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (activeJobs.isNotEmpty() || finishedJobs.isNotEmpty()) {
                item { SectionTitle("Sending") }
                items(activeJobs + finishedJobs, key = { it.id }) { job ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutboxJobCard(controller, job)
                        if (!job.finished) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (job.paused) {
                                    Button(onClick = { controller.resumeOutboxJob(job.id) }) {
                                        Icon(MaterialSymbols.Outlined.Play_arrow, contentDescription = null)
                                        Spacer(Modifier.width(6.dp))
                                        Text("Resume")
                                    }
                                } else if (job.files.any { it.status == TransferStatus.ACTIVE }) {
                                    OutlinedButton(onClick = { controller.pauseOutboxJob(job.id) }) {
                                        Icon(MaterialSymbols.Outlined.Pause, contentDescription = null)
                                        Spacer(Modifier.width(6.dp))
                                        Text("Pause")
                                    }
                                }
                                if (job.paused) {
                                    Text(
                                        "Paused — chunked files continue where they stopped.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 8.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (totalFiles > 0) {
                item {
                    // The mockup's summary bar: label + chips + overall progress.
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    "Transfer",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.weight(1f),
                                )
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                ) {
                                    Text(
                                        "$doneFiles/$totalFiles files",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    )
                                }
                                if (rate > 1) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                    ) {
                                        Text(
                                            "${formatSize(rate.toLong())}/s",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        )
                                    }
                                }
                                if (etaSeconds != null) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.tertiaryContainer,
                                    ) {
                                        Text(
                                            "ETA ${formatEta(etaSeconds)}",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        )
                                    }
                                }
                            }
                            LinearWavyProgressIndicator(
                                progress = {
                                    if (totalBytes > 0) {
                                        (doneBytes.toFloat() / totalBytes).coerceIn(0f, 1f)
                                    } else {
                                        0f
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Bytes/second, sampled from aggregate progress deltas every 600 ms. */
@Composable
private fun rememberTransferRate(doneBytes: Long): Double {
    var rate by remember { mutableStateOf(0.0) }
    val latest = rememberUpdatedState(doneBytes)
    LaunchedEffect(Unit) {
        var previous = latest.value
        while (true) {
            delay(600)
            val now = latest.value
            val instant = (now - previous) / 0.6
            rate = when {
                now == previous -> 0.0
                rate == 0.0 -> instant
                else -> rate * 0.6 + instant * 0.4
            }
            previous = now
        }
    }
    return rate
}

private fun formatEta(seconds: Long): String = when {
    seconds < 60 -> "${seconds}s"
    seconds < 3600 -> "${seconds / 60}m ${seconds % 60}s"
    else -> "${seconds / 3600}h ${(seconds % 3600) / 60}m"
}

/**
 * The receive queue (Ernest's spec): files of the incoming session in a
 * user-reorderable list. Default order comes from Settings (smallest
 * first unless changed); the arrows set a manual order for this session.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ReceiveQueueList(files: List<FileProgress>, defaultOrder: String) {
    var manualOrder by remember { mutableStateOf<List<String>?>(null) }
    val sortedDefault = remember(files, defaultOrder) {
        when (defaultOrder) {
            "smallest" -> files.sortedBy { it.total }
            "largest" -> files.sortedByDescending { it.total }
            "name" -> files.sortedBy { it.fileName.lowercase() }
            else -> files
        }
    }
    val display = manualOrder?.let { ids ->
        ids.mapNotNull { id -> files.firstOrNull { it.fileId == id } } +
            files.filter { it.fileId !in ids }
    } ?: sortedDefault

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        display.forEachIndexed { index, file ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(36.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    fileTypeIcon(file.fileName),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                file.fileName,
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                when (file.status) {
                                    TransferStatus.DONE -> formatSize(file.total)
                                    TransferStatus.ACTIVE ->
                                        "${formatSize(file.done)} / ${formatSize(file.total)}"
                                    TransferStatus.FAILED -> "Failed · ${formatSize(file.total)}"
                                    else -> "Waiting · ${formatSize(file.total)}"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        when (file.status) {
                            TransferStatus.DONE -> Icon(
                                MaterialSymbols.Outlined.Check_circle,
                                contentDescription = "Done",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            TransferStatus.FAILED -> Icon(
                                MaterialSymbols.Outlined.Error,
                                contentDescription = "Failed",
                                tint = MaterialTheme.colorScheme.error,
                            )
                            TransferStatus.ACTIVE -> Text(
                                "${(file.fraction * 100).toInt()}%",
                                style = MaterialTheme.typography.labelLarge,
                            )
                            else -> {
                                // Reorder the waiting queue (manual order).
                                if (index > 0 && display.getOrNull(index - 1)?.status ==
                                    TransferStatus.WAITING
                                ) {
                                    IconButton(
                                        onClick = {
                                            val ids = display.map { it.fileId }.toMutableList()
                                            val from = ids.indexOf(file.fileId)
                                            ids.removeAt(from)
                                            ids.add(from - 1, file.fileId)
                                            manualOrder = ids
                                        },
                                    ) {
                                        Icon(
                                            MaterialSymbols.Outlined.Arrow_upward,
                                            contentDescription = "Move earlier",
                                        )
                                    }
                                }
                            }
                        }
                    }
                    if (file.status == TransferStatus.ACTIVE) {
                        Spacer(Modifier.height(6.dp))
                        LinearWavyProgressIndicator(
                            progress = { file.fraction },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

/** Leading glyph for a transfer row, by file extension. */
private fun fileTypeIcon(fileName: String): ImageVector {
    val ext = fileName.substringAfterLast('.', "").lowercase()
    return when (ext) {
        "jpg", "jpeg", "png", "gif", "webp", "bmp", "heic" -> MaterialSymbols.Outlined.Image
        "mp4", "mkv", "webm", "mov", "avi" -> MaterialSymbols.Outlined.Movie
        "mp3", "flac", "ogg", "wav", "m4a" -> MaterialSymbols.Outlined.Music_note
        "apk", "apks", "xapk", "apkm" -> MaterialSymbols.Outlined.Android
        "zip", "rar", "7z", "tar", "gz" -> MaterialSymbols.Outlined.Folder_zip
        "pdf" -> MaterialSymbols.Outlined.Picture_as_pdf
        "txt", "md", "log" -> MaterialSymbols.Outlined.Description
        else -> MaterialSymbols.Outlined.Description
    }
}

/**
 * The GLOBAL transfer pill (Ernest's addendum): floats above the nav bar
 * on every page while anything is sending or receiving; tapping opens
 * the transfer sheet.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
/**
 * Visibility + state for the global transfer pill, isolated from the
 * scaffold so progress updates recompose only the pill (perf pass).
 */
@Composable
private fun GlobalTransferPillHost(
    controller: LocalSendController,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val outbox by controller.outbox.collectAsState()
    val sendState by controller.sendState.collectAsState()
    val incomingProgress by controller.incomingProgress.collectAsState()
    val sending = outbox.any { job ->
        !job.paused && job.files.any { it.status == TransferStatus.ACTIVE }
    } || (sendState?.active == true)
    val receiving = incomingProgress.any { it.status == TransferStatus.ACTIVE }
    if (sending || receiving) {
        GlobalTransferPill(
            controller = controller,
            sending = sending,
            receiving = receiving,
            modifier = modifier,
            onClick = onClick,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun GlobalTransferPill(
    controller: LocalSendController,
    sending: Boolean,
    receiving: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val incoming by controller.incomingProgress.collectAsState()
    val outbox by controller.outbox.collectAsState()
    val sendFiles = outbox.filter { !it.finished && !it.paused }.flatMap { it.files }
    val activeSendCount = sendFiles.count {
        it.status == TransferStatus.ACTIVE || it.status == TransferStatus.WAITING
    }
    val activeReceiveCount = incoming.count { it.status == TransferStatus.ACTIVE }
    val doneCount = sendFiles.count { it.status == TransferStatus.DONE } +
        incoming.count { it.status == TransferStatus.DONE }
    val totalBytes = sendFiles.sumOf { it.size } + incoming.sumOf { it.total }
    val doneBytes = sendFiles.sumOf { it.confirmedBytes } + incoming.sumOf { it.done }
    val label = when {
        sending && receiving -> "Sending + receiving"
        receiving -> "Receiving"
        else -> "Sending"
    }
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shadowElevation = 6.dp,
        modifier = modifier,
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                when {
                    sending && receiving -> MaterialSymbols.Outlined.Compare_arrows
                    receiving -> MaterialSymbols.Outlined.Download
                    else -> MaterialSymbols.Outlined.Send
                },
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Column(Modifier.weight(1f)) {
                Text(
                    "$label · ${doneCount + activeReceiveCount + sendFiles.count { it.status == TransferStatus.ACTIVE }}/${activeSendCount + activeReceiveCount + doneCount} files",
                    style = MaterialTheme.typography.labelLarge,
                )
                LinearWavyProgressIndicator(
                    progress = {
                        if (totalBytes > 0) {
                            (doneBytes.toFloat() / totalBytes).coerceIn(0f, 1f)
                        } else {
                            0f
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

// ── Receive / share via link page ──────────────────────────────────────────

@Composable
private fun LinkPage(
    controller: LocalSendController,
    settings: AppSettings,
    local: LocalDevice,
    localIp: String?,
    onDismiss: () -> Unit,
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp),
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(MaterialSymbols.Outlined.Arrow_back, contentDescription = "Back")
                    }
                    Text(
                        "Link sharing",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f),
                    )
                }
                Text(
                    "Anyone on this network with the link can open the page to download the files you share — and to send files to you (you approve each upload, unless auto-accept is on).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                Column(
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                ) {
                    WebSharePanel(controller, settings, local, localIp)
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

/** Avatar picker: a grid of built-in avatars (Material Symbols) + the device default. */
@Composable
private fun AvatarPickerDialog(
    current: String,
    onDismiss: () -> Unit,
    onPick: (String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose your avatar") },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 76.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(AvatarOptions, key = { it.first }) { (id, label) ->
                    val selected = id == current
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onPick(id) }
                            .padding(6.dp),
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (selected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerHighest
                            },
                            modifier = Modifier.size(52.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    avatarIcon(id) ?: MaterialSymbols.Outlined.Devices,
                                    contentDescription = label,
                                    tint = if (selected) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.size(26.dp),
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            label,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
