package com.gasguru.core.domain.fuelstation

import app.cash.turbine.test
import com.gasguru.core.domain.fakes.FakeUserDataRepository
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.previewFuelStationDomain
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetFavoriteStationsUseCaseTest {

    private lateinit var sut: GetFavoriteStationsUseCase
    private lateinit var fakeUserDataRepository: FakeUserDataRepository

    private val userLocation = LatLng(latitude = 40.4, longitude = -3.7)

    @BeforeTest
    fun setUp() {
        fakeUserDataRepository = FakeUserDataRepository()
        sut = GetFavoriteStationsUseCase(repository = fakeUserDataRepository)
    }

    @Test
    fun returnsEmptyFavoritesInitially() = runTest {
        sut(userLocation = userLocation).test {
            val result = awaitItem()
            assertTrue(result.favoriteStations.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun returnsFavoritesWhenPresent() = runTest {
        val favoriteStation = previewFuelStationDomain(idServiceStation = 5)
        fakeUserDataRepository.setFavoriteStations(listOf(favoriteStation))

        sut(userLocation = userLocation).test {
            val result = awaitItem()
            assertEquals(1, result.favoriteStations.size)
            assertEquals(5, result.favoriteStations.first().idServiceStation)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
