/*
 * File: FuelStation.kt
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
    latitude = latitude,
    locality = locality,
    longitudeWGS84 = longitudeWGS84,
    margin = margin,
    municipality = municipality,
    priceBiodiesel = priceBiodiesel,
    priceBioEthanol = priceBioEthanol,
    priceGasNaturalCompressed = priceGasNaturalCompressed,
    priceLiquefiedNaturalGas = priceLiquefiedNaturalGas,
    priceLiquefiedPetroleumGas = priceLiquefiedPetroleumGas,
    priceGasoilA = priceGasoilA,
    priceGasoilB = priceGasoilB,
    priceGasoilPremium = priceGasoilPremium,
    priceGasoline95_E10 = priceGasoline95_E10,
    priceGasoline95_E5 = priceGasoline95_E5,
    priceGasoline95_E5_Premium = priceGasoline95_E5_Premium,
    priceGasoline98_E10 = priceGasoline98_E10,
    priceGasoline98_E5 = priceGasoline98_E5,
    priceHydrogen = priceHydrogen,
    province = province,
    referral = referral,
    brandStation = brandStation,
    typeSale = typeSale
)