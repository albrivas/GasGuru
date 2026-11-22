package com.gasguru.feature.station_map.ui

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.analytics.NoOpAnalyticsHelper
import com.gasguru.core.data.repository.stations.FuelStationRepository
import com.gasguru.core.data.repository.stations.OfflineFuelStationRepository
import com.gasguru.core.data.repository.user.OfflineUserDataRepository
import com.gasguru.core.database.model.FuelStationEntity
import com.gasguru.core.database.model.UserDataEntity
import com.gasguru.core.database.model.VehicleEntity
import com.gasguru.core.domain.filters.GetFiltersUseCase
import com.gasguru.core.domain.filters.SaveFilterUseCase
import com.gasguru.core.domain.fuelstation.FuelStationByLocationUseCase
import com.gasguru.core.domain.fuelstation.GetFuelStationsInAreaUseCase
import com.gasguru.core.domain.fuelstation.GetFuelStationsInRouteUseCase
import com.gasguru.core.domain.location.GetCurrentLocationUseCase
import com.gasguru.core.domain.places.GetLocationPlaceUseCase
import com.gasguru.core.domain.route.GetRouteUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.model.data.AreaSearchResult
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.MapBounds
import com.gasguru.core.model.data.OpeningHours
import com.gasguru.core.model.data.UserData
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.VehicleType
import com.gasguru.core.testing.CoroutineTest
import com.gasguru.core.testing.fakes.data.database.FakeFavoriteStationDao
import com.gasguru.core.testing.fakes.data.database.FakeFuelStationDao
import com.gasguru.core.testing.fakes.data.database.FakePriceAlertDao
import com.gasguru.core.testing.fakes.data.database.FakeUserDataDao
import com.gasguru.core.testing.fakes.data.database.FakeVehicleDao
import com.gasguru.core.testing.fakes.data.filter.FakeFilterRepository
import com.gasguru.core.testing.fakes.data.location.FakeLocationTracker
import com.gasguru.core.testing.fakes.data.network.FakeRemoteDataSource
import com.gasguru.core.testing.fakes.data.places.FakePlacesRepository
import com.gasguru.core.testing.fakes.data.route.FakeRoutesRepository
import com.gasguru.core.testing.fakes.data.user.FakeUserDataRepository
import com.gasguru.feature.station_map.ui.model.GeoBounds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.BeforeTest
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
abstract class StationMapViewModelTestBase : CoroutineTest() {

    protected lateinit var sut: StationMapViewModel
    protected lateinit var fakeUserDataRepository: FakeUserDataRepository
    protected lateinit var fakeUserDataDao: FakeUserDataDao
    protected lateinit var fakeVehicleDao: FakeVehicleDao
    protected lateinit var fakeFavoriteStationDao: FakeFavoriteStationDao
    protected lateinit var fakeFuelStationDao: FakeFuelStationDao
    protected lateinit var fakePriceAlertDao: FakePriceAlertDao
    protected lateinit var fakeRoutesRepository: FakeRoutesRepository
    protected lateinit var fakeFilterRepository: FakeFilterRepository
    protected lateinit var fakePlacesRepository: FakePlacesRepository
    protected lateinit var fakeLocationTracker: FakeLocationTracker

    @BeforeTest
    fun setUp() {
        fakeUserDataRepository = FakeUserDataRepository(
            initialUserData = UserData(
                vehicles = listOf(
                    Vehicle(
                        id = 1L,
                        fuelType = FuelType.GASOLINE_95,
                        name = null,
                        tankCapacity = 40,
                        vehicleType = VehicleType.CAR,
                        isPrincipal = true,
                    )
                )
            )
        )
        fakeUserDataDao = FakeUserDataDao(
            initialUserData = UserDataEntity(
                lastUpdate = 0,
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
        fakeFavoriteStationDao = FakeFavoriteStationDao()
        fakeFuelStationDao = FakeFuelStationDao()
        fakePriceAlertDao = FakePriceAlertDao()
        fakeRoutesRepository = FakeRoutesRepository()
        fakeFilterRepository = FakeFilterRepository()
        fakePlacesRepository = FakePlacesRepository()
        fakeLocationTracker = FakeLocationTracker()

        sut = createViewModel()
    }

    protected fun buildOfflineRepository(): OfflineFuelStationRepository {
        val offlineUserDataRepository = OfflineUserDataRepository(
            userDataDao = fakeUserDataDao,
            favoriteStationDao = fakeFavoriteStationDao,
            vehicleDao = fakeVehicleDao,
        )
        return OfflineFuelStationRepository(
            fuelStationDao = fakeFuelStationDao,
            remoteDataSource = FakeRemoteDataSource(),
            defaultDispatcher = Dispatchers.Main,
            ioDispatcher = Dispatchers.Main,
            offlineUserDataRepository = offlineUserDataRepository,
            favoriteStationDao = fakeFavoriteStationDao,
            priceAlertDao = fakePriceAlertDao,
        )
    }

    protected val madridBounds = GeoBounds(
        southwest = LatLng(latitude = 40.0, longitude = -4.0),
        northeast = LatLng(latitude = 41.0, longitude = -3.0),
    )

    protected fun TestScope.loadNearbyStations() {
        fakeFuelStationDao.setStations(
            listOf(stationEntity(id = 1, latitude = 40.0, longitude = -3.0, price = 1.50)),
        )
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))
        sut = createViewModel()
        sut.handleEvent(StationMapEvent.GetStationByCurrentLocation)
        advanceUntilIdle()
        assertFalse(sut.state.value.loading)
    }

    protected fun createViewModel(
        fuelStationRepository: FuelStationRepository = buildOfflineRepository(),
        analyticsHelper: AnalyticsHelper = NoOpAnalyticsHelper(),
    ): StationMapViewModel {
        return StationMapViewModel(
            fuelStationByLocation = FuelStationByLocationUseCase(fuelStationRepository),
            getUserDataUseCase = GetUserDataUseCase(fakeUserDataRepository),
            getLocationPlaceUseCase = GetLocationPlaceUseCase(fakePlacesRepository),
            getCurrentLocationUseCase = GetCurrentLocationUseCase(fakeLocationTracker),
            getFiltersUseCase = GetFiltersUseCase(fakeFilterRepository),
            saveFilterUseCase = SaveFilterUseCase(fakeFilterRepository),
            getRouteUseCase = GetRouteUseCase(fakeRoutesRepository),
            getFuelStationsInRouteUseCase = GetFuelStationsInRouteUseCase(
                fuelStationRepository
            ),
            getFuelStationsInAreaUseCase = GetFuelStationsInAreaUseCase(fuelStationRepository),
            defaultDispatcher = Dispatchers.Main,
            analyticsHelper = analyticsHelper,
        )
    }

    protected fun stationEntity(
        id: Int,
        latitude: Double,
        longitude: Double,
        price: Double,
        brand: String = "Repsol",
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
            brandStation = brand,
            typeSale = "",
            lastUpdate = 0,
            isFavorite = false
        )
}

internal class RecordingFuelStationRepository(
    private val delegate: FuelStationRepository,
) : FuelStationRepository by delegate {

    val requestedLocations = mutableListOf<LatLng>()
    val requestedNearbyMaxStations = mutableListOf<Int>()
    val areaRequests = mutableListOf<AreaRequest>()

    override fun getFuelStationByLocation(
        userLocation: LatLng,
        maxStations: Int,
        brands: List<String>,
        schedule: OpeningHours,
    ): Flow<List<FuelStation>> {
        requestedLocations.add(userLocation)
        requestedNearbyMaxStations.add(maxStations)
        return delegate.getFuelStationByLocation(
            userLocation = userLocation,
            maxStations = maxStations,
            brands = brands,
            schedule = schedule,
        )
    }

    override fun getFuelStationsInArea(
        bounds: MapBounds,
        maxStations: Int,
        brands: List<String>,
        schedule: OpeningHours,
    ): Flow<AreaSearchResult> {
        areaRequests.add(
            AreaRequest(
                bounds = bounds,
                maxStations = maxStations,
                brands = brands,
            ),
        )
        return delegate.getFuelStationsInArea(
            bounds = bounds,
            maxStations = maxStations,
            brands = brands,
            schedule = schedule,
        )
    }
}

internal data class AreaRequest(
    val bounds: MapBounds,
    val maxStations: Int,
    val brands: List<String>,
)

internal class RecordingAnalyticsHelper : AnalyticsHelper {
    val events = mutableListOf<AnalyticsEvent>()

    override fun logEvent(event: AnalyticsEvent) {
        events.add(event)
    }

    override fun updateSuperProperties(properties: Map<String, Any>) = Unit
}
