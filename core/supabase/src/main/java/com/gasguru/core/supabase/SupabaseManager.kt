package com.gasguru.core.supabase

interface SupabaseManager {
    suspend fun addPriceAlert(stationId: Int)
    suspend fun removePriceAlert(stationId: Int)
}