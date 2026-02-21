package com.gasguru.core.domain.search

import com.gasguru.core.data.repository.search.OfflineRecentSearchRepository

class ClearRecentSearchQueriesUseCase(
    private val recentSearchRepository: OfflineRecentSearchRepository,
) {
    suspend operator fun invoke(): Unit = recentSearchRepository.clearRecentSearches()
}
