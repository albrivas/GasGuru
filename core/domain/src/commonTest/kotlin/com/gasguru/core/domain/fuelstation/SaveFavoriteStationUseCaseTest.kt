package com.gasguru.core.domain.fuelstation

import com.gasguru.core.domain.fakes.FakeUserDataRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SaveFavoriteStationUseCaseTest {

    private lateinit var sut: SaveFavoriteStationUseCase
    private lateinit var fakeUserDataRepository: FakeUserDataRepository

    @BeforeTest
    fun setUp() {
        fakeUserDataRepository = FakeUserDataRepository()
        sut = SaveFavoriteStationUseCase(offlineRepository = fakeUserDataRepository)
    }

    @Test
    fun addsStationByStationId() = runTest {
        sut(stationId = 42)

        assertEquals(1, fakeUserDataRepository.addedFavoriteStations.size)
        assertEquals(42, fakeUserDataRepository.addedFavoriteStations.first())
    }

    @Test
    fun addingMultipleStationsTracksAll() = runTest {
        sut(stationId = 1)
        sut(stationId = 2)

        assertEquals(2, fakeUserDataRepository.addedFavoriteStations.size)
    }

    @Test
    fun doesNotRemoveStationOnSave() = runTest {
        sut(stationId = 10)

        assertTrue(fakeUserDataRepository.removedFavoriteStations.isEmpty())
    }
}
