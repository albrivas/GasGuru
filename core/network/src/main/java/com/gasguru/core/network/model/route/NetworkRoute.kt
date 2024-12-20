package com.gasguru.core.network.model.route

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkRoute(
    @Json(name = "description")
    val description: String,
    @Json(name = "distanceMeters")
    val distanceMeters: Int,
    @Json(name = "duration")
    val duration: String,
    @Json(name = "legs")
    val legs: List<NetworkLeg>,
    @Json(name = "localizedValues")
    val localizedValues: NetworkLocalizedValues,
    @Json(name = "polyline")
    val polyline: NetworkPolyline,
    @Json(name = "polylineDetails")
    val polylineDetails: NetworkPolylineDetails,
    @Json(name = "routeLabels")
    val routeLabels: List<String>,
    @Json(name = "staticDuration")
    val staticDuration: String,
    @Json(name = "travelAdvisory")
    val travelAdvisory: NetworkTravelAdvisory,
    @Json(name = "viewport")
    val viewport: NetworkViewport
)
