package com.albrivas.feature.station_map.ui

sealed interface RecentSearchPlacesUiState {
    data object Loading : RecentSearchPlacesUiState

    data class Success(
        val recentQueries: List<SearchPlace> = emptyList(),
    ) : RecentSearchPlacesUiState
}