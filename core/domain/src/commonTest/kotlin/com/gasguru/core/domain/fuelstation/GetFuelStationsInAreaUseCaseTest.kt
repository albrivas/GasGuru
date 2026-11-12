package com.gasguru.core.domain.fuelstation

import app.cash.turbine.test
import com.gasguru.core.domain.fakes.FakeFuelStationRepository
import com.gasguru.core.model.data.AreaSearchResult
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.MapBounds
import com.gasguru.core.model.data.OpeningHours
import com.gasguru.core.model.data.previewFuelStationDomain
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetFuelStationsInAreaUseCaseTest {

    private lateinit var sut: GetFuelStationsInAreaUseCase
    private lateinit var fakeFuelStationRepository: FakeFuelStationRepository

    private val bounds = MapBounds(
        southwest = LatLng(latitude = 40.0, longitude = -4.0),
        northeast = LatLng(latitude = 41.0, longitude = -3.0),
    )
    private val referenceLocation = LatLng(latitude = 40.4, longitude = -3.7)

    @BeforeTest
    fun setUp() {
        fakeFuelStationRepository = FakeFuelStationRepository()
        sut = GetFuelStationsInAreaUseCase(repository = fakeFuelStationRepository)
    }

    @Test
    fun delegatesBoundsFiltersAndMaxStationsToRepository() = runTest {
        sut(
            bounds = bounds,
            referenceLocation = referenceLocation,
            maxStations = 25,
            brands = listOf("REPSOL"),
            schedule = OpeningHours.OPEN_NOW,
        ).test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        val request = fakeFuelStationRepository.areaRequests.single()
        assertEquals(bounds, request.bounds)
        assertEquals(referenceLocation, request.referenceLocation)
        assertEquals(25, request.maxStations)
        assertEquals(listOf("REPSOL"), request.brands)
        assertEquals(OpeningHours.OPEN_NOW, request.schedule)
    }

    @Test
    fun emitsResultProvidedByRepository() = runTest {
        val result = AreaSearchResult(
            stations = listOf(previewFuelStationDomain(idServiceStation = 1)),
            isTruncated = true,
        )
        fakeFuelStationRepository.setAreaResult(result)

        sut(
            bounds = bounds,
            referenceLocation = referenceLocation,
            maxStations = 10,
            brands = emptyList(),
            schedule = OpeningHours.NONE,
        ).test {
            val emitted = awaitItem()
            assertEquals(result, emitted)
            assertTrue(emitted.isTruncated)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
