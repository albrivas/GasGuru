/*
 * File: RemoteDataSourceImp.kt
 * Project: FuelPump
 * Module: FuelPump.core.network.main
 * Last modified: 12/28/22, 8:32 PM
 *
 * Created by albertorivas on 12/29/22, 5:33 PM
 * Copyright © 2022 Alberto Rivas. All rights reserved.
 *
 */

package com.gasguru.core.network.datasource

import arrow.core.Either
import com.gasguru.core.network.common.tryCall
import com.gasguru.core.network.model.NetworkError
import com.gasguru.core.network.model.NetworkFuelStation
import com.gasguru.core.network.retrofit.ApiService
import javax.inject.Inject

class RemoteDataSourceImp @Inject constructor(
    private val api: ApiService
) : RemoteDataSource {

    override suspend fun getListFuelStations(): Either<NetworkError, NetworkFuelStation> = tryCall {
        api.listFuelStations()
    }
}
