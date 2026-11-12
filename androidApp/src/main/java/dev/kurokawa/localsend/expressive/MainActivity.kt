package dev.kurokawa.localsend.expressive

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dev.kurokawa.localsend.expressive.app.LocalSendService
import dev.kurokawa.localsend.expressive.platform.AppContextHolder
import dev.kurokawa.localsend.expressive.platform.PermissionGate
import dev.kurokawa.localsend.expressive.platform.offerSharedUris
import dev.kurokawa.localsend.expressive.ui.AppRoot

class MainActivity : ComponentActivity() {
    private val notificationPermission =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.RequestPermission()) {}

    // Shared-module platform code (hotspot / Wi-Fi Direct toggles) asks for
    // runtime permissions through this bridge.
    private val multiplePermissions =
        registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions(),
        ) { grants -> PermissionGate.onResult(grants) }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Animated splash (core-splashscreen): must be installed before
        // super.onCreate; the theme's postSplashScreenTheme takes over after.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        // androidMain platform code (multicast lock, storage, SAF) needs a Context.
        AppContextHolder.appContext = applicationContext
        PermissionGate.launcher = { permissions -> multiplePermissions.launch(permissions) }
        handleShareIntent(intent)
        // Persistent receive service + notification + wakelock (the
        // official app runs the same way; v0.3.0 had none of this).
        LsexForegroundService.start(applicationContext)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        val service = LocalSendService()
        // The notification's Stop action lands here: shut the transfer
        // engine down together with the foreground service.
        LsexForegroundService.onStopRequested = { service.stop() }
        setContent {
            AppRoot(service)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleShareIntent(intent)
    }

    /** ACTION_SEND / ACTION_SEND_MULTIPLE from other apps → send selection. */
    @Suppress("DEPRECATION")
    private fun handleShareIntent(intent: Intent?) {
        val uris: List<Uri> = when (intent?.action) {
            Intent.ACTION_SEND ->
                listOfNotNull(intent.getParcelableExtra(Intent.EXTRA_STREAM))
            Intent.ACTION_SEND_MULTIPLE ->
                intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
                    ?: emptyList()
            else -> emptyList()
        }
        if (uris.isNotEmpty()) offerSharedUris(applicationContext, uris)
    }
}
