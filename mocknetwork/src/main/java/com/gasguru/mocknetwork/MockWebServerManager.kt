package com.gasguru.mocknetwork

import io.ktor.client.HttpClient

interface MockWebServerManager {
    val httpClient: HttpClient
    suspend fun enqueueResponse(assetFileName: String, responseCode: Int)
    fun close()
}
