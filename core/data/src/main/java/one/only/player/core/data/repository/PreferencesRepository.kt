package one.only.player.core.data.repository

import kotlinx.coroutines.flow.StateFlow
import one.only.player.core.model.ApplicationPreferences
import one.only.player.core.model.PlayerPreferences
import one.only.player.core.model.SettingsBackup

interface PreferencesRepository {

    val applicationPreferences: StateFlow<ApplicationPreferences>

    val playerPreferences: StateFlow<PlayerPreferences>

    suspend fun updateApplicationPreferences(
        transform: suspend (ApplicationPreferences) -> ApplicationPreferences,
    )

    suspend fun updatePlayerPreferences(transform: suspend (PlayerPreferences) -> PlayerPreferences)

    suspend fun exportSettings(): SettingsBackup

    suspend fun importSettings(settingsBackup: SettingsBackup)

    suspend fun resetPreferences()
}
