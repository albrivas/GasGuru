package com.gasguru.feature.station_map.ui

import app.cash.turbine.test
import com.gasguru.core.model.data.FilterType
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.OpeningHours
import com.gasguru.core.model.data.Route
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class StationMapViewModelTest : StationMapViewModelTestBase() {

    @Test
    fun `GIVEN stations in dao and current location WHEN GetStationByCurrentLocation event is sent THEN stations are loaded and sorted by price`() = runTest {
        val stations = listOf(
            stationEntity(id = 1, latitude = 40.0, longitude = -3.0, price = 1.50),
            stationEntity(id = 2, latitude = 40.1, longitude = -3.1, price = 1.20),
        )
        fakeFuelStationDao.setStations(stations)
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))

        sut = createViewModel()
        sut.handleEvent(StationMapEvent.GetStationByCurrentLocation)

        advanceUntilIdle()

        val state = sut.state.value
        assertFalse(state.loading)
        assertEquals(2, state.mapStations.size)
        val stationIds = state.listStations.map { it.fuelStation.idServiceStation }
        assertEquals(listOf(2, 1), stationIds)
    }

    @Test
    fun `GIVEN stations loaded by location WHEN ChangeTab event selects DISTANCE tab THEN list is re-sorted by distance ascending`() = runTest {
        val userLocation = LatLng(latitude = 40.0, longitude = -3.0)
        val stations = listOf(
            stationEntity(id = 1, latitude = 40.0, longitude = -3.0, price = 1.50),
            stationEntity(id = 2, latitude = 40.2, longitude = -3.2, price = 1.20),
        )
        fakeFuelStationDao.setStations(stations)
        fakeLocationTracker.setLastKnownLocation(userLocation)

        sut = createViewModel()
        sut.handleEvent(StationMapEvent.GetStationByCurrentLocation)

        advanceUntilIdle()

        sut.handleEvent(StationMapEvent.ChangeTab(selected = StationSortTab.DISTANCE))
        advanceUntilIdle()

        val stationIds = sut.state.value.listStations.map { it.fuelStation.idServiceStation }
        assertEquals(listOf(1, 2), stationIds)
    }

    @Test
    fun `GIVEN shouldCenterMap is true after loading WHEN OnMapCentered event is sent THEN shouldCenterMap becomes false`() = runTest {
        fakeFuelStationDao.setStations(
            listOf(stationEntity(id = 1, latitude = 40.0, longitude = -3.0, price = 1.50))
        )
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))

        sut = createViewModel()
        sut.handleEvent(StationMapEvent.GetStationByCurrentLocation)

        advanceUntilIdle()

        assertEquals(true, sut.state.value.shouldCenterMap)

        sut.handleEvent(StationMapEvent.OnMapCentered)

        assertEquals(false, sut.state.value.shouldCenterMap)
    }

    @Test
    fun `GIVEN map screen WHEN UpdateNearbyFilter event is sent with value 5 THEN repository saves NEARBY filter with selection 5`() = runTest {
        sut.handleEvent(StationMapEvent.UpdateNearbyFilter(number = "5"))
        advanceUntilIdle()

        val updates = fakeFilterRepository.updatedFilters
        assertEquals(1, updates.size)
        assertEquals(FilterType.NEARBY, updates[0].type)
        assertEquals(listOf("5"), updates[0].selection)
    }

    @Test
    fun `GIVEN map screen WHEN UpdateBrandFilter event is sent with Repsol THEN repository saves BRAND filter with Repsol selection`() = runTest {
        sut.handleEvent(StationMapEvent.UpdateBrandFilter(selected = listOf("Repsol")))
        advanceUntilIdle()

        val updates = fakeFilterRepository.updatedFilters
        assertEquals(1, updates.size)
        assertEquals(FilterType.BRAND, updates[0].type)
        assertEquals(listOf("Repsol"), updates[0].selection)
    }

    @Test
    fun `GIVEN a place with known location and a station in dao WHEN GetStationByPlace event is sent THEN state contains that station`() = runTest {
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
    fun `GIVEN route and destination configured WHEN StartRoute event is sent THEN state contains route data destination name and stations`() = runTest {
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
    fun `GIVEN an active route WHEN CancelRoute event is sent THEN route and destination name are cleared and stations are reloaded`() = runTest {
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
    fun `GIVEN StartRoute event in progress WHEN CancelRoute event is sent immediately THEN loading stops and route state is null`() = runTest {
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
    fun `GIVEN a route was started and then cancelled WHEN a new StartRoute event is sent THEN the new route and destination are loaded correctly`() = runTest {
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
    fun `GIVEN filter repository has no saved filters WHEN filters flow is collected THEN default values are returned for all filter types`() = runTest {
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
    fun `GIVEN filter repository has NEARBY filter with non-numeric value WHEN filters flow is collected THEN default nearby value of 10 is used`() = runTest {
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
    fun `GIVEN location tracker returns null WHEN GetStationByCurrentLocation event is sent THEN map stations list remains empty`() = runTest {
        fakeLocationTracker.setLastKnownLocation(null)
        fakeFuelStationDao.setStations(
            listOf(stationEntity(id = 1, latitude = 40.0, longitude = -3.0, price = 1.30))
        )

        sut = createViewModel()
        sut.handleEvent(StationMapEvent.GetStationByCurrentLocation)

        advanceUntilIdle()

        val state = sut.state.value
        assertEquals(emptyList<Any>(), state.mapStations)
    }

    @Test
    fun `GIVEN route repository is configured to throw WHEN StartRoute event is sent THEN error state is set and ShowRouteError effect is emitted`() = runTest {
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))
        fakePlacesRepository.setLocationForId(
            placeId = "dest",
            location = LatLng(latitude = 40.2, longitude = -3.2)
        )
        fakeRoutesRepository.setShouldThrowError(true)

        sut.effects.test {
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
            assertIs<StationMapEffect.ShowRouteError>(awaitItem())
        }
    }

    @Test
    fun `GIVEN route repository returns null route WHEN StartRoute event is sent THEN loading stops and ShowRouteError effect is emitted`() = runTest {
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))
        fakePlacesRepository.setLocationForId(
            placeId = "dest",
            location = LatLng(latitude = 40.2, longitude = -3.2)
        )
        fakeRoutesRepository.setRoute(null)

        sut.effects.test {
            sut.handleEvent(
                StationMapEvent.StartRoute(
                    originId = null,
                    destinationId = "dest",
                    destinationName = "Madrid"
                )
            )
            advanceUntilIdle()

            val state = sut.state.value
            assertFalse(state.loading)
            assertNull(state.routeDestinationName)
            assertIs<StationMapEffect.ShowRouteError>(awaitItem())
        }
    }

    @Test
    fun `GIVEN places repository is configured to throw WHEN GetStationByPlace event is sent THEN loading stops without crash`() = runTest {
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))
        fakePlacesRepository.setShouldThrowError(true)

        sut.handleEvent(StationMapEvent.GetStationByPlace(placeId = "invalid"))

        advanceUntilIdle()

        val state = sut.state.value
        assertFalse(state.loading)
    }

    @Test
    fun `GIVEN fuel station dao is configured to throw WHEN GetStationByCurrentLocation event is sent THEN error state is set and loading stops`() = runTest {
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))
        fakeFuelStationDao.setShouldThrowError(true)

        sut = createViewModel()
        sut.handleEvent(StationMapEvent.GetStationByCurrentLocation)

        advanceUntilIdle()

        val state = sut.state.value
        assertNotNull(state.error)
        assertFalse(state.loading)
    }

    @Test
    fun `GIVEN place A then place B searched WHEN a filter changes THEN only the query for place B is re-run`() = runTest {
        val locationA = LatLng(latitude = 37.0, longitude = -5.0)
        val locationB = LatLng(latitude = 41.0, longitude = -4.0)
        fakePlacesRepository.setLocationForId(placeId = "place-a", location = locationA)
        fakePlacesRepository.setLocationForId(placeId = "place-b", location = locationB)
        fakeFuelStationDao.setStations(
            listOf(
                stationEntity(id = 1, latitude = 37.0, longitude = -5.0, price = 1.50),
                stationEntity(id = 2, latitude = 41.0, longitude = -4.0, price = 1.40),
            ),
        )
        val recordingRepository = RecordingFuelStationRepository(delegate = buildOfflineRepository())
        sut = createViewModel(fuelStationRepository = recordingRepository)

        sut.handleEvent(StationMapEvent.GetStationByPlace(placeId = "place-a"))
        advanceUntilIdle()
        sut.handleEvent(StationMapEvent.GetStationByPlace(placeId = "place-b"))
        advanceUntilIdle()
        recordingRepository.requestedLocations.clear()

        sut.handleEvent(StationMapEvent.UpdateNearbyFilter(number = "15"))
        advanceUntilIdle()

        assertTrue(recordingRepository.requestedLocations.isNotEmpty())
        assertTrue(recordingRepository.requestedLocations.all { requested -> requested == locationB })
        assertEquals(2, sut.state.value.mapStations.size)
    }
}
