package com.gasguru.feature.favorite_list_station.ui

import app.cash.turbine.test
import com.gasguru.core.domain.fuelstation.GetFavoriteStationsUseCase
import com.gasguru.core.domain.fuelstation.RemoveFavoriteStationUseCase
import com.gasguru.core.domain.location.GetLastKnownLocationUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.UserData
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.previewFuelStationDomain
import com.gasguru.core.testing.CoroutinesTestExtension
import com.gasguru.core.testing.fakes.data.location.FakeLocationTracker
import com.gasguru.core.testing.fakes.data.user.FakeUserDataRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
class FavoriteListStationViewModelTest {

    private lateinit var fakeUserDataRepository: FakeUserDataRepository
    private lateinit var fakeLocationTracker: FakeLocationTracker

    @BeforeEach
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
        )
    }

    @Test
    @DisplayName("GIVEN location disabled WHEN collecting favorites THEN emits DisableLocation")
    fun emitsDisableLocationWhenLocationIsDisabled() = runTest {
        fakeLocationTracker.setLocationEnabled(false)
        val viewModel = createViewModel()

        viewModel.favoriteStations.test {
            advanceUntilIdle()
            assertEquals(FavoriteStationListUiState.Loading, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    @DisplayName("GIVEN no favorites WHEN collecting favorites THEN emits EmptyFavorites")
    fun emitsEmptyFavoritesWhenNoStations() = runTest {
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
    @DisplayName("GIVEN favorites and default tab WHEN collecting favorites THEN sorts by price")
    fun sortsByPriceByDefault() = runTest {
        fakeLocationTracker.setLocationEnabled(true)
        fakeLocationTracker.setLastKnownLocation(testLocation())
        fakeUserDataRepository.setUserData(UserData(vehicles = listOf(Vehicle(id = 1L, fuelType = FuelType.GASOLINE_95, name = null, tankCapacity = 40))))

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
    @DisplayName("GIVEN favorites WHEN changing tab THEN sorts by distance")
    fun changesSortingWhenTabChanges() = runTest {
        fakeLocationTracker.setLocationEnabled(true)
        fakeLocationTracker.setLastKnownLocation(testLocation())
        fakeUserDataRepository.setUserData(UserData(vehicles = listOf(Vehicle(id = 1L, fuelType = FuelType.GASOLINE_95, name = null, tankCapacity = 40))))

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
    @DisplayName("GIVEN remove favorite event WHEN handling THEN delegates to repository")
    fun removeFavoriteStationDelegatesToRepository() = runTest {
        val viewModel = createViewModel()

        viewModel.handleEvents(FavoriteStationEvent.RemoveFavoriteStation(idStation = 10))
        advanceUntilIdle()

        assertEquals(listOf(10), fakeUserDataRepository.removedFavoriteStations)
    }

    private fun testLocation(): LatLng = LatLng(latitude = 0.0, longitude = 0.0)
}
