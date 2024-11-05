package com.gasguru.core.network.stubs

object StubsResponse {
    fun getListFuelStations() = AssetsManager.getResponseJson(
        "com/gasguru/core/network/list_fuel_station.json"
    )
    fun getServerError() =
        AssetsManager.getResponseJson("com/gasguru/core/network/server_error.json")
}
