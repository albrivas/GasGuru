package com.gasguru.core.components.searchbar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.components.searchbar.state.GasGuruSearchBarEvent
import com.gasguru.core.components.searchbar.state.SearchResultUiState
import com.gasguru.core.ui.RecentSearchQueriesUiState
import com.gasguru.core.domain.places.GetPlacesUseCase
import com.gasguru.core.domain.search.ClearRecentSearchQueriesUseCase
import com.gasguru.core.domain.search.GetRecentSearchQueryUseCase
import com.gasguru.core.domain.search.InsertRecentSearchQueryUseCase
import com.gasguru.core.model.data.SearchPlace
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SEARCH_QUERY = "searchQuery"
private const val SEARCH_QUERY_MIN_LENGTH = 2

@HiltViewModel
class GasGuruSearchBarViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getPlacesUseCase: GetPlacesUseCase,
    private val clearRecentSearchQueriesUseCase: ClearRecentSearchQueriesUseCase,
    private val insertRecentSearchQueryUseCase: InsertRecentSearchQueryUseCase,
    getRecentSearchQueryUseCase: GetRecentSearchQueryUseCase,
) : ViewModel() {

    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResultUiState: StateFlow<SearchResultUiState> =
        searchQuery.flatMapLatest { query ->
            if (query.length < SEARCH_QUERY_MIN_LENGTH) {
                flowOf(SearchResultUiState.EmptyQuery)
            } else {
                getPlacesUseCase(query).map { predictions ->
                    if (predictions.isEmpty()) {
                        SearchResultUiState.EmptySearchResult
                    } else {
                        SearchResultUiState.Success(predictions)
                    }
                }
            }
        }.catch { SearchResultUiState.LoadFailed }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchResultUiState.Loading,
        )

    val recentSearchQueriesUiState: StateFlow<RecentSearchQueriesUiState> =
        getRecentSearchQueryUseCase().map { recentQueries ->
            RecentSearchQueriesUiState.Success(recentQueries)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RecentSearchQueriesUiState.Loading,
        )

    fun handleEvent(event: GasGuruSearchBarEvent) {
        when (event) {
            is GasGuruSearchBarEvent.UpdateSearchQuery -> updateSearchQuery(event.query)
            is GasGuruSearchBarEvent.ClearRecentSearches -> clearRecentSearches()
            is GasGuruSearchBarEvent.InsertRecentSearch -> insertRecentSearch(event.searchQuery)
        }
    }

    private fun updateSearchQuery(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
    }

    private fun clearRecentSearches() = viewModelScope.launch {
        clearRecentSearchQueriesUseCase()
    }

    private fun insertRecentSearch(searchQuery: SearchPlace) = viewModelScope.launch {
        insertRecentSearchQueryUseCase(placeId = searchQuery.id, name = searchQuery.name)
    }
}
