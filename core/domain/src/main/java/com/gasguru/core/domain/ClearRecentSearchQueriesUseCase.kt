package com.gasguru.core.domain

import com.gasguru.core.data.repository.search.OfflineRecentSearchRepository
import javax.inject.Inject

class ClearRecentSearchQueriesUseCase @Inject constructor(
    private val recentSearchRepository: OfflineRecentSearchRepository,
) {
    suspend operator fun invoke() = recentSearchRepository.clearRecentSearches()
}
