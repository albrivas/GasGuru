package com.gasguru.core.supabase.datasource

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.supabase.stubs.StubsSupabaseResponse
import com.gasguru.core.testing.CoroutinesTestExtension
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@DisplayName("SupabaseRemoteDataSource")
@ExtendWith(CoroutinesTestExtension::class)
class SupabaseRemoteDataSourceTest {

    private lateinit var mockEngine: MockEngine
    private lateinit var sut: SupabaseRemoteDataSource
    private val analyticsHelper: AnalyticsHelper = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        mockEngine = MockEngine { _ ->
            respond(
                content = ByteReadChannel(StubsSupabaseResponse.fuelStationListSuccess()),
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

    @AfterEach
    fun tearDown() {
        mockEngine.close()
    }

    @Test
    @DisplayName(
        """
        GIVEN supabase returns a list of stations
        WHEN fetching fuel stations
        THEN result is right
        """
    )
    fun fuelStationSuccess() = runTest {
        val actual = sut.getListFuelStations()

        assertTrue(actual.isRight())
    }

    @Test
    @DisplayName(
        """
        GIVEN supabase throws an exception
        WHEN fetching fuel stations
        THEN result is left with NetworkError
        """
    )
    fun fuelStationError() = runTest {
        val sutWithError = SupabaseRemoteDataSource(
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

        val actual = sutWithError.getListFuelStations()

        assertTrue(actual.isLeft())
    }

    @Test
    @DisplayName(
        """
        GIVEN supabase returns success
        WHEN fetching fuel stations
        THEN logs STARTED and COMPLETED analytics events
        """
    )
    fun fuelStationSuccessLogsAnalyticsEvents() = runTest {
        sut.getListFuelStations()

        verify {
            analyticsHelper.logEvent(
                event = AnalyticsEvent(type = AnalyticsEvent.Types.API_STATIONS_FETCH_STARTED),
            )
        }
        verify {
            analyticsHelper.logEvent(
                event = AnalyticsEvent(type = AnalyticsEvent.Types.API_STATIONS_FETCH_COMPLETED),
            )
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN supabase throws an exception
        WHEN fetching fuel stations
        THEN logs STARTED and FAILED events with error extras
        """
    )
    fun fuelStationErrorLogsAnalyticsEvents() = runTest {
        SupabaseRemoteDataSource(
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
        ).getListFuelStations()

        verify {
            analyticsHelper.logEvent(
                event = AnalyticsEvent(type = AnalyticsEvent.Types.API_STATIONS_FETCH_STARTED),
            )
        }
        verify {
            analyticsHelper.logEvent(
                event = match { event ->
                    event.type == AnalyticsEvent.Types.API_STATIONS_FETCH_FAILED &&
                        event.extras.any { it.key == AnalyticsEvent.ParamKeys.ERROR_TYPE } &&
                        event.extras.any { it.key == AnalyticsEvent.ParamKeys.ERROR_MESSAGE }
                },
            )
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN supabase returns a list of stations
        WHEN fetching fuel stations
        THEN list size matches fixture
        """
    )
    fun fuelStationListSizeMatchesFixture() = runTest {
        val actual = sut.getListFuelStations()

        actual.onRight { stations ->
            assertEquals(2, stations.size)
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN supabase returns a station with null prices
        WHEN fetching fuel stations
        THEN null price fields remain null in SupabaseFuelStation
        """
    )
    fun nullPricesRemainNull() = runTest {
        val actual = sut.getListFuelStations()

        actual.onRight { stations ->
            val firstStation = stations.first()
            assertTrue(firstStation.priceGasoilPremium == null)
            assertTrue(firstStation.priceHydrogen == null)
            assertTrue(firstStation.priceBiodiesel == null)
        }
    }
}
