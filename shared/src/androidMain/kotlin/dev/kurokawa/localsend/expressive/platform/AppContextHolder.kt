package dev.kurokawa.localsend.expressive.platform

import android.content.Context

/**
 * Set once from MainActivity so androidMain code (multicast lock, file
 * storage, SAF reads) can reach a Context without passing it everywhere.
 */
object AppContextHolder {
    @Volatile
    var appContext: Context? = null
}
