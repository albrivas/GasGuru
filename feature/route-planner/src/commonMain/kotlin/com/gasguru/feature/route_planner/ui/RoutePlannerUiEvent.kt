package com.gasguru.feature.route_planner.ui

sealed interface RoutePlannerUiEvent {
    data object ClearStartDestinationField : RoutePlannerUiEvent
    data object ClearEndDestinationField : RoutePlannerUiEvent
    data object ChangeDestinations : RoutePlannerUiEvent
    data object SelectCurrentLocation : RoutePlannerUiEvent
    data object ClearRecentSearches : RoutePlannerUiEvent
    data class SelectPlace(val placeId: String, val placeName: String) : RoutePlannerUiEvent
    data class ChangeCurrentInput(val input: InputField) : RoutePlannerUiEvent
    data class SelectRecentPlace(val placeId: String, val placeName: String) : RoutePlannerUiEvent
}
