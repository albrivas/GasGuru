package com.gasguru.mocknetwork

import com.gasguru.core.network.retrofit.ApiService

interface MockWebServerManager {
    val apiService: ApiService
    suspend fun enqueueResponse(assetFileName: String, responseCode: Int)
    fun shutdown()
}