package dev.kurokawa.localsend.expressive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.kurokawa.localsend.expressive.ui.AppRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO(M1): acquire the WifiManager.MulticastLock here and start
        // discovery + the receive server (foreground service) for the session.
        setContent {
            AppRoot()
        }
    }
}
