package com.gasguru.core.domain.fuelstation

import app.cash.turbine.test
import com.gasguru.core.domain.fakes.FakeFuelStationRepository
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.previewFuelStationDomain
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetFuelStationByIdUseCaseTest {

    private lateinit var sut: GetFuelStationByIdUseCase
    private lateinit var fakeFuelStationRepository: FakeFuelStationRepository

    private val userLocation = LatLng(latitude = 40.4, longitude = -3.7)

    @BeforeTest
    fun setUp() {
        fakeFuelStationRepository = FakeFuelStationRepository()
        sut = GetFuelStationByIdUseCase(repository = fakeFuelStationRepository)
    }

    @Test
    fun returnsStationById() = runTest {
        val station = previewFuelStationDomain(idServiceStation = 99)
        fakeFuelStationRepository.setStationById(station)

        sut(id = 99, userLocation = userLocation).test {
            val result = awaitItem()
            assertEquals(99, result.idServiceStation)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun delegatesCorrectParamsToRepository() = runTest {
        val station = previewFuelStationDomain(idServiceStation = 10)
        fakeFuelStationRepository.setStationById(station)

        sut(id = 10, userLocation = userLocation).test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        val request = fakeFuelStationRepository.byIdRequests.first()
        assertEquals(10, request.first)
        assertEquals(userLocation, request.second)
    }
}
