package com.gasguru.core.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestRoute(
    @SerialName("destination")
    val destination: RequestDestination,
    @SerialName("origin")
    val origin: RequestOrigin,
    @SerialName("travelMode")
    val travelMode: String,
    @SerialName("languageCode")
    val languageCode: String,
    @SerialName("computeAlternativeRoutes")
    val computeAlternativeRoutes: String,
)

@Serializable
data class RequestDestination(
    @SerialName("location")
    val location: RequestLocation,
)

@Serializable
data class RequestLatLng(
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
)

@Serializable
data class RequestOrigin(
    @SerialName("location")
    val location: RequestLocation,
)

@Serializable
data class RequestLocation(
    @SerialName("latLng")
    val latLng: RequestLatLng,
)
