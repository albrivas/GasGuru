package com.gasguru.core.domain.fuelstation

import app.cash.turbine.test
import com.gasguru.core.domain.fakes.FakeFuelStationRepository
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.OpeningHours
import com.gasguru.core.model.data.previewFuelStationDomain
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FuelStationByLocationUseCaseTest {

    private lateinit var sut: FuelStationByLocationUseCase
    private lateinit var fakeFuelStationRepository: FakeFuelStationRepository

    private val userLocation = LatLng(latitude = 40.4, longitude = -3.7)

    @BeforeTest
    fun setUp() {
        fakeFuelStationRepository = FakeFuelStationRepository()
        sut = FuelStationByLocationUseCase(repository = fakeFuelStationRepository)
    }

    @Test
    fun returnsStationsFromRepository() = runTest {
        val stations = listOf(
            previewFuelStationDomain(idServiceStation = 1),
            previewFuelStationDomain(idServiceStation = 2),
        )
        fakeFuelStationRepository.setStations(stations)

        sut(
            userLocation = userLocation,
            maxStations = 10,
            brands = emptyList(),
            schedule = OpeningHours.NONE,
        ).test {
            val result = awaitItem()
            assertEquals(2, result.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun delegatesCorrectParamsToRepository() = runTest {
        val brands = listOf("REPSOL")

        sut(
            userLocation = userLocation,
            maxStations = 5,
            brands = brands,
            schedule = OpeningHours.OPEN_NOW,
        ).test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        val request = fakeFuelStationRepository.locationRequests.first()
        assertEquals(userLocation, request.first)
        assertEquals(5, request.second)
        assertEquals(OpeningHours.OPEN_NOW, request.third)
    }

    @Test
    fun returnsEmptyListWhenNoStationsAvailable() = runTest {
        sut(
            userLocation = userLocation,
            maxStations = 10,
            brands = emptyList(),
            schedule = OpeningHours.NONE,
        ).test {
            val result = awaitItem()
            assertEquals(0, result.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
