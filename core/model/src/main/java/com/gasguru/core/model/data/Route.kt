package com.gasguru.core.model.data

data class Route(
    val legs: List<String>,
    val steps: List<LatLng>
)

data class LatLng(
    val latitude: Double,
    val longitude: Double
)
