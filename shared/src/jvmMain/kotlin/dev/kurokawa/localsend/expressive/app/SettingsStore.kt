package dev.kurokawa.localsend.expressive.app

import dev.kurokawa.localsend.expressive.platform.platformDataDir
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File

/** Tiny file-backed persistence for settings and history (M5). */
class SettingsStore {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }

    private val settingsFile: File get() = File(platformDataDir(), "settings.json")
    private val historyFile: File get() = File(platformDataDir(), "history.json")

    fun loadSettings(): AppSettings =
        runCatching {
            if (settingsFile.isFile) json.decodeFromString<AppSettings>(settingsFile.readText())
            else AppSettings()
        }.getOrDefault(AppSettings())

    fun saveSettings(settings: AppSettings) {
        runCatching { settingsFile.writeText(json.encodeToString<AppSettings>(settings)) }
    }

    fun loadHistory(): List<HistoryEntry> =
        runCatching {
            if (historyFile.isFile) {
                json.decodeFromString(ListSerializer(HistoryEntry.serializer()), historyFile.readText())
            } else {
                emptyList()
            }
        }.getOrDefault(emptyList())

    fun saveHistory(history: List<HistoryEntry>) {
        runCatching {
            historyFile.writeText(
                json.encodeToString(ListSerializer(HistoryEntry.serializer()), history.take(200)),
            )
        }
    }
}
