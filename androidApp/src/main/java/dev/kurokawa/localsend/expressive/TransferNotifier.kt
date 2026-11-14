package dev.kurokawa.localsend.expressive

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import dev.kurokawa.localsend.expressive.app.TransferStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * Live Updates for transfers (v0.10.0, Ernest's point 6): while files
 * move, a PROMOTED ongoing notification (Android 16 Live Updates)
 * shows the aggregate progress with the percentage in the status-bar
 * chip. Built on androidx.core's ProgressStyle: ongoing, standard
 * style, no custom views — the promotion requirements. On systems
 * without promotion it degrades to a normal ongoing notification.
 *
 * The action button is Pause XOR Resume, never both (Ernest flagged
 * the both-at-once bug in UDYAT): Pause shows while anything is
 * actively moving, Resume while a job is paused.
 *
 * The same notification carries BubbleMetadata targeting
 * TransferActivity, so a transfer can be bubbled the classic way.
 */
class TransferNotifier(private val context: Context) {

    private data class Snapshot(
        val active: Boolean,
        val paused: Boolean,
        val percent: Int,
        val label: String,
    )

    fun start(scope: CoroutineScope) {
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            android.app.NotificationChannel(
                CHANNEL_ID,
                "Transfers",
                NotificationManager.IMPORTANCE_DEFAULT,
            ),
        )
        val service = Engine.service
        scope.launch {
            combine(service.outbox, service.incomingProgress) { jobs, incoming ->
                val openJobs = jobs.filter { !it.finished }
                val sendFiles = openJobs.flatMap { it.files }
                val total = sendFiles.sumOf { it.size } + incoming.sumOf { it.total }
                val done = sendFiles.sumOf { it.confirmedBytes } + incoming.sumOf { it.done }
                val moving = sendFiles.any { it.status == TransferStatus.ACTIVE } ||
                    incoming.any {
                        it.status == TransferStatus.ACTIVE ||
                            it.status == TransferStatus.VERIFYING
                    }
                val paused = openJobs.any { it.paused }
                val percent = if (total > 0) {
                    ((done.toDouble() / total) * 100).toInt().coerceIn(0, 100)
                } else {
                    0
                }
                val label = when {
                    incoming.isNotEmpty() && sendFiles.isEmpty() -> "Receiving"
                    openJobs.size == 1 -> "Sending to ${openJobs.first().targetAlias}"
                    sendFiles.isNotEmpty() -> "Sending"
                    else -> "Transferring"
                }
                Snapshot(
                    active = moving || paused || openJobs.isNotEmpty() || incoming.isNotEmpty(),
                    paused = paused && !moving,
                    percent = percent,
                    label = label,
                )
            }
                .distinctUntilChanged()
                .collect { snapshot ->
                    if (!snapshot.active) {
                        manager.cancel(NOTIFICATION_ID)
                    } else {
                        manager.notify(
                            NOTIFICATION_ID,
                            buildNotification(snapshot),
                        )
                    }
                }
        }
    }

    private fun buildNotification(snapshot: Snapshot): android.app.Notification {
        val openTransfer = PendingIntent.getActivity(
            context,
            10,
            Intent(context, TransferActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val bubbleIntent = PendingIntent.getActivity(
            context,
            11,
            Intent(context, TransferActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_monochrome)
            .setContentTitle(snapshot.label)
            .setContentText("${snapshot.percent}%")
            .setContentIntent(openTransfer)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setRequestPromotedOngoing(true)
            .setShortCriticalText("${snapshot.percent}%")
            .setStyle(
                NotificationCompat.ProgressStyle()
                    .setProgress(snapshot.percent)
                    .setProgressTrackerIcon(
                        IconCompat.createWithResource(
                            context,
                            R.drawable.ic_launcher_monochrome,
                        ),
                    ),
            )
            .setBubbleMetadata(
                NotificationCompat.BubbleMetadata.Builder(
                    bubbleIntent,
                    IconCompat.createWithResource(
                        context,
                        R.drawable.ic_launcher_monochrome,
                    ),
                )
                    .setDesiredHeight(600)
                    .build(),
            )
        if (snapshot.paused) {
            val resume = PendingIntent.getService(
                context,
                12,
                Intent(context, LsexForegroundService::class.java)
                    .setAction(LsexForegroundService.ACTION_RESUME_ALL),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
            builder.addAction(0, "Resume", resume)
        } else {
            val pause = PendingIntent.getService(
                context,
                13,
                Intent(context, LsexForegroundService::class.java)
                    .setAction(LsexForegroundService.ACTION_PAUSE_ALL),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
            builder.addAction(0, "Pause", pause)
        }
        return builder.build()
    }

    companion object {
        private const val CHANNEL_ID = "lsex_transfers"
        private const val NOTIFICATION_ID = 42
    }
}
