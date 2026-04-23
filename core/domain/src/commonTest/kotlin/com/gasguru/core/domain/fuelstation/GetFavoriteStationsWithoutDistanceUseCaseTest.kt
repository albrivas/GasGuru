package com.gasguru.core.domain.fuelstation

import app.cash.turbine.test
import com.gasguru.core.domain.fakes.FakeUserDataRepository
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.UserData
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.VehicleType
import com.gasguru.core.model.data.previewFuelStationDomain
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetFavoriteStationsWithoutDistanceUseCaseTest {

    private lateinit var sut: GetFavoriteStationsWithoutDistanceUseCase
    private lateinit var fakeRepository: FakeUserDataRepository

    private val principalVehicle = Vehicle(
        id = 1L,
        userId = 0L,
        name = "Test Car",
        fuelType = FuelType.GASOLINE_95,
        tankCapacity = 50,
        vehicleType = VehicleType.CAR,
        isPrincipal = true,
    )

    @BeforeTest
    fun setUp() {
        fakeRepository = FakeUserDataRepository(
            initialUserData = UserData(vehicles = listOf(principalVehicle)),
        )
        sut = GetFavoriteStationsWithoutDistanceUseCase(repository = fakeRepository)
    }

    @Test
    fun returnsEmptyListWhenNoFavorites() = runTest {
        sut().test {
            val result = awaitItem()
            assertTrue(result.favoriteStations.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun returnsFavoritesWithUserData() = runTest {
        val favoriteStation = previewFuelStationDomain(idServiceStation = 42)
        fakeRepository.setFavoriteStations(listOf(favoriteStation))

        sut().test {
            val result = awaitItem()
            assertEquals(1, result.favoriteStations.size)
            assertEquals(42, result.favoriteStations.first().idServiceStation)
            assertEquals(principalVehicle, result.user.vehicles.first())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun emitsUpdatesWhenFavoritesChange() = runTest {
        val firstStation = previewFuelStationDomain(idServiceStation = 1)
        val secondStation = previewFuelStationDomain(idServiceStation = 2)
        fakeRepository.setFavoriteStations(listOf(firstStation))

        sut().test {
            val firstEmission = awaitItem()
            assertEquals(1, firstEmission.favoriteStations.size)

            fakeRepository.setFavoriteStations(listOf(firstStation, secondStation))

            val secondEmission = awaitItem()
            assertEquals(2, secondEmission.favoriteStations.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
