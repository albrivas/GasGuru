package com.gasguru.core.network.model.route


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkNavigationInstruction(
    @Json(name = "instructions")
    val instructions: String,
    @Json(name = "maneuver")
    val maneuver: String
)