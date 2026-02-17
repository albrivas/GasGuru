package com.gasguru.core.network.model.route

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkLeg(
    @SerialName("distanceMeters")
    val distanceMeters: Int,
    @SerialName("duration")
    val duration: String,
    @SerialName("endLocation")
    val endLocation: NetworkEndLocation,
    @SerialName("localizedValues")
    val localizedValues: NetworkLocalizedValues,
    @SerialName("polyline")
    val polyline: NetworkPolyline,
    @SerialName("startLocation")
    val startLocation: NetworkStartLocation,
    @SerialName("staticDuration")
    val staticDuration: String,
    @SerialName("steps")
    val steps: List<NetworkStep>,
)
