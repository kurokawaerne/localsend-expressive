package dev.kurokawa.localsend.expressive

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import dev.kurokawa.localsend.expressive.platform.AppContextHolder
import dev.kurokawa.localsend.expressive.platform.offerSharedUris

/**
 * Share-intent entry point (v0.10.0 activity split): other apps hand
 * us files via ACTION_SEND / ACTION_SEND_MULTIPLE here — NOT in
 * MainActivity, so the share flow never drags the main UI up over the
 * sending app. We stage the files, bring OUR main activity forward
 * in OUR task, and finish.
 */
class ShareActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppContextHolder.appContext = applicationContext
        handleShareIntent(intent)
        startActivity(
            Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT),
        )
        finish()
    }

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
