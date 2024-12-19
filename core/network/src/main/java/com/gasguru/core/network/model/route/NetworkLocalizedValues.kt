package com.gasguru.core.network.model.route


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkLocalizedValues(
    @Json(name = "distance")
    val distance: NetworkDistance,
    @Json(name = "duration")
    val duration: NetworkDuration?,
    @Json(name = "staticDuration")
    val staticDuration: NetworkStaticDuration
)