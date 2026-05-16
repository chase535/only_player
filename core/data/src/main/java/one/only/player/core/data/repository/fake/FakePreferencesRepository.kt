package one.only.player.core.data.repository.fake

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import one.only.player.core.data.repository.PreferencesRepository
import one.only.player.core.model.ApplicationPreferences
import one.only.player.core.model.PlayerPreferences
import one.only.player.core.model.SettingsBackup

class FakePreferencesRepository : PreferencesRepository {

    private val applicationPreferencesStateFlow = MutableStateFlow(ApplicationPreferences())
    private val playerPreferencesStateFlow = MutableStateFlow(PlayerPreferences())

    override val applicationPreferences: StateFlow<ApplicationPreferences>
        get() = applicationPreferencesStateFlow
    override val playerPreferences: StateFlow<PlayerPreferences>
        get() = playerPreferencesStateFlow

    override suspend fun updateApplicationPreferences(
        transform: suspend (ApplicationPreferences) -> ApplicationPreferences,
    ) {
        applicationPreferencesStateFlow.update { transform.invoke(it) }
    }

    override suspend fun updatePlayerPreferences(
        transform: suspend (PlayerPreferences) -> PlayerPreferences,
    ) {
        playerPreferencesStateFlow.update { transform.invoke(it) }
    }

    override suspend fun exportSettings(): SettingsBackup = SettingsBackup(
        applicationPreferences = applicationPreferencesStateFlow.value,
        playerPreferences = playerPreferencesStateFlow.value,
    )

    override suspend fun importSettings(settingsBackup: SettingsBackup) {
        applicationPreferencesStateFlow.value = settingsBackup.applicationPreferences
        playerPreferencesStateFlow.value = settingsBackup.playerPreferences
    }

    override suspend fun resetPreferences() {
        applicationPreferencesStateFlow.update { ApplicationPreferences() }
        playerPreferencesStateFlow.update { PlayerPreferences() }
    }
}
