/*
 * File: MockApiResponse.kt
 * Project: FuelPump
 * Module: FuelPump.core.network.unitTest
 * Last modified: 12/29/22, 5:29 PM
 *
 * Created by albertorivas on 12/29/22, 5:33 PM
 * Copyright © 2022 Alberto Rivas. All rights reserved.
 *
 */

package com.albrivas.fuelpump.core.network.stubs

import android.speech.SpeechRecognizer.ERROR_SERVER
import okhttp3.Headers
import okhttp3.Headers.Companion.toHeaders
import okhttp3.mockwebserver.MockResponse
import java.net.HttpURLConnection
import java.util.*
import javax.inject.Inject

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