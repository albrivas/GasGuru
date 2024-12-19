package com.gasguru.core.network.model.route


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkLeg(
    @Json(name = "distanceMeters")
    val distanceMeters: Int,
    @Json(name = "duration")
    val duration: String,
    @Json(name = "endLocation")
    val endLocation: NetworkEndLocation,
    @Json(name = "localizedValues")
    val localizedValues: NetworkLocalizedValues,
    @Json(name = "polyline")
    val polyline: NetworkPolyline,
    @Json(name = "startLocation")
    val startLocation: NetworkStartLocation,
    @Json(name = "staticDuration")
    val staticDuration: String,
    @Json(name = "steps")
    val steps: List<NetworkStep>
)