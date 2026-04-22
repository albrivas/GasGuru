package com.gasguru.core.domain.fuelstation

import com.gasguru.core.domain.fakes.FakeUserDataRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RemoveFavoriteStationUseCaseTest {

    private lateinit var sut: RemoveFavoriteStationUseCase
    private lateinit var fakeUserDataRepository: FakeUserDataRepository

    @BeforeTest
    fun setUp() {
        fakeUserDataRepository = FakeUserDataRepository()
        sut = RemoveFavoriteStationUseCase(offlineRepository = fakeUserDataRepository)
    }

    @Test
    fun removesStationByStationId() = runTest {
        sut(stationId = 42)

        assertEquals(1, fakeUserDataRepository.removedFavoriteStations.size)
        assertEquals(42, fakeUserDataRepository.removedFavoriteStations.first())
    }

    @Test
    fun removingMultipleStationsTracksAll() = runTest {
        sut(stationId = 1)
        sut(stationId = 2)

        assertEquals(2, fakeUserDataRepository.removedFavoriteStations.size)
    }

    @Test
    fun doesNotAddStationOnRemove() = runTest {
        sut(stationId = 10)

        assertTrue(fakeUserDataRepository.addedFavoriteStations.isEmpty())
    }
}
