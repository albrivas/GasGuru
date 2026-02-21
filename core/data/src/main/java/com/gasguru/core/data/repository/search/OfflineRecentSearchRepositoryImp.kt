package com.gasguru.core.data.repository.search

import com.gasguru.core.data.mapper.asExternalModel
import com.gasguru.core.database.dao.RecentSearchQueryDao
import com.gasguru.core.database.model.RecentSearchQueryEntity
import com.gasguru.core.model.data.RecentSearchQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
class OfflineRecentSearchRepositoryImp constructor(
    private val recentSearchQueryDao: RecentSearchQueryDao,
) : OfflineRecentSearchRepository {
    override fun getRecentSearchQueries(limit: Int): Flow<List<RecentSearchQuery>> =
        recentSearchQueryDao.getRecentSearchQueryEntities(limit)
            .map { searchQueries -> searchQueries.map { it.asExternalModel() } }

    override suspend fun insertOrReplaceRecentSearch(placeId: String, name: String) =
        recentSearchQueryDao.insertOrReplaceRecentSearchQuery(
            RecentSearchQueryEntity(
                id = placeId,
                name = name
            ),
        )

    override suspend fun clearRecentSearches() = recentSearchQueryDao.clearRecentSearchQueries()
}
