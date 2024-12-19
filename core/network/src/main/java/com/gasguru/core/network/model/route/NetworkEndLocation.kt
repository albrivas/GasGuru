package com.gasguru.core.network.model.route


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkEndLocation(
    @Json(name = "latLng")
    val latLng: NetworkLatLng
)