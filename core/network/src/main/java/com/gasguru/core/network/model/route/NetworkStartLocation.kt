package com.gasguru.core.network.model.route

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkStartLocation(
    @SerialName("latLng")
    val latLng: NetworkLatLng,
)
