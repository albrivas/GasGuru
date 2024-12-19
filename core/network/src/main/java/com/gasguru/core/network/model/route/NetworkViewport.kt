package com.gasguru.core.network.model.route


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkViewport(
    @Json(name = "high")
    val high: NetworkHigh,
    @Json(name = "low")
    val low: NetworkLow
)