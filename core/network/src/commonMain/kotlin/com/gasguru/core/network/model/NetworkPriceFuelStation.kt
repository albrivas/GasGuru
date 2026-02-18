package com.gasguru.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkPriceFuelStation(
    @SerialName("% BioEtanol")
    val bioEthanolPercentage: String,
    @SerialName("% Éster metílico")
    val esterMethylPercentage: String,
    @SerialName("C.P.")
    val postalCode: String,
    @SerialName("Dirección")
    val direction: String,
    @SerialName("Horario")
    val schedule: String,
    @SerialName("IDCCAA")
    val idAutonomousCommunity: String,
    @SerialName("IDEESS")
    val idServiceStation: String,
    @SerialName("IDMunicipio")
    val idMunicipality: String,
    @SerialName("IDProvincia")
    val idProvince: String,
    @SerialName("Latitud")
    val latitude: String,
    @SerialName("Localidad")
    val locality: String,
    @SerialName("Longitud (WGS84)")
    val longitudeWGS84: String,
    @SerialName("Margen")
    val margin: String,
    @SerialName("Municipio")
    val municipality: String,
    @SerialName("Precio Biodiesel")
    val priceBiodiesel: String,
    @SerialName("Precio Bioetanol")
    val priceBioEthanol: String,
    @SerialName("Precio Gas Natural Comprimido")
    val priceGasNaturalCompressed: String,
    @SerialName("Precio Gas Natural Licuado")
    val priceLiquefiedNaturalGas: String,
    @SerialName("Precio Gases licuados del petróleo")
    val priceLiquefiedPetroleumGas: String,
    @SerialName("Precio Gasoleo A")
    val priceGasoilA: String,
    @SerialName("Precio Gasoleo B")
    val priceGasoilB: String,
    @SerialName("Precio Gasoleo Premium")
    val priceGasoilPremium: String,
    @SerialName("Precio Gasolina 95 E10")
    val priceGasoline95E10: String,
    @SerialName("Precio Gasolina 95 E5")
    val priceGasoline95E5: String,
    @SerialName("Precio Gasolina 95 E5 Premium")
    val priceGasoline95E5Premium: String,
    @SerialName("Precio Gasolina 98 E10")
    val priceGasoline98E10: String,
    @SerialName("Precio Gasolina 98 E5")
    val priceGasoline98E5: String,
    @SerialName("Precio Hidrogeno")
    val priceHydrogen: String,
    @SerialName("Precio Adblue")
    val priceAdblue: String,
    @SerialName("Provincia")
    val province: String,
    @SerialName("Remisión")
    val referral: String,
    @SerialName("Rótulo")
    val brandStation: String,
    @SerialName("Tipo Venta")
    val typeSale: String,
)
