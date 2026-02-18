package com.gasguru.core.network.datasource

import com.gasguru.core.network.mock.NetworkMockEngine
import com.gasguru.core.network.stubs.MockApiResponse
import com.gasguru.core.testing.CoroutinesTestExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
class RemoteDataSourceTest {

    private val mockEngine = NetworkMockEngine()
    private lateinit var sut: RemoteDataSourceImp
    private lateinit var mockApi: MockApiResponse

    @BeforeEach
    fun setUp() {
        sut = RemoteDataSourceImp(httpClient = mockEngine.httpClient)
        mockApi = MockApiResponse()
    }

    @AfterEach
    fun tearDown() {
        mockEngine.close()
    }

    @Test
    @DisplayName("GIVEN server return success, WHEN fetching fuel stations, THEN result is right")
    fun fuelStationSuccess() = runTest {
        mockEngine.enqueue(mockApi.listFuelStationOK())

        val actual = sut.getListFuelStations()

        assertTrue(actual.isRight())
    }

    @Test
    @DisplayName("GIVEN server return error, WHEN fetching fuel stations, THEN result is left")
    fun fuelStationError() = runTest {
        mockEngine.enqueue(mockApi.listFuelStationKO())

        val actual = sut.getListFuelStations()

        assertTrue(actual.isLeft())
    }
}
