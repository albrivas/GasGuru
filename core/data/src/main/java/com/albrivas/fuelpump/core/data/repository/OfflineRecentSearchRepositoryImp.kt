package com.albrivas.fuelpump.core.data.repository

import com.albrivas.fuelpump.core.data.mapper.asExternalModel
import com.albrivas.fuelpump.core.database.dao.RecentSearchQueryDao
import com.albrivas.fuelpump.core.database.model.RecentSearchQueryEntity
import com.albrivas.fuelpump.core.model.data.RecentSearchQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineRecentSearchRepositoryImp @Inject constructor(
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