package com.gasguru.core.network.datasource

import com.gasguru.core.network.NetworkModuleTest
import com.gasguru.core.network.stubs.MockApiResponse
import com.gasguru.core.testing.CoroutinesTestExtension
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
class RemoteDataSourceTest {

    private val networkModule: NetworkModuleTest = NetworkModuleTest()
    private lateinit var server: MockWebServer
    private lateinit var sut: RemoteDataSourceImp
    private lateinit var mockApi: MockApiResponse

    @BeforeEach
    fun setUp() {
        server = networkModule.mockWebServer
        server.start()
        sut = RemoteDataSourceImp(networkModule.apiService)
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
}
