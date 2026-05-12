package com.gasguru.core.network.datasource

import arrow.core.Either
import com.gasguru.core.network.model.route.NetworkLatLng
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

@DisplayName("RoutesDataSourceImpl")
class RoutesDataSourceTest {

    private val origin = NetworkLatLng(latitude = 40.4168, longitude = -3.7038)
    private val destination = NetworkLatLng(latitude = 41.6488, longitude = -0.8891)

    private val validRouteJson = """
        {
          "geocodingResults": {},
          "routes": [
            {
              "description": "Madrid - Zaragoza",
              "distanceMeters": 320000,
              "duration": "10800s",
              "staticDuration": "10800s",
              "routeLabels": ["DEFAULT_ROUTE"],
              "polyline": { "encodedPolyline": "abc123" },
              "polylineDetails": {},
              "travelAdvisory": {},
              "legs": [],
              "localizedValues": {
                "distance": { "text": "320 km" },
                "duration": { "text": "3 h" },
                "staticDuration": { "text": "3 h" }
              },
              "viewport": {
                "high": { "latitude": 41.65, "longitude": -0.89 },
                "low": { "latitude": 40.41, "longitude": -3.70 }
              }
            }
          ]
        }
    """.trimIndent()

    private fun buildClient(
        responseBody: String,
        statusCode: HttpStatusCode = HttpStatusCode.OK,
    ): HttpClient {
        val mockEngine = MockEngine {
            respond(
                content = responseBody,
                status = statusCode,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        return HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            defaultRequest { url("https://routes.googleapis.com/") }
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN the routes API returns a valid route
        WHEN getRoute is called
        THEN returns Right with route data
        """,
    )
    fun `getRoute returns Right on success`() = runTest {
        val sut = RoutesDataSourceImpl(httpClient = buildClient(validRouteJson))

        val result = sut.getRoute(origin = origin, destination = destination)

        assertTrue(result is Either.Right)
        assertTrue(result.value.routes.isNotEmpty())
    }

    @Test
    @DisplayName(
        """
        GIVEN the routes API returns a server error
        WHEN getRoute is called
        THEN returns Left with a NetworkError
        """,
    )
    fun `getRoute returns Left on server error`() = runTest {
        val sut = RoutesDataSourceImpl(
            httpClient = buildClient(
                responseBody = "",
                statusCode = HttpStatusCode.InternalServerError,
            ),
        )

        val result = sut.getRoute(origin = origin, destination = destination)

        assertTrue(result is Either.Left)
    }
}
