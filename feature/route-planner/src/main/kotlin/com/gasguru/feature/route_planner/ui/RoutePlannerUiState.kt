package com.gasguru.feature.route_planner.ui

data class RoutePlannerUiState(
    val startQuery: RouteQuery = RouteQuery(),
    val endQuery: RouteQuery = RouteQuery(),
    val currentInput: InputField = InputField.START,
    val currentLocationInput: InputField? = InputField.START,
)

data class RouteQuery(
    val name: String = "",
    val id: String = "",
    val isCurrentLocation: Boolean = false
) {
    val isEmpty: Boolean get() = name.isEmpty() && id.isEmpty() && !isCurrentLocation
}

enum class InputField {
    START, END
}
