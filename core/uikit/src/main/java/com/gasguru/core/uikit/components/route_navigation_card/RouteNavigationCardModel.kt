package com.gasguru.core.uikit.components.route_navigation_card

data class RouteNavigationCardModel(
    val destination: String,
    val stationCount: Int,
    val distance: String,
    val duration: String,
    val onClose: () -> Unit = {},
)