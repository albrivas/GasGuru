package com.gasguru.core.domain.places

import app.cash.turbine.test
import com.gasguru.core.domain.fakes.FakePlacesRepository
import com.gasguru.core.model.data.LatLng
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetLocationPlaceUseCaseTest {

    private lateinit var sut: GetLocationPlaceUseCase
    private lateinit var fakePlacesRepository: FakePlacesRepository

    @BeforeTest
    fun setUp() {
        fakePlacesRepository = FakePlacesRepository()
        sut = GetLocationPlaceUseCase(placesRepository = fakePlacesRepository)
    }

    @Test
    fun returnsLocationForPlaceId() = runTest {
        val location = LatLng(latitude = 40.4, longitude = -3.7)
        fakePlacesRepository.setLocationForId(placeId = "place_123", location = location)

        sut(placeId = "place_123").test {
            assertEquals(location, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun returnsDefaultLocationWhenPlaceIdUnknown() = runTest {
        sut(placeId = "unknown").test {
            val result = awaitItem()
            assertEquals(0.0, result.latitude)
            assertEquals(0.0, result.longitude)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun tracksRequestedPlaceIds() = runTest {
        fakePlacesRepository.setLocationForId(placeId = "p1", location = LatLng(0.0, 0.0))

        sut(placeId = "p1").test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(listOf("p1"), fakePlacesRepository.requestedLocationIds)
    }
}
