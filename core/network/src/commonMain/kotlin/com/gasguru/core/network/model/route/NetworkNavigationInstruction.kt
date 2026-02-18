package com.gasguru.core.network.model.route

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkNavigationInstruction(
    @SerialName("instructions")
    val instructions: String,
    @SerialName("maneuver")
    val maneuver: String,
)
