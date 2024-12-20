package com.gasguru.core.network.model.route

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkStep(
    @Json(name = "distanceMeters")
    val distanceMeters: Int,
    @Json(name = "endLocation")
    val endLocation: NetworkEndLocation,
    @Json(name = "localizedValues")
    val localizedValues: NetworkLocalizedValues,
    @Json(name = "navigationInstruction")
    val navigationInstruction: NetworkNavigationInstruction,
    @Json(name = "polyline")
    val polyline: NetworkPolyline,
    @Json(name = "startLocation")
    val startLocation: NetworkStartLocation,
    @Json(name = "staticDuration")
    val staticDuration: String,
    @Json(name = "travelMode")
    val travelMode: String
)
