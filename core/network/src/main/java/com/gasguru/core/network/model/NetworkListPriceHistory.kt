package com.gasguru.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkListPriceHistory(
    @Json(name = "Fecha")
    val dateHistory: String,
    @Json(name = "ListaEESSPrecio")
    val listPrices: List<NetworkPriceHistory>
)
