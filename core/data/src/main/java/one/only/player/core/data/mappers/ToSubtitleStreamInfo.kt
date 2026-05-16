package one.only.player.core.data.mappers

import one.only.player.core.database.entities.SubtitleStreamInfoEntity
import one.only.player.core.model.SubtitleStreamInfo

fun SubtitleStreamInfoEntity.toSubtitleStreamInfo() = SubtitleStreamInfo(
    index = index,
    title = title,
    codecName = codecName,
    language = language,
    disposition = disposition,
)
