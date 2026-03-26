package com.gasguru.mocknetwork

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.gasguru.core.supabase.datasource.RemoteDataSource
import com.gasguru.core.supabase.model.NetworkError
import com.gasguru.core.supabase.model.SupabaseFuelStation

class MockRemoteDataSource(
    private val mockWebServerManager: MockWebServerManager,
) : RemoteDataSource {
    override suspend fun getListFuelStations(): Either<NetworkError, List<SupabaseFuelStation>> =
        try {
            mockWebServerManager.enqueueResponse(
                assetFileName = "fuel-stations.json",
                responseCode = 200,
            )
            val networkResult = mockWebServerManager.apiService.listFuelStations()
            networkResult.listPriceFuelStation.map { station ->
                SupabaseFuelStation(
                    bioEthanolPercentage = station.bioEthanolPercentage,
                    esterMethylPercentage = station.esterMethylPercentage,
                    postalCode = station.postalCode,
                    direction = station.direction,
                    schedule = station.schedule,
                    idAutonomousCommunity = station.idAutonomousCommunity,
                    idServiceStation = station.idServiceStation,
                    idMunicipality = station.idMunicipality,
                    idProvince = station.idProvince,
                    latitude = station.latitude.replace(",", ".").toDoubleOrNull() ?: 0.0,
                    locality = station.locality,
                    longitudeWGS84 = station.longitudeWGS84.replace(",", ".").toDoubleOrNull() ?: 0.0,
                    margin = station.margin,
                    municipality = station.municipality,
                    priceBiodiesel = station.priceBiodiesel.toDoubleOrNull(),
                    priceBioEthanol = station.priceBioEthanol.toDoubleOrNull(),
                    priceGasNaturalCompressed = station.priceGasNaturalCompressed.toDoubleOrNull(),
                    priceLiquefiedNaturalGas = station.priceLiquefiedNaturalGas.toDoubleOrNull(),
                    priceLiquefiedPetroleumGas = station.priceLiquefiedPetroleumGas.toDoubleOrNull(),
                    priceGasoilA = station.priceGasoilA.toDoubleOrNull(),
                    priceGasoilB = station.priceGasoilB.toDoubleOrNull(),
                    priceGasoilPremium = station.priceGasoilPremium.toDoubleOrNull(),
                    priceGasoline95E10 = station.priceGasoline95E10.toDoubleOrNull(),
                    priceGasoline95E5 = station.priceGasoline95E5.toDoubleOrNull(),
                    priceGasoline95E5Premium = station.priceGasoline95E5Premium.toDoubleOrNull(),
                    priceGasoline98E10 = station.priceGasoline98E10.toDoubleOrNull(),
                    priceGasoline98E5 = station.priceGasoline98E5.toDoubleOrNull(),
                    priceHydrogen = station.priceHydrogen.toDoubleOrNull(),
                    priceAdblue = station.priceAdblue.toDoubleOrNull(),
                    province = station.province,
                    referral = station.referral,
                    brandStation = station.brandStation,
                    typeSale = station.typeSale,
                )
            }.right()
        } catch (exception: Exception) {
            NetworkError(exception = exception).left()
        }
}
