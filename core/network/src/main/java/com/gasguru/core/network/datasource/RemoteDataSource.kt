package com.gasguru.core.network.datasource

import arrow.core.Either
import com.gasguru.core.network.model.NetworkError
import com.gasguru.core.network.model.NetworkFuelStation
import com.gasguru.core.network.model.NetworkPriceHistory

/**
 * Interface represent network call to the fuels API
 */
interface RemoteDataSource {
    suspend fun getListFuelStations(): Either<NetworkError, NetworkFuelStation>
    suspend fun getPriceHistory(
        date: String,
        idMunicipality: String,
        idStation: Int,
        idProduct: Int,
    ): Either<NetworkError, NetworkPriceHistory>
}
