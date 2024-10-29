package com.gasguru.core.domain

import com.gasguru.core.data.repository.OfflineRecentSearchRepository
import com.gasguru.core.model.data.RecentSearchQuery
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentSearchQueryUseCase @Inject constructor(
    private val recentSearchRepository: OfflineRecentSearchRepository,
) {
    operator fun invoke(limit: Int = 10): Flow<List<RecentSearchQuery>> =
        recentSearchRepository.getRecentSearchQueries(limit)
}
