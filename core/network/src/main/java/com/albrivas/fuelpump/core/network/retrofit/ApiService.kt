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