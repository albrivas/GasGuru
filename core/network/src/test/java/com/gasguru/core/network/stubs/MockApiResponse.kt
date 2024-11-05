package com.gasguru.core.network.stubs

import okhttp3.Headers
import okhttp3.Headers.Companion.toHeaders
import okhttp3.mockwebserver.MockResponse
import java.net.HttpURLConnection
import java.util.*

class MockApiResponse {

    companion object {
        private val headers: Headers
            get() {
                val mapHeader = TreeMap<String, String>()
                mapHeader["Content-Type"] = "application/json; charset=utf-8"
                mapHeader["Cache-Control"] = "no-cache"
                return mapHeader.toHeaders()
            }
    }

    fun listFuelStationOK(): MockResponse {
        return MockResponse().setHeaders(headers)
            .setBody(StubsResponse.getListFuelStations())
            .setResponseCode(HttpURLConnection.HTTP_OK)
    }

    fun listFuelStationKO(): MockResponse {
        return MockResponse().setHeaders(headers)
            .setBody(StubsResponse.getServerError())
            .setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
    }
}
