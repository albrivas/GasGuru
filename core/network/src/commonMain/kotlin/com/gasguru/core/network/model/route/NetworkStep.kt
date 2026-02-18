package com.gasguru.core.network.model.route

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkStep(
    @SerialName("distanceMeters")
    val distanceMeters: Int,
    @SerialName("endLocation")
    val endLocation: NetworkEndLocation,
    @SerialName("localizedValues")
    val localizedValues: NetworkLocalizedValues,
    @SerialName("navigationInstruction")
    val navigationInstruction: NetworkNavigationInstruction?,
    @SerialName("polyline")
    val polyline: NetworkPolyline,
    @SerialName("startLocation")
    val startLocation: NetworkStartLocation,
    @SerialName("staticDuration")
    val staticDuration: String,
    @SerialName("travelMode")
    val travelMode: String,
)
