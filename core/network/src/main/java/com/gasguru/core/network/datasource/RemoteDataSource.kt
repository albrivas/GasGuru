package com.gasguru.core.network.datasource

import arrow.core.Either
import com.gasguru.core.network.model.NetworkError
import com.gasguru.core.network.model.NetworkFuelStation

/**
 * Interface represent network call to the fuels API
 */
interface RemoteDataSource {
    suspend fun getListFuelStations(): Either<NetworkError, NetworkFuelStation>
}
