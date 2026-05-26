package com.gasguru.feature.favorite_list_station.ui

import app.cash.turbine.test
import com.gasguru.core.analytics.NoOpAnalyticsHelper
import com.gasguru.core.domain.fuelstation.GetFavoriteStationsUseCase
import com.gasguru.core.domain.fuelstation.RemoveFavoriteStationUseCase
import com.gasguru.core.domain.location.GetLastKnownLocationUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.UserData
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.VehicleType
import com.gasguru.core.model.data.previewFuelStationDomain
import com.gasguru.core.testing.CoroutineTest
import com.gasguru.core.testing.fakes.data.location.FakeLocationTracker
import com.gasguru.core.testing.fakes.data.user.FakeUserDataRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteListStationViewModelTest : CoroutineTest() {

    private lateinit var fakeUserDataRepository: FakeUserDataRepository
    private lateinit var fakeLocationTracker: FakeLocationTracker

    @BeforeTest
    fun setUp() {
        fakeUserDataRepository = FakeUserDataRepository()
        fakeLocationTracker = FakeLocationTracker()
    }

    private fun createViewModel(): FavoriteListStationViewModel {
        return FavoriteListStationViewModel(
            getUserDataUseCase = GetUserDataUseCase(fakeUserDataRepository),
            getFavoriteStationsUseCase = GetFavoriteStationsUseCase(fakeUserDataRepository),
            getLastKnownLocationUseCase = GetLastKnownLocationUseCase(fakeLocationTracker),
            removeFavoriteStationUseCase = RemoveFavoriteStationUseCase(fakeUserDataRepository),
            analyticsHelper = NoOpAnalyticsHelper(),
        )
    }

    @Test
    fun `GIVEN location tracker returns null WHEN favoriteStations flow is collected THEN emits Loading then DisableLocation`() = runTest {
        fakeLocationTracker.setLastKnownLocation(null)
        val viewModel = createViewModel()

        viewModel.favoriteStations.test {
            advanceUntilIdle()
            assertEquals(FavoriteStationListUiState.Loading, awaitItem())
            assertEquals(FavoriteStationListUiState.DisableLocation, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN location available and no favorite stations in repository WHEN favoriteStations flow is collected THEN emits Loading then EmptyFavorites`() = runTest {
        fakeLocationTracker.setLocationEnabled(true)
        fakeLocationTracker.setLastKnownLocation(testLocation())
        fakeUserDataRepository.setFavoriteStations(emptyList<FuelStation>())

        val viewModel = createViewModel()

        viewModel.favoriteStations.test {
            advanceUntilIdle()
            assertEquals(FavoriteStationListUiState.Loading, awaitItem())
            assertEquals(FavoriteStationListUiState.EmptyFavorites, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN two favorite stations with different prices WHEN favoriteStations flow emits THEN stations are ordered by price ascending by default`() = runTest {
        fakeLocationTracker.setLocationEnabled(true)
        fakeLocationTracker.setLastKnownLocation(testLocation())
        fakeUserDataRepository.setUserData(
            UserData(
                vehicles = listOf(
                    Vehicle(
                        id = 1L,
                        fuelType = FuelType.GASOLINE_95,
                        name = null,
                        tankCapacity = 40,
                        vehicleType = VehicleType.CAR,
                        isPrincipal = true
                    )
                )
            )
        )

        val stationA = previewFuelStationDomain(idServiceStation = 1).copy(
            priceGasoline95E5 = 1.50,
            distance = 500f,
        )
        val stationB = previewFuelStationDomain(idServiceStation = 2).copy(
            priceGasoline95E5 = 1.20,
            distance = 1000f,
        )
        fakeUserDataRepository.setFavoriteStations(listOf(stationA, stationB))

        val viewModel = createViewModel()

        viewModel.favoriteStations.test {
            advanceUntilIdle()
            assertEquals(FavoriteStationListUiState.Loading, awaitItem())
            val favorites = awaitItem() as FavoriteStationListUiState.Favorites
            val ids = favorites.favoriteStations.map { it.fuelStation.idServiceStation }
            assertEquals(listOf(2, 1), ids)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN stations sorted by price WHEN ChangeTab event selects distance tab THEN stations are re-ordered by distance ascending`() = runTest {
        fakeLocationTracker.setLocationEnabled(true)
        fakeLocationTracker.setLastKnownLocation(testLocation())
        fakeUserDataRepository.setUserData(
            UserData(
                vehicles = listOf(
                    Vehicle(
                        id = 1L,
                        fuelType = FuelType.GASOLINE_95,
                        name = null,
                        tankCapacity = 40,
                        vehicleType = VehicleType.CAR,
                        isPrincipal = true
                    )
                )
            )
        )

        val stationA = previewFuelStationDomain(idServiceStation = 1).copy(
            priceGasoline95E5 = 1.50,
            distance = 500f,
        )
        val stationB = previewFuelStationDomain(idServiceStation = 2).copy(
            priceGasoline95E5 = 1.20,
            distance = 1000f,
        )
        fakeUserDataRepository.setFavoriteStations(listOf(stationA, stationB))

        val viewModel = createViewModel()

        viewModel.favoriteStations.test {
            advanceUntilIdle()
            assertEquals(FavoriteStationListUiState.Loading, awaitItem())
            awaitItem()

            viewModel.handleEvents(FavoriteStationEvent.ChangeTab(selected = 1))
            advanceUntilIdle()

            val updated = awaitItem() as FavoriteStationListUiState.Favorites
            val ids = updated.favoriteStations.map { it.fuelStation.idServiceStation }
            assertEquals(listOf(1, 2), ids)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a favorite station exists WHEN RemoveFavoriteStation event is sent THEN repository records that station id as removed`() = runTest {
        val viewModel = createViewModel()

        viewModel.handleEvents(FavoriteStationEvent.RemoveFavoriteStation(idStation = 10))
        advanceUntilIdle()

        assertEquals(listOf(10), fakeUserDataRepository.removedFavoriteStations)
    }

    private fun testLocation(): LatLng = LatLng(latitude = 0.0, longitude = 0.0)
}
