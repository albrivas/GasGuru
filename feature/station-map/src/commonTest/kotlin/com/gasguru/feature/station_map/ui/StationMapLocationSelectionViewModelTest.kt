package com.gasguru.feature.station_map.ui

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.Route
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class StationMapLocationSelectionViewModelTest : StationMapViewModelTestBase() {

    private val pressedLocation = LatLng(latitude = 40.42, longitude = -3.70)

    @Test
    fun `GIVEN map screen WHEN a map location is selected THEN pin is placed and nearby stations are queried around it`() = runTest {
        val recordingRepository = RecordingFuelStationRepository(delegate = buildOfflineRepository())
        fakeFuelStationDao.setStations(
            listOf(stationEntity(id = 7, latitude = 40.421, longitude = -3.701, price = 1.40)),
        )
        sut = createViewModel(fuelStationRepository = recordingRepository)

        sut.handleEvent(StationMapEvent.SelectMapLocation(location = pressedLocation))
        advanceUntilIdle()

        val state = sut.state.value
        assertEquals(pressedLocation, state.customPinLocation)
        assertEquals(pressedLocation, recordingRepository.requestedLocations.last())
        assertEquals(listOf(7), state.mapStations.map { it.fuelStation.idServiceStation })
        assertFalse(state.loading)
        assertTrue(state.shouldCenterMap)
    }

    @Test
    fun `GIVEN nearby filter set to 50 WHEN a map location is selected THEN the query uses 50 stations`() = runTest {
        val recordingRepository = RecordingFuelStationRepository(delegate = buildOfflineRepository())
        sut = createViewModel(fuelStationRepository = recordingRepository)
        sut.handleEvent(StationMapEvent.UpdateNearbyFilter(number = "50"))
        advanceUntilIdle()

        sut.handleEvent(StationMapEvent.SelectMapLocation(location = pressedLocation))
        advanceUntilIdle()

        assertEquals(50, recordingRepository.requestedNearbyMaxStations.last())
    }

    @Test
    fun `GIVEN a pin on the map WHEN user location is requested THEN the pin is removed`() = runTest {
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))
        sut = createViewModel()
        sut.handleEvent(StationMapEvent.SelectMapLocation(location = pressedLocation))
        advanceUntilIdle()

        sut.handleEvent(StationMapEvent.GetStationByCurrentLocation)
        advanceUntilIdle()

        assertNull(sut.state.value.customPinLocation)
    }

    @Test
    fun `GIVEN a pin on the map WHEN search this area is used THEN the pin is removed`() = runTest {
        sut = createViewModel()
        sut.handleEvent(StationMapEvent.SelectMapLocation(location = pressedLocation))
        advanceUntilIdle()
        sut.handleEvent(StationMapEvent.CameraIdle(bounds = madridBounds, movedByUser = true))

        sut.handleEvent(StationMapEvent.SearchThisArea)
        advanceUntilIdle()

        assertNull(sut.state.value.customPinLocation)
    }

    @Test
    fun `GIVEN a pin was placed WHEN a filter changes THEN stations are queried again around the pin`() = runTest {
        val recordingRepository = RecordingFuelStationRepository(delegate = buildOfflineRepository())
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))
        sut = createViewModel(fuelStationRepository = recordingRepository)
        sut.handleEvent(StationMapEvent.SelectMapLocation(location = pressedLocation))
        advanceUntilIdle()
        recordingRepository.requestedLocations.clear()

        sut.handleEvent(StationMapEvent.UpdateNearbyFilter(number = "25"))
        advanceUntilIdle()

        assertTrue(recordingRepository.requestedLocations.isNotEmpty())
        assertTrue(recordingRepository.requestedLocations.all { location -> location == pressedLocation })
    }

    @Test
    fun `GIVEN an active route WHEN a map location is selected THEN it is ignored`() = runTest {
        fakeRoutesRepository.setRoute(
            Route(
                route = listOf(
                    LatLng(latitude = 40.0, longitude = -3.0),
                    LatLng(latitude = 40.2, longitude = -3.2),
                ),
                distanceText = "10 km",
                durationText = "15 min",
            ),
        )
        fakePlacesRepository.setLocationForId(placeId = "dest", location = LatLng(latitude = 40.2, longitude = -3.2))
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))
        sut.handleEvent(
            StationMapEvent.StartRoute(originId = null, destinationId = "dest", destinationName = "Madrid"),
        )
        advanceUntilIdle()
        assertNotNull(sut.state.value.route)

        sut.handleEvent(StationMapEvent.SelectMapLocation(location = pressedLocation))
        advanceUntilIdle()

        assertNull(sut.state.value.customPinLocation)
    }

    @Test
    fun `GIVEN map screen WHEN a map location is selected THEN map_location_selected analytics event is tracked`() = runTest {
        val analyticsHelper = RecordingAnalyticsHelper()
        sut = createViewModel(analyticsHelper = analyticsHelper)

        sut.handleEvent(StationMapEvent.SelectMapLocation(location = pressedLocation))
        advanceUntilIdle()

        assertEquals(
            1,
            analyticsHelper.events.count { event -> event.type == AnalyticsEvent.Types.MAP_LOCATION_SELECTED },
        )
    }
}
