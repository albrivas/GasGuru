package com.gasguru.core.model.data

import android.location.Location
import java.util.Locale

const val DISTANCE_KM_IN_METERS = 1000

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
    val priceGasoline95E10: Double,
    val priceGasoline95E5: Double,
    val priceGasoline95E5Premium: Double,
    val priceGasoline98E10: Double,
    val priceGasoline98E5: Double,
    val priceHydrogen: Double,
    val province: String,
    val referral: String,
    val brandStationName: String,
    val brandStationBrandsType: FuelStationBrandsType,
    val typeSale: String,
    val priceCategory: PriceCategory = PriceCategory.NONE,
    val distance: Float = 0.0f,
    val isFavorite: Boolean = false
) {
    fun formatDistance(): String {
        return when {
            distance >= DISTANCE_KM_IN_METERS -> {
                val kilometers = distance / DISTANCE_KM_IN_METERS
                String.format(Locale.ROOT, "%.2f Km", kilometers)
            }

            distance == distance.toInt().toFloat() -> {
                String.format(Locale.ROOT, "%.0f m", distance)
            }

            else -> {
                String.format(Locale.ROOT, "%.2f m", distance)
            }
        }
    }

    fun formatDirection(): String = direction.lowercase().replaceFirstChar(Char::uppercase)
}

fun previewFuelStationDomain(idServiceStation: Int = 0) = FuelStation(
    bioEthanolPercentage = "",
    esterMethylPercentage = "",
    postalCode = "",
    direction = "C/RIOS ROSAS - MADRID",
    schedule = "L-D: 24H",
    idAutonomousCommunity = "",
    idServiceStation = idServiceStation,
    idMunicipality = "",
    idProvince = "",
    location = Location(""),
    locality = "",
    margin = "",
    municipality = "Talavera de la Reina",
    priceGasoilA = 0.0,
    priceGasoilB = 0.0,
    priceGasoilPremium = 0.0,
    priceGasoline95E10 = 1.659,
    priceGasoline95E5 = 1.659,
    priceGasoline95E5Premium = 1.759,
    priceGasoline98E10 = 1.759,
    priceGasoline98E5 = 1.659,
    priceHydrogen = 0.0,
    province = "",
    referral = "",
    brandStationName = "REPSOL",
    brandStationBrandsType = FuelStationBrandsType.REPSOL,
    typeSale = "",
    priceCategory = PriceCategory.CHEAP,
    distance = 0.0f,
    isFavorite = false
)
