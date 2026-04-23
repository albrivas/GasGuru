package com.gasguru.core.domain.search

import com.gasguru.core.data.repository.search.OfflineRecentSearchRepository

class InsertRecentSearchQueryUseCase(
    private val recentSearchRepository: OfflineRecentSearchRepository,
) {
    suspend operator fun invoke(placeId: String, name: String): Unit =
        recentSearchRepository.insertOrReplaceRecentSearch(placeId, name)
}
