package com.gasguru.core.supabase.stubs

object StubsSupabaseResponse {

    fun fuelStationListSuccess(): String =
        StubsSupabaseResponse::class.java
            .classLoader!!
            .getResourceAsStream("responses/fuel_station_list.json")!!
            .bufferedReader()
            .readText()
}
