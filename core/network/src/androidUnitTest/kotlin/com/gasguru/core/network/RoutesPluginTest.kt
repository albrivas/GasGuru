package com.gasguru.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DisplayName("RoutesPlugin")
class RoutesPluginTest {

    @Test
    @DisplayName(
        """
        GIVEN a HttpClient with routesPlugin installed
        WHEN a request is made
        THEN the required Google API headers are present
        """,
    )
    fun `routesPlugin adds required google headers to request`() = runTest {
        var capturedHeaders: io.ktor.http.Headers? = null
        val testPackageName = "com.gasguru.test"

        val mockEngine = MockEngine { request ->
            capturedHeaders = request.headers
            respond(
                content = "{}",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }

        val client = HttpClient(mockEngine) {
            install(routesPlugin(packageName = testPackageName))
        }

        client.get("https://routes.googleapis.com/test")

        val headers = requireNotNull(capturedHeaders)
        assertNotNull(headers["Content-Type"])
        assertNotNull(headers["X-Goog-Api-Key"])
        assertNotNull(headers["X-Goog-FieldMask"])
        assertEquals(testPackageName, headers["X-Android-Package"])
        assertNotNull(headers["X-Android-Cert"])
    }
}
