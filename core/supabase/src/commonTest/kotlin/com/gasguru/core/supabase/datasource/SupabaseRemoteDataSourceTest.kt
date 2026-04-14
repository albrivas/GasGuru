package com.gasguru.core.supabase.datasource

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.supabase.fakes.FakeAnalyticsHelper
import com.gasguru.core.supabase.stubs.StubsSupabaseResponse
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SupabaseRemoteDataSourceTest {

    private lateinit var mockEngine: MockEngine
    private lateinit var sut: SupabaseRemoteDataSource
    private val analyticsHelper = FakeAnalyticsHelper()

    @BeforeTest
    fun setUp() {
        mockEngine = MockEngine { _ ->
            respond(
                content = ByteReadChannel(StubsSupabaseResponse.fuelStationListSuccess),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        sut = SupabaseRemoteDataSource(
            supabaseClient = createSupabaseClient(
                supabaseUrl = "https://fake.supabase.co",
                supabaseKey = "fake-key",
            ) {
                install(Postgrest)
                httpEngine = mockEngine
            },
            analyticsHelper = analyticsHelper,
        )
    }

    @AfterTest
    fun tearDown() {
        mockEngine.close()
    }

    @Test
    fun fuelStationSuccess() = runTest {
        val actual = sut.getListFuelStations()

        assertTrue(actual.isRight())
    }

    @Test
    fun fuelStationError() = runTest {
        val actual = buildSutWithError().getListFuelStations()

        assertTrue(actual.isLeft())
    }

    @Test
    fun fuelStationErrorLogsAnalyticsEvent() = runTest {
        buildSutWithError().getListFuelStations()

        val loggedEvent = analyticsHelper.loggedEvents.firstOrNull()
        assertNotNull(loggedEvent)
        assertEquals(AnalyticsEvent.Types.API_STATIONS_FETCH_FAILED, loggedEvent.type)
        assertTrue(loggedEvent.extras.any { param -> param.key == AnalyticsEvent.ParamKeys.ERROR_TYPE })
        assertTrue(loggedEvent.extras.any { param -> param.key == AnalyticsEvent.ParamKeys.ERROR_MESSAGE })
    }

    @Test
    fun fuelStationListSizeMatchesFixture() = runTest {
        val actual = sut.getListFuelStations()

        actual.onRight { stations ->
            assertEquals(2, stations.size)
        }
    }

    @Test
    fun nullPricesRemainNull() = runTest {
        val actual = sut.getListFuelStations()

        actual.onRight { stations ->
            val firstStation = stations.first()
            assertTrue(firstStation.priceGasoilPremium == null)
            assertTrue(firstStation.priceHydrogen == null)
            assertTrue(firstStation.priceBiodiesel == null)
        }
    }

    private fun buildSutWithError(): SupabaseRemoteDataSource = SupabaseRemoteDataSource(
        supabaseClient = createSupabaseClient(
            supabaseUrl = "https://fake.supabase.co",
            supabaseKey = "fake-key",
        ) {
            install(Postgrest)
            httpEngine = MockEngine { _ ->
                respond(
                    content = ByteReadChannel("Internal Server Error"),
                    status = HttpStatusCode.InternalServerError,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Text.Plain.toString()),
                )
            }
        },
        analyticsHelper = analyticsHelper,
    )
}
