package com.gasguru.core.testing.fakes.data.search

import com.gasguru.core.data.repository.search.OfflineRecentSearchRepository
import com.gasguru.core.model.data.RecentSearchQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeOfflineRecentSearchRepository(
    initialQueries: List<RecentSearchQuery> = emptyList(),
) : OfflineRecentSearchRepository {

    private val recentSearchesFlow = MutableStateFlow(initialQueries)

    var clearRecentSearchesCalls = 0
        private set
    val insertedRecentSearches = mutableListOf<RecentSearchQuery>()

    override fun getRecentSearchQueries(limit: Int): Flow<List<RecentSearchQuery>> =
        recentSearchesFlow.map { queries -> queries.take(limit) }

    override suspend fun insertOrReplaceRecentSearch(placeId: String, name: String) {
        val query = RecentSearchQuery(name = name, id = placeId)
        insertedRecentSearches.add(query)
        recentSearchesFlow.update { current ->
            listOf(query) + current.filterNot { it.id == placeId }
        }
    }

    override suspend fun clearRecentSearches() {
        clearRecentSearchesCalls += 1
        recentSearchesFlow.value = emptyList()
    }

    fun setRecentSearchQueries(queries: List<RecentSearchQuery>) {
        recentSearchesFlow.value = queries
    }
}
