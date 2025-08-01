package com.gasguru.feature.route_planner.ui

sealed interface RoutePlannerUiState {
    data object Loading: RoutePlannerUiState
}
