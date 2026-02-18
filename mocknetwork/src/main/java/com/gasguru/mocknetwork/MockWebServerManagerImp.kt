package com.gasguru.mocknetwork

import android.content.Context
import com.gasguru.core.common.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockWebServerManagerImp @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : MockWebServerManager {

    private val pendingResponses = ArrayDeque<Pair<String, Int>>()

    private val engine = MockEngine { _ ->
        val (body, code) = pendingResponses.removeFirst()
        respond(
            content = ByteReadChannel(body),
            status = HttpStatusCode.fromValue(code),
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
        )
    }

    override val httpClient: HttpClient = HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    override suspend fun enqueueResponse(assetFileName: String, responseCode: Int) {
        val json = withContext(ioDispatcher) {
            context.assets.open(assetFileName).bufferedReader().use { it.readText() }
        }
        pendingResponses.addLast(json to responseCode)
    }

    override fun close() {
        httpClient.close()
    }
}
