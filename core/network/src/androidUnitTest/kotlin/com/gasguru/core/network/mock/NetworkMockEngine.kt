package com.gasguru.core.network.mock

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.serialization.json.Json

data class MockEngineResponse(val body: String, val code: Int)

class NetworkMockEngine {

    private val pendingResponses = ArrayDeque<MockEngineResponse>()

    private val engine = MockEngine { _ ->
        val response = pendingResponses.removeFirst()
        respond(
            content = ByteReadChannel(response.body),
            status = HttpStatusCode.fromValue(response.code),
            headers = headersOf(HttpHeaders.ContentType, "application/json; charset=utf-8"),
        )
    }

    val httpClient: HttpClient = HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    fun enqueue(response: MockEngineResponse) {
        pendingResponses.addLast(response)
    }

    fun close() {
        httpClient.close()
    }
}
