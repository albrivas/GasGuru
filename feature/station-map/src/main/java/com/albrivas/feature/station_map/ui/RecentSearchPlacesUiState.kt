package com.albrivas.feature.station_map.ui

import com.albrivas.fuelpump.core.model.data.SearchPlace

sealed interface RecentSearchPlacesUiState {
    data object Loading : RecentSearchPlacesUiState

    data class Success(
        val recentQueries: List<SearchPlace> = emptyList(),
    ) : RecentSearchPlacesUiState
}