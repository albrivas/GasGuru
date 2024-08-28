package com.albrivas.fuelpump.feature.station_map.ui

import com.albrivas.fuelpump.core.model.data.SearchPlace

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

    /**
     * When the search failed to load data
     */
    data object LoadFailed : SearchResultUiState
}
