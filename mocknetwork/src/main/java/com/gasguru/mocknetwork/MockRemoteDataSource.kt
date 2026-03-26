package com.gasguru.mocknetwork

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.gasguru.core.supabase.datasource.RemoteDataSource
import com.gasguru.core.supabase.model.NetworkError
import com.gasguru.core.supabase.model.NetworkFuelStation
import com.gasguru.core.supabase.model.NetworkPriceFuelStation

class MockRemoteDataSource(
    private val mockWebServerManager: MockWebServerManager,
) : RemoteDataSource {
    override suspend fun getListFuelStations(): Either<NetworkError, NetworkFuelStation> =
        try {
            mockWebServerManager.enqueueResponse(
                assetFileName = "fuel-stations.json",
                responseCode = 200,
            )
            val networkResult = mockWebServerManager.apiService.listFuelStations()
            NetworkFuelStation(
                date = networkResult.date,
                listPriceFuelStation = networkResult.listPriceFuelStation.map { station ->
                    NetworkPriceFuelStation(
                        bioEthanolPercentage = station.bioEthanolPercentage,
                        esterMethylPercentage = station.esterMethylPercentage,
                        postalCode = station.postalCode,
                        direction = station.direction,
                        schedule = station.schedule,
                        idAutonomousCommunity = station.idAutonomousCommunity,
                        idServiceStation = station.idServiceStation,
                        idMunicipality = station.idMunicipality,
                        idProvince = station.idProvince,
                        latitude = station.latitude,
                        locality = station.locality,
                        longitudeWGS84 = station.longitudeWGS84,
                        margin = station.margin,
                        municipality = station.municipality,
                        priceBiodiesel = station.priceBiodiesel,
                        priceBioEthanol = station.priceBioEthanol,
                        priceGasNaturalCompressed = station.priceGasNaturalCompressed,
                        priceLiquefiedNaturalGas = station.priceLiquefiedNaturalGas,
                        priceLiquefiedPetroleumGas = station.priceLiquefiedPetroleumGas,
                        priceGasoilA = station.priceGasoilA,
                        priceGasoilB = station.priceGasoilB,
                        priceGasoilPremium = station.priceGasoilPremium,
                        priceGasoline95E10 = station.priceGasoline95E10,
                        priceGasoline95E5 = station.priceGasoline95E5,
                        priceGasoline95E5Premium = station.priceGasoline95E5Premium,
                        priceGasoline98E10 = station.priceGasoline98E10,
                        priceGasoline98E5 = station.priceGasoline98E5,
                        priceHydrogen = station.priceHydrogen,
                        priceAdblue = station.priceAdblue,
                        province = station.province,
                        referral = station.referral,
                        brandStation = station.brandStation,
                        typeSale = station.typeSale,
                    )
                },
            ).right()
        } catch (exception: Exception) {
            NetworkError(exception = exception).left()
        }
}
