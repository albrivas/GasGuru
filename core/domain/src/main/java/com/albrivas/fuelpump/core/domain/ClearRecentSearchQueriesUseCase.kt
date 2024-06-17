package com.albrivas.fuelpump.core.domain

import com.albrivas.fuelpump.core.data.repository.OfflineRecentSearchRepository
import javax.inject.Inject

class ClearRecentSearchQueriesUseCase @Inject constructor(
    private val recentSearchRepository: OfflineRecentSearchRepository,
) {
    suspend operator fun invoke() = recentSearchRepository.clearRecentSearches()
}