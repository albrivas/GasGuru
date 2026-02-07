package com.gasguru.feature.station_map.ui

import com.gasguru.core.data.repository.stations.OfflineFuelStationRepository
import com.gasguru.core.data.repository.user.OfflineUserDataRepository
import com.gasguru.core.database.model.FuelStationEntity
import com.gasguru.core.database.model.UserDataEntity
import com.gasguru.core.domain.filters.GetFiltersUseCase
import com.gasguru.core.domain.filters.SaveFilterUseCase
import com.gasguru.core.domain.fuelstation.FuelStationByLocationUseCase
import com.gasguru.core.domain.fuelstation.GetFuelStationsInRouteUseCase
import com.gasguru.core.domain.location.GetCurrentLocationUseCase
import com.gasguru.core.domain.places.GetLocationPlaceUseCase
import com.gasguru.core.domain.route.GetRouteUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.model.data.FilterType
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.Route
import com.gasguru.core.model.data.UserData
import com.gasguru.core.testing.CoroutinesTestExtension
import com.gasguru.core.testing.fakes.data.database.FakeFavoriteStationDao
import com.gasguru.core.testing.fakes.data.database.FakeFuelStationDao
import com.gasguru.core.testing.fakes.data.database.FakePriceAlertDao
import com.gasguru.core.testing.fakes.data.database.FakeUserDataDao
import com.gasguru.core.testing.fakes.data.filter.FakeFilterRepository
import com.gasguru.core.testing.fakes.data.location.FakeLocationTracker
import com.gasguru.core.testing.fakes.data.network.FakeRemoteDataSource
import com.gasguru.core.testing.fakes.data.places.FakePlacesRepository
import com.gasguru.core.testing.fakes.data.route.FakeRoutesRepository
import com.gasguru.core.testing.fakes.data.user.FakeUserDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
class StationMapViewModelTest {

    private lateinit var sut: StationMapViewModel
    private lateinit var fakeUserDataRepository: FakeUserDataRepository
    private lateinit var fakeUserDataDao: FakeUserDataDao
    private lateinit var fakeFavoriteStationDao: FakeFavoriteStationDao
    private lateinit var fakeFuelStationDao: FakeFuelStationDao
    private lateinit var fakePriceAlertDao: FakePriceAlertDao
    private lateinit var fakeRoutesRepository: FakeRoutesRepository
    private lateinit var fakeFilterRepository: FakeFilterRepository
    private lateinit var fakePlacesRepository: FakePlacesRepository
    private lateinit var fakeLocationTracker: FakeLocationTracker

    @BeforeEach
    fun setUp() {
        fakeUserDataRepository = FakeUserDataRepository(
            initialUserData = UserData(fuelSelection = FuelType.GASOLINE_95)
        )
        fakeUserDataDao = FakeUserDataDao(
            initialUserData = UserDataEntity(
                fuelSelection = FuelType.GASOLINE_95,
                lastUpdate = 0,
                isOnboardingSuccess = true
            )
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

    @Test
    @DisplayName("GIVEN current location WHEN initialized THEN loads stations and updates state")
    fun loadsStationsOnInit() = runTest {
        val stations = listOf(
            stationEntity(id = 1, latitude = 40.0, longitude = -3.0, price = 1.50),
            stationEntity(id = 2, latitude = 40.1, longitude = -3.1, price = 1.20),
        )
        fakeFuelStationDao.setStations(stations)
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))

        sut = createViewModel()

        advanceUntilIdle()

        val state = sut.state.value
        assertFalse(state.loading)
        assertEquals(2, state.mapStations.size)
        val stationIds = state.listStations.map { it.fuelStation.idServiceStation }
        assertEquals(listOf(2, 1), stationIds)
    }

    @Test
    @DisplayName("GIVEN loaded stations WHEN changing tab THEN sorts by distance")
    fun sortsByDistanceOnTabChange() = runTest {
        val userLocation = LatLng(latitude = 40.0, longitude = -3.0)
        val stations = listOf(
            stationEntity(id = 1, latitude = 40.0, longitude = -3.0, price = 1.50),
            stationEntity(id = 2, latitude = 40.2, longitude = -3.2, price = 1.20),
        )
        fakeFuelStationDao.setStations(stations)
        fakeLocationTracker.setLastKnownLocation(userLocation)

        sut = createViewModel()

        advanceUntilIdle()

        sut.handleEvent(StationMapEvent.ChangeTab(selected = StationSortTab.DISTANCE))
        advanceUntilIdle()

        val stationIds = sut.state.value.listStations.map { it.fuelStation.idServiceStation }
        assertEquals(listOf(1, 2), stationIds)
    }

    @Test
    @DisplayName("GIVEN stations loaded WHEN map centered THEN shouldCenterMap resets")
    fun resetsCenterMapFlag() = runTest {
        fakeFuelStationDao.setStations(
            listOf(stationEntity(id = 1, latitude = 40.0, longitude = -3.0, price = 1.50))
        )
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))

        sut = createViewModel()

        advanceUntilIdle()

        assertEquals(true, sut.state.value.shouldCenterMap)

        sut.handleEvent(StationMapEvent.OnMapCentered)

        assertEquals(false, sut.state.value.shouldCenterMap)
    }

    @Test
    @DisplayName("GIVEN update nearby filter WHEN handling THEN saves filter selection")
    fun savesFilterSelection() = runTest {
        sut.handleEvent(StationMapEvent.UpdateNearbyFilter(number = "5"))
        advanceUntilIdle()

        val updates = fakeFilterRepository.updatedFilters
        assertEquals(1, updates.size)
        assertEquals(FilterType.NEARBY, updates[0].type)
        assertEquals(listOf("5"), updates[0].selection)
    }

    @Test
    @DisplayName("GIVEN update brand filter WHEN handling THEN saves brand selection")
    fun savesBrandFilterSelection() = runTest {
        sut.handleEvent(StationMapEvent.UpdateBrandFilter(selected = listOf("Repsol")))
        advanceUntilIdle()

        val updates = fakeFilterRepository.updatedFilters
        assertEquals(1, updates.size)
        assertEquals(FilterType.BRAND, updates[0].type)
        assertEquals(listOf("Repsol"), updates[0].selection)
    }

    @Test
    @DisplayName("GIVEN place selection WHEN handling THEN loads stations for place")
    fun loadsStationsForPlace() = runTest {
        fakePlacesRepository.setLocationForId(
            placeId = "place-1",
            location = LatLng(latitude = 41.0, longitude = -4.0)
        )
        fakeFuelStationDao.setStations(
            listOf(stationEntity(id = 3, latitude = 41.0, longitude = -4.0, price = 1.40))
        )

        sut.handleEvent(StationMapEvent.GetStationByPlace(placeId = "place-1"))

        advanceUntilIdle()

        val state = sut.state.value
        assertEquals(1, state.mapStations.size)
        assertEquals(3, state.mapStations.first().fuelStation.idServiceStation)
    }

    @Test
    @DisplayName("GIVEN start route WHEN handling THEN updates route state and stations")
    fun startRouteUpdatesState() = runTest {
        val route = Route(
            route = listOf(
                LatLng(latitude = 40.0, longitude = -3.0),
                LatLng(latitude = 40.2, longitude = -3.2),
            ),
            distanceText = "10 km",
            durationText = "15 min"
        )
        fakeRoutesRepository.setRoute(route)
        fakePlacesRepository.setLocationForId(
            placeId = "dest",
            location = LatLng(latitude = 40.2, longitude = -3.2)
        )
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))
        fakeFuelStationDao.setStations(
            listOf(stationEntity(id = 1, latitude = 40.0, longitude = -3.0, price = 1.30))
        )

        sut.handleEvent(
            StationMapEvent.StartRoute(
                originId = null,
                destinationId = "dest",
                destinationName = "Madrid"
            )
        )

        advanceUntilIdle()

        val state = sut.state.value
        assertNotNull(state.route)
        assertEquals("Madrid", state.routeDestinationName)
        assertEquals(1, state.mapStations.size)
        assertFalse(state.loading)
    }

    @Test
    @DisplayName("GIVEN active route WHEN canceling THEN clears route and reloads stations")
    fun cancelRouteClearsRouteAndReloadsStations() = runTest {
        val route = Route(
            route = listOf(
                LatLng(latitude = 40.0, longitude = -3.0),
                LatLng(latitude = 40.2, longitude = -3.2),
            ),
            distanceText = "10 km",
            durationText = "15 min"
        )
        fakeRoutesRepository.setRoute(route)
        fakePlacesRepository.setLocationForId(
            placeId = "dest",
            location = LatLng(latitude = 40.2, longitude = -3.2)
        )
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))
        fakeFuelStationDao.setStations(
            listOf(stationEntity(id = 1, latitude = 40.0, longitude = -3.0, price = 1.30))
        )

        sut.handleEvent(
            StationMapEvent.StartRoute(
                originId = null,
                destinationId = "dest",
                destinationName = "Madrid"
            )
        )
        advanceUntilIdle()

        sut.handleEvent(StationMapEvent.CancelRoute)
        advanceUntilIdle()

        val state = sut.state.value
        assertNull(state.route)
        assertNull(state.routeDestinationName)
        assertEquals(1, state.mapStations.size)
    }

    @Test
    @DisplayName("GIVEN route in progress WHEN canceling THEN clears route state and stops loading")
    fun cancelRouteStopsLoadingAndClearsState() = runTest {
        val route = Route(
            route = listOf(
                LatLng(latitude = 40.0, longitude = -3.0),
                LatLng(latitude = 40.2, longitude = -3.2),
            ),
            distanceText = "10 km",
            durationText = "15 min"
        )
        fakeRoutesRepository.setRoute(route)
        fakePlacesRepository.setLocationForId(
            placeId = "dest",
            location = LatLng(latitude = 40.2, longitude = -3.2)
        )
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))
        fakeFuelStationDao.setStations(
            listOf(stationEntity(id = 1, latitude = 40.0, longitude = -3.0, price = 1.30))
        )

        sut.handleEvent(
            StationMapEvent.StartRoute(
                originId = null,
                destinationId = "dest",
                destinationName = "Madrid"
            )
        )

        sut.handleEvent(StationMapEvent.CancelRoute)

        advanceUntilIdle()

        val state = sut.state.value
        assertNull(state.route)
        assertNull(state.routeDestinationName)
        assertFalse(state.loading)
    }

    @Test
    @DisplayName("GIVEN cancelled route WHEN starting new route THEN new route loads successfully")
    fun canStartNewRouteAfterCancellation() = runTest {
        val route1 = Route(
            route = listOf(
                LatLng(latitude = 40.0, longitude = -3.0),
                LatLng(latitude = 40.1, longitude = -3.1)
            ),
            distanceText = "5 km",
            durationText = "10 min"
        )
        val route2 = Route(
            route = listOf(
                LatLng(latitude = 40.0, longitude = -3.0),
                LatLng(latitude = 40.3, longitude = -3.3)
            ),
            distanceText = "15 km",
            durationText = "20 min"
        )

        fakeRoutesRepository.setRoute(route1)
        fakePlacesRepository.setLocationForId(
            placeId = "dest1",
            location = LatLng(latitude = 40.1, longitude = -3.1)
        )
        fakePlacesRepository.setLocationForId(
            placeId = "dest2",
            location = LatLng(latitude = 40.3, longitude = -3.3)
        )
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))
        fakeFuelStationDao.setStations(
            listOf(stationEntity(id = 1, latitude = 40.0, longitude = -3.0, price = 1.30))
        )

        sut.handleEvent(
            StationMapEvent.StartRoute(
                originId = null,
                destinationId = "dest1",
                destinationName = "Madrid"
            )
        )

        sut.handleEvent(StationMapEvent.CancelRoute)

        advanceUntilIdle()

        fakeRoutesRepository.setRoute(route2)
        sut.handleEvent(
            StationMapEvent.StartRoute(
                originId = null,
                destinationId = "dest2",
                destinationName = "Barcelona"
            )
        )

        advanceUntilIdle()

        val state = sut.state.value
        assertNotNull(state.route)
        assertEquals("Barcelona", state.routeDestinationName)
        assertEquals("15 km", state.route?.distanceText)
        assertEquals(1, state.mapStations.size)
        assertFalse(state.loading)
    }

    @Test
    @DisplayName("GIVEN no filters WHEN observing filters THEN returns default values")
    fun returnsDefaultFilterValues() = runTest {
        fakeFilterRepository.clearFilters()
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))

        sut = createViewModel()

        advanceUntilIdle()

        val filterState = sut.filters.value
        assertEquals(emptyList<String>(), filterState.filterBrand)
        assertEquals(10, filterState.filterStationsNearby)
        assertEquals(FilterUiState.OpeningHours.NONE, filterState.filterSchedule)
    }

    @Test
    @DisplayName("GIVEN invalid nearby filter WHEN observing filters THEN returns default 10")
    fun returnsDefaultNearbyWhenInvalid() = runTest {
        fakeFilterRepository.setFilter(
            type = FilterType.NEARBY,
            selection = listOf("invalid")
        )
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))

        sut = createViewModel()

        advanceUntilIdle()

        val filterState = sut.filters.value
        assertEquals(10, filterState.filterStationsNearby)
    }

    @Test
    @DisplayName("GIVEN no current location WHEN getting station by current location THEN handles error")
    fun handlesNoCurrentLocation() = runTest {
        fakeLocationTracker.setLastKnownLocation(null)
        fakeFuelStationDao.setStations(
            listOf(stationEntity(id = 1, latitude = 40.0, longitude = -3.0, price = 1.30))
        )

        sut = createViewModel()

        advanceUntilIdle()

        val state = sut.state.value
        assertEquals(emptyList<Any>(), state.mapStations)
    }

    @Test
    @DisplayName("GIVEN error getting route WHEN starting route THEN updates error state")
    fun handlesRouteError() = runTest {
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))
        fakePlacesRepository.setLocationForId(
            placeId = "dest",
            location = LatLng(latitude = 40.2, longitude = -3.2)
        )
        fakeRoutesRepository.setShouldThrowError(true)

        sut.handleEvent(
            StationMapEvent.StartRoute(
                originId = null,
                destinationId = "dest",
                destinationName = "Madrid"
            )
        )

        advanceUntilIdle()

        val state = sut.state.value
        assertNotNull(state.error)
        assertFalse(state.loading)
        assertNull(state.routeDestinationName)
    }

    @Test
    @DisplayName("GIVEN error getting place location WHEN getting stations THEN handles error")
    fun handlesPlaceLocationError() = runTest {
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))
        fakePlacesRepository.setShouldThrowError(true)

        sut.handleEvent(StationMapEvent.GetStationByPlace(placeId = "invalid"))

        advanceUntilIdle()

        val state = sut.state.value
        assertFalse(state.loading)
    }

    @Test
    @DisplayName("GIVEN error getting stations WHEN loading by location THEN updates error state")
    fun handlesStationLoadingError() = runTest {
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))
        fakeFuelStationDao.setShouldThrowError(true)

        sut = createViewModel()

        advanceUntilIdle()

        val state = sut.state.value
        assertNotNull(state.error)
        assertFalse(state.loading)
    }

    private fun createViewModel(): StationMapViewModel {
        val offlineUserDataRepository = OfflineUserDataRepository(
            userDataDao = fakeUserDataDao,
            favoriteStationDao = fakeFavoriteStationDao
        )
        val offlineFuelStationRepository = OfflineFuelStationRepository(
            fuelStationDao = fakeFuelStationDao,
            remoteDataSource = FakeRemoteDataSource(),
            defaultDispatcher = Dispatchers.Main,
            ioDispatcher = Dispatchers.Main,
            offlineUserDataRepository = offlineUserDataRepository,
            favoriteStationDao = fakeFavoriteStationDao,
            priceAlertDao = fakePriceAlertDao
        )

        return StationMapViewModel(
            fuelStationByLocation = FuelStationByLocationUseCase(offlineFuelStationRepository),
            getUserDataUseCase = GetUserDataUseCase(fakeUserDataRepository),
            getLocationPlaceUseCase = GetLocationPlaceUseCase(fakePlacesRepository),
            getCurrentLocationUseCase = GetCurrentLocationUseCase(fakeLocationTracker),
            getFiltersUseCase = GetFiltersUseCase(fakeFilterRepository),
            saveFilterUseCase = SaveFilterUseCase(fakeFilterRepository),
            getRouteUseCase = GetRouteUseCase(fakeRoutesRepository),
            getFuelStationsInRouteUseCase = GetFuelStationsInRouteUseCase(offlineFuelStationRepository),
            defaultDispatcher = Dispatchers.Main
        )
    }

    private fun stationEntity(
        id: Int,
        latitude: Double,
        longitude: Double,
        price: Double,
        brand: String = "Repsol"
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
