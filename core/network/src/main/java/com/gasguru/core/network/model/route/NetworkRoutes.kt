package com.gasguru.core.network.model.route

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkRoutes(
    @Json(name = "geocodingResults")
    val geocodingResults: NetworkGeocodingResults,
    @Json(name = "routes")
    val routes: List<NetworkRoute>
)
