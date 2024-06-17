package com.albrivas.fuelpump.core.domain

import com.albrivas.fuelpump.core.data.repository.OfflineRecentSearchRepository
import com.albrivas.fuelpump.core.model.data.RecentSearchQuery
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentSearchQueryUseCase @Inject constructor(
    private val recentSearchRepository: OfflineRecentSearchRepository,
) {
    operator fun invoke(limit: Int = 10): Flow<List<RecentSearchQuery>> =
        recentSearchRepository.getRecentSearchQueries(limit)
}