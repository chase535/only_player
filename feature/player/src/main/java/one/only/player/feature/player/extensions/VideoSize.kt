package one.only.player.feature.player.extensions

import androidx.media3.common.VideoSize

// 正方形视为竖屏
val VideoSize.isPortrait: Boolean
    get() = height >= width
