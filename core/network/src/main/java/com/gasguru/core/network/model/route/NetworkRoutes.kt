package com.gasguru.core.network.model.route

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkRoutes(
    @SerialName("geocodingResults")
    val geocodingResults: NetworkGeocodingResults,
    @SerialName("routes")
    val routes: List<NetworkRoute>,
)
