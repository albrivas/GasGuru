package com.gasguru.core.supabase.model

import kotlinx.serialization.Serializable

@Serializable
data class PriceAlertSupabase(
    val stationId: Int,
    val createdAt: String? = null
)