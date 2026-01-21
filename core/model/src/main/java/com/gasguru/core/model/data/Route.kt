package com.gasguru.core.model.data

data class Route(
    val route: List<LatLng>,
    val distanceText: String,
    val durationText: String,
)

data class LatLng(
    val latitude: Double,
    val longitude: Double
)
