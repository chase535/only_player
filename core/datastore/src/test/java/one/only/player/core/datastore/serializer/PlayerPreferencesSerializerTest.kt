package one.only.player.core.datastore.serializer

import androidx.datastore.core.CorruptionException
import java.io.ByteArrayInputStream
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerPreferencesSerializerTest {

    @Test(expected = CorruptionException::class)
    fun readFrom_throwsForLegacyPlayerPreferencesJson() {
        runBlocking {
            val legacyJson = """
                {
                    "rememberPlayerBrightness": true,
                    "autoplay": false,
                    "useSwipeControls": false,
                    "enableVolumeSwipeGesture": false,
                    "pauseOnHeadsetDisconnect": false,
                    "useSystemCaptionStyle": true,
                    "subtitleTextSize": 16.0,
                    "shouldUseLibass": true
                }
            """.trimIndent()

            PlayerPreferencesSerializer.readFrom(
                ByteArrayInputStream(legacyJson.encodeToByteArray()),
            )
        }
    }

    @Test
    fun readFrom_enablesAdjustedLegacyVideoFilters() = runBlocking {
        val currentJson = """
            {
                "shouldApplyVideoFilters": true,
                "videoBrightness": 0.25,
                "videoSharpening": 0.5
            }
        """.trimIndent()

        val result = PlayerPreferencesSerializer.readFrom(
            ByteArrayInputStream(currentJson.encodeToByteArray()),
        )

        assertEquals(true, result.isVideoBrightnessFilterEnabled)
        assertEquals(false, result.isVideoContrastFilterEnabled)
        assertEquals(true, result.isVideoSharpeningFilterEnabled)
        assertEquals(0.25f, result.videoBrightness)
        assertEquals(0.5f, result.videoSharpening)
    }

    @Test
    fun readFrom_migratesMissingVideoFilterEnabledKeysIndividually() = runBlocking {
        val currentJson = """
            {
                "shouldApplyVideoFilters": true,
                "isVideoBrightnessFilterEnabled": false,
                "videoBrightness": 0.25,
                "videoContrast": 0.5
            }
        """.trimIndent()

        val result = PlayerPreferencesSerializer.readFrom(
            ByteArrayInputStream(currentJson.encodeToByteArray()),
        )

        assertEquals(false, result.isVideoBrightnessFilterEnabled)
        assertEquals(true, result.isVideoContrastFilterEnabled)
        assertEquals(0.25f, result.videoBrightness)
        assertEquals(0.5f, result.videoContrast)
    }

    @Test
    fun readFrom_keepsExplicitDisabledVideoFilters() = runBlocking {
        val currentJson = """
            {
                "shouldApplyVideoFilters": true,
                "isVideoBrightnessFilterEnabled": false,
                "videoBrightness": 0.25,
                "isVideoSharpeningFilterEnabled": true,
                "videoSharpening": 0.5
            }
        """.trimIndent()

        val result = PlayerPreferencesSerializer.readFrom(
            ByteArrayInputStream(currentJson.encodeToByteArray()),
        )

        assertEquals(false, result.isVideoBrightnessFilterEnabled)
        assertEquals(true, result.isVideoSharpeningFilterEnabled)
        assertEquals(0.25f, result.videoBrightness)
        assertEquals(0.5f, result.videoSharpening)
    }

    @Test
    fun readFrom_readsCurrentPlayerPreferencesJson() = runBlocking {
        val currentJson = """
            {
                "shouldRememberPlayerBrightness": true,
                "shouldAutoPlay": false,
                "subtitleTextSize": 16
            }
        """.trimIndent()

        val result = PlayerPreferencesSerializer.readFrom(
            ByteArrayInputStream(currentJson.encodeToByteArray()),
        )

        assertEquals(true, result.shouldRememberPlayerBrightness)
        assertEquals(false, result.shouldAutoPlay)
        assertEquals(16, result.subtitleTextSize)
    }
}
