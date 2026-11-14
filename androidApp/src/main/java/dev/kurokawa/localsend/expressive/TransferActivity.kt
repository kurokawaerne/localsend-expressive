package dev.kurokawa.localsend.expressive

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.kurokawa.localsend.expressive.platform.AppContextHolder
import dev.kurokawa.localsend.expressive.theme.LocalSendExpressiveTheme
import dev.kurokawa.localsend.expressive.ui.TransferSheet

/**
 * The transfer view as its own activity (v0.10.0): opened from the
 * transfer notification / Live Update, and declared
 * allowEmbedded + documentLaunchMode="always" so transfers can also
 * open as a classic notification BUBBLE. Android 17's app bubbles
 * need no API — this activity follows the multi-window rules
 * (resizable, state survives configuration changes).
 */
class TransferActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppContextHolder.appContext = applicationContext
        val service = Engine.service
        service.start()
        setContent {
            val settings by service.settings.collectAsState()
            LocalSendExpressiveTheme(settings) {
                TransferSheet(
                    controller = service,
                    settings = settings,
                    onDismiss = { finish() },
                    onGoToShare = {
                        startActivity(
                            Intent(this, MainActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT),
                        )
                        finish()
                    },
                )
            }
        }
    }
}
