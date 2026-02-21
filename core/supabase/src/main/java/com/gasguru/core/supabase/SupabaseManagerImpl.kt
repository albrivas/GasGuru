package com.gasguru.core.supabase

import com.gasguru.core.supabase.model.PriceAlertSupabase
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
class SupabaseManagerImpl(
    private val supabaseClient: SupabaseClient,
) : SupabaseManager {

    companion object {
        private const val TABLE_ALERTS = "user_stations_alerts"
    }

    override suspend fun addPriceAlert(
        stationId: Int,
        onesignalPlayerId: String,
        fuelType: String,
        lastNotifiedPrice: Double,
    ) {
        supabaseClient
            .from(TABLE_ALERTS)
            .insert(
                PriceAlertSupabase(
                    stationId = stationId,
                    onesignalPlayerId = onesignalPlayerId,
                    fuelType = fuelType,
                    lastNotifiedPrice = lastNotifiedPrice,
                )
            )
    }

    override suspend fun removePriceAlert(stationId: Int) {
        supabaseClient
            .from(TABLE_ALERTS)
            .delete {
                filter {
                    eq(column = "id_service_station", value = stationId)
                }
            }
    }
}
