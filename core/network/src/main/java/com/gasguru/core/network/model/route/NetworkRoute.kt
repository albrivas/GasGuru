package com.gasguru.core.network.model.route

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkRoute(
    @SerialName("description")
    val description: String,
    @SerialName("distanceMeters")
    val distanceMeters: Int,
    @SerialName("duration")
    val duration: String,
    @SerialName("legs")
    val legs: List<NetworkLeg>,
    @SerialName("localizedValues")
    val localizedValues: NetworkLocalizedValues,
    @SerialName("polyline")
    val polyline: NetworkPolyline,
    @SerialName("polylineDetails")
    val polylineDetails: NetworkPolylineDetails,
    @SerialName("routeLabels")
    val routeLabels: List<String>,
    @SerialName("staticDuration")
    val staticDuration: String,
    @SerialName("travelAdvisory")
    val travelAdvisory: NetworkTravelAdvisory,
    @SerialName("viewport")
    val viewport: NetworkViewport,
)
