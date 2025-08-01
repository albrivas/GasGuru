package com.gasguru.feature.route_planner.ui

data class RoutePlannerUiState(
    val startQuery: String = "",
    val endQuery: String = "",
    val currentInput: InputField = InputField.START,
    val currentLocationInput: InputField? = InputField.START
)

enum class InputField {
    START, END
}
