package com.gasguru.core.network.mockwebserver

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.MockWebServer
import java.util.concurrent.TimeUnit

class NetworkModuleTest {

    val mockWebServer: MockWebServer = MockWebServer()

    val httpClient: HttpClient
        get() = HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            engine {
                config {
                    readTimeout(1, TimeUnit.SECONDS)
                    connectTimeout(1, TimeUnit.SECONDS)
                    retryOnConnectionFailure(false)
                }
            }
            defaultRequest {
                url(mockWebServer.url("/").toString())
            }
        }
}
