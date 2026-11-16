package dev.kurokawa.localsend.expressive.ui

import androidx.compose.runtime.staticCompositionLocalOf
import dev.kurokawa.localsend.expressive.platform.systemLanguageCode

/**
 * UI strings for the whole app (v0.12.4: every label, not just the
 * chrome).
 *
 * Covers EVERY language the official LocalSend app ships (verified
 * against localsend/localsend `app/assets/i18n`, 57 locale files).
 * English and Spanish carry full tables; the other locales translate
 * the chrome keys and fall back to English for the rest, the same way
 * upstream's partial locales behave.
 *
 * Implementation note: the table is a value MAP with one getter per
 * key, NOT a data class — at ~270 keys a data class's constructor /
 * copy() signature exceeds the JVM's 255-argument method limit and
 * the class fails to load (ClassFormatError). Do not convert back.
 */
internal class UiStrings(private val values: Map<String, String>) {
    private fun v(key: String, en: String): String = values[key] ?: en

    val send: String get() = v("send", "Send")
    val receive: String get() = v("receive", "Receive")
    val settings: String get() = v("settings", "Settings")
    val connect: String get() = v("connect", "Connect")
    val nearbyDevices: String get() = v("nearbyDevices", "Nearby devices")
    val selection: String get() = v("selection", "Selection")
    val readyToSend: String get() = v("readyToSend", "Ready to send")
    val history: String get() = v("history", "History")
    val searching: String get() = v("searching", "Searching for devices…")
    val troubleshoot: String get() = v("troubleshoot", "Troubleshoot")
    val accept: String get() = v("accept", "Accept")
    val add: String get() = v("add", "Add")
    val addIpAddress: String get() = v("addIpAddress", "Add IP address")
    val addDeviceByIp: String get() = v("addDeviceByIp", "Add device by IP")
    val addFiles: String get() = v("addFiles", "Add files")
    val addText: String get() = v("addText", "Add text")
    val advancedSettings: String get() = v("advancedSettings", "Advanced settings")
    val alwaysOnTop: String get() = v("alwaysOnTop", "Always on top")
    val animations: String get() = v("animations", "Animations")
    val appearance: String get() = v("appearance", "Appearance")
    val autoFinish: String get() = v("autoFinish", "Auto Finish")
    val autoAcceptWebUploads: String get() = v("autoAcceptWebUploads", "Auto-accept web uploads")
    val back: String get() = v("back", "Back")
    val band: String get() = v("band", "Band")
    val browseStorage: String get() = v("browseStorage", "Browse your storage")
    val browserDownloadRequest: String get() = v("browserDownloadRequest", "Browser download request")
    val browsersCanOpen: String get() = v("browsersCanOpen", "Browsers on your network can open:")
    val browseEllipsis: String get() = v("browseEllipsis", "Browse…")
    val cancel: String get() = v("cancel", "Cancel")
    val cancelSend: String get() = v("cancelSend", "Cancel send")
    val cancelThisSend: String get() = v("cancelThisSend", "Cancel this send")
    val changeAvatar: String get() = v("changeAvatar", "Change avatar")
    val chooseFiles: String get() = v("chooseFiles", "Choose files")
    val chooseFolder: String get() = v("chooseFolder", "Choose folder")
    val chooseYourAvatar: String get() = v("chooseYourAvatar", "Choose your avatar")
    val chunkedTransfers: String get() = v("chunkedTransfers", "Chunked transfers")
    val clear: String get() = v("clear", "Clear")
    val close: String get() = v("close", "Close")
    val colorScheme: String get() = v("colorScheme", "Color scheme")
    val comingInFutureUpdate: String get() = v("comingInFutureUpdate", "Coming in a future update")
    val connectLabel: String get() = v("connectLabel", "Connect")
    val connected: String get() = v("connected", "Connected")
    val connection: String get() = v("connection", "Connection")
    val copy: String get() = v("copy", "Copy")
    val copyAddress: String get() = v("copyAddress", "Copy address")
    val copyDownloadLink: String get() = v("copyDownloadLink", "Copy download link")
    val copyText: String get() = v("copyText", "Copy text")
    val createChecksums: String get() = v("createChecksums", "Create checksums")
    val decline: String get() = v("decline", "Decline")
    val desktop: String get() = v("desktop", "Desktop")
    val destinationFolder: String get() = v("destinationFolder", "Destination folder")
    val destinationFolderPath: String get() = v("destinationFolderPath", "Destination folder path")
    val details: String get() = v("details", "Details")
    val deviceInfo: String get() = v("deviceInfo", "Device info")
    val deviceModel: String get() = v("deviceModel", "Device model")
    val deviceModelHint: String get() = v("deviceModelHint", "Device model (empty = automatic)")
    val deviceName: String get() = v("deviceName", "Device name")
    val deviceNameAlias: String get() = v("deviceNameAlias", "Device name (alias)")
    val deviceType: String get() = v("deviceType", "Device type")
    val devicesFindEachOther: String get() = v("devicesFindEachOther", "Devices on the same network find each other automatically.")
    val discard: String get() = v("discard", "Discard")
    val discoveryTimeout: String get() = v("discoveryTimeout", "Discovery timeout (seconds)")
    val dismiss: String get() = v("dismiss", "Dismiss")
    val donate: String get() = v("donate", "Donate")
    val done: String get() = v("done", "Done")
    val downloadApi: String get() = v("downloadApi", "Download API")
    val editText: String get() = v("editText", "Edit text")
    val encryptionHttps: String get() = v("encryptionHttps", "Encryption (HTTPS)")
    val everyVersionWithNotes: String get() = v("everyVersionWithNotes", "Every version, with notes")
    val failed: String get() = v("failed", "Failed")
    val favorite: String get() = v("favorite", "Favorite")
    val favoriteDevices: String get() = v("favoriteDevices", "Favorite devices")
    val favorites: String get() = v("favorites", "Favorites")
    val filter: String get() = v("filter", "Filter")
    val fingerprint: String get() = v("fingerprint", "Fingerprint")
    val general: String get() = v("general", "General")
    val grant: String get() = v("grant", "Grant")
    val groupNamePasswordBand: String get() = v("groupNamePasswordBand", "Group name, password and band")
    val hideSystem: String get() = v("hideSystem", "Hide system")
    val historyLabel: String get() = v("historyLabel", "History")
    val hotspot: String get() = v("hotspot", "Hotspot")
    val hotspotIsOn: String get() = v("hotspotIsOn", "Hotspot is on")
    val hotspotSettings: String get() = v("hotspotSettings", "Hotspot settings")
    val ipAddressHint: String get() = v("ipAddressHint", "IP address (optionally IP:port)")
    val inFileBrowser: String get() = v("inFileBrowser", "In the file browser")
    val installPackage: String get() = v("installPackage", "Install this package")
    val language: String get() = v("language", "Language")
    val linkSharing: String get() = v("linkSharing", "Link sharing")
    val shareAppSubtitle: String get() = v("shareAppSubtitle", "Link, Bluetooth or the releases page")
    val litecoin: String get() = v("litecoin", "Litecoin (LTC)")
    val loading: String get() = v("loading", "Loading…")
    val materialYou: String get() = v("materialYou", "Material You")
    val mediaAccessNeeded: String get() = v("mediaAccessNeeded", "Media access needed")
    val message: String get() = v("message", "Message")
    val minimizeToTray: String get() = v("minimizeToTray", "Minimize to tray")
    val moreSendOptions: String get() = v("moreSendOptions", "More send options")
    val nearbyNetworks: String get() = v("nearbyNetworks", "Nearby networks")
    val network: String get() = v("network", "Network")
    val noFilesSharedYet: String get() = v("noFilesSharedYet", "No files are being shared yet.")
    val nothingHereYet: String get() = v("nothingHereYet", "Nothing here yet.")
    val nothingTransferring: String get() = v("nothingTransferring", "Nothing is transferring right now.")
    val nothingYetFinished: String get() = v("nothingYetFinished", "Nothing yet — finished transfers land here.")
    val ok: String get() = v("ok", "OK")
    val on: String get() = v("on", "On")
    val onlyForThisFolder: String get() = v("onlyForThisFolder", "Only for this folder")
    val open: String get() = v("open", "Open")
    val openFile: String get() = v("openFile", "Open file")
    val openReleasesPage: String get() = v("openReleasesPage", "Open the releases page")
    val opensShareSheetBluetooth: String get() = v("opensShareSheetBluetooth", "Opens the share sheet — pick Bluetooth")
    val otherDevicesCanFind: String get() = v("otherDevicesCanFind", "Other devices can find and send to you")
    val pin: String get() = v("pin", "PIN")
    val pinHint: String get() = v("pinHint", "PIN (empty = no PIN)")
    val password: String get() = v("password", "Password")
    val passwordHint: String get() = v("passwordHint", "Password (empty = none / system)")
    val paste: String get() = v("paste", "Paste")
    val pasteFromClipboard: String get() = v("pasteFromClipboard", "Paste from clipboard")
    val pause: String get() = v("pause", "Pause")
    val pausedChunkedNote: String get() = v("pausedChunkedNote", "Paused — chunked files continue where they stopped.")
    val photosVideosGalleryNote: String get() = v("photosVideosGalleryNote", "Photos and videos go to Pictures / Movies")
    val pickDeviceFirst: String get() = v("pickDeviceFirst", "Pick a device first — the text goes to the selected device.")
    val pickFilesToSend: String get() = v("pickFilesToSend", "Pick files to send")
    val pickFilesSystemPicker: String get() = v("pickFilesSystemPicker", "Pick files with the system picker")
    val playInSystemPlayer: String get() = v("playInSystemPlayer", "Play in the system player")
    val port: String get() = v("port", "Port")
    val preview: String get() = v("preview", "Preview")
    val quickSave: String get() = v("quickSave", "Quick Save")
    val quickShare: String get() = v("quickShare", "Quick Share")
    val quickShareTile: String get() = v("quickShareTile", "Quick share tile")
    val receiveQueueOrder: String get() = v("receiveQueueOrder", "Receive queue order")
    val receiveViaLink: String get() = v("receiveViaLink", "Receive via link")
    val recheck: String get() = v("recheck", "Recheck")
    val removeFavorite: String get() = v("removeFavorite", "Remove favorite")
    val removeFromHistory: String get() = v("removeFromHistory", "Remove from history")
    val rename: String get() = v("rename", "Rename")
    val renameEdit: String get() = v("renameEdit", "Rename / edit")
    val renameFile: String get() = v("renameFile", "Rename file")
    val reorder: String get() = v("reorder", "Reorder")
    val requirePin: String get() = v("requirePin", "Require PIN")
    val restart: String get() = v("restart", "Restart")
    val restartServerToApply: String get() = v("restartServerToApply", "Restart the server to apply")
    val resume: String get() = v("resume", "Resume")
    val retry: String get() = v("retry", "Retry")
    val retryAutomatically: String get() = v("retryAutomatically", "Retry automatically")
    val retryThisFile: String get() = v("retryThisFile", "Retry this file")
    val retryWithPin: String get() = v("retryWithPin", "Retry with PIN")
    val reverseOrder: String get() = v("reverseOrder", "Reverse order")
    val save: String get() = v("save", "Save")
    val saveAdvancedNetwork: String get() = v("saveAdvancedNetwork", "Save advanced network settings")
    val saveDestination: String get() = v("saveDestination", "Save destination")
    val saveModel: String get() = v("saveModel", "Save model")
    val saveName: String get() = v("saveName", "Save name")
    val savePortAndPin: String get() = v("savePortAndPin", "Save port and PIN")
    val saveToGallery: String get() = v("saveToGallery", "Save to Gallery")
    val saveToHistory: String get() = v("saveToHistory", "Save to History")
    val saveWindowPlacement: String get() = v("saveWindowPlacement", "Save window placement")
    val scanCode: String get() = v("scanCode", "Scan a code")
    val scanCodeToJoin: String get() = v("scanCodeToJoin", "Scan a code to join a network")
    val scanAgain: String get() = v("scanAgain", "Scan again")
    val scanForNetworks: String get() = v("scanForNetworks", "Scan for networks")
    val searchAgain: String get() = v("searchAgain", "Search again")
    val searchApps: String get() = v("searchApps", "Search apps")
    val searchMusic: String get() = v("searchMusic", "Search music")
    val searchThisFolder: String get() = v("searchThisFolder", "Search this folder")
    val secured: String get() = v("secured", "Secured")
    val selected: String get() = v("selected", "Selected")
    val sendLabel: String get() = v("sendLabel", "Send")
    val sendText: String get() = v("sendText", "Send a text")
    val sendToMultiple: String get() = v("sendToMultiple", "Send to multiple devices")
    val sendViaLink: String get() = v("sendViaLink", "Send via link")
    val sending: String get() = v("sending", "Sending")
    val server: String get() = v("server", "Server")
    val setupConnection: String get() = v("setupConnection", "Setup your connection")
    val share: String get() = v("share", "Share")
    val shareLsex: String get() = v("shareLsex", "Share LSEx")
    val shareViaBluetooth: String get() = v("shareViaBluetooth", "Share via Bluetooth")
    val shareViaLink: String get() = v("shareViaLink", "Share via link")
    val shareViaLinkWebNote: String get() = v("shareViaLinkWebNote", "Share-via-link web page and uploads")
    val showHiddenFilesLabel: String get() = v("showHiddenFilesLabel", "Show hidden files")
    val skipAcceptPromptEveryone: String get() = v("skipAcceptPromptEveryone", "Skip the accept prompt — for everyone,")
    val skipPromptWebUploads: String get() = v("skipPromptWebUploads", "Skip the prompt for files uploaded from the web page")
    val someFilesNeedAttention: String get() = v("someFilesNeedAttention", "Some files need attention — tap to review")
    val sort: String get() = v("sort", "Sort")
    val splashAndMotion: String get() = v("splashAndMotion", "Splash and expressive motion")
    val stageForResending: String get() = v("stageForResending", "Stage for resending")
    val startWifiDirect: String get() = v("startWifiDirect", "Start Wi-Fi Direct")
    val startHotspot: String get() = v("startHotspot", "Start hotspot")
    val stopWifiDirect: String get() = v("stopWifiDirect", "Stop Wi-Fi Direct")
    val stopHotspot: String get() = v("stopHotspot", "Stop hotspot")
    val storage: String get() = v("storage", "Storage")
    val supportProject: String get() = v("supportProject", "Support the project")
    val textStagedNote: String get() = v("textStagedNote", "Text is staged like a file — pick the devices above, then send to all of them.")
    val deviceAppearsNote: String get() = v("deviceAppearsNote", "The device appears in the list once it answers.")
    val folderNoFiles: String get() = v("folderNoFiles", "This folder has no files to send.")
    val folderEmpty: String get() = v("folderEmpty", "This folder is empty.")
    val transfer: String get() = v("transfer", "Transfer")
    val transfers: String get() = v("transfers", "Transfers")
    val troubleshootDiscovery: String get() = v("troubleshootDiscovery", "Troubleshoot discovery")
    val turnOn: String get() = v("turnOn", "Turn on")
    val turnOnWebSharing: String get() = v("turnOnWebSharing", "Turn on web sharing")
    val turnWebSharingOff: String get() = v("turnWebSharingOff", "Turn web sharing off")
    val unfavorite: String get() = v("unfavorite", "Unfavorite")
    val unknownDevice: String get() = v("unknownDevice", "Unknown device")
    val useDownloads: String get() = v("useDownloads", "Use Downloads")
    val verifyChecksums: String get() = v("verifyChecksums", "Verify checksums")
    val wallpaperColorsNote: String get() = v("wallpaperColorsNote", "Wallpaper colors for the brand theme")
    val wifiDirect: String get() = v("wifiDirect", "Wi-Fi Direct")
    val wifiDirectGroupOn: String get() = v("wifiDirectGroupOn", "Wi-Fi Direct group is on")
    val wifiDirectSettings: String get() = v("wifiDirectSettings", "Wi-Fi Direct settings")
    val writeMessage: String get() = v("writeMessage", "Write a message")
    val yourDevice: String get() = v("yourDevice", "Your device")
    val model: String get() = v("model", "Model")
    val type: String get() = v("type", "Type")
    val lastSeen: String get() = v("lastSeen", "Last seen")
    val address: String get() = v("address", "Address")
    val shareAppIntro: String get() = v("shareAppIntro", "Send a friend the download link — LSEx is free and works with the official LocalSend app.")
    val copied: String get() = v("copied", "Copied")
    val active: String get() = v("active", "Active")
    val queued: String get() = v("queued", "Queued")
    val filesProgress: String get() = v("filesProgress", "%s/%s files")
    val savedCount: String get() = v("savedCount", "%s saved")
    val itemsCount: String get() = v("itemsCount", "%s items")
    val wantsToSend: String get() = v("wantsToSend", "%s wants to send")
    val etaLabel: String get() = v("etaLabel", "ETA %s")
    val joinNetwork: String get() = v("joinNetwork", "Join “%s”")
    val messageFrom: String get() = v("messageFrom", "Message from %s")
    val musicCount: String get() = v("musicCount", "Music (%s)")
    val myAppsCount: String get() = v("myAppsCount", "My apps (%s)")
    val networkName: String get() = v("networkName", "Network: %s")
    val passwordValue: String get() = v("passwordValue", "Password: %s")
    val resumeTo: String get() = v("resumeTo", "Resume · %s to %s")
    val textTo: String get() = v("textTo", "Text to %s")
    val toDevice: String get() = v("toDevice", "To %s")
    val totalFrom: String get() = v("totalFrom", "Total %s · from %s")
    val doneCount: String get() = v("doneCount", "Done · %s")
    val networks: String get() = v("networks", "Networks")
    val hotspotGroupSubtitle: String get() = v("hotspotGroupSubtitle", "Name, password and band for your hotspot")
    val anyoneCanOffer: String get() = v("anyoneCanOffer", "Anyone on the network can offer files")
    val folderItems: String get() = v("folderItems", "Folder · %s items")
    val locationOffNote: String get() = v("locationOffNote", "Location is off — Android hides nearby networks and can block Wi-Fi Direct while it's off.")
    val startHotspotNote: String get() = v("startHotspotNote", "Start a hotspot on this device — other devices join it and find you, no router needed.")
    val hotspotAssignedNote: String get() = v("hotspotAssignedNote", "Android assigns the hotspot's name and password itself — regular apps can't choose them. Start the hotspot and the connection card shows the name and password that actually took effect, with a QR code to join.")
    val serveFilesNote: String get() = v("serveFilesNote", "Serve files to any browser on this network — no app needed on the other side.")
    val identityAnnouncedNote: String get() = v("identityAnnouncedNote", "Name, model and type are announced when the server starts")
    val quickTileNote: String get() = v("quickTileNote", "Add the LSEx tile from your Quick Settings panel editor to start a share in one tap.")
    val advancedSectionsNote: String get() = v("advancedSectionsNote", "Show the advanced sections, like the official app")
    val quickSaveNote: String get() = v("quickSaveNote", "Skip the accept prompt — for everyone, or only for favorites.")
    val autoFinishNote: String get() = v("autoFinishNote", "Close the receive progress when a transfer ends")
    val verifyChecksumsNote: String get() = v("verifyChecksumsNote", "Reject files whose SHA-256 doesn't match")
    val shareViaLinkAutoAcceptNote: String get() = v("shareViaLinkAutoAcceptNote", "Receive-via-link uploads skip the prompt")
    val createChecksumsNote: String get() = v("createChecksumsNote", "SHA-256 for every file sent (slower on huge files)")
    val autoRetryNote: String get() = v("autoRetryNote", "Failed sends back off and retry on their own")
    val chunkedNote: String get() = v("chunkedNote", "Resumable 1 MiB chunks between LSEx devices")
    val httpsNote: String get() = v("httpsNote", "Self-signed certificate, fingerprint-verified")
    val multicastGroupLabel: String get() = v("multicastGroupLabel", "Multicast group (empty = 224.0.0.167)")
    val wantsToDownloadFiles: String get() = v("wantsToDownloadFiles", "%s · %s wants to download the files you are sharing via link.")
    val bulletSameWifi: String get() = v("bulletSameWifi", "• Both devices must be on the same Wi-Fi / LAN (not a guest network).")
    val bulletVpn: String get() = v("bulletVpn", "• Turn off VPNs and \"AP isolation\" on the router — they block local discovery.")
    val bulletKeepAwake: String get() = v("bulletKeepAwake", "• Keep the other device awake with LocalSend open, then hit the refresh button.")
    val tourHint: String get() = v("tourHint", "Swipe to reveal actions · double-tap to dismiss")
    val clearHistoryTitle: String get() = v("clearHistoryTitle", "Clear history?")
    val clearHistoryText: String get() = v("clearHistoryText", "Every entry in the history will be removed. The files themselves stay where they are.")
    val clearSelectionTitle: String get() = v("clearSelectionTitle", "Clear the selection?")
    val clearSelectionText: String get() = v("clearSelectionText", "All staged files and text will be removed from the selection.")
    val clearWebShareTitle: String get() = v("clearWebShareTitle", "Stop sharing these files?")
    val clearWebShareText: String get() = v("clearWebShareText", "The web page will have no files to offer until you pick new ones.")
    val sendingTo: String get() = v("sendingTo", "Sending to %s")
    val transferring: String get() = v("transferring", "Transferring")
    val serviceRunning: String get() = v("serviceRunning", "LSEx is running")
    val serviceReady: String get() = v("serviceReady", "Ready to send and receive files on this network")
    val stop: String get() = v("stop", "Stop")
    val thisDevice: String get() = v("thisDevice", "This device")
    val tapAvatarToChange: String get() = v("tapAvatarToChange", "tap the avatar to change it")
    val receivingLabel: String get() = v("receivingLabel", "Receiving")
}

internal val EnStrings = UiStrings(emptyMap())

internal val EsStrings = UiStrings(mapOf(
    "send" to "Enviar",
    "receive" to "Recibir",
    "settings" to "Ajustes",
    "connect" to "Conectar",
    "nearbyDevices" to "Dispositivos cercanos",
    "selection" to "Selección",
    "readyToSend" to "Listo para enviar",
    "history" to "Historial",
    "searching" to "Buscando dispositivos…",
    "troubleshoot" to "Solución de problemas",
    "accept" to "Aceptar",
    "add" to "Añadir",
    "addIpAddress" to "Añadir dirección IP",
    "addDeviceByIp" to "Añadir dispositivo por IP",
    "addFiles" to "Añadir archivos",
    "addText" to "Añadir texto",
    "advancedSettings" to "Ajustes avanzados",
    "alwaysOnTop" to "Siempre visible",
    "animations" to "Animaciones",
    "appearance" to "Apariencia",
    "autoFinish" to "Finalización automática",
    "autoAcceptWebUploads" to "Aceptar subidas web automáticamente",
    "back" to "Atrás",
    "band" to "Banda",
    "browseStorage" to "Explorar tu almacenamiento",
    "browserDownloadRequest" to "Solicitud de descarga del navegador",
    "browsersCanOpen" to "Los navegadores de tu red pueden abrir:",
    "browseEllipsis" to "Explorar…",
    "cancel" to "Cancelar",
    "cancelSend" to "Cancelar envío",
    "cancelThisSend" to "Cancelar este envío",
    "changeAvatar" to "Cambiar avatar",
    "chooseFiles" to "Elegir archivos",
    "chooseFolder" to "Elegir carpeta",
    "chooseYourAvatar" to "Elige tu avatar",
    "chunkedTransfers" to "Transferencias por partes",
    "clear" to "Borrar",
    "close" to "Cerrar",
    "colorScheme" to "Esquema de color",
    "comingInFutureUpdate" to "Disponible en una actualización futura",
    "connectLabel" to "Conectar",
    "connected" to "Conectado",
    "connection" to "Conexión",
    "copy" to "Copiar",
    "copyAddress" to "Copiar dirección",
    "copyDownloadLink" to "Copiar enlace de descarga",
    "copyText" to "Copiar texto",
    "createChecksums" to "Crear sumas de verificación",
    "decline" to "Rechazar",
    "desktop" to "Escritorio",
    "destinationFolder" to "Carpeta de destino",
    "destinationFolderPath" to "Ruta de la carpeta de destino",
    "details" to "Detalles",
    "deviceInfo" to "Información del dispositivo",
    "deviceModel" to "Modelo del dispositivo",
    "deviceModelHint" to "Modelo del dispositivo (vacío = automático)",
    "deviceName" to "Nombre del dispositivo",
    "deviceNameAlias" to "Nombre del dispositivo (alias)",
    "deviceType" to "Tipo de dispositivo",
    "devicesFindEachOther" to "Los dispositivos en la misma red se encuentran automáticamente.",
    "discard" to "Descartar",
    "discoveryTimeout" to "Tiempo de descubrimiento (segundos)",
    "dismiss" to "Descartar",
    "donate" to "Donar",
    "done" to "Listo",
    "downloadApi" to "API de descarga",
    "editText" to "Editar texto",
    "encryptionHttps" to "Cifrado (HTTPS)",
    "everyVersionWithNotes" to "Todas las versiones, con notas",
    "failed" to "Falló",
    "favorite" to "Favorito",
    "favoriteDevices" to "Dispositivos favoritos",
    "favorites" to "Favoritos",
    "filter" to "Filtrar",
    "fingerprint" to "Huella digital",
    "general" to "General",
    "grant" to "Conceder",
    "groupNamePasswordBand" to "Nombre del grupo, contraseña y banda",
    "hideSystem" to "Ocultar del sistema",
    "historyLabel" to "Historial",
    "hotspot" to "Punto de acceso",
    "hotspotIsOn" to "El punto de acceso está activo",
    "hotspotSettings" to "Ajustes del punto de acceso",
    "ipAddressHint" to "Dirección IP (opcionalmente IP:puerto)",
    "inFileBrowser" to "En el explorador de archivos",
    "installPackage" to "Instalar este paquete",
    "language" to "Idioma",
    "linkSharing" to "Compartir por enlace",
    "shareAppSubtitle" to "Enlace, Bluetooth o la página de versiones",
    "litecoin" to "Litecoin (LTC)",
    "loading" to "Cargando…",
    "materialYou" to "Material You",
    "mediaAccessNeeded" to "Se necesita acceso a los medios",
    "message" to "Mensaje",
    "minimizeToTray" to "Minimizar a la bandeja",
    "moreSendOptions" to "Más opciones de envío",
    "nearbyNetworks" to "Redes cercanas",
    "network" to "Red",
    "noFilesSharedYet" to "Aún no se comparte ningún archivo.",
    "nothingHereYet" to "Nada por aquí todavía.",
    "nothingTransferring" to "No se está transfiriendo nada ahora.",
    "nothingYetFinished" to "Nada todavía — las transferencias terminadas aparecen aquí.",
    "ok" to "Aceptar",
    "on" to "Activado",
    "onlyForThisFolder" to "Solo para esta carpeta",
    "open" to "Abrir",
    "openFile" to "Abrir archivo",
    "openReleasesPage" to "Abrir la página de versiones",
    "opensShareSheetBluetooth" to "Abre el menú de compartir — elige Bluetooth",
    "otherDevicesCanFind" to "Otros dispositivos pueden encontrarte y enviarte archivos",
    "pin" to "PIN",
    "pinHint" to "PIN (vacío = sin PIN)",
    "password" to "Contraseña",
    "passwordHint" to "Contraseña (vacío = ninguna / del sistema)",
    "paste" to "Pegar",
    "pasteFromClipboard" to "Pegar del portapapeles",
    "pause" to "Pausar",
    "pausedChunkedNote" to "En pausa — los archivos por partes continúan donde se detuvieron.",
    "photosVideosGalleryNote" to "Las fotos y videos van a Pictures / Movies",
    "pickDeviceFirst" to "Elige primero un dispositivo — el texto va al dispositivo seleccionado.",
    "pickFilesToSend" to "Elige archivos para enviar",
    "pickFilesSystemPicker" to "Elige archivos con el selector del sistema",
    "playInSystemPlayer" to "Reproducir en el reproductor del sistema",
    "port" to "Puerto",
    "preview" to "Vista previa",
    "quickSave" to "Guardado rápido",
    "quickShare" to "Quick Share",
    "quickShareTile" to "Mosaico de Quick Share",
    "receiveQueueOrder" to "Orden de la cola de recepción",
    "receiveViaLink" to "Recibir por enlace",
    "recheck" to "Volver a comprobar",
    "removeFavorite" to "Quitar de favoritos",
    "removeFromHistory" to "Quitar del historial",
    "rename" to "Renombrar",
    "renameEdit" to "Renombrar / editar",
    "renameFile" to "Renombrar archivo",
    "reorder" to "Reordenar",
    "requirePin" to "Requerir PIN",
    "restart" to "Reiniciar",
    "restartServerToApply" to "Reinicia el servidor para aplicar",
    "resume" to "Reanudar",
    "retry" to "Reintentar",
    "retryAutomatically" to "Reintentar automáticamente",
    "retryThisFile" to "Reintentar este archivo",
    "retryWithPin" to "Reintentar con PIN",
    "reverseOrder" to "Invertir orden",
    "save" to "Guardar",
    "saveAdvancedNetwork" to "Guardar ajustes avanzados de red",
    "saveDestination" to "Guardar destino",
    "saveModel" to "Guardar modelo",
    "saveName" to "Guardar nombre",
    "savePortAndPin" to "Guardar puerto y PIN",
    "saveToGallery" to "Guardar en la galería",
    "saveToHistory" to "Guardar en el historial",
    "saveWindowPlacement" to "Guardar la posición de la ventana",
    "scanCode" to "Escanear un código",
    "scanCodeToJoin" to "Escanea un código para unirte a una red",
    "scanAgain" to "Escanear de nuevo",
    "scanForNetworks" to "Buscar redes",
    "searchAgain" to "Buscar de nuevo",
    "searchApps" to "Buscar aplicaciones",
    "searchMusic" to "Buscar música",
    "searchThisFolder" to "Buscar en esta carpeta",
    "secured" to "Segura",
    "selected" to "Seleccionado",
    "sendLabel" to "Enviar",
    "sendText" to "Enviar un texto",
    "sendToMultiple" to "Enviar a varios dispositivos",
    "sendViaLink" to "Enviar por enlace",
    "sending" to "Enviando",
    "server" to "Servidor",
    "setupConnection" to "Configura tu conexión",
    "share" to "Compartir",
    "shareLsex" to "Compartir LSEx",
    "shareViaBluetooth" to "Compartir por Bluetooth",
    "shareViaLink" to "Compartir por enlace",
    "shareViaLinkWebNote" to "Página web y subidas por enlace",
    "showHiddenFilesLabel" to "Mostrar archivos ocultos",
    "skipAcceptPromptEveryone" to "Saltar la confirmación — para todos,",
    "skipPromptWebUploads" to "Saltar la confirmación para archivos subidos desde la web",
    "someFilesNeedAttention" to "Algunos archivos necesitan atención — toca para revisar",
    "sort" to "Ordenar",
    "splashAndMotion" to "Pantalla de inicio y movimiento expresivo",
    "stageForResending" to "Preparar para reenviar",
    "startWifiDirect" to "Iniciar Wi-Fi Direct",
    "startHotspot" to "Iniciar punto de acceso",
    "stopWifiDirect" to "Detener Wi-Fi Direct",
    "stopHotspot" to "Detener punto de acceso",
    "storage" to "Almacenamiento",
    "supportProject" to "Apoya el proyecto",
    "textStagedNote" to "El texto se prepara como un archivo — elige los dispositivos arriba y envíalo a todos.",
    "deviceAppearsNote" to "El dispositivo aparece en la lista cuando responde.",
    "folderNoFiles" to "Esta carpeta no tiene archivos para enviar.",
    "folderEmpty" to "Esta carpeta está vacía.",
    "transfer" to "Transferir",
    "transfers" to "Transferencias",
    "troubleshootDiscovery" to "Solucionar el descubrimiento",
    "turnOn" to "Activar",
    "turnOnWebSharing" to "Activar el uso compartido web",
    "turnWebSharingOff" to "Desactivar el uso compartido web",
    "unfavorite" to "Quitar de favoritos",
    "unknownDevice" to "Dispositivo desconocido",
    "useDownloads" to "Usar Descargas",
    "verifyChecksums" to "Verificar sumas",
    "wallpaperColorsNote" to "Colores del fondo de pantalla para el tema de la marca",
    "wifiDirect" to "Wi-Fi Direct",
    "wifiDirectGroupOn" to "El grupo de Wi-Fi Direct está activo",
    "wifiDirectSettings" to "Ajustes de Wi-Fi Direct",
    "writeMessage" to "Escribe un mensaje",
    "yourDevice" to "Tu dispositivo",
    "model" to "Modelo",
    "type" to "Tipo",
    "lastSeen" to "Visto por última vez",
    "address" to "Dirección",
    "shareAppIntro" to "Envíale a un amigo el enlace de descarga — LSEx es gratis y funciona con la app oficial de LocalSend.",
    "copied" to "Copiado",
    "active" to "Activos",
    "queued" to "En cola",
    "filesProgress" to "%s/%s archivos",
    "savedCount" to "%s guardados",
    "itemsCount" to "%s elementos",
    "wantsToSend" to "%s quiere enviar",
    "etaLabel" to "ETA %s",
    "joinNetwork" to "Unirse a “%s”",
    "messageFrom" to "Mensaje de %s",
    "musicCount" to "Música (%s)",
    "myAppsCount" to "Mis aplicaciones (%s)",
    "networkName" to "Red: %s",
    "passwordValue" to "Contraseña: %s",
    "resumeTo" to "Reanudar · %s a %s",
    "textTo" to "Texto para %s",
    "toDevice" to "Para %s",
    "totalFrom" to "Total %s · de %s",
    "doneCount" to "Listo · %s",
    "networks" to "Redes",
    "hotspotGroupSubtitle" to "Nombre, contraseña y banda de tu punto de acceso",
    "anyoneCanOffer" to "Cualquiera en la red puede ofrecer archivos",
    "folderItems" to "Carpeta · %s elementos",
    "locationOffNote" to "La ubicación está desactivada — Android oculta las redes cercanas y puede bloquear Wi-Fi Direct mientras está desactivada.",
    "startHotspotNote" to "Inicia un punto de acceso en este dispositivo — otros dispositivos se unen y te encuentran, sin necesidad de router.",
    "hotspotAssignedNote" to "Android asigna el nombre y la contraseña del punto de acceso por sí mismo — las apps normales no pueden elegirlos. Inicia el punto de acceso y la tarjeta de conexión muestra el nombre y la contraseña que realmente se aplicaron, con un código QR para unirse.",
    "serveFilesNote" to "Sirve archivos a cualquier navegador de esta red — no hace falta ninguna app en el otro lado.",
    "identityAnnouncedNote" to "El nombre, el modelo y el tipo se anuncian cuando el servidor se inicia",
    "quickTileNote" to "Añade el mosaico de LSEx desde el editor del panel de ajustes rápidos para compartir con un toque.",
    "advancedSectionsNote" to "Muestra las secciones avanzadas, como la app oficial",
    "quickSaveNote" to "Salta la confirmación de recepción — para todos, o solo para favoritos.",
    "autoFinishNote" to "Cierra el progreso de recepción cuando termina una transferencia",
    "verifyChecksumsNote" to "Rechaza archivos cuyo SHA-256 no coincida",
    "shareViaLinkAutoAcceptNote" to "Las subidas por enlace se saltan la confirmación",
    "createChecksumsNote" to "SHA-256 para cada archivo enviado (más lento con archivos enormes)",
    "autoRetryNote" to "Los envíos fallidos esperan y reintentan solos",
    "chunkedNote" to "Partes reanudables de 1 MiB entre dispositivos LSEx",
    "httpsNote" to "Certificado autofirmado, verificado por huella",
    "multicastGroupLabel" to "Grupo multicast (vacío = 224.0.0.167)",
    "wantsToDownloadFiles" to "%s · %s quiere descargar los archivos que compartes por enlace.",
    "bulletSameWifi" to "• Ambos dispositivos deben estar en la misma Wi-Fi / LAN (no en una red de invitados).",
    "bulletVpn" to "• Desactiva las VPN y el «aislamiento AP» del router — bloquean el descubrimiento local.",
    "bulletKeepAwake" to "• Mantén el otro dispositivo despierto con LocalSend abierto y pulsa el botón de actualizar.",
    "tourHint" to "Desliza para ver las acciones · toca dos veces para cerrar",
    "clearHistoryTitle" to "¿Borrar el historial?",
    "clearHistoryText" to "Se eliminarán todas las entradas del historial. Los archivos se quedan donde están.",
    "clearSelectionTitle" to "¿Vaciar la selección?",
    "clearSelectionText" to "Se quitarán todos los archivos y textos preparados de la selección.",
    "clearWebShareTitle" to "¿Dejar de compartir estos archivos?",
    "clearWebShareText" to "La página web no tendrá archivos para ofrecer hasta que elijas otros.",
    "sendingTo" to "Enviando a %s",
    "transferring" to "Transfiriendo",
    "serviceRunning" to "LSEx está en ejecución",
    "serviceReady" to "Listo para enviar y recibir archivos en esta red",
    "stop" to "Detener",
    "thisDevice" to "Este dispositivo",
    "tapAvatarToChange" to "toca el avatar para cambiarlo",
    "receivingLabel" to "Recibiendo",
))

private val UiStringsByCode: Map<String, UiStrings> = mapOf(
    "en" to EnStrings,
    "es" to EsStrings,
    "ar" to UiStrings(mapOf("send" to "إرسال", "receive" to "استقبال", "settings" to "الإعدادات", "connect" to "اتصال", "nearbyDevices" to "الأجهزة القريبة", "selection" to "التحديد", "readyToSend" to "جاهز للإرسال", "history" to "السجل", "searching" to "البحث عن الأجهزة…", "troubleshoot" to "استكشاف الأخطاء وإصلاحها")),
    "az" to UiStrings(mapOf("send" to "Göndər", "receive" to "Qəbul et", "settings" to "Parametrlər", "connect" to "Qoşul", "nearbyDevices" to "Yaxınlıqdakı cihazlar", "selection" to "Seçim", "readyToSend" to "Göndərməyə hazırdır", "history" to "Tarixçə", "searching" to "Cihazlar axtarılır…", "troubleshoot" to "Problemlərin həlli")),
    "be" to UiStrings(mapOf("send" to "Адправіць", "receive" to "Прыняць", "settings" to "Налады", "connect" to "Падключыцца", "nearbyDevices" to "Блізкія прылады", "selection" to "Выбар", "readyToSend" to "Гатова да адпраўкі", "history" to "Гісторыя", "searching" to "Пошук прылад…", "troubleshoot" to "Усуненне непаладак")),
    "bg" to UiStrings(mapOf("send" to "Изпращане", "receive" to "Получаване", "settings" to "Настройки", "connect" to "Свързване", "nearbyDevices" to "Устройства наблизо", "selection" to "Избор", "readyToSend" to "Готово за изпращане", "history" to "История", "searching" to "Търсене на устройства…", "troubleshoot" to "Отстраняване на неизправности")),
    "bn" to UiStrings(mapOf("send" to "পাঠান", "receive" to "গ্রহণ করুন", "settings" to "সেটিংস", "connect" to "সংযোগ করুন", "nearbyDevices" to "কাছের ডিভাইস", "selection" to "নির্বাচন", "readyToSend" to "পাঠানোর জন্য প্রস্তুত", "history" to "ইতিহাস", "searching" to "ডিভাইস খোঁজা হচ্ছে…", "troubleshoot" to "সমস্যা সমাধান")),
    "ca" to UiStrings(mapOf("send" to "Envia", "receive" to "Rep", "settings" to "Configuració", "connect" to "Connecta", "nearbyDevices" to "Dispositius propers", "selection" to "Selecció", "readyToSend" to "A punt per enviar", "history" to "Historial", "searching" to "Cercant dispositius…", "troubleshoot" to "Resolució de problemes")),
    "cs" to UiStrings(mapOf("send" to "Odeslat", "receive" to "Přijmout", "settings" to "Nastavení", "connect" to "Připojit", "nearbyDevices" to "Zařízení v okolí", "selection" to "Výběr", "readyToSend" to "Připraveno k odeslání", "history" to "Historie", "searching" to "Hledání zařízení…", "troubleshoot" to "Řešení potíží")),
    "da" to UiStrings(mapOf("send" to "Send", "receive" to "Modtag", "settings" to "Indstillinger", "connect" to "Forbind", "nearbyDevices" to "Enheder i nærheden", "selection" to "Valg", "readyToSend" to "Klar til at sende", "history" to "Historik", "searching" to "Søger efter enheder…", "troubleshoot" to "Fejlfinding")),
    "de" to UiStrings(mapOf("send" to "Senden", "receive" to "Empfangen", "settings" to "Einstellungen", "connect" to "Verbinden", "nearbyDevices" to "Geräte in der Nähe", "selection" to "Auswahl", "readyToSend" to "Bereit zum Senden", "history" to "Verlauf", "searching" to "Geräte werden gesucht…", "troubleshoot" to "Fehlerbehebung")),
    "el" to UiStrings(mapOf("send" to "Αποστολή", "receive" to "Λήψη", "settings" to "Ρυθμίσεις", "connect" to "Σύνδεση", "nearbyDevices" to "Κοντινές συσκευές", "selection" to "Επιλογή", "readyToSend" to "Έτοιμο για αποστολή", "history" to "Ιστορικό", "searching" to "Αναζήτηση συσκευών…", "troubleshoot" to "Αντιμετώπιση προβλημάτων")),
    "et" to UiStrings(mapOf("send" to "Saada", "receive" to "Võta vastu", "settings" to "Seaded", "connect" to "Ühenda", "nearbyDevices" to "Läheduses olevad seadmed", "selection" to "Valik", "readyToSend" to "Saatmiseks valmis", "history" to "Ajalugu", "searching" to "Seadmete otsimine…", "troubleshoot" to "Tõrkeotsing")),
    "eu" to UiStrings(mapOf("send" to "Bidali", "receive" to "Jaso", "settings" to "Ezarpenak", "connect" to "Konektatu", "nearbyDevices" to "Inguruko gailuak", "selection" to "Hautaketa", "readyToSend" to "Bidaltzeko prest", "history" to "Historia", "searching" to "Gailuak bilatzen…", "troubleshoot" to "Arazoak konpontzea")),
    "fa" to UiStrings(mapOf("send" to "ارسال", "receive" to "دریافت", "settings" to "تنظیمات", "connect" to "اتصال", "nearbyDevices" to "دستگاه‌های نزدیک", "selection" to "انتخاب", "readyToSend" to "آماده ارسال", "history" to "تاریخچه", "searching" to "در حال جستجوی دستگاه‌ها…", "troubleshoot" to "عیب‌یابی")),
    "fi" to UiStrings(mapOf("send" to "Lähetä", "receive" to "Vastaanota", "settings" to "Asetukset", "connect" to "Yhdistä", "nearbyDevices" to "Lähellä olevat laitteet", "selection" to "Valinta", "readyToSend" to "Valmis lähetettäväksi", "history" to "Historia", "searching" to "Etsitään laitteita…", "troubleshoot" to "Vianmääritys")),
    "fil" to UiStrings(mapOf("send" to "Ipadala", "receive" to "Tanggapin", "settings" to "Mga Setting", "connect" to "Kumonekta", "nearbyDevices" to "Mga kalapit na device", "selection" to "Pinili", "readyToSend" to "Handang ipadala", "history" to "Kasaysayan", "searching" to "Naghahanap ng mga device…", "troubleshoot" to "Pag-troubleshoot")),
    "fr" to UiStrings(mapOf("send" to "Envoyer", "receive" to "Recevoir", "settings" to "Paramètres", "connect" to "Connecter", "nearbyDevices" to "Appareils à proximité", "selection" to "Sélection", "readyToSend" to "Prêt à envoyer", "history" to "Historique", "searching" to "Recherche d'appareils…", "troubleshoot" to "Dépannage")),
    "ga" to UiStrings(mapOf("send" to "Seol", "receive" to "Faigh", "settings" to "Socruithe", "connect" to "Ceangail", "nearbyDevices" to "Gléasanna in aice láimhe", "selection" to "Roghnúchán", "readyToSend" to "Réidh le seoladh", "history" to "Stair", "searching" to "Ag cuardach gléasanna…", "troubleshoot" to "Fabhtcheartú")),
    "gl" to UiStrings(mapOf("send" to "Enviar", "receive" to "Recibir", "settings" to "Axustes", "connect" to "Conectar", "nearbyDevices" to "Dispositivos próximos", "selection" to "Selección", "readyToSend" to "Listo para enviar", "history" to "Historial", "searching" to "Buscando dispositivos…", "troubleshoot" to "Resolución de problemas")),
    "gu" to UiStrings(mapOf("send" to "મોકલો", "receive" to "પ્રાપ્ત કરો", "settings" to "સેટિંગ્સ", "connect" to "કનેક્ટ કરો", "nearbyDevices" to "નજીકના ઉપકરણો", "selection" to "પસંદગી", "readyToSend" to "મોકલવા માટે તૈયાર", "history" to "ઇતિહાસ", "searching" to "ઉપકરણો શોધી રહ્યાં છે…", "troubleshoot" to "મુશ્કેલીનિવારણ")),
    "he" to UiStrings(mapOf("send" to "שליחה", "receive" to "קבלה", "settings" to "הגדרות", "connect" to "התחברות", "nearbyDevices" to "מכשירים בקרבת מקום", "selection" to "בחירה", "readyToSend" to "מוכן לשליחה", "history" to "היסטוריה", "searching" to "מחפש מכשירים…", "troubleshoot" to "פתרון בעיות")),
    "hi" to UiStrings(mapOf("send" to "भेजें", "receive" to "प्राप्त करें", "settings" to "सेटिंग्स", "connect" to "कनेक्ट करें", "nearbyDevices" to "आस-पास के डिवाइस", "selection" to "चयन", "readyToSend" to "भेजने के लिए तैयार", "history" to "इतिहास", "searching" to "डिवाइस खोजे जा रहे हैं…", "troubleshoot" to "समस्या निवारण")),
    "hu" to UiStrings(mapOf("send" to "Küldés", "receive" to "Fogadás", "settings" to "Beállítások", "connect" to "Csatlakozás", "nearbyDevices" to "Közeli eszközök", "selection" to "Kijelölés", "readyToSend" to "Küldésre kész", "history" to "Előzmények", "searching" to "Eszközök keresése…", "troubleshoot" to "Hibaelhárítás")),
    "hy" to UiStrings(mapOf("send" to "Ուղարկել", "receive" to "Ստանալ", "settings" to "Կարգավորումներ", "connect" to "Միանալ", "nearbyDevices" to "Մոտակա սարքեր", "selection" to "Ընտրություն", "readyToSend" to "Պատրաստ է ուղարկման", "history" to "Պատմություն", "searching" to "Սարքերի որոնում…", "troubleshoot" to "Խնդիրների լուծում")),
    "id" to UiStrings(mapOf("send" to "Kirim", "receive" to "Terima", "settings" to "Pengaturan", "connect" to "Hubungkan", "nearbyDevices" to "Perangkat di sekitar", "selection" to "Pilihan", "readyToSend" to "Siap dikirim", "history" to "Riwayat", "searching" to "Mencari perangkat…", "troubleshoot" to "Pemecahan masalah")),
    "it" to UiStrings(mapOf("send" to "Invia", "receive" to "Ricevi", "settings" to "Impostazioni", "connect" to "Connetti", "nearbyDevices" to "Dispositivi vicini", "selection" to "Selezione", "readyToSend" to "Pronto per l'invio", "history" to "Cronologia", "searching" to "Ricerca dispositivi…", "troubleshoot" to "Risoluzione dei problemi")),
    "ja" to UiStrings(mapOf("send" to "送信", "receive" to "受信", "settings" to "設定", "connect" to "接続", "nearbyDevices" to "近くのデバイス", "selection" to "選択", "readyToSend" to "送信の準備ができました", "history" to "履歴", "searching" to "デバイスを検索中…", "troubleshoot" to "トラブルシューティング")),
    "km" to UiStrings(mapOf("send" to "ផ្ញើ", "receive" to "ទទួល", "settings" to "ការកំណត់", "connect" to "ភ្ជាប់", "nearbyDevices" to "ឧបករណ៍នៅជិត", "selection" to "ការជ្រើសរើស", "readyToSend" to "រួចរាល់ដើម្បីផ្ញើ", "history" to "ប្រវត្តិ", "searching" to "កំពុងស្វែងរកឧបករណ៍…", "troubleshoot" to "ការដោះស្រាយបញ្ហា")),
    "ko" to UiStrings(mapOf("send" to "보내기", "receive" to "받기", "settings" to "설정", "connect" to "연결", "nearbyDevices" to "주변 기기", "selection" to "선택", "readyToSend" to "보낼 준비 완료", "history" to "기록", "searching" to "기기 검색 중…", "troubleshoot" to "문제 해결")),
    "ky" to UiStrings(mapOf("send" to "Жөнөтүү", "receive" to "Кабыл алуу", "settings" to "Орнотуулар", "connect" to "Туташуу", "nearbyDevices" to "Жакындагы түзмөктөр", "selection" to "Тандама", "readyToSend" to "Жөнөтүүгө даяр", "history" to "Тарых", "searching" to "Түзмөктөр изделип жатат…", "troubleshoot" to "Каталарды оңдоо")),
    "lo" to UiStrings(mapOf("send" to "ສົ່ງ", "receive" to "ຮັບ", "settings" to "ການຕັ້ງຄ່າ", "connect" to "ເຊື່ອມຕໍ່", "nearbyDevices" to "ອຸປະກອນໃກ້ຄຽງ", "selection" to "ການເລືອກ", "readyToSend" to "ພ້ອມສົ່ງ", "history" to "ປະຫວັດ", "searching" to "ກຳລັງຄົ້ນຫາອຸປະກອນ…", "troubleshoot" to "ການແກ້ໄຂບັນຫາ")),
    "ml" to UiStrings(mapOf("send" to "അയയ്ക്കുക", "receive" to "സ്വീകരിക്കുക", "settings" to "ക്രമീകരണങ്ങൾ", "connect" to "കണക്റ്റ് ചെയ്യുക", "nearbyDevices" to "സമീപത്തെ ഉപകരണങ്ങൾ", "selection" to "തിരഞ്ഞെടുപ്പ്", "readyToSend" to "അയയ്ക്കാൻ തയ്യാറാണ്", "history" to "ചരിത്രം", "searching" to "ഉപകരണങ്ങൾ തിരയുന്നു…", "troubleshoot" to "ട്രബിൾഷൂട്ടിംഗ്")),
    "mn" to UiStrings(mapOf("send" to "Илгээх", "receive" to "Хүлээн авах", "settings" to "Тохиргоо", "connect" to "Холбогдох", "nearbyDevices" to "Ойролцоох төхөөрөмжүүд", "selection" to "Сонголт", "readyToSend" to "Илгээхэд бэлэн", "history" to "Түүх", "searching" to "Төхөөрөмж хайж байна…", "troubleshoot" to "Алдаа засах")),
    "ms" to UiStrings(mapOf("send" to "Hantar", "receive" to "Terima", "settings" to "Tetapan", "connect" to "Sambung", "nearbyDevices" to "Peranti berdekatan", "selection" to "Pilihan", "readyToSend" to "Sedia untuk dihantar", "history" to "Sejarah", "searching" to "Mencari peranti…", "troubleshoot" to "Penyelesaian masalah")),
    "ne" to UiStrings(mapOf("send" to "पठाउनुहोस्", "receive" to "प्राप्त गर्नुहोस्", "settings" to "सेटिङहरू", "connect" to "जडान गर्नुहोस्", "nearbyDevices" to "नजिकका उपकरणहरू", "selection" to "चयन", "readyToSend" to "पठाउन तयार", "history" to "इतिहास", "searching" to "उपकरणहरू खोज्दै…", "troubleshoot" to "समस्या समाधान")),
    "nl" to UiStrings(mapOf("send" to "Verzenden", "receive" to "Ontvangen", "settings" to "Instellingen", "connect" to "Verbinden", "nearbyDevices" to "Apparaten in de buurt", "selection" to "Selectie", "readyToSend" to "Klaar om te verzenden", "history" to "Geschiedenis", "searching" to "Zoeken naar apparaten…", "troubleshoot" to "Probleemoplossing")),
    "pl" to UiStrings(mapOf("send" to "Wyślij", "receive" to "Odbierz", "settings" to "Ustawienia", "connect" to "Połącz", "nearbyDevices" to "Urządzenia w pobliżu", "selection" to "Wybór", "readyToSend" to "Gotowe do wysłania", "history" to "Historia", "searching" to "Wyszukiwanie urządzeń…", "troubleshoot" to "Rozwiązywanie problemów")),
    "pt-BR" to UiStrings(mapOf("send" to "Enviar", "receive" to "Receber", "settings" to "Configurações", "connect" to "Conectar", "nearbyDevices" to "Dispositivos próximos", "selection" to "Seleção", "readyToSend" to "Pronto para enviar", "history" to "Histórico", "searching" to "Procurando dispositivos…", "troubleshoot" to "Solução de problemas")),
    "pt-PT" to UiStrings(mapOf("send" to "Enviar", "receive" to "Receber", "settings" to "Definições", "connect" to "Ligar", "nearbyDevices" to "Dispositivos próximos", "selection" to "Seleção", "readyToSend" to "Pronto para enviar", "history" to "Histórico", "searching" to "A procurar dispositivos…", "troubleshoot" to "Resolução de problemas")),
    "ro" to UiStrings(mapOf("send" to "Trimite", "receive" to "Primește", "settings" to "Setări", "connect" to "Conectare", "nearbyDevices" to "Dispozitive din apropiere", "selection" to "Selecție", "readyToSend" to "Gata de trimis", "history" to "Istoric", "searching" to "Se caută dispozitive…", "troubleshoot" to "Depanare")),
    "ru" to UiStrings(mapOf("send" to "Отправить", "receive" to "Получить", "settings" to "Настройки", "connect" to "Подключиться", "nearbyDevices" to "Устройства рядом", "selection" to "Выбор", "readyToSend" to "Готово к отправке", "history" to "История", "searching" to "Поиск устройств…", "troubleshoot" to "Устранение неполадок")),
    "si" to UiStrings(mapOf("send" to "යවන්න", "receive" to "ලබාගන්න", "settings" to "සැකසීම්", "connect" to "සම්බන්ධ වන්න", "nearbyDevices" to "අසල උපාංග", "selection" to "තේරීම", "readyToSend" to "යැවීමට සූදානම්", "history" to "ඉතිහාසය", "searching" to "උපාංග සොයමින්…", "troubleshoot" to "දෝශ නිරාකරණය")),
    "sk" to UiStrings(mapOf("send" to "Odoslať", "receive" to "Prijať", "settings" to "Nastavenia", "connect" to "Pripojiť", "nearbyDevices" to "Zariadenia v okolí", "selection" to "Výber", "readyToSend" to "Pripravené na odoslanie", "history" to "História", "searching" to "Hľadanie zariadení…", "troubleshoot" to "Riešenie problémov")),
    "sl" to UiStrings(mapOf("send" to "Pošlji", "receive" to "Prejmi", "settings" to "Nastavitve", "connect" to "Poveži", "nearbyDevices" to "Naprave v bližini", "selection" to "Izbira", "readyToSend" to "Pripravljeno na pošiljanje", "history" to "Zgodovina", "searching" to "Iskanje naprav…", "troubleshoot" to "Odpravljanje težav")),
    "sr" to UiStrings(mapOf("send" to "Пошаљи", "receive" to "Прими", "settings" to "Подешавања", "connect" to "Повежи", "nearbyDevices" to "Уређаји у близини", "selection" to "Избор", "readyToSend" to "Спремно за слање", "history" to "Историја", "searching" to "Тражење уређаја…", "troubleshoot" to "Решавање проблема")),
    "sr-Cyrl" to UiStrings(mapOf("send" to "Пошаљи", "receive" to "Прими", "settings" to "Подешавања", "connect" to "Повежи", "nearbyDevices" to "Уређаји у близини", "selection" to "Избор", "readyToSend" to "Спремно за слање", "history" to "Историја", "searching" to "Тражење уређаја…", "troubleshoot" to "Решавање проблема")),
    "sv" to UiStrings(mapOf("send" to "Skicka", "receive" to "Ta emot", "settings" to "Inställningar", "connect" to "Anslut", "nearbyDevices" to "Enheter i närheten", "selection" to "Val", "readyToSend" to "Redo att skicka", "history" to "Historik", "searching" to "Söker efter enheter…", "troubleshoot" to "Felsökning")),
    "ta" to UiStrings(mapOf("send" to "அனுப்பு", "receive" to "பெறு", "settings" to "அமைப்புகள்", "connect" to "இணை", "nearbyDevices" to "அருகிலுள்ள சாதனங்கள்", "selection" to "தேர்வு", "readyToSend" to "அனுப்பத் தயார்", "history" to "வரலாறு", "searching" to "சாதனங்களைத் தேடுகிறது…", "troubleshoot" to "சரிசெய்தல்")),
    "th" to UiStrings(mapOf("send" to "ส่ง", "receive" to "รับ", "settings" to "การตั้งค่า", "connect" to "เชื่อมต่อ", "nearbyDevices" to "อุปกรณ์ใกล้เคียง", "selection" to "รายการที่เลือก", "readyToSend" to "พร้อมส่ง", "history" to "ประวัติ", "searching" to "กำลังค้นหาอุปกรณ์…", "troubleshoot" to "การแก้ปัญหา")),
    "tr" to UiStrings(mapOf("send" to "Gönder", "receive" to "Al", "settings" to "Ayarlar", "connect" to "Bağlan", "nearbyDevices" to "Yakındaki cihazlar", "selection" to "Seçim", "readyToSend" to "Göndermeye hazır", "history" to "Geçmiş", "searching" to "Cihazlar aranıyor…", "troubleshoot" to "Sorun giderme")),
    "uk" to UiStrings(mapOf("send" to "Надіслати", "receive" to "Отримати", "settings" to "Налаштування", "connect" to "Підключитися", "nearbyDevices" to "Пристрої поблизу", "selection" to "Вибір", "readyToSend" to "Готово до надсилання", "history" to "Історія", "searching" to "Пошук пристроїв…", "troubleshoot" to "Усунення несправностей")),
    "ur" to UiStrings(mapOf("send" to "بھیجیں", "receive" to "وصول کریں", "settings" to "ترتیبات", "connect" to "منسلک کریں", "nearbyDevices" to "قریبی آلات", "selection" to "انتخاب", "readyToSend" to "بھیجنے کے لیے تیار", "history" to "تاریخ", "searching" to "آلات تلاش کیے جا رہے ہیں…", "troubleshoot" to "خرابیوں کا ازالہ")),
    "uz" to UiStrings(mapOf("send" to "Yuborish", "receive" to "Qabul qilish", "settings" to "Sozlamalar", "connect" to "Ulanish", "nearbyDevices" to "Yaqin atrofdagi qurilmalar", "selection" to "Tanlov", "readyToSend" to "Yuborishga tayyor", "history" to "Tarix", "searching" to "Qurilmalar qidirilmoqda…", "troubleshoot" to "Muammolarni bartaraf etish")),
    "vi" to UiStrings(mapOf("send" to "Gửi", "receive" to "Nhận", "settings" to "Cài đặt", "connect" to "Kết nối", "nearbyDevices" to "Thiết bị lân cận", "selection" to "Đã chọn", "readyToSend" to "Sẵn sàng gửi", "history" to "Lịch sử", "searching" to "Đang tìm thiết bị…", "troubleshoot" to "Khắc phục sự cố")),
    "zh-CN" to UiStrings(mapOf("send" to "发送", "receive" to "接收", "settings" to "设置", "connect" to "连接", "nearbyDevices" to "附近的设备", "selection" to "已选内容", "readyToSend" to "准备发送", "history" to "历史记录", "searching" to "正在搜索设备…", "troubleshoot" to "故障排除")),
    "zh-HK" to UiStrings(mapOf("send" to "傳送", "receive" to "接收", "settings" to "設定", "connect" to "連接", "nearbyDevices" to "附近的裝置", "selection" to "選取項目", "readyToSend" to "準備傳送", "history" to "記錄", "searching" to "正在搜尋裝置…", "troubleshoot" to "疑難排解")),
    "zh-TW" to UiStrings(mapOf("send" to "傳送", "receive" to "接收", "settings" to "設定", "connect" to "連線", "nearbyDevices" to "附近的裝置", "selection" to "選取項目", "readyToSend" to "準備傳送", "history" to "歷史記錄", "searching" to "正在搜尋裝置…", "troubleshoot" to "疑難排解")),
)

/** Language picker entries: code → the language's own name for itself. */
internal val UiLanguageOptions: List<Pair<String, String>> = listOf(
    "ar" to "العربية",
    "az" to "Azərbaycan",
    "be" to "Беларуская",
    "bg" to "Български",
    "bn" to "বাংলা",
    "ca" to "Català",
    "cs" to "Čeština",
    "da" to "Dansk",
    "de" to "Deutsch",
    "el" to "Ελληνικά",
    "en" to "English",
    "es" to "Español",
    "et" to "Eesti",
    "eu" to "Euskara",
    "fa" to "فارسی",
    "fi" to "Suomi",
    "fil" to "Filipino",
    "fr" to "Français",
    "ga" to "Gaeilge",
    "gl" to "Galego",
    "gu" to "ગુજરાતી",
    "he" to "עברית",
    "hi" to "हिन्दी",
    "hu" to "Magyar",
    "hy" to "Հայերեն",
    "id" to "Bahasa Indonesia",
    "it" to "Italiano",
    "ja" to "日本語",
    "km" to "ខ្មែរ",
    "ko" to "한국어",
    "ky" to "Кыргызча",
    "lo" to "ລາວ",
    "ml" to "മലയാളം",
    "mn" to "Монгол",
    "ms" to "Bahasa Melayu",
    "ne" to "नेपाली",
    "nl" to "Nederlands",
    "pl" to "Polski",
    "pt-BR" to "Português (Brasil)",
    "pt-PT" to "Português (Portugal)",
    "ro" to "Română",
    "ru" to "Русский",
    "si" to "සිංහල",
    "sk" to "Slovenčina",
    "sl" to "Slovenščina",
    "sr" to "Српски",
    "sr-Cyrl" to "Српски (ћирилица)",
    "sv" to "Svenska",
    "ta" to "தமிழ்",
    "th" to "ไทย",
    "tr" to "Türkçe",
    "uk" to "Українська",
    "ur" to "اردو",
    "uz" to "O'zbek",
    "vi" to "Tiếng Việt",
    "zh-CN" to "简体中文",
    "zh-HK" to "繁體中文（香港）",
    "zh-TW" to "繁體中文（台灣）",
)

/**
 * Resolves a settings language value (or the system language for
 * "system") to a string table: exact code, then the base language with
 * a sensible regional default, then English.
 */
internal fun uiStringsFor(language: String): UiStrings {
    val code = if (language == "system" || language.isEmpty()) {
        systemLanguageCode()
    } else {
        language
    }
    UiStringsByCode[code]?.let { return it }
    val base = code.substringBefore('-').substringBefore('_')
    UiStringsByCode[base]?.let { return it }
    return when (base) {
        "pt" -> UiStringsByCode.getValue("pt-BR")
        "zh" -> UiStringsByCode.getValue("zh-CN")
        else -> EnStrings
    }
}

internal val LocalStrings = staticCompositionLocalOf { EnStrings }

internal fun stringsFor(language: String): UiStrings = uiStringsFor(language)

/** Fill %s placeholders in order (Kotlin-common replacement for format). */
internal fun String.fill(vararg args: Any): String {
    var out = this
    for (arg in args) out = out.replaceFirst("%s", arg.toString())
    return out
}

/** Notification strings for the Android service layer (public bridge). */
public data class NotifStrings(
    val channelName: String,
    val receiving: String,
    val sending: String,
    val sendingTo: String,
    val transferring: String,
    val pause: String,
    val resume: String,
    val serviceTitle: String,
    val serviceText: String,
    val stop: String,
)

public fun notifStringsFor(language: String): NotifStrings {
    val s = stringsFor(language)
    return NotifStrings(
        channelName = s.transfers,
        receiving = s.receivingLabel,
        sending = s.sending,
        sendingTo = s.sendingTo,
        transferring = s.transferring,
        pause = s.pause,
        resume = s.resume,
        serviceTitle = s.serviceRunning,
        serviceText = s.serviceReady,
        stop = s.stop,
    )
}
