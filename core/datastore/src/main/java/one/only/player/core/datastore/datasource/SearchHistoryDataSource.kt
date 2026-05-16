package one.only.player.core.datastore.datasource

import androidx.datastore.core.DataStore
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import one.only.player.core.common.Logger
import one.only.player.core.model.SearchHistory

class SearchHistoryDataSource @Inject constructor(
    private val searchHistoryDataStore: DataStore<SearchHistory>,
) {

    companion object {
        private const val TAG = "SearchHistoryDataSource"
    }

    val searchHistory: Flow<SearchHistory> = searchHistoryDataStore.data

    suspend fun update(
        transform: suspend (SearchHistory) -> SearchHistory,
    ) {
        try {
            searchHistoryDataStore.updateData(transform)
        } catch (ioException: Exception) {
            Logger.error(TAG, "Failed to update search history: $ioException")
        }
    }
}
