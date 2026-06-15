package one.only.player.feature.player.service.playback

import one.only.player.core.data.models.VideoState
import one.only.player.core.data.repository.fake.FakeMediaRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class PlaybackStateCoordinatorTest {

    @Test
    fun mergeVideoState_fillsMissingFieldsWithoutOverridingPrimaryState() {
        val coordinator = PlaybackStateCoordinator(FakeMediaRepository())
        val primaryState = videoState(
            path = "remote:smb:1:/Movies/Episode.mkv",
            position = 10_000L,
            audioTrackIndex = null,
            subtitleTrackIndex = 2,
            playbackSpeed = null,
            videoScale = 1f,
            subtitleDelayMilliseconds = 0L,
            subtitleSpeed = 1f,
        )
        val fallbackState = videoState(
            path = "content://media/external/video/media/42",
            position = 30_000L,
            audioTrackIndex = 1,
            subtitleTrackIndex = 3,
            playbackSpeed = 1.25f,
            videoScale = 1.4f,
            subtitleDelayMilliseconds = 250L,
            subtitleSpeed = 0.9f,
        )

        val mergedState = coordinator.mergeVideoState(primaryState, fallbackState)!!

        assertEquals(10_000L, mergedState.position)
        assertEquals(1, mergedState.audioTrackIndex)
        assertEquals(2, mergedState.subtitleTrackIndex)
        assertEquals(1.25f, mergedState.playbackSpeed ?: 0f, 0.0001f)
        assertEquals(1.4f, mergedState.videoScale, 0.0001f)
        assertEquals(250L, mergedState.subtitleDelayMilliseconds)
        assertEquals(0.9f, mergedState.subtitleSpeed, 0.0001f)
    }

    private fun videoState(
        path: String,
        position: Long?,
        audioTrackIndex: Int?,
        subtitleTrackIndex: Int?,
        playbackSpeed: Float?,
        videoScale: Float,
        subtitleDelayMilliseconds: Long,
        subtitleSpeed: Float,
    ): VideoState = VideoState(
        path = path,
        position = position,
        audioTrackIndex = audioTrackIndex,
        subtitleTrackIndex = subtitleTrackIndex,
        playbackSpeed = playbackSpeed,
        externalSubs = emptyList(),
        videoScale = videoScale,
        subtitleDelayMilliseconds = subtitleDelayMilliseconds,
        subtitleSpeed = subtitleSpeed,
    )
}
