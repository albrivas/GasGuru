/*
 * File: NetworkModuleTest.kt
 * Project: FuelPump
 * Module: FuelPump.core.network.unitTest
 * Last modified: 12/29/22, 12:12 PM
 *
 * Created by albertorivas on 12/29/22, 5:33 PM
 * Copyright © 2022 Alberto Rivas. All rights reserved.
 *
 */

package com.albrivas.fuelpump.core.network

import com.albrivas.fuelpump.core.network.retrofit.ApiService
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class NetworkModuleTest {

    val mockWebServer: MockWebServer = MockWebServer()

    val apiService: ApiService
        get() = retrofitTest.create(ApiService::class.java)

    private val endPoint: String
        get() = "/"

    private val moshi: Moshi
        get() {
            val moshiBuilder = Moshi.Builder()
            return moshiBuilder.build()
        }

    private val okHttpClient: OkHttpClient
        get() {
            val builder = OkHttpClient.Builder()
            builder.readTimeout(1, TimeUnit.SECONDS)
            builder.connectTimeout(1, TimeUnit.SECONDS)
            builder.retryOnConnectionFailure(false)
            return builder.build()
        }

    private val retrofitTest: Retrofit
        get() = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(mockWebServer.url(endPoint))
            .client(okHttpClient)
            .build()
}
