package com.albrivas.fuelpump.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkFuelStation(
    @Json(name = "Fecha")
    val date: String,
    @Json(name = "ListaEESSPrecio")
    val listPriceFuelStation: PriceFuelStation
)