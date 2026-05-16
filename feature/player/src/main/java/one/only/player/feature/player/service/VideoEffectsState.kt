package one.only.player.feature.player.service

import one.only.player.core.model.DecoderPriority

internal data class VideoEffectsState(
    val filters: VideoFilterPreferences,
    val decoderPriority: DecoderPriority,
    val isPipelineInitialized: Boolean = false,
)
