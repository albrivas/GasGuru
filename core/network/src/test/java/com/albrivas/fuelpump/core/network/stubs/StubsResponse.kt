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

package com.albrivas.fuelpump.core.network.stubs

object StubsResponse {
    fun getListFuelStations() = AssetsManager.getResponseJson(
        "com/albrivas/fuelpump/core/network/list_fuel_station.json"
    )
    fun getServerError() =
        AssetsManager.getResponseJson("com/albrivas/fuelpump/core/network/server_error.json")
}
