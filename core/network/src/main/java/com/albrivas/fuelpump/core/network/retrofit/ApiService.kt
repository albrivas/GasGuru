/*
 * File: ApiService.kt
 * Project: FuelPump
 * Module: FuelPump.core.network.main
 * Last modified: 12/28/22, 8:11 PM
 *
 * Created by albertorivas on 12/29/22, 5:33 PM
 * Copyright © 2022 Alberto Rivas. All rights reserved.
 *
 */

package com.albrivas.fuelpump.core.network.retrofit

import com.albrivas.fuelpump.core.network.model.NetworkFuelStation
import retrofit2.http.GET

interface ApiService {

    companion object {
        const val LIST_FUEL_STATIONS =
            "ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/"
    }

    @GET(LIST_FUEL_STATIONS)
    suspend fun listFuelStations(): NetworkFuelStation
}