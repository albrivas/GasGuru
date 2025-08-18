package com.gasguru.feature.route_planner.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.domain.search.ClearRecentSearchQueriesUseCase
import com.gasguru.core.domain.search.GetRecentSearchQueryUseCase
import com.gasguru.core.ui.RecentSearchQueriesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class RoutePlannerViewModel @Inject constructor(
    private val clearRecentSearchQueriesUseCase: ClearRecentSearchQueriesUseCase,
    getRecentSearchQueryUseCase: GetRecentSearchQueryUseCase,
) : ViewModel() {

    val recentSearchQueriesUiState: StateFlow<RecentSearchQueriesUiState> =
        getRecentSearchQueryUseCase().map { recentQueries ->
            RecentSearchQueriesUiState.Success(recentQueries)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RecentSearchQueriesUiState.Loading,
        )

    fun handleEvent(event: RoutePlannerUiEvent) {
        when (event) {
            is RoutePlannerUiEvent.ChangeCurrentInput -> TODO()
            RoutePlannerUiEvent.ChangeDestinations -> TODO()
            RoutePlannerUiEvent.ClearEndDestinationField -> TODO()
            RoutePlannerUiEvent.ClearRecentSearches -> clearRecentSearch()
            RoutePlannerUiEvent.ClearStartDestinationField -> TODO()
            RoutePlannerUiEvent.GetCurrentLocation -> TODO()
            is RoutePlannerUiEvent.SelectPlace -> TODO()
            is RoutePlannerUiEvent.UpdateSearchQuery -> TODO()
        }
    }

    private fun clearRecentSearch() = viewModelScope.launch {
        clearRecentSearchQueriesUseCase()
    }
}
