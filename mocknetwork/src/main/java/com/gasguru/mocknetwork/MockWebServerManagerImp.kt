package com.gasguru.mocknetwork

import android.content.Context
import com.gasguru.core.common.IoDispatcher
import com.gasguru.core.network.retrofit.ApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockWebServerManagerImp @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : MockWebServerManager {

    private val mockWebServer: MockWebServer by lazy {
        MockWebServer().apply { start() }
    }

    override val apiService: ApiService by lazy {
        val baseUrl = mockWebServer.url("/").toString()
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
            .create(ApiService::class.java)
    }

    override suspend fun enqueueResponse(assetFileName: String, responseCode: Int) {
        val json = withContext(ioDispatcher) {
            readAssetFile(assetFileName)
        }

        val mockResponse = MockResponse()
            .setResponseCode(responseCode)
            .setBody(json)
            .addHeader("Content-Type", "application/json")

        mockWebServer.enqueue(mockResponse)
    }

    override fun shutdown() {
        mockWebServer.shutdown()
    }

    private fun readAssetFile(fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }
}
