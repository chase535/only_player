package one.only.player.core.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import one.only.player.core.database.entities.AudioStreamInfoEntity
import one.only.player.core.database.entities.MediumEntity
import one.only.player.core.database.entities.MediumStateEntity
import one.only.player.core.database.entities.SubtitleStreamInfoEntity
import one.only.player.core.database.entities.VideoStreamInfoEntity

data class MediumWithInfo(
    @Embedded val mediumEntity: MediumEntity,
    @Relation(
        parentColumn = "uri",
        entityColumn = "uri",
    )
    val mediumStateEntity: MediumStateEntity?,
    @Relation(
        parentColumn = "uri",
        entityColumn = "medium_uri",
    )
    val videoStreamInfo: VideoStreamInfoEntity?,
    @Relation(
        parentColumn = "uri",
        entityColumn = "medium_uri",
    )
    val audioStreamsInfo: List<AudioStreamInfoEntity>,
    @Relation(
        parentColumn = "uri",
        entityColumn = "medium_uri",
    )
    val subtitleStreamsInfo: List<SubtitleStreamInfoEntity>,
)
