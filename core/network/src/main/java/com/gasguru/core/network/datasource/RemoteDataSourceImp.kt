package com.gasguru.core.network.datasource

import arrow.core.Either
import com.gasguru.core.network.common.tryCall
import com.gasguru.core.network.di.FuelApi
import com.gasguru.core.network.model.NetworkError
import com.gasguru.core.network.model.NetworkFuelStation
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class RemoteDataSourceImp @Inject constructor(
    @FuelApi private val httpClient: HttpClient,
) : RemoteDataSource {

    override suspend fun getListFuelStations(): Either<NetworkError, NetworkFuelStation> = tryCall {
        httpClient.get("ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/").body()
    }
}
