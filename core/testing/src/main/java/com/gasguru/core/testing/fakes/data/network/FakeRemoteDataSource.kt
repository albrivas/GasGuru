package com.gasguru.core.testing.fakes.data.network

import arrow.core.Either
import com.gasguru.core.network.datasource.RemoteDataSource
import com.gasguru.core.network.model.NetworkError
import com.gasguru.core.network.model.NetworkFuelStation

class FakeRemoteDataSource(
    var result: Either<NetworkError, NetworkFuelStation> =
        Either.Right(NetworkFuelStation(date = "", listPriceFuelStation = emptyList()))
) : RemoteDataSource {
    var getListFuelStationsCalls = 0
        private set

    override suspend fun getListFuelStations(): Either<NetworkError, NetworkFuelStation> {
        getListFuelStationsCalls += 1
        return result
    }
}
