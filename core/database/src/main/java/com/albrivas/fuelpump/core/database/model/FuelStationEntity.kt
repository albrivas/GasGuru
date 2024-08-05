/*
 * File: FuelStationEntity.kt
 * Project: FuelPump
 * Module: FuelPump.core.database.main
 * Last modified: 1/4/23, 10:13 PM
 *
 * Created by albertorivas on 1/5/23, 12:13 AM
 * Copyright © 2023 Alberto Rivas. All rights reserved.
 *
 */

package com.albrivas.fuelpump.core.database.model

import android.location.Location
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.albrivas.fuelpump.core.model.data.FuelStation
import com.albrivas.fuelpump.core.model.data.FuelStationBrandsType

@Entity(
    tableName = "fuel-station"
)
data class FuelStationEntity(
    val bioEthanolPercentage: String,
    val esterMethylPercentage: String,
    val postalCode: String,
    val direction: String,
    val schedule: String,
    val idAutonomousCommunity: String,
    @PrimaryKey(autoGenerate = false)
    val idServiceStation: Int,
    val idMunicipality: String,
    val idProvince: String,
    val latitude: Double,
    val locality: String,
    val longitudeWGS84: Double,
    val margin: String,
    val municipality: String,
    val priceBiodiesel: Double,
    val priceBioEthanol: Double,
    val priceGasNaturalCompressed: Double,
    val priceLiquefiedNaturalGas: Double,
    val priceLiquefiedPetroleumGas: Double,
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
    val brandStation: String,
    val typeSale: String,
    @ColumnInfo(defaultValue = "0")
    val lastUpdate: Long,
    @ColumnInfo(defaultValue = "0")
    val isFavorite: Boolean,

    )

fun FuelStationEntity.asExternalModel() = FuelStation(
    bioEthanolPercentage = bioEthanolPercentage,
    esterMethylPercentage = esterMethylPercentage,
    postalCode = postalCode,
    direction = direction,
    schedule = schedule,
    idAutonomousCommunity = idAutonomousCommunity,
    idServiceStation = idServiceStation,
    idMunicipality = idMunicipality,
    idProvince = idProvince,
    location = getLocation(),
    locality = locality,
    margin = margin,
    municipality = municipality,
    priceGasoilA = priceGasoilA,
    priceGasoilB = priceGasoilB,
    priceGasoilPremium = priceGasoilPremium,
    priceGasoline95E10 = priceGasoline95E10,
    priceGasoline95E5 = priceGasoline95E5,
    priceGasoline95E5Premium = priceGasoline95E5Premium,
    priceGasoline98E10 = priceGasoline98E10,
    priceGasoline98E5 = priceGasoline98E5,
    priceHydrogen = priceHydrogen,
    province = province,
    referral = referral,
    brandStationName = brandStation,
    brandStationBrandsType = brandStation.toBrandStation(),
    typeSale = typeSale,
    isFavorite = isFavorite,
)

fun String.toBrandStation(): FuelStationBrandsType {
    val brandMap = mapOf(
        "repsol" to FuelStationBrandsType.REPSOL,
        "petronor" to FuelStationBrandsType.PETRONOR,
        "galp" to FuelStationBrandsType.GALP,
        "bp" to FuelStationBrandsType.BP,
        "shell" to FuelStationBrandsType.SHELL,
        "carrefour" to FuelStationBrandsType.CARREFOUR,
        "cepsa" to FuelStationBrandsType.CEPSA,
        "eroski" to FuelStationBrandsType.EROSKI,
        "bonarea" to FuelStationBrandsType.BONAREA,
        "alcampo" to FuelStationBrandsType.ALCAMPO,
        "meroil" to FuelStationBrandsType.MEROIL,
        "ballenoil" to FuelStationBrandsType.BALLENOIL,
        "esso" to FuelStationBrandsType.ESSO,
        "makro" to FuelStationBrandsType.MAKRO,
        "tgas" to FuelStationBrandsType.TGAS,
        "eleclerc" to FuelStationBrandsType.ELECLERC,
        "eclerc" to FuelStationBrandsType.ECLERC,
        "disa" to FuelStationBrandsType.DISA,
        "pc" to FuelStationBrandsType.PC,
        "texaco" to FuelStationBrandsType.TEXACO,
        "zoloil" to FuelStationBrandsType.ZOLOIL,
        "q8" to FuelStationBrandsType.Q8,
        "azul-oil" to FuelStationBrandsType.AZUL_OIL,
        "silver" to FuelStationBrandsType.SILVER_FUEL,
        "farruco" to FuelStationBrandsType.FARRUCO,
        "fernandez bermejo" to FuelStationBrandsType.REPOSTAR
    )

    val brandNameLowercase = this.lowercase()
    brandMap.forEach { (key, value) ->
        if (brandNameLowercase.contains(key)) return value
    }
    return FuelStationBrandsType.UNKOWN // Manejo del caso 'else'
}

fun FuelStationEntity.getLocation() = Location("").apply {
    latitude = this@getLocation.latitude
    longitude = this@getLocation.longitudeWGS84
}
