package com.gasguru.feature.detail_station.ui

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.analytics.NoOpAnalyticsHelper
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
import com.gasguru.core.domain.vehicle.UpdateVehicleTankCapacityUseCase
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.UserData
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.VehicleType
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
import com.gasguru.core.testing.fakes.data.vehicle.FakeVehicleRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
@DisplayName("DetailStationViewModel")
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
    private lateinit var fakeVehicleRepository: FakeVehicleRepository

    @BeforeEach
    fun setUp() {
        fakeLocationTracker = FakeLocationTracker()
        fakeFuelStationDao = FakeFuelStationDao()
        fakeFavoriteStationDao = FakeFavoriteStationDao()
        fakePriceAlertDao = FakePriceAlertDao()
        fakeUserDataRepository = FakeUserDataRepository(
            initialUserData = UserData(
                lastUpdate = 123L,
                vehicles = listOf(
                    Vehicle(
                        id = 1L,
                        fuelType = FuelType.GASOLINE_95,
                        name = null,
                        tankCapacity = 40,
                        vehicleType = VehicleType.CAR,
                        isPrincipal = true,
                    )
                ),
            )
        )
        fakeUserDataDao = FakeUserDataDao(
            initialUserData = UserDataEntity(
                lastUpdate = 123L,
                isOnboardingSuccess = true,
            )
        )
        fakeVehicleDao = FakeVehicleDao(
            initialVehicles = listOf(
                VehicleEntity(
                    id = 1L,
                    userId = 0L,
                    name = null,
                    fuelType = FuelType.GASOLINE_95,
                    tankCapacity = 40,
                    vehicleType = VehicleType.CAR,
                    isPrincipal = true,
                )
            ),
        )
        fakeGeocoderAddress = FakeGeocoderAddress(address = "Calle Mayor 1")
        fakeStaticMapRepository = FakeStaticMapRepository()
        fakePriceAlertRepository = FakePriceAlertRepository()
        fakeVehicleRepository = FakeVehicleRepository(
            initialVehicles = listOf(
                Vehicle(
                    id = 1L,
                    fuelType = FuelType.GASOLINE_95,
                    name = null,
                    tankCapacity = 40,
                    vehicleType = VehicleType.CAR,
                    isPrincipal = true,
                )
            ),
        )

        sut = createViewModel()
    }

    @Test
    @DisplayName(
        """
        GIVEN no location available
        WHEN collecting fuel station state
        THEN emits Loading then Error
        """
    )
    fun emitsErrorWhenLocationMissing() = runTest {
        sut.fuelStation.test {
            assertEquals(DetailStationUiState.Loading, awaitItem())
            assertEquals(DetailStationUiState.Error, awaitItem())
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN location and station available
        WHEN collecting fuel station state
        THEN emits Success with station id and address
        """
    )
    fun emitsSuccessWithAddress() = runTest {
        fakeLocationTracker.setLastKnownLocation(testLocation())
        fakeFuelStationDao.setStations(listOf(stationEntity(id = 10, price = 1.55)))

        sut = createViewModel()

        sut.fuelStation.test {
            assertEquals(DetailStationUiState.Loading, awaitItem())
            val successState = awaitItem() as DetailStationUiState.Success
            assertEquals(10, successState.stationModel.fuelStation.idServiceStation)
            assertEquals("Calle Mayor 1", successState.address)
            assertEquals(false, successState.isOpen)
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN geocoder throws an error
        WHEN collecting fuel station state
        THEN emits Success with null address
        """
    )
    fun emitsSuccessWithNullAddressOnGeocoderError() = runTest {
        fakeGeocoderAddress.shouldThrow = true
        fakeLocationTracker.setLastKnownLocation(testLocation())
        fakeFuelStationDao.setStations(listOf(stationEntity(id = 10, price = 1.55)))

        sut = createViewModel()

        sut.fuelStation.test {
            assertEquals(DetailStationUiState.Loading, awaitItem())
            val successState = awaitItem() as DetailStationUiState.Success
            assertEquals(10, successState.stationModel.fuelStation.idServiceStation)
            assertNull(successState.address)
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN success state available
        WHEN collecting static map url
        THEN emits null then the map url
        """
    )
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
    @DisplayName(
        """
        GIVEN ToggleFavorite events with true then false
        WHEN handling events
        THEN station is added and removed from favorites
        """
    )
    fun togglesFavorite() = runTest {
        sut.onEvent(DetailStationEvent.ToggleFavorite(isFavorite = true))
        sut.onEvent(DetailStationEvent.ToggleFavorite(isFavorite = false))
        advanceUntilIdle()

        assertEquals(listOf(10), fakeUserDataRepository.addedFavoriteStations)
        assertEquals(listOf(10), fakeUserDataRepository.removedFavoriteStations)
    }

    @Test
    @DisplayName(
        """
        GIVEN station loaded and TogglePriceAlert events with true then false
        WHEN handling events
        THEN price alert is added and removed
        """
    )
    fun togglesPriceAlert() = runTest {
        fakeLocationTracker.setLastKnownLocation(testLocation())
        fakeFuelStationDao.setStations(listOf(stationEntity(id = 10, price = 1.55)))
        sut = createViewModel()

        sut.fuelStation.test {
            assertEquals(DetailStationUiState.Loading, awaitItem())
            assertNotNull(awaitItem() as? DetailStationUiState.Success)
            cancelAndIgnoreRemainingEvents()
        }

        sut.onEvent(DetailStationEvent.TogglePriceAlert(isEnabled = true))
        sut.onEvent(DetailStationEvent.TogglePriceAlert(isEnabled = false))
        advanceUntilIdle()

        assertEquals(listOf(10 to 1.55), fakePriceAlertRepository.addedAlerts)
        assertEquals(listOf(10), fakePriceAlertRepository.removedAlerts)
    }

    @Test
    @DisplayName(
        """
        GIVEN vehicle loaded in state
        WHEN UpdateTankCapacity event is handled
        THEN vehicle tank capacity is updated in repository
        """
    )
    fun updatesTankCapacity() = runTest {
        sut.vehicle.test {
            awaitItem() // null (initial value)
            awaitItem() // Vehicle loaded from repository
        }

        sut.onEvent(DetailStationEvent.UpdateTankCapacity(capacity = 60))
        advanceUntilIdle()

        assertEquals(listOf(1L to 60), fakeVehicleRepository.updatedTankCapacities)
    }

    @Test
    @DisplayName(
        """
        GIVEN user data with lastUpdate set
        WHEN collecting lastUpdate state
        THEN emits initial 0 then actual value
        """
    )
    fun emitsLastUpdate() = runTest {
        sut.lastUpdate.test {
            assertEquals(0L, awaitItem())
            assertEquals(123L, awaitItem())
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN user data with a principal vehicle
        WHEN collecting vehicle state
        THEN emits null then the correct vehicle
        """
    )
    fun emitsVehicleFromUserData() = runTest {
        sut.vehicle.test {
            assertNull(awaitItem())
            val loadedVehicle = awaitItem()
            assertNotNull(loadedVehicle)
            assertEquals(1L, loadedVehicle!!.id)
            assertEquals(FuelType.GASOLINE_95, loadedVehicle.fuelType)
            assertEquals(40, loadedVehicle.tankCapacity)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN ShareStation event is dispatched
        WHEN handling event
        THEN logs STATION_SHARED analytics event with the station brand
        """
    )
    fun logsAnalyticsOnShareStation() = runTest {
        fakeLocationTracker.setLastKnownLocation(testLocation())
        fakeFuelStationDao.setStations(listOf(stationEntity(id = 10, price = 1.55)))
        val fakeAnalyticsHelper = FakeAnalyticsHelper()
        sut = createViewModel(analyticsHelper = fakeAnalyticsHelper)
        advanceUntilIdle()

        sut.onEvent(DetailStationEvent.ShareStation)
        advanceUntilIdle()

        val sharedEvents = fakeAnalyticsHelper.loggedEvents.filter { it.type == AnalyticsEvent.Types.STATION_SHARED }
        assertEquals(1, sharedEvents.size)
        assertTrue(sharedEvents.first().extras.any { it.key == AnalyticsEvent.ParamKeys.STATION_BRAND })
    }

    private fun createViewModel(
        analyticsHelper: AnalyticsHelper = NoOpAnalyticsHelper(),
    ): DetailStationViewModel {
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
            priceAlertDao = fakePriceAlertDao,
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
            removePriceAlertUseCase = RemovePriceAlertUseCase(fakePriceAlertRepository),
            updateVehicleTankCapacityUseCase = UpdateVehicleTankCapacityUseCase(
                fakeVehicleRepository
            ),
            analyticsHelper = analyticsHelper,
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
            isFavorite = false,
        )

    private class FakeAnalyticsHelper : AnalyticsHelper {
        val loggedEvents = mutableListOf<AnalyticsEvent>()

        override fun logEvent(event: AnalyticsEvent) {
            loggedEvents.add(event)
        }

        override fun updateSuperProperties(properties: Map<String, Any>) = Unit
    }
}
