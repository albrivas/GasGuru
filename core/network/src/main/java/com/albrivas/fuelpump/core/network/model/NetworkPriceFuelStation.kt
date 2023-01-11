/*
 * File: NetworkPriceFuelStation.kt
 * Project: FuelPump
 * Module: FuelPump.core.network.main
 * Last modified: 12/29/22, 5:09 PM
 *
 * Created by albertorivas on 12/29/22, 5:33 PM
 * Copyright © 2022 Alberto Rivas. All rights reserved.
 *
 */

package com.albrivas.fuelpump.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkPriceFuelStation(
    @Json(name = "% BioEtanol")
    val bioEthanolPercentage: String,
    @Json(name = "% Éster metílico")
    val esterMethylPercentage: String,
    @Json(name = "C.P.")
    val postalCode: String,
    @Json(name = "Dirección")
    val direction: String,
    @Json(name = "Horario")
    val schedule: String,
    @Json(name = "IDCCAA")
    val idAutonomousCommunity: String,
    @Json(name = "IDEESS")
    val idServiceStation: String,
    @Json(name = "IDMunicipio")
    val idMunicipality: String,
    @Json(name = "IDProvincia")
    val idProvince: String,
    @Json(name = "Latitud")
    val latitude: String,
    @Json(name = "Localidad")
    val locality: String,
    @Json(name = "Longitud (WGS84)")
    val longitudeWGS84: String,
    @Json(name = "Margen")
    val margin: String,
    @Json(name = "Municipio")
    val municipality: String,
    @Json(name = "Precio Biodiesel")
    val priceBiodiesel: String,
    @Json(name = "Precio Bioetanol")
    val priceBioEthanol: String,
    @Json(name = "Precio Gas Natural Comprimido")
    val priceGasNaturalCompressed: String,
    @Json(name = "Precio Gas Natural Licuado")
    val priceLiquefiedNaturalGas: String,
    @Json(name = "Precio Gases licuados del petróleo")
    val priceLiquefiedPetroleumGas: String,
    @Json(name = "Precio Gasoleo A")
    val priceGasoilA: String,
    @Json(name = "Precio Gasoleo B")
    val priceGasoilB: String,
    @Json(name = "Precio Gasoleo Premium")
    val priceGasoilPremium: String,
    @Json(name = "Precio Gasolina 95 E10")
    val priceGasoline95_E10: String,
    @Json(name = "Precio Gasolina 95 E5")
    val priceGasoline95_E5: String,
    @Json(name = "Precio Gasolina 95 E5 Premium")
    val priceGasoline95_E5_Premium: String,
    @Json(name = "Precio Gasolina 98 E10")
    val priceGasoline98_E10: String,
    @Json(name = "Precio Gasolina 98 E5")
    val priceGasoline98_E5: String,
    @Json(name = "Precio Hidrogeno")
    val priceHydrogen: String,
    @Json(name = "Provincia")
    val province: String,
    @Json(name = "Remisión")
    val referral: String,
    @Json(name = "Rótulo")
    val brandStation: String,
    @Json(name = "Tipo Venta")
    val typeSale: String
)