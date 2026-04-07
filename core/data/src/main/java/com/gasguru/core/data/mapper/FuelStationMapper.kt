package com.gasguru.core.data.mapper

import com.gasguru.core.database.model.FuelStationEntity
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.PriceCategory
import com.gasguru.core.supabase.model.SupabaseFuelStation

fun SupabaseFuelStation.asEntity() = FuelStationEntity(
    bioEthanolPercentage = bioEthanolPercentage,
    esterMethylPercentage = esterMethylPercentage,
    postalCode = postalCode,
    direction = direction,
    schedule = schedule,
    idAutonomousCommunity = idAutonomousCommunity,
    idServiceStation = idServiceStation.toInt(),
    idMunicipality = idMunicipality,
    idProvince = idProvince,
    latitude = latitude,
    locality = locality,
    longitudeWGS84 = longitudeWGS84,
    margin = margin,
    municipality = municipality,
    priceBiodiesel = priceBiodiesel ?: 0.0,
    priceBioEthanol = priceBioEthanol ?: 0.0,
    priceGasNaturalCompressed = priceGasNaturalCompressed ?: 0.0,
    priceLiquefiedNaturalGas = priceLiquefiedNaturalGas ?: 0.0,
    priceLiquefiedPetroleumGas = priceLiquefiedPetroleumGas ?: 0.0,
    priceGasoilA = priceGasoilA ?: 0.0,
    priceGasoilB = priceGasoilB ?: 0.0,
    priceGasoilPremium = priceGasoilPremium ?: 0.0,
    priceGasoline95E10 = priceGasoline95E10 ?: 0.0,
    priceGasoline95E5 = priceGasoline95E5 ?: 0.0,
    priceGasoline95E5Premium = priceGasoline95E5Premium ?: 0.0,
    priceGasoline98E10 = priceGasoline98E10 ?: 0.0,
    priceGasoline98E5 = priceGasoline98E5 ?: 0.0,
    priceHydrogen = priceHydrogen ?: 0.0,
    priceAdblue = priceAdblue ?: 0.0,
    province = province,
    referral = referral,
    brandStation = brandStation,
    typeSale = typeSale,
    lastUpdate = System.currentTimeMillis(),
    isFavorite = false,
)

fun List<FuelStation>.calculateFuelPrices(fuelType: FuelType): Pair<Double, Double> {
    val prices = map { fuelType.extractPrice(it) }
    return Pair(prices.minOrNull() ?: 0.0, prices.maxOrNull() ?: 0.0)
}

fun FuelStation.getPriceCategory(
    fuelType: FuelType,
    minPrice: Double,
    maxPrice: Double,
): PriceCategory {
    val currentPrice = fuelType.extractPrice(this)
    val priceRange = maxPrice - minPrice
    val step = priceRange / 3 // 3 range prices (cheap, normal, expensive)

    return when {
        currentPrice < minPrice + step -> PriceCategory.CHEAP
        currentPrice < minPrice + 2 * step -> PriceCategory.NORMAL
        else -> PriceCategory.EXPENSIVE
    }
}
