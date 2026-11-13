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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class StationMapAreaSearchViewModelTest : StationMapViewModelTestBase() {

    @Test
    fun `GIVEN stations loaded WHEN camera becomes idle after a user move THEN search this area button is shown`() = runTest {
        loadNearbyStations()

        sut.handleEvent(StationMapEvent.CameraIdle(bounds = madridBounds, movedByUser = true))

        assertTrue(sut.state.value.showSearchThisArea)
        assertEquals(madridBounds, sut.state.value.visibleBounds)
    }

    @Test
    fun `GIVEN stations loaded WHEN camera becomes idle after a programmatic move THEN search this area button stays hidden`() = runTest {
        loadNearbyStations()

        sut.handleEvent(StationMapEvent.CameraIdle(bounds = madridBounds, movedByUser = false))

        assertFalse(sut.state.value.showSearchThisArea)
        assertEquals(madridBounds, sut.state.value.visibleBounds)
    }

    @Test
    fun `GIVEN stations are still loading WHEN camera becomes idle after a user move THEN search this area button stays hidden`() = runTest {
        sut.handleEvent(StationMapEvent.CameraIdle(bounds = madridBounds, movedByUser = true))

        assertFalse(sut.state.value.showSearchThisArea)
    }

    @Test
    fun `GIVEN an active route WHEN camera becomes idle after a user move THEN search this area button stays hidden`() = runTest {
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
        fakeFuelStationDao.setStations(listOf(stationEntity(id = 1, latitude = 40.0, longitude = -3.0, price = 1.30)))
        sut.handleEvent(
            StationMapEvent.StartRoute(originId = null, destinationId = "dest", destinationName = "Madrid"),
        )
        advanceUntilIdle()
        assertNotNull(sut.state.value.route)

        sut.handleEvent(StationMapEvent.CameraIdle(bounds = madridBounds, movedByUser = true))

        assertFalse(sut.state.value.showSearchThisArea)
    }

    @Test
    fun `GIVEN search this area button is shown WHEN a nearby search starts THEN the button is hidden`() = runTest {
        loadNearbyStations()
        sut.handleEvent(StationMapEvent.CameraIdle(bounds = madridBounds, movedByUser = true))
        assertTrue(sut.state.value.showSearchThisArea)

        sut.handleEvent(StationMapEvent.GetStationByCurrentLocation)
        advanceUntilIdle()

        assertFalse(sut.state.value.showSearchThisArea)
    }

    @Test
    fun `GIVEN visible bounds WHEN SearchThisArea event is sent THEN area use case is called with those bounds and the map is not recentered`() = runTest {
        val recordingRepository = RecordingFuelStationRepository(delegate = buildOfflineRepository())
        fakeFuelStationDao.setStations(
            listOf(
                stationEntity(id = 1, latitude = 40.4, longitude = -3.7, price = 1.50),
                stationEntity(id = 2, latitude = 45.0, longitude = -3.7, price = 1.20),
            ),
        )
        val userLocation = LatLng(latitude = 40.0, longitude = -3.0)
        fakeLocationTracker.setLastKnownLocation(userLocation)
        sut = createViewModel(fuelStationRepository = recordingRepository)
        sut.handleEvent(StationMapEvent.GetStationByCurrentLocation)
        advanceUntilIdle()
        sut.handleEvent(StationMapEvent.OnMapCentered)
        val boundsBeforeSearch = sut.state.value.mapBounds
        sut.handleEvent(StationMapEvent.CameraIdle(bounds = madridBounds, movedByUser = true))

        sut.handleEvent(StationMapEvent.SearchThisArea)
        advanceUntilIdle()

        val areaRequest = recordingRepository.areaRequests.single()
        assertEquals(madridBounds.toMapBounds(), areaRequest.bounds)
        assertEquals(DEFAULT_NEARBY_FILTER, areaRequest.maxStations)
        val state = sut.state.value
        assertEquals(listOf(1), state.mapStations.map { it.fuelStation.idServiceStation })
        assertFalse(state.shouldCenterMap)
        assertEquals(boundsBeforeSearch, state.mapBounds)
        assertFalse(state.showSearchThisArea)
        assertFalse(state.isSearchingArea)
    }

    @Test
    fun `GIVEN user far from the area WHEN SearchThisArea event is sent THEN distances are measured from the area center`() = runTest {
        fakeFuelStationDao.setStations(
            listOf(
                stationEntity(id = 1, latitude = 40.5, longitude = -3.5, price = 1.50),
                stationEntity(id = 2, latitude = 40.9, longitude = -3.9, price = 1.40),
            ),
        )
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))
        sut = createViewModel()
        sut.handleEvent(StationMapEvent.CameraIdle(bounds = madridBounds, movedByUser = true))

        sut.handleEvent(StationMapEvent.SearchThisArea)
        advanceUntilIdle()

        val distanceById = sut.state.value.mapStations.associate { station ->
            station.fuelStation.idServiceStation to station.fuelStation.distance
        }
        assertTrue(distanceById.getValue(1) < 1f)
        assertTrue(distanceById.getValue(1) < distanceById.getValue(2))
    }

    @Test
    fun `GIVEN no visible bounds yet WHEN SearchThisArea event is sent THEN nothing is queried`() = runTest {
        val recordingRepository = RecordingFuelStationRepository(delegate = buildOfflineRepository())
        sut = createViewModel(fuelStationRepository = recordingRepository)

        sut.handleEvent(StationMapEvent.SearchThisArea)
        advanceUntilIdle()

        assertTrue(recordingRepository.areaRequests.isEmpty())
        assertFalse(sut.state.value.isSearchingArea)
    }

    @Test
    fun `GIVEN more stations than the nearby filter WHEN SearchThisArea event is sent THEN result is capped to the filter`() = runTest {
        val totalStations = DEFAULT_NEARBY_FILTER + 1
        fakeFuelStationDao.setStations(
            (1..totalStations).map { index ->
                stationEntity(id = index, latitude = 40.4 + index * 0.0001, longitude = -3.7, price = 1.50)
            },
        )
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))
        sut = createViewModel()
        sut.handleEvent(StationMapEvent.CameraIdle(bounds = madridBounds, movedByUser = true))

        sut.handleEvent(StationMapEvent.SearchThisArea)
        advanceUntilIdle()

        assertEquals(DEFAULT_NEARBY_FILTER, sut.state.value.mapStations.size)
    }

    @Test
    fun `GIVEN an area search is active WHEN a filter changes THEN the area is queried again and nearby is not`() = runTest {
        val recordingRepository = RecordingFuelStationRepository(delegate = buildOfflineRepository())
        fakeFuelStationDao.setStations(
            listOf(
                stationEntity(id = 1, latitude = 40.4, longitude = -3.7, price = 1.50, brand = "Repsol"),
                stationEntity(id = 2, latitude = 40.5, longitude = -3.6, price = 1.20, brand = "Cepsa"),
            ),
        )
        fakeLocationTracker.setLastKnownLocation(LatLng(latitude = 40.0, longitude = -3.0))
        sut = createViewModel(fuelStationRepository = recordingRepository)
        sut.handleEvent(StationMapEvent.CameraIdle(bounds = madridBounds, movedByUser = true))
        sut.handleEvent(StationMapEvent.SearchThisArea)
        advanceUntilIdle()
        assertEquals(2, sut.state.value.mapStations.size)
        recordingRepository.requestedLocations.clear()
        recordingRepository.areaRequests.clear()

        sut.handleEvent(StationMapEvent.UpdateBrandFilter(selected = listOf("Cepsa")))
        advanceUntilIdle()

        assertTrue(recordingRepository.requestedLocations.isEmpty())
        assertTrue(recordingRepository.areaRequests.isNotEmpty())
        assertTrue(recordingRepository.areaRequests.all { request -> request.brands == listOf("Cepsa") })
        assertEquals(listOf(2), sut.state.value.mapStations.map { it.fuelStation.idServiceStation })
        assertFalse(sut.state.value.shouldCenterMap)
    }

    @Test
    fun `GIVEN an area search WHEN results arrive THEN search_this_area analytics event is tracked once`() = runTest {
        val analyticsHelper = RecordingAnalyticsHelper()
        fakeFuelStationDao.setStations(
            listOf(stationEntity(id = 1, latitude = 40.4, longitude = -3.7, price = 1.50)),
        )
        sut = createViewModel(analyticsHelper = analyticsHelper)
        sut.handleEvent(StationMapEvent.CameraIdle(bounds = madridBounds, movedByUser = true))

        sut.handleEvent(StationMapEvent.SearchThisArea)
        advanceUntilIdle()
        sut.handleEvent(StationMapEvent.UpdateNearbyFilter(number = "15"))
        advanceUntilIdle()

        val areaEvents = analyticsHelper.events.filter { it.type == AnalyticsEvent.Types.SEARCH_THIS_AREA }
        assertEquals(1, areaEvents.size)
        assertEquals(
            listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.STATION_COUNT, value = "1"),
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.IS_TRUNCATED, value = "false"),
            ),
            areaEvents.single().extras,
        )
    }

    @Test
    fun `GIVEN an area search is active WHEN a place is searched THEN nearby mode replaces the area query and recenters the map`() = runTest {
        val recordingRepository = RecordingFuelStationRepository(delegate = buildOfflineRepository())
        fakePlacesRepository.setLocationForId(placeId = "place-1", location = LatLng(latitude = 41.0, longitude = -4.0))
        fakeFuelStationDao.setStations(
            listOf(stationEntity(id = 1, latitude = 40.4, longitude = -3.7, price = 1.50)),
        )
        sut = createViewModel(fuelStationRepository = recordingRepository)
        sut.handleEvent(StationMapEvent.CameraIdle(bounds = madridBounds, movedByUser = true))
        sut.handleEvent(StationMapEvent.SearchThisArea)
        advanceUntilIdle()
        sut.handleEvent(StationMapEvent.GetStationByPlace(placeId = "place-1"))
        advanceUntilIdle()
        recordingRepository.areaRequests.clear()

        sut.handleEvent(StationMapEvent.UpdateNearbyFilter(number = "15"))
        advanceUntilIdle()

        assertTrue(recordingRepository.areaRequests.isEmpty())
        assertTrue(sut.state.value.shouldCenterMap)
    }

    @Test
    fun `GIVEN nearby filter set to 25 WHEN SearchThisArea event is sent THEN area query is limited to 25 stations`() = runTest {
        val recordingRepository = RecordingFuelStationRepository(delegate = buildOfflineRepository())
        sut = createViewModel(fuelStationRepository = recordingRepository)
        sut.handleEvent(StationMapEvent.UpdateNearbyFilter(number = "25"))
        advanceUntilIdle()
        sut.handleEvent(StationMapEvent.CameraIdle(bounds = madridBounds, movedByUser = true))

        sut.handleEvent(StationMapEvent.SearchThisArea)
        advanceUntilIdle()

        assertEquals(25, recordingRepository.areaRequests.last().maxStations)
    }
}

private const val DEFAULT_NEARBY_FILTER = 10
