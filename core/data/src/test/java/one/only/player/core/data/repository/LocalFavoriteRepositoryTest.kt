package one.only.player.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import one.only.player.core.database.dao.FavoriteItemDao
import one.only.player.core.database.entities.FavoriteItemEntity
import one.only.player.core.model.FavoriteTargetType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LocalFavoriteRepositoryTest {

    @Test
    fun deleteFolderDeletesDescendantsOnly() = runBlocking {
        val dao = FakeFavoriteItemDao(
            listOf(
                favoriteFolder(id = 1, title = "Root"),
                favoriteFolder(id = 2, parentId = 1, title = "Child"),
                localVideo(id = 3, parentId = 2, title = "Nested video"),
                localVideo(id = 4, title = "Unrelated video"),
            ),
        )
        val repository = LocalFavoriteRepository(dao)

        repository.delete(listOf(1))

        assertEquals(listOf(4L), dao.currentIds())
    }

    @Test
    fun moveRejectsCyclesAndNonFolderParents() = runBlocking {
        val dao = FakeFavoriteItemDao(
            listOf(
                favoriteFolder(id = 1, title = "Root"),
                favoriteFolder(id = 2, parentId = 1, title = "Child"),
                localVideo(id = 3, title = "Video"),
            ),
        )
        val repository = LocalFavoriteRepository(dao)

        repository.move(ids = listOf(1), parentId = 2)
        repository.move(ids = listOf(2), parentId = 3)
        repository.move(ids = listOf(3), parentId = 1)

        assertNull(dao.parentOf(1))
        assertEquals(1L, dao.parentOf(2))
        assertEquals(1L, dao.parentOf(3))
    }
}

private class FakeFavoriteItemDao(
    initialItems: List<FavoriteItemEntity>,
) : FavoriteItemDao {
    private val items = linkedMapOf<Long, FavoriteItemEntity>()
    private val state = MutableStateFlow<List<FavoriteItemEntity>>(emptyList())
    private var nextId = 1L

    init {
        initialItems.forEach { entity ->
            items[entity.id] = entity
            nextId = maxOf(nextId, entity.id + 1)
        }
        syncState()
    }

    override fun observeAll(): Flow<List<FavoriteItemEntity>> = state

    override suspend fun getAll(): List<FavoriteItemEntity> = items.values.toList()

    override fun observeByParent(parentId: Long?): Flow<List<FavoriteItemEntity>> = state.map { entities ->
        entities.filter { entity -> entity.parentId == parentId }
    }

    override suspend fun getById(id: Long): FavoriteItemEntity? = items[id]

    override suspend fun getByTargetKey(targetKey: String): FavoriteItemEntity? = items.values.firstOrNull { entity ->
        entity.targetKey == targetKey
    }

    override suspend fun getByTargetTypeAndLocalUri(
        targetType: String,
        localUri: String,
    ): FavoriteItemEntity? = items.values.firstOrNull { entity ->
        entity.targetType == targetType && entity.localUri == localUri
    }

    override suspend fun getByTargetType(targetType: String): List<FavoriteItemEntity> = items.values.filter { entity ->
        entity.targetType == targetType
    }

    override suspend fun insert(entity: FavoriteItemEntity): Long {
        if (items.values.any { it.targetKey == entity.targetKey }) return -1
        val id = entity.id.takeIf { it > 0L } ?: nextId++
        items[id] = entity.copy(id = id)
        syncState()
        return id
    }

    override suspend fun update(entity: FavoriteItemEntity) {
        items[entity.id] = entity
        syncState()
    }

    override suspend fun move(
        ids: List<Long>,
        parentId: Long?,
        updatedAt: Long,
        sortOrder: Long,
    ) {
        ids.forEach { id ->
            items[id]?.let { entity ->
                items[id] = entity.copy(
                    parentId = parentId,
                    updatedAt = updatedAt,
                    sortOrder = sortOrder,
                )
            }
        }
        syncState()
    }

    override suspend fun delete(ids: List<Long>) {
        ids.forEach(items::remove)
        syncState()
    }

    override suspend fun clear() {
        items.clear()
        syncState()
    }

    fun currentIds(): List<Long> = items.keys.toList()

    fun parentOf(id: Long): Long? = items[id]?.parentId

    private fun syncState() {
        state.value = items.values.toList()
    }
}

private fun favoriteFolder(
    id: Long,
    parentId: Long? = null,
    title: String,
): FavoriteItemEntity = favoriteEntity(
    id = id,
    parentId = parentId,
    targetType = FavoriteTargetType.FAVORITE_FOLDER,
    title = title,
)

private fun localVideo(
    id: Long,
    parentId: Long? = null,
    title: String,
): FavoriteItemEntity = favoriteEntity(
    id = id,
    parentId = parentId,
    targetType = FavoriteTargetType.LOCAL_VIDEO,
    title = title,
)

private fun favoriteEntity(
    id: Long,
    parentId: Long? = null,
    targetType: FavoriteTargetType,
    title: String,
): FavoriteItemEntity = FavoriteItemEntity(
    id = id,
    parentId = parentId,
    targetType = targetType.name,
    targetKey = "${targetType.name}:$id",
    title = title,
    subtitle = "",
    localUri = "content://media/$id".takeIf { targetType == FavoriteTargetType.LOCAL_VIDEO },
    localPath = null,
    remoteServerId = null,
    remoteProtocol = null,
    remotePath = null,
    remoteServerName = null,
    createdAt = id,
    updatedAt = id,
    sortOrder = id,
)
