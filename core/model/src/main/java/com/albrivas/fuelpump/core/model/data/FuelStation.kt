package com.albrivas.fuelpump.core.model.data

import android.location.Location

data class FuelStation(
    val bioEthanolPercentage: String,
    val esterMethylPercentage: String,
    val postalCode: String,
    val direction: String,
    val schedule: String,
    val idAutonomousCommunity: String,
    val idServiceStation: Int,
    val idMunicipality: String,
    val idProvince: String,
    val location: Location,
    val locality: String,
    val margin: String,
    val municipality: String,
    val priceGasoilA: Double,
    val priceGasoilB: Double,
    val priceGasoilPremium: Double,
    val priceGasoline95_E10: Double,
    val priceGasoline95_E5: Double,
    val priceGasoline95_E5_Premium: Double,
    val priceGasoline98_E10: Double,
    val priceGasoline98_E5: Double,
    val priceHydrogen: Double,
    val province: String,
    val referral: String,
    val brandStationName: String,
    val brandStationBrandsType: FuelStationBrandsType,
    val typeSale: String,
    val priceCategory: PriceCategory = PriceCategory.NORMAL
)

fun FuelStation.getPriceForFuelType(fuelType: FuelType): Double {
    return when (fuelType) {
        FuelType.GASOLINE_95 -> this.priceGasoline95_E10
        FuelType.GASOLINE_98 -> this.priceGasoline98_E10
        FuelType.DIESEL -> this.priceGasoilA
        FuelType.DIESEL_PLUS -> this.priceGasoilPremium
        FuelType.ELECTRIC -> 0.0
    }
}

fun FuelStation.hasNonZeroPriceForFuelType(fuelType: FuelType): Boolean {
    return when (fuelType) {
        FuelType.GASOLINE_95 -> priceGasoline95_E10 != 0.0 || priceGasoline95_E5 != 0.0
        FuelType.GASOLINE_98 -> priceGasoline98_E10 != 0.0 || priceGasoline98_E5 != 0.0
        FuelType.DIESEL -> priceGasoilA != 0.0 || priceGasoilB != 0.0
        FuelType.DIESEL_PLUS -> priceGasoilPremium != 0.0
        FuelType.ELECTRIC -> false
    }
}

fun previewFuelStationDomain() =  FuelStation(
    "",
    "",
    "",
    "",
    "",
    "",
    1,
    "",
    "",
    Location(""),
    "",
    "",
    "",
    0.0,
    0.0,
    0.0,
    0.0,
    1.65,
    0.0,
    0.0,
    0.0,
    0.0,
    "",
    "",
    "REPSOL",
    FuelStationBrandsType.REPSOL,
    ""
)

