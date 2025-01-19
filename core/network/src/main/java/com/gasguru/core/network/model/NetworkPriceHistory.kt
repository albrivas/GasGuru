package com.gasguru.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkPriceHistory(
    @Json(name = "C.P.")
    val postalCode: String,
    @Json(name = "Dirección")
    val address: String,
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
    @Json(name = "PrecioProducto")
    val price: Double,
    @Json(name = "Provincia")
    val province: String,
    @Json(name = "Remisión")
    val referral: String,
    @Json(name = "Rótulo")
    val brandStation: String,
    @Json(name = "Tipo Venta")
    val typeSale: String
)
