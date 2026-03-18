package com.gasguru.core.testing.fakes.data.supabase

import com.gasguru.core.supabase.SupabaseManager

class FakeSupabaseManager : SupabaseManager {
    val addedAlerts = mutableListOf<Int>()
    val removedAlerts = mutableListOf<Int>()

    var shouldThrowOnAdd: Exception? = null
    var shouldThrowOnRemove: Exception? = null

    override suspend fun addPriceAlert(
        stationId: Int,
        onesignalPlayerId: String,
        fuelType: String,
        lastNotifiedPrice: Double,
    ) {
        shouldThrowOnAdd?.let { throw it }
        addedAlerts.add(stationId)
    }

    override suspend fun removePriceAlert(stationId: Int) {
        shouldThrowOnRemove?.let { throw it }
        removedAlerts.add(stationId)
    }
}
