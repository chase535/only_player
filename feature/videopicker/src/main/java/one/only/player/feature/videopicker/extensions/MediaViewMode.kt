package one.only.player.feature.videopicker.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import one.only.player.core.model.MediaViewMode
import one.only.player.core.ui.R

@Composable
fun MediaViewMode.name(): String = when (this) {
    MediaViewMode.VIDEOS -> stringResource(id = R.string.videos)
    MediaViewMode.FOLDERS -> stringResource(id = R.string.folders)
    MediaViewMode.FOLDER_TREE -> stringResource(id = R.string.tree)
}
