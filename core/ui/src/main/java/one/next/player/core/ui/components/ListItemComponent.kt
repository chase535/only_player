package one.next.player.core.ui.components

import android.os.Build
import android.view.RoundedCorner
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NextSegmentedListItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isEnabled: Boolean = true,
    isFirstItem: Boolean = false,
    isLastItem: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    colors: ListItemColors = ListItemDefaults.segmentedColors(),
    shapes: ListItemShapes = ListItemDefaults.shapes(),
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val view = LocalView.current
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val overrideShape = MaterialTheme.shapes.large
    val deviceRoundedCorners = remember(view.rootWindowInsets, density, layoutDirection) {
        deviceRoundedCorners(
            windowInsets = view.rootWindowInsets,
            density = density,
            layoutDirection = layoutDirection,
        )
    }

    SegmentedListItem(
        modifier = modifier,
        selected = isSelected,
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = isEnabled,
        verticalAlignment = Alignment.CenterVertically,
        shapes = remember(isFirstItem, isLastItem, shapes, overrideShape, deviceRoundedCorners) {
            val defaultBaseShape = shapes.shape
            if (defaultBaseShape is CornerBasedShape) {
                shapes.copy(
                    shape = defaultBaseShape.copy(
                        topStart = deviceRoundedCorners.topStart.takeIf { isFirstItem } ?: overrideShape.topStart.takeIf { isFirstItem } ?: defaultBaseShape.topStart,
                        topEnd = deviceRoundedCorners.topEnd.takeIf { isFirstItem } ?: overrideShape.topEnd.takeIf { isFirstItem } ?: defaultBaseShape.topEnd,
                        bottomStart = deviceRoundedCorners.bottomStart.takeIf { isLastItem } ?: overrideShape.bottomStart.takeIf { isLastItem } ?: defaultBaseShape.bottomStart,
                        bottomEnd = deviceRoundedCorners.bottomEnd.takeIf { isLastItem } ?: overrideShape.bottomEnd.takeIf { isLastItem } ?: defaultBaseShape.bottomEnd,
                    ),
                )
            } else {
                shapes
            }
        },
        colors = colors,
        contentPadding = contentPadding,
        leadingContent = leadingContent,
        supportingContent = supportingContent,
        trailingContent = trailingContent,
        overlineContent = overlineContent,
        interactionSource = interactionSource,
        content = content,
    )
}

private data class DeviceRoundedCorners(
    val topStart: CornerSize? = null,
    val topEnd: CornerSize? = null,
    val bottomStart: CornerSize? = null,
    val bottomEnd: CornerSize? = null,
)

private fun deviceRoundedCorners(
    windowInsets: WindowInsets?,
    density: Density,
    layoutDirection: LayoutDirection,
): DeviceRoundedCorners {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return DeviceRoundedCorners()
    if (windowInsets == null) return DeviceRoundedCorners()

    return deviceRoundedCornersApi31(
        windowInsets = windowInsets,
        density = density,
        layoutDirection = layoutDirection,
    )
}

@RequiresApi(Build.VERSION_CODES.S)
private fun deviceRoundedCornersApi31(
    windowInsets: WindowInsets,
    density: Density,
    layoutDirection: LayoutDirection,
): DeviceRoundedCorners {
    val topLeft = windowInsets.cornerSize(RoundedCorner.POSITION_TOP_LEFT, density)
    val topRight = windowInsets.cornerSize(RoundedCorner.POSITION_TOP_RIGHT, density)
    val bottomLeft = windowInsets.cornerSize(RoundedCorner.POSITION_BOTTOM_LEFT, density)
    val bottomRight = windowInsets.cornerSize(RoundedCorner.POSITION_BOTTOM_RIGHT, density)

    return when (layoutDirection) {
        LayoutDirection.Ltr -> DeviceRoundedCorners(
            topStart = topLeft,
            topEnd = topRight,
            bottomStart = bottomLeft,
            bottomEnd = bottomRight,
        )
        LayoutDirection.Rtl -> DeviceRoundedCorners(
            topStart = topRight,
            topEnd = topLeft,
            bottomStart = bottomRight,
            bottomEnd = bottomLeft,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.S)
private fun WindowInsets.cornerSize(
    position: Int,
    density: Density,
): CornerSize? = getRoundedCorner(position)
    ?.radius
    ?.takeIf { it > 0 }
    ?.let { radius -> CornerSize(with(density) { radius.toDp() }) }

@Composable
fun ListSectionTitle(
    modifier: Modifier = Modifier,
    text: String,
    contentPadding: PaddingValues = PaddingValues(
        start = 12.dp,
        top = 20.dp,
        bottom = 10.dp,
    ),
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Text(
        text = text,
        modifier = modifier.padding(contentPadding),
        color = color,
        style = MaterialTheme.typography.labelLarge,
    )
}
