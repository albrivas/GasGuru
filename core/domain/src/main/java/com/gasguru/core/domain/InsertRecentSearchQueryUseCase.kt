package com.gasguru.core.domain

import com.gasguru.core.data.repository.OfflineRecentSearchRepository
import javax.inject.Inject

class InsertRecentSearchQueryUseCase @Inject constructor(
    private val recentSearchRepository: OfflineRecentSearchRepository,
) {
    suspend operator fun invoke(placeId: String, name: String) =
        recentSearchRepository.insertOrReplaceRecentSearch(placeId, name)
}
