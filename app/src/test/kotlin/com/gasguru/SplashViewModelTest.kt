package com.gasguru

import app.cash.turbine.test
import com.gasguru.core.data.repository.stations.OfflineFuelStationRepository
import com.gasguru.core.data.repository.user.OfflineUserDataRepository
import com.gasguru.core.database.model.UserDataEntity
import com.gasguru.core.database.model.VehicleEntity
import com.gasguru.core.domain.fuelstation.GetFuelStationUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.model.data.UserData
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.testing.CoroutinesTestExtension
import com.gasguru.core.testing.fakes.data.database.FakeFavoriteStationDao
import com.gasguru.core.testing.fakes.data.database.FakeFuelStationDao
import com.gasguru.core.testing.fakes.data.database.FakePriceAlertDao
import com.gasguru.core.testing.fakes.data.database.FakeUserDataDao
import com.gasguru.core.testing.fakes.data.database.FakeVehicleDao
import com.gasguru.core.testing.fakes.data.network.FakeRemoteDataSource
import com.gasguru.core.testing.fakes.data.user.FakeUserDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
class SplashViewModelTest {

    private lateinit var sut: SplashViewModel
    private lateinit var fakeUserDataRepository: FakeUserDataRepository
    private lateinit var fakeUserDataDao: FakeUserDataDao
    private lateinit var fakeFuelStationDao: FakeFuelStationDao
    private lateinit var fakeFavoriteStationDao: FakeFavoriteStationDao
    private lateinit var fakePriceAlertDao: FakePriceAlertDao
    private lateinit var fakeVehicleDao: FakeVehicleDao
    private lateinit var fakeRemoteDataSource: FakeRemoteDataSource

    @BeforeEach
    fun setUp() {
        fakeUserDataRepository = FakeUserDataRepository(
            initialUserData = UserData(
                isOnboardingSuccess = true,
                themeMode = ThemeMode.DARK,
                vehicles = listOf(Vehicle(id = 1L, fuelType = FuelType.GASOLINE_95, name = null, tankCapacity = 40)),
            )
        )
        fakeUserDataDao = FakeUserDataDao(
            initialUserData = UserDataEntity(
                lastUpdate = 0,
                isOnboardingSuccess = true,
            )
        )
        fakeFuelStationDao = FakeFuelStationDao()
        fakeFavoriteStationDao = FakeFavoriteStationDao()
        fakePriceAlertDao = FakePriceAlertDao()
        fakeVehicleDao = FakeVehicleDao(
            initialVehicles = listOf(VehicleEntity(id = 1L, userId = 0L, name = null, fuelType = FuelType.GASOLINE_95, tankCapacity = 40)),
        )
        fakeRemoteDataSource = FakeRemoteDataSource()

        sut = createViewModel()
    }

    @Test
    @DisplayName("GIVEN user data WHEN collecting uiState THEN emits Success with onboarding flag")
    fun emitsUiStateSuccess() = runTest {
        sut.uiState.test {
            val first = awaitItem()
            val result = if (first.getOrNull() == SplashUiState.Loading) {
                awaitItem()
            } else {
                first
            }
            assertTrue(result.isSuccess)
            assertEquals(SplashUiState.Success(true), result.getOrNull())
        }
    }

    @Test
    @DisplayName("GIVEN user data WHEN collecting themeMode THEN emits user theme")
    fun emitsThemeMode() = runTest {
        sut.themeMode.test {
            when (val state = awaitItem()) {
                ThemeMode.SYSTEM -> {
                    assertEquals(ThemeMode.DARK, awaitItem())
                }
                else -> {
                    assertEquals(ThemeMode.DARK, state)
                }
            }
        }
    }

    @Test
    @DisplayName("GIVEN old last update WHEN updating fuel stations THEN refreshes stations")
    fun updatesFuelStationsWhenOutdated() = runTest {
        val oldTimestamp = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(31)
        fakeUserDataRepository.setUserData(
            UserData(
                isOnboardingSuccess = true,
                themeMode = ThemeMode.DARK,
                vehicles = listOf(Vehicle(id = 1L, fuelType = FuelType.GASOLINE_95, name = null, tankCapacity = 40)),
                lastUpdate = oldTimestamp,
            )
        )

        advanceUntilIdle()

        sut.updateFuelStations()
        advanceUntilIdle()

        assertEquals(1, fakeRemoteDataSource.getListFuelStationsCalls)
    }

    private fun createViewModel(): SplashViewModel {
        val offlineUserDataRepository = OfflineUserDataRepository(
            userDataDao = fakeUserDataDao,
            favoriteStationDao = fakeFavoriteStationDao,
            vehicleDao = fakeVehicleDao,
        )
        val offlineFuelStationRepository = OfflineFuelStationRepository(
            fuelStationDao = fakeFuelStationDao,
            remoteDataSource = fakeRemoteDataSource,
            defaultDispatcher = Dispatchers.Main,
            ioDispatcher = Dispatchers.Main,
            offlineUserDataRepository = offlineUserDataRepository,
            favoriteStationDao = fakeFavoriteStationDao,
            priceAlertDao = fakePriceAlertDao
        )

        return SplashViewModel(
            fuelStation = GetFuelStationUseCase(offlineFuelStationRepository),
            userData = GetUserDataUseCase(fakeUserDataRepository),
            ioDispatcher = Dispatchers.Main
        )
    }
}
