package com.gasguru.core.data.mapper

import com.gasguru.core.database.model.FuelStationEntity
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.PriceCategory
import com.gasguru.core.network.model.NetworkPriceFuelStation

fun NetworkPriceFuelStation.asEntity() = FuelStationEntity(
    bioEthanolPercentage = bioEthanolPercentage,
    esterMethylPercentage = esterMethylPercentage,
    postalCode = postalCode,
    direction = direction,
    schedule = schedule,
    idAutonomousCommunity = idAutonomousCommunity,
    idServiceStation = idServiceStation.toInt(),
    idMunicipality = idMunicipality,
    idProvince = idProvince,
    latitude = latitude.replace(",", ".").toDouble(),
    locality = locality,
    longitudeWGS84 = longitudeWGS84.replace(",", ".").toDouble(),
    margin = margin,
    municipality = municipality,
    priceBiodiesel = priceBiodiesel.toSafeDouble(),
    priceBioEthanol = priceBioEthanol.toSafeDouble(),
    priceGasNaturalCompressed = priceGasNaturalCompressed.toSafeDouble(),
    priceLiquefiedNaturalGas = priceLiquefiedNaturalGas.toSafeDouble(),
    priceLiquefiedPetroleumGas = priceLiquefiedPetroleumGas.toSafeDouble(),
    priceGasoilA = priceGasoilA.toSafeDouble(),
    priceGasoilB = priceGasoilB.toSafeDouble(),
    priceGasoilPremium = priceGasoilPremium.toSafeDouble(),
    priceGasoline95E10 = priceGasoline95E10.toSafeDouble(),
    priceGasoline95E5 = priceGasoline95E5.toSafeDouble(),
    priceGasoline95E5Premium = priceGasoline95E5Premium.toSafeDouble(),
    priceGasoline98E10 = priceGasoline98E10.toSafeDouble(),
    priceGasoline98E5 = priceGasoline98E5.toSafeDouble(),
    priceHydrogen = priceHydrogen.toSafeDouble(),
    province = province,
    referral = referral,
    brandStation = brandStation,
    typeSale = typeSale,
    lastUpdate = System.currentTimeMillis(),
    isFavorite = false,
)

fun String.toSafeDouble(): Double =
    this.takeIf { it.isNotEmpty() }?.replace(",", ".")?.toDoubleOrNull() ?: 0.0

fun List<FuelStation>.calculateFuelPrices(fuelType: FuelType): Pair<Double, Double> {
    val prices = when (fuelType) {
        FuelType.GASOLINE_95 -> map { it.priceGasoline95E5 }
        FuelType.GASOLINE_98 -> map { it.priceGasoline98E5 }
        FuelType.DIESEL -> map { it.priceGasoilA }
        FuelType.DIESEL_PLUS -> map { it.priceGasoilPremium }
        FuelType.GASOLINE_95_PREMIUM -> map { it.priceGasoline95E5Premium }
        FuelType.GASOLINE_95_E10 -> map { it.priceGasoline95E10 }
        FuelType.GASOLINE_98_PREMIUM -> map { it.priceGasoline98E10 }
        FuelType.GASOIL_B -> map { it.priceGasoilB }
    }

    return Pair(prices.minOrNull() ?: 0.0, prices.maxOrNull() ?: 0.0)
}

fun FuelStation.getPriceCategory(
    fuelType: FuelType,
    minPrice: Double,
    maxPrice: Double,
): PriceCategory {
    val currentPrice = when (fuelType) {
        FuelType.GASOLINE_95 -> priceGasoline95E5
        FuelType.GASOLINE_98 -> priceGasoline98E5
        FuelType.DIESEL -> priceGasoilA
        FuelType.DIESEL_PLUS -> priceGasoilPremium
        FuelType.GASOLINE_95_PREMIUM -> priceGasoline95E5Premium
        FuelType.GASOLINE_95_E10 -> priceGasoline95E10
        FuelType.GASOLINE_98_PREMIUM -> priceGasoline98E10
        FuelType.GASOIL_B -> priceGasoilB
    }

    val priceRange = maxPrice - minPrice
    val step = priceRange / 3 // 3 range prices (cheap, normal, expensive)

    return when {
        currentPrice < minPrice + step -> PriceCategory.CHEAP
        currentPrice < minPrice + 2 * step -> PriceCategory.NORMAL
        else -> PriceCategory.EXPENSIVE
    }
}
