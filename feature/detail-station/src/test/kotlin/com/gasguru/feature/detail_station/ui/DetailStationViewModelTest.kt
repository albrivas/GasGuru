package com.gasguru.feature.detail_station.ui

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.gasguru.core.data.repository.stations.OfflineFuelStationRepository
import com.gasguru.core.data.repository.user.OfflineUserDataRepository
import com.gasguru.core.database.model.FuelStationEntity
import com.gasguru.core.database.model.UserDataEntity
import com.gasguru.core.database.model.VehicleEntity
import com.gasguru.core.domain.alerts.AddPriceAlertUseCase
import com.gasguru.core.domain.alerts.RemovePriceAlertUseCase
import com.gasguru.core.domain.fuelstation.GetFuelStationByIdUseCase
import com.gasguru.core.domain.fuelstation.RemoveFavoriteStationUseCase
import com.gasguru.core.domain.fuelstation.SaveFavoriteStationUseCase
import com.gasguru.core.domain.location.GetLastKnownLocationUseCase
import com.gasguru.core.domain.maps.GetStaticMapUrlUseCase
import com.gasguru.core.domain.places.GetAddressFromLocationUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.UserData
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.testing.CoroutinesTestExtension
import com.gasguru.core.testing.fakes.data.alerts.FakePriceAlertRepository
import com.gasguru.core.testing.fakes.data.database.FakeFavoriteStationDao
import com.gasguru.core.testing.fakes.data.database.FakeFuelStationDao
import com.gasguru.core.testing.fakes.data.database.FakePriceAlertDao
import com.gasguru.core.testing.fakes.data.database.FakeUserDataDao
import com.gasguru.core.testing.fakes.data.database.FakeVehicleDao
import com.gasguru.core.testing.fakes.data.geocoder.FakeGeocoderAddress
import com.gasguru.core.testing.fakes.data.location.FakeLocationTracker
import com.gasguru.core.testing.fakes.data.maps.FakeStaticMapRepository
import com.gasguru.core.testing.fakes.data.network.FakeRemoteDataSource
import com.gasguru.core.testing.fakes.data.user.FakeUserDataRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
class DetailStationViewModelTest {

    private lateinit var sut: DetailStationViewModel
    private lateinit var fakeLocationTracker: FakeLocationTracker
    private lateinit var fakeFuelStationDao: FakeFuelStationDao
    private lateinit var fakeFavoriteStationDao: FakeFavoriteStationDao
    private lateinit var fakePriceAlertDao: FakePriceAlertDao
    private lateinit var fakeUserDataRepository: FakeUserDataRepository
    private lateinit var fakeUserDataDao: FakeUserDataDao
    private lateinit var fakeVehicleDao: FakeVehicleDao
    private lateinit var fakeGeocoderAddress: FakeGeocoderAddress
    private lateinit var fakeStaticMapRepository: FakeStaticMapRepository
    private lateinit var fakePriceAlertRepository: FakePriceAlertRepository

    @BeforeEach
    fun setUp() {
        fakeLocationTracker = FakeLocationTracker()
        fakeFuelStationDao = FakeFuelStationDao()
        fakeFavoriteStationDao = FakeFavoriteStationDao()
        fakePriceAlertDao = FakePriceAlertDao()
        fakeUserDataRepository = FakeUserDataRepository(
            initialUserData = UserData(
                lastUpdate = 123L,
                vehicles = listOf(Vehicle(id = 1L, fuelType = FuelType.GASOLINE_95, name = null, tankCapacity = 40)),
            )
        )
        fakeUserDataDao = FakeUserDataDao(
            initialUserData = UserDataEntity(
                lastUpdate = 123L,
                isOnboardingSuccess = true,
            )
        )
        fakeVehicleDao = FakeVehicleDao(
            initialVehicles = listOf(VehicleEntity(id = 1L, userId = 0L, name = null, fuelType = FuelType.GASOLINE_95, tankCapacity = 40)),
        )
        fakeGeocoderAddress = FakeGeocoderAddress(address = "Calle Mayor 1")
        fakeStaticMapRepository = FakeStaticMapRepository()
        fakePriceAlertRepository = FakePriceAlertRepository()

        sut = createViewModel()
    }

    @Test
    @DisplayName("GIVEN no location WHEN collecting fuel station THEN emits Error")
    fun emitsErrorWhenLocationMissing() = runTest {
        sut.fuelStation.test {
            assertEquals(DetailStationUiState.Loading, awaitItem())
            assertEquals(DetailStationUiState.Error, awaitItem())
        }
    }

    @Test
    @DisplayName("GIVEN location and station WHEN collecting fuel station THEN emits Success with address")
    fun emitsSuccessWithAddress() = runTest {
        fakeLocationTracker.setLastKnownLocation(testLocation())
        fakeFuelStationDao.setStations(listOf(stationEntity(id = 10, price = 1.55)))

        sut = createViewModel()

        sut.fuelStation.test {
            assertEquals(DetailStationUiState.Loading, awaitItem())
            val state = awaitItem() as DetailStationUiState.Success
            assertEquals(10, state.stationModel.fuelStation.idServiceStation)
            assertEquals("Calle Mayor 1", state.address)
        }
    }

    @Test
    @DisplayName("GIVEN geocoder error WHEN collecting fuel station THEN emits Success with null address")
    fun emitsSuccessWithNullAddressOnGeocoderError() = runTest {
        fakeGeocoderAddress.shouldThrow = true
        fakeLocationTracker.setLastKnownLocation(testLocation())
        fakeFuelStationDao.setStations(listOf(stationEntity(id = 10, price = 1.55)))

        sut = createViewModel()

        sut.fuelStation.test {
            assertEquals(DetailStationUiState.Loading, awaitItem())
            val state = awaitItem() as DetailStationUiState.Success
            assertEquals(10, state.stationModel.fuelStation.idServiceStation)
            assertNull(state.address)
        }
    }

    @Test
    @DisplayName("GIVEN success state WHEN collecting static map THEN emits url")
    fun emitsStaticMapUrl() = runTest {
        fakeStaticMapRepository.urlToReturn = "static://map"
        fakeLocationTracker.setLastKnownLocation(testLocation())
        fakeFuelStationDao.setStations(listOf(stationEntity(id = 10, price = 1.55)))

        sut = createViewModel()

        sut.staticMapUrl.test {
            assertNull(awaitItem())
            assertEquals("static://map", awaitItem())
        }
    }

    @Test
    @DisplayName("GIVEN toggle favorite WHEN handling THEN updates repository")
    fun togglesFavorite() = runTest {
        sut.onEvent(DetailStationEvent.ToggleFavorite(isFavorite = true))
        sut.onEvent(DetailStationEvent.ToggleFavorite(isFavorite = false))
        advanceUntilIdle()

        assertEquals(listOf(10), fakeUserDataRepository.addedFavoriteStations)
        assertEquals(listOf(10), fakeUserDataRepository.removedFavoriteStations)
    }

    @Test
    @DisplayName("GIVEN toggle price alert WHEN handling THEN updates price alerts")
    fun togglesPriceAlert() = runTest {
        fakeLocationTracker.setLastKnownLocation(testLocation())
        fakeFuelStationDao.setStations(listOf(stationEntity(id = 10, price = 1.55)))
        sut = createViewModel()

        sut.fuelStation.test {
            assertEquals(DetailStationUiState.Loading, awaitItem())
            awaitItem()
        }

        sut.onEvent(DetailStationEvent.TogglePriceAlert(isEnabled = true))
        sut.onEvent(DetailStationEvent.TogglePriceAlert(isEnabled = false))
        advanceUntilIdle()

        assertEquals(listOf(10 to 1.55), fakePriceAlertRepository.addedAlerts)
        assertEquals(listOf(10), fakePriceAlertRepository.removedAlerts)
    }

    @Test
    @DisplayName("GIVEN user data WHEN collecting lastUpdate THEN emits value")
    fun emitsLastUpdate() = runTest {
        sut.lastUpdate.test {
            assertEquals(0L, awaitItem())
            assertEquals(123L, awaitItem())
        }
    }

    private fun createViewModel(): DetailStationViewModel {
        val offlineUserDataRepository = OfflineUserDataRepository(
            userDataDao = fakeUserDataDao,
            favoriteStationDao = fakeFavoriteStationDao,
            vehicleDao = fakeVehicleDao,
        )
        val offlineFuelStationRepository = OfflineFuelStationRepository(
            fuelStationDao = fakeFuelStationDao,
            remoteDataSource = FakeRemoteDataSource(),
            defaultDispatcher = kotlinx.coroutines.Dispatchers.Main,
            ioDispatcher = kotlinx.coroutines.Dispatchers.Main,
            offlineUserDataRepository = offlineUserDataRepository,
            favoriteStationDao = fakeFavoriteStationDao,
            priceAlertDao = fakePriceAlertDao
        )

        return DetailStationViewModel(
            savedStateHandle = SavedStateHandle(mapOf("idServiceStation" to 10)),
            getFuelStationByIdUseCase = GetFuelStationByIdUseCase(offlineFuelStationRepository),
            getLastKnownLocationUseCase = GetLastKnownLocationUseCase(fakeLocationTracker),
            userDataUseCase = GetUserDataUseCase(fakeUserDataRepository),
            saveFavoriteStationUseCase = SaveFavoriteStationUseCase(fakeUserDataRepository),
            removeFavoriteStationUseCase = RemoveFavoriteStationUseCase(fakeUserDataRepository),
            getAddressFromLocationUseCase = GetAddressFromLocationUseCase(fakeGeocoderAddress),
            getStaticMapUrlUseCase = GetStaticMapUrlUseCase(fakeStaticMapRepository),
            addPriceAlertUseCase = AddPriceAlertUseCase(fakePriceAlertRepository),
            removePriceAlertUseCase = RemovePriceAlertUseCase(fakePriceAlertRepository)
        )
    }

    private fun testLocation(): LatLng = LatLng(latitude = 40.0, longitude = -3.0)

    private fun stationEntity(
        id: Int,
        price: Double,
        latitude: Double = 40.0,
        longitude: Double = -3.0,
    ): FuelStationEntity =
        FuelStationEntity(
            bioEthanolPercentage = "",
            esterMethylPercentage = "",
            postalCode = "",
            direction = "",
            schedule = "",
            idAutonomousCommunity = "",
            idServiceStation = id,
            idMunicipality = "",
            idProvince = "",
            latitude = latitude,
            locality = "",
            longitudeWGS84 = longitude,
            margin = "",
            municipality = "",
            priceBiodiesel = 0.0,
            priceBioEthanol = 0.0,
            priceGasNaturalCompressed = 0.0,
            priceLiquefiedNaturalGas = 0.0,
            priceLiquefiedPetroleumGas = 0.0,
            priceGasoilA = 0.0,
            priceGasoilB = 0.0,
            priceGasoilPremium = 0.0,
            priceGasoline95E10 = 0.0,
            priceGasoline95E5 = price,
            priceGasoline95E5Premium = 0.0,
            priceGasoline98E10 = 0.0,
            priceGasoline98E5 = 0.0,
            priceHydrogen = 0.0,
            priceAdblue = 0.0,
            province = "",
            referral = "",
            brandStation = "Repsol",
            typeSale = "",
            lastUpdate = 0,
            isFavorite = false
        )
}
