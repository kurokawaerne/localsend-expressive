package dev.kurokawa.localsend.expressive

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import dev.kurokawa.localsend.expressive.app.TransferStatus
import dev.kurokawa.localsend.expressive.ui.notifStringsFor
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

    /** UI language code (settings); read on every snapshot. */
    @Volatile
    var language: String = "system"

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
                notifStringsFor(language).channelName,
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
                val paused = openJobs.any { job ->
                    job.paused && job.files.any { it.status != TransferStatus.DONE }
                }
                val percent = if (total > 0) {
                    ((done.toDouble() / total) * 100).toInt().coerceIn(0, 100)
                } else {
                    0
                }
                val ns = notifStringsFor(language)
                val label = when {
                    incoming.isNotEmpty() && sendFiles.isEmpty() -> ns.receiving
                    openJobs.size == 1 ->
                        ns.sendingTo.replace("%s", openJobs.first().targetAlias)
                    sendFiles.isNotEmpty() -> ns.sending
                    else -> ns.transferring
                }
                Snapshot(
                    // The notification lives only while bytes are
                    // actually moving or a job is paused mid-transfer.
                    // v0.11.0 kept it alive while any unfinished job or
                    // any (stale, completed) receive snapshot existed,
                    // so a finished transfer sat at 100% forever.
                    active = moving || paused,
                    paused = paused && !moving,
                    percent = percent,
                    label = label,
                )
            }
                .distinctUntilChanged()
                .collect { snapshot ->
                    // A notification failure must NEVER kill the app:
                    // v0.10.0 crashed here (bubble PendingIntent had to
                    // be mutable) and the persisted outbox re-triggered
                    // the post on every relaunch — a crash loop.
                    runCatching {
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
    }

    private fun buildNotification(snapshot: Snapshot): android.app.Notification {
        // Tap (v0.12.4): the whole app + its transfers sheet, via
        // MainActivity — NOT the sheet-only TransferActivity (that
        // stays the bubble target below, where a sheet is the point).
        val openTransfer = PendingIntent.getActivity(
            context,
            10,
            Intent(context, MainActivity::class.java)
                .putExtra(MainActivity.EXTRA_OPEN_TRANSFERS, true)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        // Android REQUIRES the intent attached to a bubble to be
        // mutable — with FLAG_IMMUTABLE the NotificationManager throws
        // IllegalArgumentException ("PendingIntents attached to bubbles
        // must be mutable") and the notification is never posted.
        val bubbleIntent = PendingIntent.getActivity(
            context,
            11,
            Intent(context, TransferActivity::class.java),
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
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
        val ns = notifStringsFor(language)
        if (snapshot.paused) {
            val resume = PendingIntent.getService(
                context,
                12,
                Intent(context, LsexForegroundService::class.java)
                    .setAction(LsexForegroundService.ACTION_RESUME_ALL),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
            builder.addAction(0, ns.resume, resume)
        } else {
            val pause = PendingIntent.getService(
                context,
                13,
                Intent(context, LsexForegroundService::class.java)
                    .setAction(LsexForegroundService.ACTION_PAUSE_ALL),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
            builder.addAction(0, ns.pause, pause)
        }
        return builder.build()
    }

    companion object {
        private const val CHANNEL_ID = "lsex_transfers"
        private const val NOTIFICATION_ID = 42
    }
}
