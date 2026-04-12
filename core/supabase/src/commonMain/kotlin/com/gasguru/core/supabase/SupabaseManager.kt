package com.gasguru.core.supabase

interface SupabaseManager {
    suspend fun addPriceAlert(
        stationId: Int,
        onesignalPlayerId: String,
        fuelType: String,
        lastNotifiedPrice: Double,
    )
    suspend fun removePriceAlert(stationId: Int)
}
