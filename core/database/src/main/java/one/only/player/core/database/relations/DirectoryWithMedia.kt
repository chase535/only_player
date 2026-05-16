package one.only.player.core.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import one.only.player.core.database.entities.DirectoryEntity
import one.only.player.core.database.entities.MediumEntity

data class DirectoryWithMedia(
    @Embedded val directory: DirectoryEntity,
    @Relation(
        entity = MediumEntity::class,
        parentColumn = "path",
        entityColumn = "parent_path",
    )
    val media: List<MediumWithInfo>,
)
