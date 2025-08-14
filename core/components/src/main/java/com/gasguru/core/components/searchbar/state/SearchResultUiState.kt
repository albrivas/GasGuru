package com.gasguru.core.components.searchbar.state

import com.gasguru.core.model.data.SearchPlace

sealed interface SearchResultUiState {
    data object EmptyQuery : SearchResultUiState
    data object EmptySearchResult : SearchResultUiState
    data class Success(val places: List<SearchPlace>) : SearchResultUiState
    data object Loading : SearchResultUiState
    data object LoadFailed : SearchResultUiState
}
