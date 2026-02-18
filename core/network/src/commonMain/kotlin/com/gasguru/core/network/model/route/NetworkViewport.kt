package com.gasguru.core.network.model.route

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkViewport(
    @SerialName("high")
    val high: NetworkHigh,
    @SerialName("low")
    val low: NetworkLow,
)
