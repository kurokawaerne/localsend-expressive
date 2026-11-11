package dev.kurokawa.localsend.expressive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.kurokawa.localsend.expressive.app.LocalSendService
import dev.kurokawa.localsend.expressive.platform.AppContextHolder
import dev.kurokawa.localsend.expressive.ui.AppRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // androidMain platform code (multicast lock, storage, SAF) needs a Context.
        AppContextHolder.appContext = applicationContext
        val service = LocalSendService()
        setContent {
            AppRoot(service)
        }
    }
}
