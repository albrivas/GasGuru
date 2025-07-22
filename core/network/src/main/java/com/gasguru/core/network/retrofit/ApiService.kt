package com.gasguru.core.network.retrofit

import com.gasguru.core.network.model.NetworkFuelStation
import retrofit2.http.GET

fun interface ApiService {

    companion object {
        const val LIST_FUEL_STATIONS =
            "ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/"
    }

    @GET(LIST_FUEL_STATIONS)
    suspend fun listFuelStations(): NetworkFuelStation
}
