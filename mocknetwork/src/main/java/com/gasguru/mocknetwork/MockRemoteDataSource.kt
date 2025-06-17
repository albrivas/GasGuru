package com.gasguru.mocknetwork

import arrow.core.Either
import com.gasguru.core.network.common.tryCall
import com.gasguru.core.network.datasource.RemoteDataSource
import com.gasguru.core.network.model.NetworkError
import com.gasguru.core.network.model.NetworkFuelStation
import jakarta.inject.Inject

class MockRemoteDataSource @Inject constructor(
    private val mockWebServerManager: MockWebServerManagerImp,
) : RemoteDataSource {
    override suspend fun getListFuelStations(): Either<NetworkError, NetworkFuelStation> = tryCall {
        mockWebServerManager.enqueueResponse(
            assetFileName = "fuel-stations.json",
            responseCode = 200
        )
        mockWebServerManager.apiService.listFuelStations()
    }
}