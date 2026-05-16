package one.only.player.settings.composables

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import one.only.player.core.ui.components.OptionsDialog as CoreOptionsDialog

@Composable
fun OptionsDialog(
    text: String,
    onDismissClick: () -> Unit,
    options: LazyListScope.() -> Unit,
) {
    CoreOptionsDialog(
        title = text,
        onDismissClick = onDismissClick,
        options = options,
    )
}
