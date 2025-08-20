package com.gasguru.feature.route_planner.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.domain.search.ClearRecentSearchQueriesUseCase
import com.gasguru.core.domain.search.GetRecentSearchQueryUseCase
import com.gasguru.core.ui.RecentSearchQueriesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutePlannerViewModel @Inject constructor(
    private val clearRecentSearchQueriesUseCase: ClearRecentSearchQueriesUseCase,
    getRecentSearchQueryUseCase: GetRecentSearchQueryUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(RoutePlannerUiState())
    val state = _state.asStateFlow()

    val recentSearchQueriesUiState: StateFlow<RecentSearchQueriesUiState> =
        getRecentSearchQueryUseCase().map { recentQueries ->
            RecentSearchQueriesUiState.Success(recentQueries)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RecentSearchQueriesUiState.Loading,
        )

    val isRouteEnabled: StateFlow<Boolean> = _state.map { uiState ->
        !uiState.startQuery.isEmpty && !uiState.endQuery.isEmpty
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    fun handleEvent(event: RoutePlannerUiEvent) {
        when (event) {
            is RoutePlannerUiEvent.ChangeCurrentInput -> changeCurrentInput(inputField = event.input)
            RoutePlannerUiEvent.ChangeDestinations -> changeDestinations()
            RoutePlannerUiEvent.ClearEndDestinationField -> clearDestination(inputField = InputField.END)
            RoutePlannerUiEvent.ClearRecentSearches -> clearRecentSearch()
            RoutePlannerUiEvent.ClearStartDestinationField -> clearDestination(inputField = InputField.START)
            RoutePlannerUiEvent.SelectCurrentLocation -> selectCurrentLocation()
            is RoutePlannerUiEvent.SelectPlace -> selectedPlace(
                placeId = event.placeId,
                placeName = event.placeName
            )

            is RoutePlannerUiEvent.SelectRecentPlace -> selectedRecentPlace(
                placeId = event.placeId,
                placeName = event.placeName
            )
        }
    }

    private fun clearRecentSearch() = viewModelScope.launch {
        clearRecentSearchQueriesUseCase()
    }

    private fun changeCurrentInput(inputField: InputField) {
        _state.update { it.copy(currentInput = inputField) }
    }

    private fun selectedPlace(placeId: String, placeName: String) {
        when (_state.value.currentInput) {
            InputField.START -> _state.update {
                it.copy(
                    startQuery = RouteQuery(
                        name = placeName,
                        id = placeId
                    )
                )
            }

            InputField.END -> _state.update {
                it.copy(
                    endQuery = RouteQuery(
                        name = placeName,
                        id = placeId
                    )
                )
            }
        }
    }

    private fun changeDestinations() {
        val start = _state.value.startQuery
        val end = _state.value.endQuery
        _state.update { it.copy(startQuery = end, endQuery = start) }
    }

    private fun clearDestination(inputField: InputField) {
        when (inputField) {
            InputField.START -> _state.update { it.copy(startQuery = RouteQuery()) }
            InputField.END -> _state.update { it.copy(endQuery = RouteQuery()) }
        }
    }

    private fun selectedRecentPlace(placeId: String, placeName: String) {
        val currentState = _state.value

        when {
            currentState.startQuery.isEmpty -> {
                _state.update {
                    it.copy(
                        startQuery = RouteQuery(
                            name = placeName,
                            id = placeId
                        )
                    )
                }
            }

            currentState.endQuery.isEmpty -> {
                _state.update {
                    it.copy(
                        endQuery = RouteQuery(
                            name = placeName,
                            id = placeId
                        )
                    )
                }
            }
        }
    }

    private fun selectCurrentLocation() {
        val currentState = _state.value

        when {
            currentState.startQuery.isEmpty -> {
                _state.update {
                    it.copy(
                        startQuery = RouteQuery(isCurrentLocation = true)
                    )
                }
            }

            currentState.endQuery.isEmpty -> {
                _state.update {
                    it.copy(
                        endQuery = RouteQuery(isCurrentLocation = true)
                    )
                }
            }
        }
    }
}
