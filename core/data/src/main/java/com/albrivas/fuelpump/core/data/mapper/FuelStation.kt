/*
 * File: FuelStationModel.kt
 * Project: FuelPump
 * Module: FuelPump.core.data.main
 * Last modified: 1/7/23, 1:33 PM
 *
 * Created by albertorivas on 1/7/23, 1:33 PM
 * Copyright © 2023 Alberto Rivas. All rights reserved.
 *
 */

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
    priceBiodiesel = priceBiodiesel.takeIf { it.isNotEmpty() }?.replace(",", ".")?.toDouble() ?: 0.0,
    priceBioEthanol = priceBioEthanol.takeIf { it.isNotEmpty() }?.replace(",", ".")?.toDouble() ?: 0.0,
    priceGasNaturalCompressed = priceGasNaturalCompressed.takeIf { it.isNotEmpty() }?.replace(",", ".")?.toDouble() ?: 0.0,
    priceLiquefiedNaturalGas = priceLiquefiedNaturalGas.takeIf { it.isNotEmpty() }?.replace(",", ".")?.toDouble() ?: 0.0,
    priceLiquefiedPetroleumGas = priceLiquefiedPetroleumGas.takeIf { it.isNotEmpty() }?.replace(",", ".")?.toDouble() ?: 0.0,
    priceGasoilA = priceGasoilA.takeIf { it.isNotEmpty() }?.replace(",", ".")?.toDouble() ?: 0.0,
    priceGasoilB = priceGasoilB.takeIf { it.isNotEmpty() }?.replace(",", ".")?.toDouble() ?: 0.0,
    priceGasoilPremium = priceGasoilPremium.takeIf { it.isNotEmpty() }?.replace(",", ".")?.toDouble() ?: 0.0,
    priceGasoline95_E10 = priceGasoline95_E10.takeIf { it.isNotEmpty() }?.replace(",", ".")?.toDouble() ?: 0.0,
    priceGasoline95_E5 = priceGasoline95_E5.takeIf { it.isNotEmpty() }?.replace(",", ".")?.toDouble() ?: 0.0,
    priceGasoline95_E5_Premium = priceGasoline95_E5_Premium.takeIf { it.isNotEmpty() }?.replace(",", ".")?.toDouble() ?: 0.0,
    priceGasoline98_E10 = priceGasoline98_E10.takeIf { it.isNotEmpty() }?.replace(",", ".")?.toDouble() ?: 0.0,
    priceGasoline98_E5 = priceGasoline98_E5.takeIf { it.isNotEmpty() }?.replace(",", ".")?.toDouble() ?: 0.0,
    priceHydrogen = priceHydrogen.takeIf { it.isNotEmpty() }?.replace(",", ".")?.toDouble() ?: 0.0,
    province = province,
    referral = referral,
    brandStation = brandStation,
    typeSale = typeSale,
    lastUpdate = System.currentTimeMillis()
)