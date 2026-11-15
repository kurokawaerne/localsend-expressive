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
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.VerticalFloatingToolbar
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.toShape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
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
import dev.kurokawa.localsend.expressive.platform.stageAppBundle
import dev.kurokawa.localsend.expressive.platform.installerSupported
import dev.kurokawa.localsend.expressive.platform.fullScreenDialogProperties
import dev.kurokawa.localsend.expressive.platform.isInstallablePackage
import dev.kurokawa.localsend.expressive.platform.joinWifiNetwork
import dev.kurokawa.localsend.expressive.platform.supportsWifiNetworkList
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
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import dev.kurokawa.localsend.expressive.platform.hotspotConfigEditable
import dev.kurokawa.localsend.expressive.theme.DarkScheme
import dev.kurokawa.localsend.expressive.theme.wordmarkFamily
import dev.kurokawa.localsend.expressive.theme.wordmarkItalicFamily
import dev.kurokawa.localsend.expressive.theme.LightScheme
import dev.kurokawa.localsend.expressive.theme.MinusColorSchemeIds
import dev.kurokawa.localsend.expressive.theme.MonoDarkScheme
import dev.kurokawa.localsend.expressive.theme.MonoLightScheme
import dev.kurokawa.localsend.expressive.theme.minusColorScheme
import dev.kurokawa.localsend.expressive.theme.LocalSendExpressiveTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.coroutineScope
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.PredictiveBackHandler
import androidx.compose.ui.draw.scale
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

internal enum class MainTab { Home, Share, Settings }

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
            // ONE splash per platform (v0.10.0, Ernest's verdict on
            // the v0.9.0 double splash): Android shows ONLY its system
            // splash (animated icon, dynamic-colored since v0.10.0) —
            // the Compose overlay is desktop-only, where there is no
            // system splash. v0.8.0 behaved exactly like this and he
            // liked that splash.
            if (showSplash && settings.enableAnimations && !platformHasSystemSplash()) {
                SplashOverlay { showSplash = false }
            }
            LaunchedEffect(Unit) { onReady() }
        }
    }
}
/** The wordmark from Ernest's mockups: "LocalSend" + italic "Ex", in primary. */
@Composable
internal fun LsexWordmark(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Text(
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = scheme.primary)) {
                append("LocalSend")
            }
            withStyle(
                SpanStyle(
                    // Ernest's mockup: the wordmark is ALL Google
                    // Sans Flex CONDENSED (wdth 80) — "Ex" is the
                    // same condensed cut slanted (slnt axis), with a
                    // lowercase x. (v0.9.0 shipped a serif here; he
                    // circled it in red, and he was right.)
                    fontFamily = wordmarkItalicFamily(),
                    fontWeight = FontWeight.SemiBold,
                    color = scheme.primary,
                ),
            ) { append("Ex") }
        },
        style = MaterialTheme.typography.headlineMedium.copy(
            fontFamily = wordmarkFamily(),
        ),
        modifier = modifier,
    )
}

/**
 * The in-app splash (desktop; Android's ONE splash is the system one
 * with the same artwork and motion). v0.12.0 rework (Ernest): the old
 * flying-arrows artwork is gone — the splash is the CURRENT app logo,
 * the gradient radar mark, spinning one full turn while it bounces
 * in on springs, over the theme's surface color so the dynamic /
 * chosen theme colors carry the whole splash. The wordmark fades in
 * at the bottom. Hardened: the completion callback fires from a
 * finally block, so no animation failure can trap the user here.
 */
@Composable
internal fun SplashOverlay(onDone: () -> Unit) {
    val rotation = remember { Animatable(0f) }
    val scale = remember { Animatable(0.45f) }
    val fade = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        try {
            coroutineScope {
                launch {
                    rotation.animateTo(
                        360f,
                        spring(
                            dampingRatio = 0.55f,
                            stiffness = Spring.StiffnessMediumLow,
                        ),
                    )
                }
                launch {
                    scale.animateTo(
                        1f,
                        spring(
                            dampingRatio = 0.42f,
                            stiffness = Spring.StiffnessMediumLow,
                        ),
                    )
                }
                launch { fade.animateTo(1f, tween(350)) }
            }
            delay(300)
        } finally {
            onDone()
        }
    }
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Box(Modifier.fillMaxSize()) {
            AppLogo(
                Modifier
                    .align(Alignment.Center)
                    .size(156.dp)
                    .graphicsLayer {
                        rotationZ = rotation.value
                        scaleX = scale.value
                        scaleY = scale.value
                        alpha = fade.value
                    },
            )
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp)
                    .graphicsLayer { alpha = fade.value },
            ) {
                LsexWordmark()
            }
        }
    }
}

/**
 * The tab pages' swipe area (Ernest, 2026-11-15): a horizontal
 * slide across a FREE SPACE of the page moves to the adjacent
 * page. The detector sits on the container, so it only ever sees
 * drags no child consumed — swipe-reveal rows keep their drags,
 * horizontal scrollers keep theirs, vertical lists only consume
 * vertical movement. Past a 56dp accumulated drag the page flips
 * (left = next, right = previous), animated by the tab
 * AnimatedContent's lateral transition.
 */
@Composable
private fun TabSwipeArea(
    tab: MainTab,
    onSelect: (MainTab) -> Unit,
    content: @Composable () -> Unit,
) {
    var dragTotal by remember { mutableFloatStateOf(0f) }
    Box(
        Modifier
            .fillMaxSize()
            .pointerInput(tab) {
                detectHorizontalDragGestures(
                    onDragStart = { dragTotal = 0f },
                    onDragEnd = {
                        val threshold = 56.dp.toPx()
                        val tabs = MainTab.entries
                        when {
                            dragTotal <= -threshold &&
                                tab.ordinal < tabs.lastIndex ->
                                onSelect(tabs[tab.ordinal + 1])
                            dragTotal >= threshold && tab.ordinal > 0 ->
                                onSelect(tabs[tab.ordinal - 1])
                        }
                        dragTotal = 0f
                    },
                    onDragCancel = { dragTotal = 0f },
                ) { _, dragAmount -> dragTotal += dragAmount }
            },
    ) {
        content()
    }
}

/** MD3's emphasized easing, cubic-bezier(0.2, 0, 0, 1). */
private val LsexEmphasizedEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
internal fun AppScaffold(
    controller: LocalSendController,
    settings: AppSettings,
    initialTab: MainTab = MainTab.Home,
) {
    val strings = LocalStrings.current
    var tab by rememberSaveable { mutableStateOf(initialTab) }
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
            AnimatedContent(
                targetState = tab,
                transitionSpec = {
                    // Lateral shared-axis (Ernest, 2026-11-15):
                    // pages slide WITH the direction of travel and
                    // crossfade — the same motion whether the change
                    // comes from the navbar or from the new
                    // free-space swipe. (Supersedes the v0.12.0
                    // scale-settle for tab switches.)
                    val forward = targetState.ordinal > initialState.ordinal
                    val enterShift = if (forward) 1 else -1
                    (slideInHorizontally(
                        animationSpec = tween(340, easing = LsexEmphasizedEasing),
                    ) { enterShift * it / 3 } +
                        fadeIn(tween(240))) togetherWith
                        (slideOutHorizontally(
                            animationSpec = tween(340, easing = LsexEmphasizedEasing),
                        ) { -enterShift * it / 3 } +
                            fadeOut(tween(200)))
                },
                label = "mainTab",
            ) { currentTab ->
                when (currentTab) {
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
        }
        if (wide) {
            // Desktop (GooglebookOS direction): the improvised
            // navbar floats on the plain background (Ernest,
            // 2026-11-15: the rectangle behind the bar is
            // transparent now), the content as one large rounded
            // sheet, the transfer pill inside the sheet's
            // bottom-end corner.
            Row(Modifier.fillMaxSize()) {
                Column(
                    Modifier
                        .width(96.dp)
                        .fillMaxHeight()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AppLogo(Modifier.size(44.dp))
                    // The improvised navbar stood upright (Ernest,
                    // 2026-11-15, UDYAT's design): the official
                    // VerticalFloatingToolbar, icons only, the
                    // selected icon in a circle — centred on the rail.
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        LsexSideNavBar(
                            tab = tab,
                            onSelect = { tab = it },
                        )
                    }
                }
                Box(Modifier.weight(1f).fillMaxHeight().padding(16.dp)) {
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLowest,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        TabSwipeArea(tab = tab, onSelect = { tab = it }) {
                            content()
                        }
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
                    TabSwipeArea(tab = tab, onSelect = { tab = it }) {
                        content()
                    }
                }
                Column(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    // The transfer pill tracks the improvised
                    // navbar's measured width — the pill stays
                    // exactly as wide as the bar under it (Ernest's
                    // pill rule, carried over to the new bar).
                    var navWidthPx by remember { mutableIntStateOf(0) }
                    val pillWidth by animateDpAsState(
                        targetValue = with(LocalDensity.current) { navWidthPx.toDp() },
                        animationSpec = bouncyDpSpring(),
                        label = "pillWidth",
                    )
                    GlobalTransferPillHost(
                        controller = controller,
                        modifier = if (navWidthPx > 0) {
                            Modifier.width(pillWidth)
                        } else {
                            Modifier
                        },
                    ) { showTransfers = true }
                    // The improvised navbar (Ernest, 2026-11-15 —
                    // UDYAT's design, replacing the
                    // ShortNavigationBar): the official
                    // HorizontalFloatingToolbar, Google Photos style —
                    // the selected destination is a pill carrying its
                    // filled icon + Medium label; the others are
                    // plain Regular labels.
                    LsexFloatingNavBar(
                        tab = tab,
                        sendLabel = strings.send,
                        settingsLabel = strings.settings,
                        onSelect = { tab = it },
                        modifier = Modifier.onSizeChanged { navWidthPx = it.width },
                    )
                }
            }
        }
    }

    }
    offer?.let { IncomingOfferDialog(controller, it) }
    val webDownloadRequests by controller.webDownloadRequests.collectAsState()
    webDownloadRequests.firstOrNull()?.let { request ->
        AlertDialog(
            onDismissRequest = { controller.answerWebDownload(request.id, false) },
            icon = {
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                    Icon(
                        MaterialSymbols.Outlined.Language,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            },
            title = { Text("Browser download request") },
            text = {
                Text(
                    "${request.deviceInfo} · ${request.ip} wants to download " +
                        "the files you are sharing via link.",
                )
            },
            confirmButton = {
                Button(onClick = { controller.answerWebDownload(request.id, true) }) {
                    Text("Accept")
                }
            },
            dismissButton = {
                TextButton(onClick = { controller.answerWebDownload(request.id, false) }) {
                    Text("Decline")
                }
            },
        )
    }
    val incomingText by controller.incomingText.collectAsState()
    incomingText?.let { message ->
        val textClipboard = LocalClipboardManager.current
        AlertDialog(
            onDismissRequest = { controller.dismissIncomingText() },
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
            title = { Text("Message from ${message.peerAlias}") },
            text = {
                SelectionContainer {
                    Text(message.text)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        textClipboard.setText(AnnotatedString(message.text))
                        controller.dismissIncomingText()
                    },
                ) {
                    Icon(MaterialSymbols.Outlined.Content_copy, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Copy")
                }
            },
            dismissButton = {
                TextButton(onClick = { controller.dismissIncomingText() }) {
                    Text("Close")
                }
            },
        )
    }
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

/**
 * One destination inside the improvised navbar (UDYAT's item,
 * Ernest 2026-11-15): the selected destination is a pill segment
 * carrying its filled icon + label; the others are plain labels,
 * exactly like Google Photos.
 *
 * The pill CHOP fix (Ernest's v0.12.1 / UDYAT -7 verdict: "while
 * the selected pill transitions between sections it chops part of
 * the pill mid-animation"). Root cause: the old item swapped its
 * whole content structure on selection (pill Box vs plain Text)
 * inside an animateContentSize box CLIPPED to a pill — the
 * incoming content was laid out at its FINAL width from frame
 * zero while the box was still animating, so the clip sliced the
 * pill (and the label's tail) at the box's intermediate width;
 * the outgoing pill's background also popped off instantly while
 * its width was still animating down. The cure: ONE layout in
 * both states, no animateContentSize, no structural swap — a
 * single fraction (0 = unselected, 1 = selected) drives the
 * label's start padding (12 → 44, opening the icon slot), the
 * pill colour's alpha, the label colour and the overlaid icon's
 * alpha. The box wraps its content exactly at every frame, so
 * there is never anything to chop.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LsexNavToolbarItem(
    selected: Boolean,
    selectedIcon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val fraction by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.85f,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "navItemSelectedFraction",
    )
    val f = fraction.coerceIn(0f, 1f)
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(
                scheme.secondaryContainer.copy(alpha = f),
                RoundedCornerShape(50),
            )
            .clickable(onClick = onClick),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (f >= 0.5f) {
                FontWeight.Medium
            } else {
                FontWeight.Normal
            },
            color = lerp(scheme.onSurface, scheme.onSecondaryContainer, f),
            maxLines = 1,
            modifier = Modifier.padding(
                start = lerp(12.dp, 44.dp, f),
                end = 12.dp,
                top = 10.dp,
                bottom = 10.dp,
            ),
        )
        if (f > 0.01f) {
            Box(
                Modifier.align(Alignment.CenterStart).padding(start = 16.dp),
            ) {
                Icon(
                    selectedIcon,
                    contentDescription = null,
                    tint = scheme.onSecondaryContainer.copy(alpha = f),
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

/**
 * The improvised navbar, portrait (Ernest, 2026-11-15 — UDYAT's
 * design): the OFFICIAL MD3 Expressive HorizontalFloatingToolbar,
 * Google Photos style. The 4dp end spacers keep the selected pill's
 * breathing room at the bar's ends equal to its y-axis gaps.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LsexFloatingNavBar(
    tab: MainTab,
    sendLabel: String,
    settingsLabel: String,
    onSelect: (MainTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    HorizontalFloatingToolbar(
        expanded = true,
        colors = FloatingToolbarDefaults.standardFloatingToolbarColors(),
        modifier = modifier,
    ) {
        Spacer(Modifier.width(4.dp))
        LsexNavToolbarItem(
            selected = tab == MainTab.Home,
            selectedIcon = MaterialSymbols.OutlinedFilled.Home,
            label = "Home",
            onClick = { onSelect(MainTab.Home) },
        )
        LsexNavToolbarItem(
            selected = tab == MainTab.Share,
            selectedIcon = MaterialSymbols.OutlinedFilled.Compare_arrows,
            label = sendLabel,
            onClick = { onSelect(MainTab.Share) },
        )
        LsexNavToolbarItem(
            selected = tab == MainTab.Settings,
            selectedIcon = MaterialSymbols.OutlinedFilled.Settings,
            label = settingsLabel,
            onClick = { onSelect(MainTab.Settings) },
        )
        Spacer(Modifier.width(4.dp))
    }
}

/**
 * The improvised navbar, landscape / wide windows (Ernest,
 * 2026-11-15 — UDYAT's design): the same floating toolbar stood
 * upright (VerticalFloatingToolbar), but ICONS ONLY — no labels, so
 * the rail stays slim — and the selected icon sits in a CIRCLE of
 * secondaryContainer instead of a pill segment.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LsexSideNavBar(
    tab: MainTab,
    onSelect: (MainTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    VerticalFloatingToolbar(
        expanded = true,
        colors = FloatingToolbarDefaults.standardFloatingToolbarColors(),
        modifier = modifier,
    ) {
        val items = listOf(
            Triple(
                MainTab.Home,
                MaterialSymbols.Outlined.Home,
                MaterialSymbols.OutlinedFilled.Home,
            ),
            Triple(
                MainTab.Share,
                MaterialSymbols.Outlined.Compare_arrows,
                MaterialSymbols.OutlinedFilled.Compare_arrows,
            ),
            Triple(
                MainTab.Settings,
                MaterialSymbols.Outlined.Settings,
                MaterialSymbols.OutlinedFilled.Settings,
            ),
        )
        for ((target, icon, selectedIcon) in items) {
            val selected = tab == target
            val circle by animateColorAsState(
                targetValue = if (selected) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    Color.Transparent
                },
                label = "sideNavCircle",
            )
            Box(
                Modifier.size(48.dp)
                    .background(circle, CircleShape)
                    .clip(CircleShape)
                    .clickable { onSelect(target) },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (selected) selectedIcon else icon,
                    contentDescription = null,
                    tint = if (selected) {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

internal sealed interface StagedItem {
    data class FileItem(val file: PickedFile) : StagedItem
    data class FolderItem(val folder: BrowserItem) : StagedItem
    data class TextItem(val text: String) : StagedItem
}

internal enum class ContentTab { Apps, Photos, Videos, Audio, Files, Text, Web }

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
    // With many options (the 5 browse sections), unchecked buttons
    // show only their icon — icon+label in every segment overflows a
    // phone's width and the labels clip (v0.10.0 render catch).
    compactLabels: Boolean = false,
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
                modifier = Modifier.weight(
                    if (compactLabels && index != selectedIndex) 1f
                    else if (compactLabels) 2.2f
                    else 1f,
                ),
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                if (!compactLabels || index == selectedIndex) {
                    Spacer(Modifier.width(6.dp))
                    Text(label, maxLines = 1, softWrap = false)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun HomeScreen(
    controller: LocalSendController,
    settings: AppSettings,
    local: LocalDevice,
    connectivity: ConnectivityController,
    onReceiveViaLink: () -> Unit,
    onGoToShare: () -> Unit,
) {
    val connState by connectivity.state.collectAsState()
    val localIp by controller.localIpAddress.collectAsState()
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
        if (mode == 0 && supportsWifiNetworkList()) connectivity.scanWifiNetworks()
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
                    // Ernest (2026-11-13): the chips read (IP) (Port)
                    // (Protocol) — the current IP replaces the HTTPS
                    // chip in this view.
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoChip(localIp ?: "No IP")
                        InfoChip("Port ${local.port}")
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
                    Modifier.animateContentSize(bouncySizeSpring()).padding(16.dp),
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
                            if (supportsWifiNetworkList()) {
                            Text("Networks", style = MaterialTheme.typography.titleSmall)
    // Ernest's point 21 (v0.10.0): with Location off, Android
    // returns an empty scan list — say so, with the way out.
    if (connState.locationOff) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                MaterialSymbols.Outlined.Location_off,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Location is off — Android hides nearby networks and can block Wi-Fi Direct while it's off.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = { connectivity.openLocationSettings() }) {
                Text("Turn on")
            }
        }
    }

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
                            } else {
                                // Android (Ernest, v0.12.0): no network
                                // listing — the whole status area is one
                                // full-width Wi-Fi button; the QR scanner
                                // below stays.
                                Button(
                                    onClick = { connectivity.toggleWifi() },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
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
                            if (supportsWifiNetworkList() && connState.wifiNetworks.isEmpty()) {
                                // Never leave this section silently empty
                                // (v0.10.0 verdict): say WHY there is no
                                // list and offer the one tap that fixes it.
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        when {
                                            connState.wifiPermissionMissing ->
                                                "Listing networks needs the nearby-devices permission."
                                            connState.scanningNetworks ->
                                                "Scanning for networks\u2026"
                                            else ->
                                                "No networks listed yet."
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f),
                                    )
                                    if (connState.wifiPermissionMissing) {
                                        Button(onClick = { connectivity.scanWifiNetworks() }) {
                                            Text("Grant")
                                        }
                                    } else {
                                        IconButton(onClick = { connectivity.scanWifiNetworks() }) {
                                            Icon(
                                                MaterialSymbols.Outlined.Refresh,
                                                contentDescription = "Scan for networks",
                                            )
                                        }
                                    }
                                }
                            }
                            if (supportsWifiNetworkList() && connState.wifiNetworks.isNotEmpty()) {
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
    // Ernest's point 21 (v0.10.0): with Location off, Android
    // returns an empty scan list — say so, with the way out.
    if (connState.locationOff) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                MaterialSymbols.Outlined.Location_off,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Location is off — Android hides nearby networks and can block Wi-Fi Direct while it's off.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = { connectivity.openLocationSettings() }) {
                Text("Turn on")
            }
        }
    }
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
                        LinearProgressIndicator(Modifier.fillMaxWidth())
                    }
                }
            }
        }

        // ── History ──
        // The list pattern UDYAT nailed and Ernest pointed at
        // (v0.10.0): ONE segmented Minus-style group directly on the
        // page background — header is the group's first tile, the
        // expand chevron rides in the vertical pill, rows are the
        // remaining tiles with 3dp seams. (Earlier sin: the whole
        // group nested inside an extra card, which flattened it.)
        item {
            Column(
                modifier = Modifier.animateContentSize(bouncySizeSpring()),
                verticalArrangement =
                    Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
            ) {
                LsexSegmentedItem(
                    index = 0,
                    count = if (historyExpanded && history.isNotEmpty()) {
                        minOf(history.size, 30) + 1
                    } else {
                        1
                    },
                    onClick = { historyExpanded = !historyExpanded },
                    leading = {
                        Icon(
                            MaterialSymbols.Outlined.History,
                            contentDescription = null,
                        )
                    },
                    trailing = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (history.isNotEmpty()) {
                                TextButton(
                                    onClick = { controller.clearHistory() },
                                ) {
                                    Text("Clear")
                                }
                            }
                            PillChevron(
                                icon = if (historyExpanded) {
                                    MaterialSymbols.Outlined.Expand_less
                                } else {
                                    MaterialSymbols.Outlined.Expand_more
                                },
                            )
                        }
                    },
                ) {
                    Text("History")
                }
                if (historyExpanded) {
                    if (history.isEmpty()) {
                        Text(
                            "Nothing yet — finished transfers land here.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    } else {
                        val clipboard = LocalClipboardManager.current
                        val shown = history.take(30)
                        Column(
                            verticalArrangement =
                                Arrangement.spacedBy(
                                    ListItemDefaults.SegmentedGap,
                                ),
                        ) {
                            shown.forEachIndexed { index, entry ->
                                HistoryRow(
                                    entry = entry,
                                    index = index + 1,
                                    count = shown.size + 1,
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
                                    onCopy = (
                                        entry.preview
                                            ?: if (entry.uri.isEmpty() && entry.handle.isEmpty()) {
                                                // Pre-v0.12.0 text entries kept the
                                                // (truncated) text in fileName.
                                                entry.fileName
                                            } else {
                                                null
                                            }
                                        )?.let { text ->
                                        { clipboard.setText(AnnotatedString(text)) }
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
                if (isHotspot && !hotspotConfigEditable()) {
                    // Android reserves hotspot credentials to system
                    // apps: the system assigns them, and the running
                    // hotspot card shows what took effect.
                    Text(
                        "Android assigns the hotspot's name and password " +
                            "itself — regular apps can't choose them. " +
                            "Start the hotspot and the connection card " +
                            "shows the name and password that actually " +
                            "took effect, with a QR code to join.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
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
                ConnectedChoiceGroup(
                    options = listOf(
                        MaterialSymbols.Outlined.Wifi to "Auto",
                        MaterialSymbols.Outlined.Wifi_1_bar to "2.4 GHz",
                        MaterialSymbols.Outlined.Signal_wifi_4_bar to "5 GHz",
                    ),
                    selectedIndex = when (band) {
                        "2.4" -> 1
                        "5" -> 2
                        else -> 0
                    },
                    onSelect = { index ->
                        band = listOf("auto", "2.4", "5")[index]
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    if (isHotspot) {
                        "Applied when the hotspot starts."
                    } else {
                        "Wi-Fi Direct uses your name, password and band when the group is created (Android 10+)."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                }
            }
        },
        confirmButton = {
            if (isHotspot && !hotspotConfigEditable()) {
                TextButton(onClick = onDismiss) { Text("OK") }
            } else {
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
            }
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
    // Split apps (v0.10.0): tapping one first zips base + splits into
    // a .apks (IO), then stages THAT — sending the base alone shipped
    // a broken app. Prepared bundles are remembered per package so
    // un-staging uses the same handle.
    val scope = rememberCoroutineScope()
    var bundles by remember { mutableStateOf<Map<String, PickedFile>>(emptyMap()) }
    var preparing by remember { mutableStateOf<String?>(null) }
    fun toggleApp(app: InstalledApp) {
        if (app.splitHandles.isEmpty()) {
            onToggleApp(app)
            return
        }
        val prepared = bundles[app.packageName]
        if (prepared != null) {
            onToggleApp(
                app.copy(apkHandle = prepared.handle, apkSize = prepared.size),
            )
            return
        }
        if (preparing != null) return
        preparing = app.packageName
        scope.launch {
            val bundle = withContext(Dispatchers.IO) { stageAppBundle(app) }
            preparing = null
            if (bundle != null) {
                bundles = bundles + (app.packageName to bundle)
                onToggleApp(
                    app.copy(apkHandle = bundle.handle, apkSize = bundle.size),
                )
            }
        }
    }
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
            LazyColumn(
                verticalArrangement =
                    Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
            ) {
                itemsIndexed(
                    visible,
                    key = { _, app -> app.packageName },
                ) { index, app ->
                    val effectiveHandle =
                        bundles[app.packageName]?.handle ?: app.apkHandle
                    val checked = "file:$effectiveHandle" in stagedIds ||
                        "file:${app.apkHandle}" in stagedIds
                    LsexSegmentedItem(
                        index = index,
                        count = visible.size,
                        selected = checked,
                        onClick = { toggleApp(app) },
                        leading = {
                            AppIcon(app.packageName, Modifier.size(42.dp))
                        },
                        supporting = { Text(formatSize(app.apkSize)) },
                        trailing = {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { toggleApp(app) },
                            )
                        },
                    ) {
                        Text(app.label)
                    }
                }
            }
        }
    }
}

private enum class BrowseSort { Name, Size, Date, Type }

/** File-browser filters (Ernest, v0.11.0: "we need filter options"). */
private enum class FileFilter { All, Folders, Images, Videos, Audio, Documents, Apks, Archives }

private fun FileFilter.matches(item: BrowserItem): Boolean {
    if (this == FileFilter.All) return true
    if (this == FileFilter.Folders) return item.isFolder
    if (item.isFolder) return false
    val ext = item.name.substringAfterLast('.', "").lowercase()
    val mime = item.mimeType.lowercase()
    return when (this) {
        FileFilter.Images -> mime.startsWith("image/") ||
            ext in setOf("jpg", "jpeg", "png", "gif", "webp", "bmp", "svg", "heic")
        FileFilter.Videos -> mime.startsWith("video/") ||
            ext in setOf("mp4", "mkv", "webm", "avi", "mov", "3gp")
        FileFilter.Audio -> mime.startsWith("audio/") ||
            ext in setOf("mp3", "flac", "ogg", "opus", "wav", "m4a", "aac")
        FileFilter.Documents -> mime.startsWith("text/") ||
            mime == "application/pdf" ||
            ext in setOf("pdf", "doc", "docx", "txt", "md", "odt", "xls", "xlsx", "ppt", "pptx", "csv", "json", "xml")
        FileFilter.Apks -> ext in setOf("apk", "apks", "xapk", "apkm", "aab")
        FileFilter.Archives -> ext in setOf("zip", "tar", "gz", "xz", "7z", "rar", "zst", "bz2")
        else -> true
    }
}

/** Music filters: by format family (the metadata the browser has). */
private enum class AudioFilter { All, Mp3, Flac, Other }

private fun AudioFilter.matches(item: BrowserItem): Boolean {
    if (this == AudioFilter.All) return true
    val ext = item.name.substringAfterLast('.', "").lowercase()
    val mime = item.mimeType.lowercase()
    return when (this) {
        AudioFilter.Mp3 -> ext == "mp3" || mime == "audio/mpeg"
        AudioFilter.Flac -> ext == "flac" || mime == "audio/flac"
        AudioFilter.Other -> ext !in setOf("mp3", "flac") &&
            mime !in setOf("audio/mpeg", "audio/flac")
        else -> true
    }
}

/** Filter control mirroring [SortMenuButton], shared by Files + Music. */
@Composable
private fun FilterMenuButton(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { open = true }) {
            Icon(MaterialSymbols.Outlined.Filter_alt, contentDescription = "Filter")
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            options.forEachIndexed { index, label ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = { onSelect(index); open = false },
                    trailingIcon = {
                        if (selectedIndex == index) {
                            Icon(MaterialSymbols.Outlined.Check, contentDescription = null)
                        }
                    },
                )
            }
        }
    }
}

/** Sort control shared by the Files and Music browsers (v0.10.0). */
@Composable
private fun SortMenuButton(
    sort: BrowseSort,
    onSort: (BrowseSort) -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { open = true }) {
            Icon(MaterialSymbols.Outlined.Sort, contentDescription = "Sort")
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            BrowseSort.entries.forEach { mode ->
                DropdownMenuItem(
                    text = { Text(mode.name) },
                    onClick = { onSort(mode); open = false },
                    trailingIcon = {
                        if (sort == mode) {
                            Icon(MaterialSymbols.Outlined.Check, contentDescription = null)
                        }
                    },
                )
            }
        }
    }
}

private fun List<BrowserItem>.sortedForBrowse(sort: BrowseSort): List<BrowserItem> =
    when (sort) {
        BrowseSort.Name -> sortedBy { it.name.lowercase() }
        BrowseSort.Size -> sortedByDescending { it.size }
        BrowseSort.Date -> sortedByDescending { it.dateMillis }
        BrowseSort.Type -> sortedWith(
            compareBy<BrowserItem> {
                it.name.substringAfterLast('.', "").lowercase()
            }.thenBy { it.name.lowercase() },
        )
    }

/**
 * Music browser (v0.10.0): Ernest asked for search + filter + sort in
 * the explorer and the music view. Rows are the rounded
 * surfaceContainer tiles used across the redesigned browse.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun AudioList(
    items: List<BrowserItem>,
    stagedIds: Set<String>,
    onToggleItem: (BrowserItem) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var sort by remember { mutableStateOf(BrowseSort.Name) }
    var filter by remember { mutableStateOf(AudioFilter.All) }
    val visible = items
        .filter { query.isBlank() || it.name.contains(query, ignoreCase = true) }
        .filter { filter.matches(it) }
        .sortedForBrowse(sort)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = {},
                    placeholder = { Text("Search music") },
                    leadingIcon = {
                        Icon(MaterialSymbols.Outlined.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(
                                    MaterialSymbols.Outlined.Close,
                                    contentDescription = "Clear",
                                )
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
                "Music (${visible.size})",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f),
            )
            FilterMenuButton(
                options = AudioFilter.entries.map { it.name },
                selectedIndex = AudioFilter.entries.indexOf(filter),
            ) { filter = AudioFilter.entries[it] }
            SortMenuButton(sort) { sort = it }
        }
        LazyColumn(
            verticalArrangement =
                Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
        ) {
            itemsIndexed(visible, key = { _, item -> item.id }) { index, item ->
                val checked = "file:${item.handle}" in stagedIds
                LsexSegmentedItem(
                    index = index,
                    count = visible.size,
                    selected = checked,
                    onClick = { onToggleItem(item) },
                    leading = {
                        BrowserThumbnail(
                            item,
                            Modifier.size(42.dp)
                                .clip(RoundedCornerShape(12.dp)),
                        )
                    },
                    supporting = { Text(formatSize(item.size)) },
                    trailing = {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { onToggleItem(item) },
                        )
                    },
                ) {
                    Text(
                        item.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
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
    // Ernest (v0.10.0): Photos/Videos get a long-press preview
    // (desktop shows an expand icon on every tile instead).
    // v0.11.0: the preview is a true FULLSCREEN viewer, and the
    // Photos/Videos tabs can switch between grid and list.
    var preview by remember { mutableStateOf<BrowserItem?>(null) }
    var listView by remember(kind) { mutableStateOf(false) }
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
        kind == MediaKind.AUDIO -> AudioList(
            items = loaded,
            stagedIds = stagedIds,
            onToggleItem = onToggleItem,
        )
        else -> Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${loaded.size} items",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = { listView = !listView }) {
                    Icon(
                        if (listView) {
                            MaterialSymbols.Outlined.Grid_view
                        } else {
                            MaterialSymbols.Outlined.View_list
                        },
                        contentDescription = if (listView) "Grid view" else "List view",
                    )
                }
            }
            if (listView) {
                LazyColumn(
                    verticalArrangement =
                        Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
                ) {
                    itemsIndexed(loaded, key = { _, item -> item.id }) { index, item ->
                        val checked = "file:${item.handle}" in stagedIds
                        LsexSegmentedItem(
                            index = index,
                            count = loaded.size,
                            selected = checked,
                            onClick = { onToggleItem(item) },
                            leading = {
                                BrowserThumbnail(
                                    item,
                                    Modifier.size(42.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                )
                            },
                            supporting = {
                                Text(
                                    listOf(
                                        formatSize(item.size),
                                        formatDate(item.dateMillis),
                                    ).filter { it.isNotEmpty() }.joinToString(" · "),
                                )
                            },
                            trailing = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { preview = item }) {
                                        Icon(
                                            MaterialSymbols.Outlined.Open_in_full,
                                            contentDescription = "Preview",
                                        )
                                    }
                                    Checkbox(
                                        checked = checked,
                                        onCheckedChange = { onToggleItem(item) },
                                    )
                                }
                            },
                        ) {
                            Text(
                                item.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(loaded, key = { it.id }) { item ->
                        val checked = "file:${item.handle}" in stagedIds
                        Box(
                            Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .combinedClickable(
                                    onClick = { onToggleItem(item) },
                                    onLongClick = { preview = item },
                                ),
                        ) {
                            BrowserThumbnail(item, Modifier.fillMaxSize())
                            if (!platformHasSystemSplash()) {
                                // Desktop has no long-press: an expand icon in
                                // the corner opens the same preview.
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(6.dp)
                                        .size(26.dp),
                                ) {
                                    IconButton(onClick = { preview = item }) {
                                        Icon(
                                            MaterialSymbols.Outlined.Open_in_full,
                                            contentDescription = "Preview",
                                            modifier = Modifier.size(15.dp),
                                        )
                                    }
                                }
                            }
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
    }
    preview?.let { item ->
        FullscreenMediaPreview(
            item = item,
            checked = "file:${item.handle}" in stagedIds,
            onToggle = { onToggleItem(item) },
            onDismiss = { preview = null },
        )
    }
}

/**
 * Fullscreen media preview (Ernest, v0.11.0: "an actual fullscreen
 * preview, not what you implemented"). The media fills the screen on
 * black; videos show their poster frame with a play button that hands
 * off to the system player. Back is PREDICTIVE: the view shrinks with
 * the gesture and springs back if cancelled.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun FullscreenMediaPreview(
    item: BrowserItem,
    checked: Boolean,
    onToggle: () -> Unit,
    onDismiss: () -> Unit,
) {
    var scale by remember { mutableFloatStateOf(1f) }
    // Animated opening/closing (Ernest, 2026-11-15): the preview
    // fades/scales in, and dismissals play the exit before the
    // dialog actually closes — the predictive-back scale below
    // still rides on top during a back gesture.
    val transition = remember {
        MutableTransitionState(false).apply { targetState = true }
    }
    val dismiss: () -> Unit = { transition.targetState = false }
    LaunchedEffect(transition.currentState, transition.targetState) {
        if (!transition.currentState && !transition.targetState) {
            onDismiss()
        }
    }
    PredictiveBackHandler { progress ->
        try {
            progress.collect { event ->
                scale = 1f - event.progress * 0.12f
            }
            dismiss()
        } catch (e: kotlinx.coroutines.CancellationException) {
            scale = 1f
            throw e
        }
    }
    val isVideo = item.mimeType.startsWith("video/") ||
        item.name.substringAfterLast('.', "").lowercase() in
            setOf("mp4", "mkv", "webm", "avi", "mov", "3gp")
    androidx.compose.ui.window.Dialog(
        onDismissRequest = dismiss,
        properties = fullScreenDialogProperties(),
    ) {
        AnimatedVisibility(
            visibleState = transition,
            enter = fadeIn(tween(200)) +
                scaleIn(
                    animationSpec = tween(260, easing = LsexEmphasizedEasing),
                    initialScale = 0.96f,
                ),
            exit = fadeOut(tween(160)) +
                scaleOut(
                    animationSpec = tween(200, easing = LsexEmphasizedEasing),
                    targetScale = 0.96f,
                ),
        ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .scale(scale),
        ) {
            BrowserThumbnail(item, Modifier.fillMaxSize())
            if (isVideo) {
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.55f),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(72.dp),
                ) {
                    IconButton(
                        onClick = { openReceivedFile(item.handle, item.name) },
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Icon(
                            MaterialSymbols.Outlined.Play_arrow,
                            contentDescription = "Play in the system player",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp),
                        )
                    }
                }
            }
            // Top bar: close + name, over a scrim.
            Row(
                Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = dismiss) {
                    Icon(
                        MaterialSymbols.Outlined.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                    )
                }
                Text(
                    item.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            // Bottom bar: metadata + actions.
            Row(
                Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    listOf(formatSize(item.size), formatDate(item.dateMillis))
                        .filter { it.isNotEmpty() }
                        .joinToString(" · "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = { openReceivedFile(item.handle, item.name) }) {
                    Text("Open", color = Color.White)
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { onToggle(); dismiss() }) {
                    Text(if (checked) "Remove" else "Select")
                }
            }
        }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
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
    // Ernest (v0.10.0): the explorer gets search + sort, like Music.
    var query by remember { mutableStateOf("") }
    var sort by remember { mutableStateOf(BrowseSort.Name) }
    var fileFilter by remember { mutableStateOf(FileFilter.All) }
    LaunchedEffect(currentPath) {
        query = ""
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = query,
                        onQueryChange = { query = it },
                        onSearch = {},
                        expanded = false,
                        onExpandedChange = {},
                        placeholder = { Text("Search this folder") },
                        leadingIcon = {
                            Icon(
                                MaterialSymbols.Outlined.Search,
                                contentDescription = null,
                            )
                        },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { query = "" }) {
                                    Icon(
                                        MaterialSymbols.Outlined.Close,
                                        contentDescription = "Clear",
                                    )
                                }
                            }
                        },
                    )
                },
                expanded = false,
                onExpandedChange = {},
                modifier = Modifier.weight(1f),
            ) {}
            FilterMenuButton(
                options = FileFilter.entries.map { it.name },
                selectedIndex = FileFilter.entries.indexOf(fileFilter),
            ) { fileFilter = FileFilter.entries[it] }
            SortMenuButton(sort) { sort = it }
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
            else -> {
                val visible = loaded
                    .filter {
                        (query.isBlank() ||
                            it.name.contains(query, ignoreCase = true)) &&
                            fileFilter.matches(it)
                    }
                    .let { filtered ->
                        filtered.filter { it.isFolder }.sortedForBrowse(sort) +
                            filtered.filter { !it.isFolder }.sortedForBrowse(sort)
                    }
                LazyColumn(
                    verticalArrangement =
                        Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
                ) {
                    itemsIndexed(
                        visible,
                        key = { _, item -> item.id },
                    ) { index, item ->
                        val fileChecked = "file:${item.handle}" in stagedIds
                        val folderChecked = item.id in stagedIds
                        val supporting = if (item.isFolder) {
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
                        }
                        LsexListTile(
                            headline = item.name,
                            supporting = supporting,
                            index = index,
                            count = visible.size,
                            leading = {
                                if (item.isFolder) {
                                    Icon(
                                        MaterialSymbols.Outlined.Folder,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(40.dp).padding(6.dp),
                                    )
                                } else if (
                                    item.name.endsWith(".apk", ignoreCase = true)
                                ) {
                                    // Ernest (point 12): APK files carry
                                    // the apk_document glyph.
                                    Icon(
                                        MaterialSymbols.Outlined.Apk_document,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(40.dp).padding(6.dp),
                                    )
                                } else {
                                    BrowserThumbnail(
                                        item,
                                        Modifier.size(40.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                    )
                                }
                            },
                            trailing = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    if (currentPath != null || !item.isFolder) {
                                        Checkbox(
                                            checked = if (item.isFolder) {
                                                folderChecked
                                            } else {
                                                fileChecked
                                            },
                                            onCheckedChange = {
                                                if (item.isFolder) {
                                                    onToggleFolder(item)
                                                } else {
                                                    onToggleFile(item)
                                                }
                                            },
                                        )
                                    }
                                    if (item.isFolder) {
                                        Icon(
                                            MaterialSymbols.Outlined.Chevron_right,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            },
                            onClick = {
                                if (item.isFolder && currentPath == null) {
                                    pathStack = listOf(item.handle)
                                } else if (item.isFolder) {
                                    pathStack = pathStack + item.handle
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

@OptIn(
    androidx.compose.foundation.ExperimentalFoundationApi::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
private fun StagedItemCard(
    item: StagedItem,
    index: Int,
    count: Int,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDiscard: () -> Unit,
) {
    if (!prefersSwipeActions()) {
        // Desktop (pointer): actions stay visible.
        StagedItemRow(
            item = item,
            index = index,
            count = count,
            onEdit = onEdit,
            onDiscard = onDiscard,
            showActions = true,
        )
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
    val actionCount = if (item is StagedItem.FolderItem) 1 else 3
    // Same measured geometry as HistoryRow (UDYAT's list fix).
    var itemHeightPx by remember { mutableStateOf(0) }
    var revealWidthPx by remember { mutableStateOf(0f) }
    val revealPx = with(density) { (actionCount * 50).dp.toPx() }
    val state = remember {
        AnchoredDraggableState(
            initialValue = 0,
            anchors = DraggableAnchors {
                0 at 0f
                1 at -revealPx
            },
        )
    }
    LaunchedEffect(revealWidthPx) {
        if (revealWidthPx > 0f) {
            state.updateAnchors(DraggableAnchors {
                0 at 0f
                1 at -(revealWidthPx + with(density) { 2.dp.toPx() })
            })
        }
    }
    val scope = rememberCoroutineScope()
    Box(
        Modifier
            .fillMaxWidth()
            .clip(lsexSegmentedShapes(index, count).shape),
    ) {
        Box(Modifier.matchParentSize(), contentAlignment = Alignment.CenterEnd) {
            Row(
                Modifier
                    .then(
                        if (itemHeightPx > 0) {
                            Modifier.height(with(density) { itemHeightPx.toDp() })
                        } else {
                            Modifier
                        },
                    )
                    .onSizeChanged { revealWidthPx = it.width.toFloat() },
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
            if (item !is StagedItem.FolderItem) {
                // Open — the door glyph from the mockup (v0.11.0: this
                // is its own action again, distinct from Edit).
                SwipeActionButton(
                    container = MaterialTheme.colorScheme.secondaryContainer,
                    content = MaterialTheme.colorScheme.onSecondaryContainer,
                    icon = MaterialSymbols.Outlined.Logout,
                    description = "Open",
                ) {
                    scope.launch { state.snapTo(0) }
                    onOpen()
                }
                SwipeActionButton(
                    container = MaterialTheme.colorScheme.secondaryContainer,
                    content = MaterialTheme.colorScheme.onSecondaryContainer,
                    icon = MaterialSymbols.Outlined.Edit,
                    description = "Rename / edit",
                ) {
                    scope.launch { state.snapTo(0) }
                    onEdit()
                }
            }
            SwipeActionButton(
                container = MaterialTheme.colorScheme.error,
                content = MaterialTheme.colorScheme.onError,
                icon = MaterialSymbols.Outlined.Close,
                description = "Discard",
            ) { onDiscard() }
            }
        }
        StagedItemRow(
            item = item,
            index = index,
            count = count,
            onEdit = onEdit,
            onDiscard = onDiscard,
            showActions = false,
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { itemHeightPx = it.height }
                .offset {
                    val off = state.offset
                    IntOffset(if (off.isNaN()) 0 else off.roundToInt(), 0)
                }
                .anchoredDraggable(state, Orientation.Horizontal),
            onClick = {
                if (state.currentValue == 1) {
                    scope.launch { state.snapTo(0) }
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun StagedItemRow(
    item: StagedItem,
    index: Int,
    count: Int,
    onEdit: () -> Unit,
    onDiscard: () -> Unit,
    showActions: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    LsexSegmentedItem(
        index = index,
        count = count,
        modifier = modifier,
        onClick = onClick,
        leading = {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(40.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        when (item) {
                            is StagedItem.FileItem ->
                                if (item.file.name.endsWith(".apk", ignoreCase = true)) {
                                    MaterialSymbols.Outlined.Apk_document
                                } else {
                                    MaterialSymbols.Outlined.Attach_file
                                }
                            is StagedItem.FolderItem -> MaterialSymbols.Outlined.Folder
                            is StagedItem.TextItem -> MaterialSymbols.Outlined.Chat
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        },
        supporting = {
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
            )
        },
        trailing = if (showActions) {
            {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (item !is StagedItem.FolderItem) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        ) {
                            IconButton(
                                onClick = onEdit,
                                modifier = Modifier.size(38.dp),
                            ) {
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
                        IconButton(
                            onClick = onDiscard,
                            modifier = Modifier.size(38.dp),
                        ) {
                            Icon(
                                MaterialSymbols.Outlined.Close,
                                contentDescription = "Discard",
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }
        } else {
            null
        },
    ) {
        Text(
            when (item) {
                is StagedItem.FileItem -> item.file.displayPath ?: item.file.name
                is StagedItem.FolderItem -> item.folder.name
                is StagedItem.TextItem -> item.text
            },
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
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
    // Ernest's mockup, measured from his Share design (2026-11-13):
    // NO progress fill inside the card. The solid bar floats a few
    // px BEHIND/BELOW the card — not stuck to it (a real gap) — and
    // its ends line up with the END OF THE CARD'S BOTTOM CURVE on
    // each side: inset = the card's corner radius. Selected cards
    // are near-pills (wide inset), unselected cards use their
    // normal-state radius (narrower inset) — his addition.
    Column(Modifier.fillMaxWidth()) {
        Card(
            onClick = onClick,
            colors = CardDefaults.cardColors(containerColor = container, contentColor = content),
            shape = RoundedCornerShape(shape),
            modifier = Modifier.fillMaxWidth(),
        ) {
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
            }
        }
        if (transferFraction != null) {
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { transferFraction.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = shape.coerceAtMost(36.dp)),
                color = scheme.primary,
                trackColor = scheme.surfaceContainerHighest,
            )
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

/**
 * The vertical-pill chevron (v0.10.0, Ernest's point 9 — demanded in
 * both projects). Where it belongs was settled by UDYAT's v1.3.3
 * list, which Ernest approved: the pill is the trailing of an
 * EXPANDABLE SECTION HEADER (expand_less / expand_more glyph), on a
 * segmented surfaceContainer tile group. Data rows carry a plain
 * chevron or their action icon — no pill. Researched honestly:
 * material3's only list composable is ListItem and it has no
 * pill-trailing variant — this component lives in the Material 3
 * Figma design kit, not the code library — so it is built here to
 * the kit's geometry: 24x40dp pill, fully rounded,
 * surfaceContainerHighest, 20dp glyph in onSurfaceVariant.
 */
@Composable
internal fun PillChevron(
    icon: androidx.compose.ui.graphics.vector.ImageVector =
        MaterialSymbols.Outlined.Chevron_right,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = scheme.secondaryContainer,
        modifier = modifier.size(width = 30.dp, height = 42.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                icon,
                contentDescription = null,
                tint = scheme.onSecondaryContainer,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/**
 * The segmented-list colours for every app list (material3 1.11's
 * segmentedColors() defaults the container to a near-transparent
 * tone, so it is pinned to the M3 segmented-list tokens:
 * surfaceContainer, secondaryContainer when selected).
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun lsexSegmentedColors(): ListItemColors =
    ListItemDefaults.segmentedColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
    )

/**
 * THE list row of this app: the official Material 3 Expressive
 * [SegmentedListItem] — the component behind the Android Settings
 * expressive lists and the Figma kit's segmented list (it was
 * internal-only in material3 1.10.0-alpha05, which is why earlier
 * passes missed it; it is public since material3 1.11.0-alpha07).
 * Position in the group comes from [index]/[count] via
 * [ListItemDefaults.segmentedShapes]; groups space their rows by
 * [ListItemDefaults.SegmentedGap]. The vertical pill belongs in the
 * trailing slot of expandable section headers (PillChevron).
 */

/**
 * The shapes [LsexSegmentedItem] actually draws with. The library's
 * segmentedShapes() leaves a single-item group (count == 1) at the
 * default item shape — CornerExtraSmall on every corner — so a
 * collapsed section header rendered almost square (Ernest's v0.10.0
 * verdict). A lone item takes the large corner on all sides, the
 * same CornerLarge the component itself applies to group ends.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun lsexSegmentedShapes(index: Int, count: Int): ListItemShapes =
    if (count == 1) {
        ListItemDefaults.shapes(
            shape = MaterialTheme.shapes.large,
            selectedShape = MaterialTheme.shapes.large,
            pressedShape = MaterialTheme.shapes.large,
            hoveredShape = MaterialTheme.shapes.large,
            focusedShape = MaterialTheme.shapes.large,
            draggedShape = MaterialTheme.shapes.large,
        )
    } else {
        ListItemDefaults.segmentedShapes(index, count)
    }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun LsexSegmentedItem(
    index: Int,
    count: Int,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    supporting: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    SegmentedListItem(
        selected = selected,
        onClick = onClick ?: {},
        modifier = modifier.fillMaxWidth(),
        shapes = lsexSegmentedShapes(index, count),
        colors = lsexSegmentedColors(),
        leadingContent = leading,
        supportingContent = supporting,
        trailingContent = trailing,
        content = content,
    )
}

/**
 * Text-slot convenience over [LsexSegmentedItem] for the browse
 * lists: headline + optional supporting text.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun LsexListTile(
    headline: String,
    modifier: Modifier = Modifier,
    supporting: String? = null,
    index: Int = 0,
    count: Int = 1,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    LsexSegmentedItem(
        index = index,
        count = count,
        modifier = modifier,
        onClick = onClick,
        leading = leading,
        supporting = supporting?.let { text -> { Text(text) } },
        trailing = trailing,
    ) {
        Text(headline, maxLines = 2, overflow = TextOverflow.Ellipsis)
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
/** Copy-to-clipboard button with a brief check-mark confirmation. */
@Composable
private fun CopyButton(text: String) {
    val scheme = MaterialTheme.colorScheme
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    LaunchedEffect(copied) {
        if (copied) {
            delay(1200)
            copied = false
        }
    }
    IconButton(
        onClick = {
            clipboard.setText(AnnotatedString(text))
            copied = true
        },
    ) {
        Icon(
            if (copied) {
                MaterialSymbols.Outlined.Check
            } else {
                MaterialSymbols.Outlined.Content_copy
            },
            contentDescription = "Copy address",
            tint = if (copied) scheme.primary else scheme.onSurfaceVariant,
        )
    }
}

/**
 * A full-screen page hosted in a Dialog that OPENS and CLOSES
 * with motion (Ernest, 2026-11-15: switches and openings must
 * animate, not cut): the content rises and fades in, and on any
 * dismiss request the exit plays BEFORE the page actually
 * closes — [content] receives the animated dismiss as its
 * requestDismiss. The window is edge-to-edge via
 * [fullScreenDialogProperties] — the Donate black-bars cure,
 * applied here to every full-screen page (the link page and the
 * picker still had plain dialog windows with the same black-bar
 * defect until this batch).
 */
@Composable
private fun AnimatedPageDialog(
    onDismiss: () -> Unit,
    content: @Composable (requestDismiss: () -> Unit) -> Unit,
) {
    val transition = remember {
        MutableTransitionState(false).apply { targetState = true }
    }
    LaunchedEffect(transition.currentState, transition.targetState) {
        if (!transition.currentState && !transition.targetState) {
            onDismiss()
        }
    }
    Dialog(
        onDismissRequest = { transition.targetState = false },
        properties = fullScreenDialogProperties(),
    ) {
        AnimatedVisibility(
            visibleState = transition,
            enter = fadeIn(tween(240)) +
                slideInVertically(
                    animationSpec = tween(320, easing = LsexEmphasizedEasing),
                ) { it / 16 },
            exit = fadeOut(tween(160)) +
                slideOutVertically(
                    animationSpec = tween(220, easing = LsexEmphasizedEasing),
                ) { it / 16 },
        ) {
            content { transition.targetState = false }
        }
    }
}

/**
 * The Donate page (Ernest, 2026-11-13): ONE section reached from a
 * single Settings entry — his words and wallets, every address with
 * a copy button.
 */
@Composable
private fun DonatePage(onDismiss: () -> Unit) {
    AnimatedPageDialog(onDismiss = onDismiss) { requestDismiss ->
        DonateContent(requestDismiss)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun DonateContent(onDismiss: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val ltcAddress = "ltc1q2t7pg8xyyqvu2cm2jxpxtqcxr5layxztf32q5g"
    val wallets = listOf(
        "Bitcoin (BTC)" to "bc1qvfr0kk56lhjn72fklaeg83flq3tjapls6gtpmy",
        "Bitcoin Cash (BCH)" to
            "qpg572zm69ln9tskesg4lptqjdde29260uqqvg4zpk",
        "TON" to "UQAOQmzcUNKFfyvKMr8z5dTK1rBdj1D4uopw0G9Lxnpp6lw3",
        "USDT (TRC20 · Tron)" to "TVfwxMMa4dsAsRmd6VNkV1sUcGFK8TKwSA",
        "USDT (ERC20 · Ethereum)" to
            "0x796759d5c38fF7CF1b452b8AB92a5ec23f16CC66",
        "Tron (TRX)" to "TVfwxMMa4dsAsRmd6VNkV1sUcGFK8TKwSA",
        "Ethereum (ETH)" to
            "0x796759d5c38fF7CF1b452b8AB92a5ec23f16CC66",
    )
    Surface(Modifier.fillMaxSize(), color = scheme.background) {
        Column(
            Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        MaterialSymbols.Outlined.Arrow_back,
                        contentDescription = "Back",
                    )
                }
                Text(
                    "Donate",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = scheme.surfaceContainer,
                ) {
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
                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically,
                        ) {
                            Text(
                                ltcAddress,
                                style = MaterialTheme.typography.bodySmall,
                                color = scheme.onSurfaceVariant,
                                fontFamily = FontFamily.Monospace,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f),
                            )
                            CopyButton(ltcAddress)
                        }
                    }
                }
                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
                ) {
                    wallets.forEachIndexed { index, (label, address) ->
                        LsexSegmentedItem(
                            index = index,
                            count = wallets.size,
                            supporting = {
                                Text(
                                    address,
                                    fontFamily = FontFamily.Monospace,
                                )
                            },
                            trailing = { CopyButton(address) },
                        ) {
                            Text(label)
                        }
                    }
                }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = scheme.surfaceContainer,
                ) {
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
                            color = scheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun SettingsScreen(controller: LocalSendController, settings: AppSettings) {
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
    var showDonate by remember { mutableStateOf(false) }
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
        // Ernest (2026-11-13): ONE section inside Settings that
        // opens its own page — not nine tiles spread down the
        // settings list.
        item {
            MinusGroup(
                title = null,
                rows = listOf { _, _, shape ->
                    MinusNavRow(
                        shape = shape,
                        icon = MaterialSymbols.OutlinedFilled
                            .Volunteer_activism,
                        title = "Donate",
                        subtitle = "Support the project",
                        onClick = { showDonate = true },
                    )
                },
            )
        }

        item {
            val ltcAddress = "ltc1q2t7pg8xyyqvu2cm2jxpxtqcxr5layxztf32q5g"
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
    if (showDonate) {
        DonatePage(onDismiss = { showDonate = false })
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
    count == 1 -> RoundedCornerShape(20.dp)
    index == 0 -> RoundedCornerShape(
        topStart = 20.dp, topEnd = 20.dp, bottomStart = 4.dp, bottomEnd = 4.dp,
    )
    index == count - 1 -> RoundedCornerShape(
        topStart = 4.dp, topEnd = 4.dp, bottomStart = 20.dp, bottomEnd = 20.dp,
    )
    else -> RoundedCornerShape(4.dp)
}

/** One circular action button behind a swipe-revealed row (mockup style). */
@Composable
private fun SwipeActionButton(
    container: Color,
    content: Color,
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
) {
    // UDYAT's list fix, ported (Ernest, 2026-11-14): the swipe buttons
    // are 48dp-wide stadiums as tall as the list item itself — the
    // height arrives through fillMaxHeight() against the reveal row's
    // EXPLICIT measured height (inside a LazyColumn the constraint is
    // unbounded, so an unmeasured fillMaxHeight collapses to the
    // content, ~29dp). Corner morphs 20dp -> 28dp on press/hover.
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val hovered by interaction.collectIsHoveredAsState()
    val corner by animateDpAsState(
        targetValue = if (pressed || hovered) 28.dp else 20.dp,
        animationSpec = spring(
            dampingRatio = 0.65f,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "swipeButtonShape",
    )
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(corner),
        color = container,
        interactionSource = interaction,
        modifier = Modifier.width(48.dp).fillMaxHeight(),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                icon,
                contentDescription = description,
                tint = content,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HistoryRow(
    entry: HistoryEntry,
    index: Int,
    count: Int,
    onOpen: (() -> Unit)? = null,
    onInstall: (() -> Unit)? = null,
    onResend: (() -> Unit)? = null,
    onCopy: (() -> Unit)? = null,
    onRemove: () -> Unit,
    onDeleteFile: (() -> Unit)? = null,
) {
    if (!prefersSwipeActions()) {
        // Pointer platforms: actions stay visible on the tile, and
        // the row's click runs its primary action.
        HistoryRowItem(
            entry = entry,
            index = index,
            count = count,
            onOpen = onOpen,
            onInstall = onInstall,
            onResend = onResend,
            onCopy = onCopy,
            onRemove = onRemove,
            showActions = true,
            onClick = onOpen ?: onInstall ?: onResend ?: onCopy,
        )
        return
    }
    // Touch: a left swipe reveals the mockup's circular action buttons —
    // remove-entry, the row's primary action (open / install / resend),
    // and for received files a red delete that removes the file itself.
    // (v0.8.0 shipped History with NO slide options at all.)
    val hasPrimary = onOpen != null || onInstall != null || onResend != null || onCopy != null
    val actionCount = 1 + (if (hasPrimary) 1 else 0) + (if (onDeleteFile != null) 1 else 0)
    val density = LocalDensity.current
    // UDYAT's measured geometry (its latest list fix, ported): the
    // item measures its own height for the buttons, the button
    // cluster measures its own width, and the open anchor is the
    // cluster width + the 2dp gap between the item and the first
    // button. The constructor anchors use the computed cluster size
    // (48dp buttons, 2dp gaps) so they exist from the first frame.
    var itemHeightPx by remember { mutableStateOf(0) }
    var revealWidthPx by remember { mutableStateOf(0f) }
    val revealPx = with(density) { (actionCount * 50).dp.toPx() }
    val state = remember {
        AnchoredDraggableState(
            initialValue = 0,
            anchors = DraggableAnchors {
                0 at 0f
                1 at -revealPx
            },
        )
    }
    LaunchedEffect(revealWidthPx) {
        if (revealWidthPx > 0f) {
            state.updateAnchors(DraggableAnchors {
                0 at 0f
                1 at -(revealWidthPx + with(density) { 2.dp.toPx() })
            })
        }
    }
    val scope = rememberCoroutineScope()
    Box(
        Modifier
            .fillMaxWidth()
            .clip(lsexSegmentedShapes(index, count).shape),
    ) {
        Box(Modifier.matchParentSize(), contentAlignment = Alignment.CenterEnd) {
            Row(
                Modifier
                    .then(
                        if (itemHeightPx > 0) {
                            Modifier.height(with(density) { itemHeightPx.toDp() })
                        } else {
                            Modifier
                        },
                    )
                    .onSizeChanged { revealWidthPx = it.width.toFloat() },
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
            SwipeActionButton(
                container = MaterialTheme.colorScheme.secondaryContainer,
                content = MaterialTheme.colorScheme.onSecondaryContainer,
                icon = MaterialSymbols.Outlined.Close,
                description = "Remove from history",
            ) {
                scope.launch { state.snapTo(0) }
                onRemove()
            }
            if (hasPrimary) {
                SwipeActionButton(
                    container = MaterialTheme.colorScheme.secondaryContainer,
                    content = MaterialTheme.colorScheme.onSecondaryContainer,
                    icon = when {
                        onInstall != null -> MaterialSymbols.Outlined.System_update
                        onCopy != null -> MaterialSymbols.Outlined.Content_copy
                        else -> MaterialSymbols.Outlined.Logout
                    },
                    description = when {
                        onOpen != null -> "Open file"
                        onInstall != null -> "Install this package"
                        onCopy != null -> "Copy text"
                        else -> "Resend"
                    },
                ) {
                    scope.launch { state.snapTo(0) }
                    (onOpen ?: onInstall ?: onResend ?: onCopy)?.invoke()
                }
            }
            if (onDeleteFile != null) {
                SwipeActionButton(
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
        }
        HistoryRowItem(
            entry = entry,
            index = index,
            count = count,
            onOpen = onOpen,
            onInstall = onInstall,
            onResend = onResend,
            onCopy = onCopy,
            onRemove = onRemove,
            showActions = false,
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { itemHeightPx = it.height }
                .offset {
                    val off = state.offset
                    IntOffset(if (off.isNaN()) 0 else off.roundToInt(), 0)
                }
                .anchoredDraggable(state, Orientation.Horizontal),
            onClick = {
                if (state.currentValue == 1) {
                    scope.launch { state.snapTo(0) }
                } else if (onOpen == null && onInstall == null && onResend == null) {
                    // A text entry's primary action is copying it.
                    onCopy?.invoke()
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HistoryRowItem(
    entry: HistoryEntry,
    index: Int,
    count: Int,
    onOpen: (() -> Unit)?,
    onInstall: (() -> Unit)?,
    onResend: (() -> Unit)?,
    onCopy: (() -> Unit)?,
    onRemove: () -> Unit,
    showActions: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    LsexSegmentedItem(
        index = index,
        count = count,
        modifier = modifier,
        onClick = onClick,
        leading = {
            Icon(
                if (entry.incoming) {
                    MaterialSymbols.Outlined.Download
                } else {
                    MaterialSymbols.Outlined.Send
                },
                contentDescription = null,
            )
        },
        supporting = {
            Text(
                "${entry.peerAlias} · ${formatSize(entry.size)}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        trailing = {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    if (onCopy != null) {
                        IconButton(onClick = onCopy) {
                            Icon(
                                MaterialSymbols.Outlined.Content_copy,
                                contentDescription = "Copy text",
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
                    if (entry.success) {
                        MaterialSymbols.Outlined.Check_circle
                    } else {
                        MaterialSymbols.Outlined.Error
                    },
                    contentDescription = null,
                    tint = if (entry.success) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                )
            }
        },
    ) {
        Text(
            entry.fileName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
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
                LinearProgressIndicator(
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
/**
 * Ernest's progress rule (v0.10.0): every bar is the SOLID MD3E
 * LinearProgressIndicator — except while a received file is being
 * checked (VERIFYING), which keeps the wavy one, like UDYAT's
 * completed-file check.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun TransferProgressBar(progress: FileProgress, modifier: Modifier = Modifier) {
    if (progress.status == TransferStatus.VERIFYING) {
        LinearWavyProgressIndicator(
            progress = { progress.fraction },
            modifier = modifier,
        )
    } else {
        LinearProgressIndicator(
            progress = { progress.fraction },
            modifier = modifier,
        )
    }
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
        if (progress.status == TransferStatus.ACTIVE ||
            progress.status == TransferStatus.WAITING ||
            progress.status == TransferStatus.VERIFYING
        ) {
            TransferProgressBar(progress, Modifier.fillMaxWidth())
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
internal fun ShareScreen(
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
                                text = { Text("Send to multiple devices") },
                                leadingIcon = {
                                    Icon(MaterialSymbols.Outlined.Devices, contentDescription = null)
                                },
                                trailingIcon = {
                                    if (settings.multiDeviceSend) {
                                        Icon(
                                            MaterialSymbols.Outlined.Check,
                                            contentDescription = "On",
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                },
                                onClick = {
                                    sendMenuOpen = false
                                    controller.updateSettings(
                                        settings.copy(multiDeviceSend = !settings.multiDeviceSend),
                                    )
                                    if (settings.multiDeviceSend) {
                                        // Leaving multi mode: collapse the
                                        // target set to at most one device.
                                        onSelectedChange(selectedFingerprints.take(1).toSet())
                                    }
                                },
                            )
                            DropdownMenuItem(
                                text = { Text("Send via link") },
                                leadingIcon = {
                                    Icon(MaterialSymbols.Outlined.Link, contentDescription = null)
                                },
                                onClick = {
                                    sendMenuOpen = false
                                    onShareViaLink()
                                },
                            )
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
            Column(
                modifier = Modifier.animateItem(placementSpec = bouncyOffsetSpring()),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
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
        // Same list pattern as History: one segmented group on the
        // background, header tile first, pill chevron, 3dp seams.
        if (staged.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier.animateContentSize(bouncySizeSpring()),
                    verticalArrangement =
                        Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
                ) {
                    LsexSegmentedItem(
                        index = 0,
                        count = if (selectionExpanded) staged.size + 1 else 1,
                        onClick = { selectionExpanded = !selectionExpanded },
                        leading = {
                            Icon(
                                MaterialSymbols.Outlined.Attach_file,
                                contentDescription = null,
                            )
                        },
                        trailing = {
                            Row(
                                verticalAlignment =
                                    Alignment.CenterVertically,
                            ) {
                                TextButton(
                                    onClick = { onStagedChange(emptyList()) },
                                ) {
                                    Text("Clear")
                                }
                                PillChevron(
                                    icon = if (selectionExpanded) {
                                        MaterialSymbols.Outlined.Expand_less
                                    } else {
                                        MaterialSymbols.Outlined.Expand_more
                                    },
                                )
                            }
                        },
                    ) {
                        Text("${strings.selection} · ${staged.size}")
                    }
                    if (selectionExpanded) {
                        Column(
                            verticalArrangement =
                                Arrangement.spacedBy(
                                    ListItemDefaults.SegmentedGap,
                                ),
                        ) {
                            staged.forEachIndexed { index, item ->
                                StagedItemCard(
                                    item = item,
                                    index = index + 1,
                                    count = staged.size + 1,
                                        onOpen = {
                                            when (item) {
                                                is StagedItem.FileItem ->
                                                    openReceivedFile(item.file.handle, item.file.name)
                                                is StagedItem.TextItem -> editIndex = index
                                                is StagedItem.FolderItem -> Unit
                                            }
                                        },
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
internal fun PickerScreen(
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
    AnimatedPageDialog(onDismiss = onDismiss) { requestDismiss ->
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
                    IconButton(onClick = requestDismiss) {
                        Icon(MaterialSymbols.Outlined.Arrow_back, contentDescription = "Back")
                    }
                    Text(
                        "Add files",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f),
                    )
                    Button(onClick = requestDismiss) { Text("Done · $stagedCount") }
                }
                // Ernest (v0.10.0): NO tabs — the content sections
                // switch through the same connected ToggleButton
                // group as "Off / Favorites / Everyone", icons included.
                ConnectedChoiceGroup(
                    options = tabs.map { entry ->
                        when (entry) {
                            ContentTab.Apps ->
                                MaterialSymbols.Outlined.Apps to "Apps"
                            ContentTab.Photos ->
                                MaterialSymbols.Outlined.Image to "Photos"
                            ContentTab.Videos ->
                                MaterialSymbols.Outlined.Movie to "Videos"
                            ContentTab.Audio ->
                                MaterialSymbols.Outlined.Music_note to "Music"
                            ContentTab.Files ->
                                MaterialSymbols.Outlined.Folder to "Files"
                            else -> MaterialSymbols.Outlined.Description to entry.name
                        }
                    },
                    selectedIndex = tabs.indexOf(tab),
                    modifier = Modifier.fillMaxWidth(),
                    compactLabels = true,
                    onSelect = { tab = tabs[it] },
                )
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
                                        name = if (
                                            app.apkHandle.endsWith(".apks")
                                        ) {
                                            "${app.label}.apks"
                                        } else {
                                            "${app.label}.apk"
                                        },
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
fun TransferSheet(
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
                                LinearProgressIndicator(Modifier.fillMaxWidth())
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
                            LinearProgressIndicator(
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
                    if (file.status == TransferStatus.ACTIVE ||
                        file.status == TransferStatus.VERIFYING
                    ) {
                        Spacer(Modifier.height(6.dp))
                        TransferProgressBar(file, Modifier.fillMaxWidth())
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
    val receiving = incomingProgress.any {
        it.status == TransferStatus.ACTIVE || it.status == TransferStatus.VERIFYING
    }
    AnimatedVisibility(
        visible = sending || receiving,
        modifier = modifier,
        enter =
            scaleIn(animationSpec = bouncyFloatSpring(), initialScale = 0.7f) +
                fadeIn(tween(160)) +
                slideInVertically(
                    animationSpec = bouncyOffsetSpring(),
                    initialOffsetY = { it / 2 },
                ),
        exit = scaleOut(tween(160)) + fadeOut(tween(160)),
    ) {
        GlobalTransferPill(
            controller = controller,
            sending = sending,
            receiving = receiving,
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
    val activeReceiveCount = incoming.count {
        it.status == TransferStatus.ACTIVE || it.status == TransferStatus.VERIFYING
    }
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
            Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                when {
                    sending && receiving -> MaterialSymbols.Outlined.Compare_arrows
                    receiving -> MaterialSymbols.Outlined.Download
                    else -> MaterialSymbols.Outlined.Send
                },
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    "$label · ${doneCount + activeReceiveCount + sendFiles.count { it.status == TransferStatus.ACTIVE }}/${activeSendCount + activeReceiveCount + doneCount} files",
                    style = MaterialTheme.typography.titleSmall,
                )
                LinearProgressIndicator(
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
    AnimatedPageDialog(onDismiss = onDismiss) { requestDismiss ->
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
                    IconButton(onClick = requestDismiss) {
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
