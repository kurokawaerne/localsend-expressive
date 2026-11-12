package dev.kurokawa.localsend.expressive

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import dev.kurokawa.localsend.expressive.platform.QuickShareSignal

/**
 * Quick Settings tile ("quick share kinda style", Ernest's request):
 * one tap opens LSEx on the Send screen with the file picker ready.
 */
class QuickShareTileService : TileService() {
    override fun onStartListening() {
        super.onStartListening()
        qsTile?.let { tile ->
            tile.state = Tile.STATE_ACTIVE
            tile.updateTile()
        }
    }

    override fun onClick() {
        super.onClick()
        // Bump the in-process signal; the Send screen consumes it and opens
        // the picker once. The activity launch below makes sure the UI is
        // in front (and the process exists to receive the signal).
        QuickShareSignal.requests.value += 1
        val intent = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val pending = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
            startActivityAndCollapse(pending)
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }
}
