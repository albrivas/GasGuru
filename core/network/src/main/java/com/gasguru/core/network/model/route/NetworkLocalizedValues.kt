package com.gasguru.core.network.model.route

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkLocalizedValues(
    @SerialName("distance")
    val distance: NetworkDistance,
    @SerialName("duration")
    val duration: NetworkDuration?,
    @SerialName("staticDuration")
    val staticDuration: NetworkStaticDuration,
)
