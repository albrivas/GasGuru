package com.gasguru.core.network.datasource

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.network.mockwebserver.NetworkModuleTest
import com.gasguru.core.network.stubs.MockApiResponse
import com.gasguru.core.testing.CoroutinesTestExtension
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@DisplayName("RemoteDataSource")
@ExtendWith(CoroutinesTestExtension::class)
class RemoteDataSourceTest {

    private val networkModule: NetworkModuleTest = NetworkModuleTest()
    private val analyticsHelper: AnalyticsHelper = mockk(relaxed = true)
    private lateinit var server: MockWebServer
    private lateinit var sut: RemoteDataSourceImp
    private lateinit var mockApi: MockApiResponse

    @BeforeEach
    fun setUp() {
        server = networkModule.mockWebServer
        server.start()
        sut = RemoteDataSourceImp(api = networkModule.apiService, analyticsHelper = analyticsHelper)
        mockApi = MockApiResponse()
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    @Test
    @DisplayName("GIVEN server return success, WHEN fetching fuel stations, THEN result is right")
    fun fuelStationSuccess() = runTest {
        server.enqueue(mockApi.listFuelStationOK())

        val actual = sut.getListFuelStations()

        assertTrue(actual.isRight())
    }

    @Test
    @DisplayName("GIVEN server return error, WHEN fetching fuel stations, THEN result is left")
    fun fuelStationError() = runTest {
        server.enqueue(mockApi.listFuelStationKO())

        val actual = sut.getListFuelStations()

        assertTrue(actual.isLeft())
    }

    @Test
    @DisplayName("GIVEN server return success, WHEN fetching fuel stations, THEN logs STARTED and COMPLETED events")
    fun fuelStationSuccessLogsAnalyticsEvents() = runTest {
        server.enqueue(mockApi.listFuelStationOK())

        sut.getListFuelStations()

        verify {
            analyticsHelper.logEvent(
                event = AnalyticsEvent(type = AnalyticsEvent.Types.API_STATIONS_FETCH_STARTED)
            )
        }
        verify {
            analyticsHelper.logEvent(
                event = AnalyticsEvent(type = AnalyticsEvent.Types.API_STATIONS_FETCH_COMPLETED)
            )
        }
    }

    @Test
    @DisplayName("GIVEN server return error, WHEN fetching fuel stations, THEN logs STARTED and FAILED events with error extras")
    fun fuelStationErrorLogsAnalyticsEvents() = runTest {
        server.enqueue(mockApi.listFuelStationKO())

        sut.getListFuelStations()

        verify {
            analyticsHelper.logEvent(
                event = AnalyticsEvent(type = AnalyticsEvent.Types.API_STATIONS_FETCH_STARTED)
            )
        }
        verify {
            analyticsHelper.logEvent(
                event = match { event ->
                    event.type == AnalyticsEvent.Types.API_STATIONS_FETCH_FAILED &&
                        event.extras.any { it.key == AnalyticsEvent.ParamKeys.ERROR_TYPE } &&
                        event.extras.any { it.key == AnalyticsEvent.ParamKeys.ERROR_MESSAGE }
                }
            )
        }
    }
}
