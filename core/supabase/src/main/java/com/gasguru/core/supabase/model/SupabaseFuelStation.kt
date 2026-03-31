package com.gasguru.core.supabase.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseFuelStation(
    @SerialName("bio_ethanol_percentage") val bioEthanolPercentage: String = "",
    @SerialName("ester_methyl_percentage") val esterMethylPercentage: String = "",
    @SerialName("postal_code") val postalCode: String = "",
    @SerialName("direction") val direction: String = "",
    @SerialName("schedule") val schedule: String = "",
    @SerialName("id_autonomous_community") val idAutonomousCommunity: String = "",
    @SerialName("id_service_station") val idServiceStation: String = "",
    @SerialName("id_municipality") val idMunicipality: String = "",
    @SerialName("id_province") val idProvince: String = "",
    @SerialName("latitude") val latitude: Double = 0.0,
    @SerialName("locality") val locality: String = "",
    @SerialName("longitude_wgs84") val longitudeWGS84: Double = 0.0,
    @SerialName("margin") val margin: String = "",
    @SerialName("municipality") val municipality: String = "",
    @SerialName("price_biodiesel") val priceBiodiesel: Double? = null,
    @SerialName("price_bio_ethanol") val priceBioEthanol: Double? = null,
    @SerialName("price_gas_natural_compressed") val priceGasNaturalCompressed: Double? = null,
    @SerialName("price_liquefied_natural_gas") val priceLiquefiedNaturalGas: Double? = null,
    @SerialName("price_liquefied_petroleum_gas") val priceLiquefiedPetroleumGas: Double? = null,
    @SerialName("price_gasoil_a") val priceGasoilA: Double? = null,
    @SerialName("price_gasoil_b") val priceGasoilB: Double? = null,
    @SerialName("price_gasoil_premium") val priceGasoilPremium: Double? = null,
    @SerialName("price_gasoline_95_e10") val priceGasoline95E10: Double? = null,
    @SerialName("price_gasoline_95_e5") val priceGasoline95E5: Double? = null,
    @SerialName("price_gasoline_95_e5_premium") val priceGasoline95E5Premium: Double? = null,
    @SerialName("price_gasoline_98_e10") val priceGasoline98E10: Double? = null,
    @SerialName("price_gasoline_98_e5") val priceGasoline98E5: Double? = null,
    @SerialName("price_hydrogen") val priceHydrogen: Double? = null,
    @SerialName("price_adblue") val priceAdblue: Double? = null,
    @SerialName("province") val province: String = "",
    @SerialName("referral") val referral: String = "",
    @SerialName("brand_station") val brandStation: String = "",
    @SerialName("type_sale") val typeSale: String = "",
)
