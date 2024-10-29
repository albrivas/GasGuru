/*
 * File: StubsResponse.kt
 * Project: FuelPump
 * Module: FuelPump.core.network.unitTest
 * Last modified: 12/29/22, 5:29 PM
 *
 * Created by albertorivas on 12/29/22, 5:33 PM
 * Copyright © 2022 Alberto Rivas. All rights reserved.
 *
 */

package com.gasguru.core.network.stubs

object StubsResponse {
    fun getListFuelStations() = AssetsManager.getResponseJson(
        "com/gasguru/core/network/list_fuel_station.json"
    )
    fun getServerError() =
        AssetsManager.getResponseJson("com/gasguru/core/network/server_error.json")
}
