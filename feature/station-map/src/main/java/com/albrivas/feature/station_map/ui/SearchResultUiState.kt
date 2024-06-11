package com.albrivas.feature.station_map.ui

sealed interface SearchResultUiState {
    /**
     * When user has not typed anything in the search bar
     */
    data object EmptyQuery : SearchResultUiState

    /**
     * When the search result is empty
     */
    data object EmptySearchResult : SearchResultUiState

    /**
     * When the search return places
     */
    data class Success(val places: List<SearchPlace>) : SearchResultUiState
    data object Loading : SearchResultUiState
}

data class SearchPlace(
    val name: String,
    val id: String,
)