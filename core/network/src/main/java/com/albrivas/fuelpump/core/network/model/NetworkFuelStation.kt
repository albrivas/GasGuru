/*
 * File: NetworkFuelStation.kt
 * Project: FuelPump
 * Module: FuelPump.core.network.main
 * Last modified: 12/29/22, 5:07 PM
 *
 * Created by albertorivas on 12/29/22, 5:33 PM
 * Copyright © 2022 Alberto Rivas. All rights reserved.
 *
 */

package com.albrivas.fuelpump.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkFuelStation(
    @Json(name = "Fecha")
    val date: String,
    @Json(name = "ListaEESSPrecio")
    val listPriceFuelStation: List<PriceFuelStation>
)