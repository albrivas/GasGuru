package com.gasguru.feature.route_planner.ui

sealed interface RoutePlannerUiEvent {
    data object ClearStartDestinationField: RoutePlannerUiEvent
    data object ClearEndDestinationField: RoutePlannerUiEvent
    data object ChangeDestinations: RoutePlannerUiEvent
    data object GetCurrentLocation: RoutePlannerUiEvent
    data object ClearRecentSearches: RoutePlannerUiEvent
    data class UpdateSearchQuery(val query: String): RoutePlannerUiEvent
    data class SelectPlace(val placeId: String): RoutePlannerUiEvent
    data class ChangeCurrentInput(val input: InputField) : RoutePlannerUiEvent
}