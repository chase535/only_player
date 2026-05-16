package one.only.player.core.domain

import java.util.Date
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import one.only.player.core.data.repository.fake.FakeMediaRepository
import one.only.player.core.data.repository.fake.FakePreferencesRepository
import one.only.player.core.model.Folder
import one.only.player.core.model.Video
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchMediaUseCaseTest {

    private val mediaRepository = FakeMediaRepository()
    private val preferencesRepository = FakePreferencesRepository()

    @Test
    fun searchMedia_blankQuery_returnsEmptyResults() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val useCase = SearchMediaUseCase(
            getSortedVideosUseCase = GetSortedVideosUseCase(mediaRepository, preferencesRepository, dispatcher),
            getSortedFoldersUseCase = GetSortedFoldersUseCase(mediaRepository, preferencesRepository, dispatcher),
            defaultDispatcher = dispatcher,
        )

        val results = useCase("   ").first()

        assertTrue(results.isEmpty)
        assertEquals(0, results.totalCount)
    }

    @Test
    fun searchMedia_ranksExactPhraseBeforeOrderedAndUnorderedMatches() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val useCase = SearchMediaUseCase(
            getSortedVideosUseCase = GetSortedVideosUseCase(mediaRepository, preferencesRepository, dispatcher),
            getSortedFoldersUseCase = GetSortedFoldersUseCase(mediaRepository, preferencesRepository, dispatcher),
            defaultDispatcher = dispatcher,
        )

        mediaRepository.directories.addAll(
            listOf(
                testFolder(
                    name = "Stranger 2019",
                    path = "/videos/stranger-2019",
                    videos = listOf(testVideo(name = "FolderVideo1.mp4", path = "/videos/stranger-2019/FolderVideo1.mp4")),
                ),
                testFolder(
                    name = "Stranger Things 2019",
                    path = "/videos/stranger-things-2019",
                    videos = listOf(testVideo(name = "FolderVideo2.mp4", path = "/videos/stranger-things-2019/FolderVideo2.mp4")),
                ),
                testFolder(
                    name = "2019 Stranger Archive",
                    path = "/videos/2019-stranger-archive",
                    videos = listOf(testVideo(name = "FolderVideo3.mp4", path = "/videos/2019-stranger-archive/FolderVideo3.mp4")),
                ),
            ),
        )
        mediaRepository.videos.addAll(
            listOf(
                testVideo(name = "Stranger 2019.mkv", path = "/videos/Stranger 2019.mkv"),
                testVideo(name = "Stranger Things 2019.mkv", path = "/videos/Stranger Things 2019.mkv"),
                testVideo(name = "2019 Stranger Archive.mkv", path = "/videos/2019 Stranger Archive.mkv"),
            ),
        )

        val results = useCase("  stranger 2019  ").first()

        assertEquals(
            listOf("Stranger 2019", "Stranger Things 2019", "2019 Stranger Archive"),
            results.folders.map(Folder::name),
        )
        assertEquals(
            listOf("Stranger 2019.mkv", "Stranger Things 2019.mkv", "2019 Stranger Archive.mkv"),
            results.videos.map(Video::nameWithExtension),
        )
        assertEquals(6, results.totalCount)
    }

    private fun testFolder(name: String, path: String, videos: List<Video>): Folder = Folder(
        name = name,
        path = path,
        dateModified = 0L,
        parentPath = "/videos",
        mediaList = videos,
    )

    private fun testVideo(name: String, path: String, lastPlayedAt: Date? = null): Video = Video(
        id = path.hashCode().toLong(),
        path = path,
        parentPath = path.substringBeforeLast('/'),
        duration = 1_000L,
        uriString = "content://$path",
        nameWithExtension = name,
        width = 1920,
        height = 1080,
        size = 1_000L,
        lastPlayedAt = lastPlayedAt,
    )
}
