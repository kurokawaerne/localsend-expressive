package dev.kurokawa.localsend.expressive

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.widget.Toast

/**
 * Receives PackageInstaller session results for the built-in installer.
 * On STATUS_PENDING_USER_ACTION the system hands us the confirmation
 * intent, which we launch — that's the standard install prompt.
 */
class InstallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, -999)
        val label = intent.getStringExtra("package_label") ?: "package"
        when (status) {
            PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                @Suppress("DEPRECATION")
                val confirm = intent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
                if (confirm != null) {
                    context.startActivity(confirm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }
            }
            PackageInstaller.STATUS_SUCCESS ->
                Toast.makeText(context, "Installed $label", Toast.LENGTH_LONG).show()
            else -> {
                val message = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
                Toast.makeText(
                    context,
                    "Install of $label didn't finish: ${message ?: "unknown error"}",
                    Toast.LENGTH_LONG,
                ).show()
            }
        }
    }
}
