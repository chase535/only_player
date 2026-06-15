package one.only.player.core.domain

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import one.only.player.core.data.repository.fake.FakeMediaRepository
import one.only.player.core.data.repository.fake.FakePreferencesRepository
import one.only.player.core.model.Video
import org.junit.Assert.assertEquals
import org.junit.Test

class GetSortedVideosUseCaseTest {

    @Test
    fun invoke_excludesConfiguredFolderDescendantsWithoutHidingPrefixSiblings() = runTest {
        val mediaRepository = FakeMediaRepository()
        val preferencesRepository = FakePreferencesRepository()
        val useCase = GetSortedVideosUseCase(
            mediaRepository = mediaRepository,
            preferencesRepository = preferencesRepository,
            defaultDispatcher = StandardTestDispatcher(testScheduler),
        )
        preferencesRepository.updateApplicationPreferences {
            it.copy(excludeFolders = listOf("/storage/emulated/0/Movies/Private"))
        }
        mediaRepository.videos.addAll(
            listOf(
                testVideo(
                    id = 1L,
                    path = "/storage/emulated/0/Movies/Private/Hidden.mp4",
                    name = "Hidden.mp4",
                ),
                testVideo(
                    id = 2L,
                    path = "/storage/emulated/0/Movies/Privateer/VisiblePrefix.mp4",
                    name = "VisiblePrefix.mp4",
                ),
                testVideo(
                    id = 3L,
                    path = "/storage/emulated/0/Movies/Public/VisiblePublic.mp4",
                    name = "VisiblePublic.mp4",
                ),
            ),
        )

        val videos = useCase().first()

        assertEquals(
            listOf(
                "/storage/emulated/0/Movies/Privateer/VisiblePrefix.mp4",
                "/storage/emulated/0/Movies/Public/VisiblePublic.mp4",
            ),
            videos.map(Video::path),
        )
    }

    @Test
    fun invoke_hidesRecycleBinVideosFromLibraryAndReturnsThemForRecycleBin() = runTest {
        val mediaRepository = FakeMediaRepository()
        val preferencesRepository = FakePreferencesRepository()
        val useCase = GetSortedVideosUseCase(
            mediaRepository = mediaRepository,
            preferencesRepository = preferencesRepository,
            defaultDispatcher = StandardTestDispatcher(testScheduler),
        )
        val activeVideo = testVideo(
            id = 1L,
            path = "/storage/emulated/0/Movies/Active.mp4",
            name = "Active.mp4",
        )
        val recycledVideo = testVideo(
            id = 2L,
            path = "/storage/emulated/0/Movies/Recycled.mp4",
            name = "Recycled.mp4",
        )
        preferencesRepository.updateApplicationPreferences {
            it.copy(isRecycleBinEnabled = true)
        }
        mediaRepository.videos.addAll(listOf(activeVideo, recycledVideo))
        mediaRepository.moveVideosToRecycleBin(listOf(recycledVideo.uriString))

        val libraryVideos = useCase().first()
        val recycleBinVideos = useCase(isRecycleBinOnly = true).first()

        assertEquals(listOf(activeVideo.uriString), libraryVideos.map(Video::uriString))
        assertEquals(listOf(recycledVideo.uriString), recycleBinVideos.map(Video::uriString))

        mediaRepository.restoreVideosFromRecycleBin(listOf(recycledVideo.uriString))

        assertEquals(
            listOf(activeVideo.uriString, recycledVideo.uriString),
            useCase().first().map(Video::uriString),
        )
    }

    private fun testVideo(
        id: Long,
        path: String,
        name: String,
    ): Video = Video(
        id = id,
        path = path,
        parentPath = path.substringBeforeLast('/'),
        duration = 1_000L,
        uriString = "content://media/external/video/media/$id",
        nameWithExtension = name,
        width = 1920,
        height = 1080,
        size = 1_000L,
    )
}
