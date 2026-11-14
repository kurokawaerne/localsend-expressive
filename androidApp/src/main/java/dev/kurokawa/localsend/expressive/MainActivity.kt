package dev.kurokawa.localsend.expressive

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dev.kurokawa.localsend.expressive.platform.AppContextHolder
import dev.kurokawa.localsend.expressive.platform.PermissionGate
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
        // ONE splash (v0.6.0): the system splash, held until the app's
        // first real frame is composed — no second in-app splash on top.
        var ready = false
        val splash = installSplashScreen()
        splash.setKeepOnScreenCondition { !ready }
        super.onCreate(savedInstanceState)
        // Insurance: never trap the user on the splash.
        Handler(Looper.getMainLooper()).postDelayed({ ready = true }, 5_000)
        // Edge-to-edge: the floating nav pill floats over the content;
        // nothing paints a strip behind it.
        enableEdgeToEdge()
        // androidMain platform code (multicast lock, storage, SAF) needs a Context.
        AppContextHolder.appContext = applicationContext
        PermissionGate.launcher = { permissions -> multiplePermissions.launch(permissions) }
        // Share intents live in ShareActivity since v0.10.0.
        // Persistent receive service + notification + wakelock (the
        // official app runs the same way; v0.3.0 had none of this).
        LsexForegroundService.start(applicationContext)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        val service = Engine.service
        // The notification's Stop action lands here: shut the transfer
        // engine down together with the foreground service.
        LsexForegroundService.onStopRequested = { service.stop() }
        setContent {
            AppRoot(service, onReady = { ready = true })
        }
    }
}
