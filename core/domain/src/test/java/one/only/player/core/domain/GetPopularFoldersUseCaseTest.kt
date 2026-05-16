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
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetPopularFoldersUseCaseTest {

    private val mediaRepository = FakeMediaRepository()
    private val preferencesRepository = FakePreferencesRepository()

    @Test
    fun getPopularFolders_sortsByPlayedCountThenRecencyThenMediaCount() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val useCase = GetPopularFoldersUseCase(
            getSortedFoldersUseCase = GetSortedFoldersUseCase(mediaRepository, preferencesRepository, dispatcher),
            defaultDispatcher = dispatcher,
        )

        mediaRepository.directories.addAll(
            listOf(
                testFolder(
                    name = "Recent",
                    path = "/library/recent",
                    videos = listOf(
                        testVideo("recent-1.mp4", "/library/recent/recent-1.mp4", lastPlayedAt = Date(2_000L)),
                        testVideo("recent-2.mp4", "/library/recent/recent-2.mp4", lastPlayedAt = Date(1_500L)),
                    ),
                ),
                testFolder(
                    name = "Large",
                    path = "/library/large",
                    videos = listOf(
                        testVideo("large-1.mp4", "/library/large/large-1.mp4", lastPlayedAt = Date(1_000L)),
                        testVideo("large-2.mp4", "/library/large/large-2.mp4", lastPlayedAt = Date(900L)),
                        testVideo("large-3.mp4", "/library/large/large-3.mp4"),
                    ),
                ),
                testFolder(
                    name = "Small",
                    path = "/library/small",
                    videos = listOf(
                        testVideo("small-1.mp4", "/library/small/small-1.mp4", lastPlayedAt = Date(1_000L)),
                        testVideo("small-2.mp4", "/library/small/small-2.mp4", lastPlayedAt = Date(900L)),
                    ),
                ),
                testFolder(
                    name = "LeastPlayed",
                    path = "/library/least-played",
                    videos = listOf(
                        testVideo("least-1.mp4", "/library/least-played/least-1.mp4", lastPlayedAt = Date(3_000L)),
                        testVideo("least-2.mp4", "/library/least-played/least-2.mp4"),
                    ),
                ),
            ),
        )

        val folders = useCase(limit = 3).first()

        assertEquals(
            listOf("Recent", "Large", "Small"),
            folders.map(Folder::name),
        )
    }

    private fun testFolder(name: String, path: String, videos: List<Video>): Folder = Folder(
        name = name,
        path = path,
        dateModified = 0L,
        parentPath = "/library",
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
