package one.only.player.feature.videopicker.screens.mediapicker

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MediaPickerMoveSelectionTest {

    @Test
    fun canMoveTo_rejectsCurrentParentsSelfAndDescendants() {
        val rootPath = File(System.getProperty("java.io.tmpdir"), "only_player_move_selection").path
        val selection = MediaPickerMoveSelection(
            videoUris = listOf("content://video/1"),
            videoParentPaths = listOf("$rootPath/Movies"),
            folderPaths = listOf("$rootPath/Movies/Shows"),
            folderParentPaths = listOf("$rootPath/Movies"),
        )

        assertFalse(selection.canMoveTo("$rootPath/Movies"))
        assertFalse(selection.canMoveTo("$rootPath/Movies/Shows"))
        assertFalse(selection.canMoveTo("$rootPath/Movies/Shows/Season01"))
        assertTrue(selection.canMoveTo("$rootPath/Archive"))
    }
}
