package dev.kurokawa.localsend.expressive

import dev.kurokawa.localsend.expressive.app.LocalSendService

/**
 * The one transfer engine for the whole process (v0.10.0 activity
 * split): MainActivity, ShareActivity, TransferActivity and the
 * foreground service's Live Update notifier must all observe and
 * drive the SAME LocalSendService — before the split, the activity
 * created its own instance and nobody else could reach it.
 */
object Engine {
    val service: LocalSendService by lazy { LocalSendService() }
}
