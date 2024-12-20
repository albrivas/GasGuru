package com.gasguru.core.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequestRoute(
    @Json(name = "destination")
    val destination: RequestDestination,
    @Json(name = "origin")
    val origin: RequestOrigin,
    @Json(name = "travelMode")
    val travelMode: String,
    @Json(name = "languageCode")
    val languageCode: String
)

@JsonClass(generateAdapter = true)
data class RequestDestination(
    @Json(name = "location")
    val location: RequestLocation
)

@JsonClass(generateAdapter = true)
data class RequestLatLng(
    @Json(name = "latitude")
    val latitude: Double,
    @Json(name = "longitude")
    val longitude: Double
)

@JsonClass(generateAdapter = true)
data class RequestOrigin(
    @Json(name = "location")
    val location: RequestLocation
)

@JsonClass(generateAdapter = true)
data class RequestLocation(
    @Json(name = "latLng")
    val latLng: RequestLatLng
)
