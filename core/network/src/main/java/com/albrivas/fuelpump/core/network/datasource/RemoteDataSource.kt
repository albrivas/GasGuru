package com.albrivas.fuelpump.core.network.datasource

import arrow.core.Either
import com.albrivas.fuelpump.core.network.model.NetworkError
import com.albrivas.fuelpump.core.network.model.NetworkFuelStation

/**
 * Interface represent network call to the fuels API
 */
interface RemoteDataSource {
    suspend fun getListFuelStations(): Either<NetworkError, NetworkFuelStation>
}