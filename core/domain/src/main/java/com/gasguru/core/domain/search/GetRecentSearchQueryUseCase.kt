package com.gasguru.core.domain.search

import com.gasguru.core.data.repository.search.OfflineRecentSearchRepository
import com.gasguru.core.model.data.RecentSearchQuery
import kotlinx.coroutines.flow.Flow

class GetRecentSearchQueryUseCase(
    private val recentSearchRepository: OfflineRecentSearchRepository,
) {
    operator fun invoke(limit: Int = 10): Flow<List<RecentSearchQuery>> =
        recentSearchRepository.getRecentSearchQueries(limit)
}
