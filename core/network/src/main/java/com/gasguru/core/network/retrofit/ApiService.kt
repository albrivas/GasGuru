package com.gasguru.core.network.retrofit

import com.gasguru.core.network.model.NetworkFuelStation
import com.gasguru.core.network.model.NetworkListPriceHistory
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    companion object {
        const val LIST_FUEL_STATIONS =
            "ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/"

        const val PRICE_HISTORY =
            "ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestresHist/{date}/{idMunicipality}/{idProduct}"
    }

    @GET(LIST_FUEL_STATIONS)
    suspend fun listFuelStations(): NetworkFuelStation

    @GET(PRICE_HISTORY)
    suspend fun priceHistory(
        @Path("date") date: String,
        @Path("idMunicipality") idMunicipality: String,
        @Path("idProduct") idProduct: String,
    ): NetworkListPriceHistory
}
