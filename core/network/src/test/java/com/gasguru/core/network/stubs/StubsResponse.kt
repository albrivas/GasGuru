package com.gasguru.core.network.stubs

object StubsResponse {
    fun getListFuelStations() = AssetsManager.getResponseJson("list_fuel_station.json")
    fun getServerError() = AssetsManager.getResponseJson("server_error.json")
}
