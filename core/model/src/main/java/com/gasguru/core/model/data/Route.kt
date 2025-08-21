package com.gasguru.core.model.data

data class Route(
    val route: List<LatLng>
)

data class LatLng(
    val latitude: Double,
    val longitude: Double
)
