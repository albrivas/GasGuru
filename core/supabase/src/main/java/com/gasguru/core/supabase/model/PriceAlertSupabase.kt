package com.gasguru.core.supabase.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PriceAlertSupabase(
    @SerialName("id_service_station")
    val stationId: Int,
    @SerialName("onesignal_player_id")
    val onesignalPlayerId: String,
    @SerialName("last_notified_price")
    val lastNotifiedPrice: Double? = null,
    @SerialName("fuel_type")
    val fuelType: String,
    @SerialName("created_at") 
    val createdAt: String? = null,
)