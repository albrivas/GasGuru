package com.gasguru.core.data.repository.search

import com.gasguru.core.model.data.RecentSearchQuery
import kotlinx.coroutines.flow.Flow

interface OfflineRecentSearchRepository {
    /**
     * Get the recent search queries up to the number of queries specified as [limit].
     */
    fun getRecentSearchQueries(limit: Int): Flow<List<RecentSearchQuery>>

    /**
     * Insert or replace the [placeId] [name] as part of the recent searches.
     */
    suspend fun insertOrReplaceRecentSearch(placeId: String, name: String)

    /**
     * Clear the recent searches.
     */
    suspend fun clearRecentSearches()
}
