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
    val priceGasoline95_E10: Double,
    val priceGasoline95_E5: Double,
    val priceGasoline95_E5_Premium: Double,
    val priceGasoline98_E10: Double,
    val priceGasoline98_E5: Double,
    val priceHydrogen: Double,
    val province: String,
    val referral: String,
    val brandStation: String,
    val typeSale: String,
    @ColumnInfo(defaultValue = "0")
    val lastUpdate: Long
)


fun FuelStationEntity.asExternalModel() = FuelStation(
    bioEthanolPercentage,
    esterMethylPercentage,
    postalCode,
    direction,
    schedule,
    idAutonomousCommunity,
    idServiceStation,
    idMunicipality,
    idProvince,
    getLocation(),
    locality,
    margin,
    municipality,
    priceGasoilA,
    priceGasoilB,
    priceGasoilPremium,
    priceGasoline95_E10,
    priceGasoline95_E5,
    priceGasoline95_E5_Premium,
    priceGasoline98_E10,
    priceGasoline98_E5,
    priceHydrogen,
    province,
    referral,
    brandStation,
    brandStation.toBrandStation(),
    typeSale
)

fun String.toBrandStation(): FuelStationBrandsType {
    return when {
        lowercase().contains("repsol") -> FuelStationBrandsType.REPSOL
        lowercase().contains("petronor") -> FuelStationBrandsType.PETRONOR
        lowercase().contains("galp") -> FuelStationBrandsType.GALP
        lowercase().contains("bp") -> FuelStationBrandsType.BP
        lowercase().contains("shell") -> FuelStationBrandsType.SHELL
        lowercase().contains("carrefour") -> FuelStationBrandsType.CARREFOUR
        lowercase().contains("cepsa") -> FuelStationBrandsType.CEPSA
        lowercase().contains("eroski") -> FuelStationBrandsType.EROSKI
        lowercase().contains("bonarea") -> FuelStationBrandsType.BONAREA
        lowercase().contains("alcampo") -> FuelStationBrandsType.ALCAMPO
        lowercase().contains("meroil") -> FuelStationBrandsType.MEROIL
        lowercase().contains("ballenoil") -> FuelStationBrandsType.BALLENOIL
        lowercase().contains("esso") -> FuelStationBrandsType.ESSO
        lowercase().contains("makro") -> FuelStationBrandsType.MAKRO
        lowercase().contains("tgas") -> FuelStationBrandsType.TGAS
        lowercase().contains("eleclerc") -> FuelStationBrandsType.ELECLERC
        lowercase().contains("eclerc") -> FuelStationBrandsType.ECLERC
        lowercase().contains("disa") -> FuelStationBrandsType.DISA
        lowercase().contains("pc") -> FuelStationBrandsType.PC
        lowercase().contains("texaco") -> FuelStationBrandsType.TEXACO
        lowercase().contains("zoloil") -> FuelStationBrandsType.ZOLOIL
        lowercase().contains("q8") -> FuelStationBrandsType.Q8
        lowercase().contains("azul-oil") -> FuelStationBrandsType.AZUL_OIL
        lowercase().contains("silver") -> FuelStationBrandsType.SILVER_FUEL
        lowercase().contains("farruco") -> FuelStationBrandsType.FARRUCO
        lowercase().contains("fernandez bermejo") -> FuelStationBrandsType.REPOSTAR
        else -> FuelStationBrandsType.UNKOWN
    }
}

fun FuelStationEntity.getLocation() = Location("").apply {
    latitude = this@getLocation.latitude
    longitude = this@getLocation.longitudeWGS84
}

