package com.gasguru.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkFuelStation(
    @SerialName("Fecha")
    val date: String,
    @SerialName("ListaEESSPrecio")
    val listPriceFuelStation: List<NetworkPriceFuelStation>,
)
