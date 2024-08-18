package com.albrivas.fuelpump.core.data.mapper

import com.albrivas.fuelpump.core.database.model.FuelStationEntity
import com.albrivas.fuelpump.core.network.model.NetworkPriceFuelStation

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
