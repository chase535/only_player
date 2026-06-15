package one.only.player.settings.screens.player

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import one.only.player.core.data.repository.fake.FakePreferencesRepository
import one.only.player.core.model.LastPlayerScreenOrientation
import one.only.player.core.model.ScreenOrientation
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerPreferencesViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun updatePreferredPlayerOrientation_clearsRememberedOrientation() = runTest(dispatcher) {
        val repository = FakePreferencesRepository()
        repository.updatePlayerPreferences {
            it.copy(
                playerScreenOrientation = ScreenOrientation.VIDEO_ORIENTATION,
                shouldRememberPlayerScreenOrientation = true,
                lastPlayerScreenOrientation = LastPlayerScreenOrientation.LANDSCAPE,
            )
        }
        val viewModel = PlayerPreferencesViewModel(repository)

        viewModel.onEvent(PlayerPreferencesUiEvent.UpdatePreferredPlayerOrientation(ScreenOrientation.PORTRAIT))
        advanceUntilIdle()

        val preferences = repository.playerPreferences.value
        assertEquals(ScreenOrientation.PORTRAIT, preferences.playerScreenOrientation)
        assertNull(preferences.lastPlayerScreenOrientation)
    }

    @Test
    fun toggleRememberPlayerScreenOrientation_clearsStaleRememberedOrientation() = runTest(dispatcher) {
        val repository = FakePreferencesRepository()
        repository.updatePlayerPreferences {
            it.copy(
                shouldRememberPlayerScreenOrientation = true,
                lastPlayerScreenOrientation = LastPlayerScreenOrientation.PORTRAIT,
            )
        }
        val viewModel = PlayerPreferencesViewModel(repository)

        viewModel.onEvent(PlayerPreferencesUiEvent.ToggleRememberPlayerScreenOrientation)
        advanceUntilIdle()

        val preferences = repository.playerPreferences.value
        assertFalse(preferences.shouldRememberPlayerScreenOrientation)
        assertNull(preferences.lastPlayerScreenOrientation)
    }
}
