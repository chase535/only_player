package one.only.player.feature.videopicker.state

import one.only.player.core.model.Folder
import one.only.player.core.model.Video
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SelectionManagerTest {

    @Test
    fun toggleVideoSelection_entersSelectionModeAndTracksSingleVideoSelection() {
        val manager = SelectionManager()
        val video = testVideo(name = "Alpha.mp4", path = "/library/alpha/Alpha.mp4")

        manager.toggleVideoSelection(video)

        assertTrue(manager.isInSelectionMode)
        assertTrue(manager.isVideoSelected(video))
        assertTrue(manager.isSingleVideoSelected)
        assertEquals(setOf(video.uriString), manager.allSelectedVideos.map { it.uriString }.toSet())
    }

    @Test
    fun toggleFolderSelection_includesAllVideosFromFolderAndClearsStateWhenToggledOff() {
        val manager = SelectionManager()
        val video1 = testVideo(name = "One.mp4", path = "/library/folder/One.mp4")
        val video2 = testVideo(name = "Two.mp4", path = "/library/folder/Two.mp4")
        val folder = testFolder(name = "Folder", path = "/library/folder", videos = listOf(video1, video2))

        manager.toggleFolderSelection(folder)

        assertTrue(manager.isInSelectionMode)
        assertTrue(manager.isFolderSelected(folder))
        assertEquals(setOf(video1.uriString, video2.uriString), manager.allSelectedVideos.map { it.uriString }.toSet())

        manager.toggleFolderSelection(folder)

        assertFalse(manager.isInSelectionMode)
        assertFalse(manager.isFolderSelected(folder))
        assertTrue(manager.allSelectedVideos.isEmpty())
    }

    @Test
    fun clearSelection_keepsSelectionModeButRemovesSelections_untilExitSelectionMode() {
        val manager = SelectionManager()
        val video = testVideo(name = "Solo.mp4", path = "/library/solo/Solo.mp4")

        manager.selectVideo(video)
        manager.clearSelection()

        assertTrue(manager.isInSelectionMode)
        assertTrue(manager.allSelectedVideos.isEmpty())

        manager.exitSelectionMode()

        assertFalse(manager.isInSelectionMode)
        assertTrue(manager.allSelectedVideos.isEmpty())
    }

    private fun testFolder(name: String, path: String, videos: List<Video>): Folder = Folder(
        name = name,
        path = path,
        dateModified = 0L,
        parentPath = "/library",
        mediaList = videos,
    )

    private fun testVideo(name: String, path: String): Video = Video(
        id = path.hashCode().toLong(),
        path = path,
        parentPath = path.substringBeforeLast('/'),
        duration = 1_000L,
        uriString = "content://$path",
        nameWithExtension = name,
        width = 1920,
        height = 1080,
        size = 1_000L,
    )
}
