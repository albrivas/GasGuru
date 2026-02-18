package com.gasguru.core.network.stubs

import com.gasguru.core.network.mock.MockEngineResponse
import java.net.HttpURLConnection

class MockApiResponse {

    fun listFuelStationOK(): MockEngineResponse = MockEngineResponse(
        body = StubsResponse.getListFuelStations(),
        code = HttpURLConnection.HTTP_OK,
    )

    fun listFuelStationKO(): MockEngineResponse = MockEngineResponse(
        body = StubsResponse.getServerError(),
        code = HttpURLConnection.HTTP_INTERNAL_ERROR,
    )
}
