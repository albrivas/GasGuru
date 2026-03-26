package com.gasguru.core.supabase.model

data class NetworkFuelStation(
    val date: String,
    val listPriceFuelStation: List<NetworkPriceFuelStation>,
)
