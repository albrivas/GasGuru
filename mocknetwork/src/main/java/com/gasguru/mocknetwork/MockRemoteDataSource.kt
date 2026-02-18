package com.gasguru.mocknetwork

import arrow.core.Either
import com.gasguru.core.network.common.tryCall
import com.gasguru.core.network.datasource.RemoteDataSource
import com.gasguru.core.network.model.NetworkError
import com.gasguru.core.network.model.NetworkFuelStation
import io.ktor.client.call.body
import io.ktor.client.request.get
import jakarta.inject.Inject

class MockRemoteDataSource @Inject constructor(
    private val mockWebServerManager: MockWebServerManager,
) : RemoteDataSource {
    override suspend fun getListFuelStations(): Either<NetworkError, NetworkFuelStation> = tryCall {
        mockWebServerManager.enqueueResponse(
            assetFileName = "fuel-stations.json",
            responseCode = 200,
        )
        mockWebServerManager.httpClient.get("fuel-stations").body()
    }
}
