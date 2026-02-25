package com.gasguru.core.network.datasource

import arrow.core.Either
import com.gasguru.core.network.common.tryCall
import com.gasguru.core.network.di.FuelApi
import com.gasguru.core.network.model.NetworkError
import com.gasguru.core.network.model.NetworkFuelStation
import com.gasguru.core.network.retrofit.ApiService

class RemoteDataSourceImp(
    private val api: ApiService,
) : RemoteDataSource {

    override suspend fun getListFuelStations(): Either<NetworkError, NetworkFuelStation> = tryCall {
        api.listFuelStations()
    }
}
