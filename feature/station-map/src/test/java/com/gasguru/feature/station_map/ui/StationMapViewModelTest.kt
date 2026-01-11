package com.gasguru.feature.station_map.ui

import app.cash.turbine.test
import com.gasguru.core.domain.filters.GetFiltersUseCase
import com.gasguru.core.domain.filters.SaveFilterUseCase
import com.gasguru.core.domain.fuelstation.FuelStationByLocationUseCase
import com.gasguru.core.domain.fuelstation.GetFuelStationsInRouteUseCase
import com.gasguru.core.domain.location.GetCurrentLocationUseCase
import com.gasguru.core.domain.places.GetLocationPlaceUseCase
import com.gasguru.core.domain.route.GetRouteUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.testing.CoroutinesTestExtension
import com.gasguru.feature.station_map.ui.StationMapViewModelTestFixtures.createTestFuelStation
import com.gasguru.feature.station_map.ui.StationMapViewModelTestFixtures.createTestGoogleLatLng
import com.gasguru.feature.station_map.ui.StationMapViewModelTestFixtures.createTestLocation
import com.gasguru.feature.station_map.ui.StationMapViewModelTestFixtures.createTestRoute
import com.gasguru.feature.station_map.ui.StationMapViewModelTestFixtures.createTestUserData
import io.mockk.coEvery
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(
    CoroutinesTestExtension::class,
    MockKExtension::class,
)
class StationMapViewModelTest {

    private lateinit var fuelStationByLocation: FuelStationByLocationUseCase
    private lateinit var getUserDataUseCase: GetUserDataUseCase
    private lateinit var getLocationPlaceUseCase: GetLocationPlaceUseCase
    private lateinit var getCurrentLocationUseCase: GetCurrentLocationUseCase
    private lateinit var getFiltersUseCase: GetFiltersUseCase
    private lateinit var saveFilterUseCase: SaveFilterUseCase
    private lateinit var getRouteUseCase: GetRouteUseCase
    private lateinit var getFuelStationsInRouteUseCase: GetFuelStationsInRouteUseCase
    private lateinit var defaultDispatcher: CoroutineDispatcher

    private lateinit var viewModel: StationMapViewModel

    @BeforeEach
    fun setup() {
        fuelStationByLocation = mockk(relaxed = true)
        getUserDataUseCase = mockk(relaxed = true)
        getLocationPlaceUseCase = mockk(relaxed = true)
        getCurrentLocationUseCase = mockk(relaxed = true)
        getFiltersUseCase = mockk(relaxed = true)
        saveFilterUseCase = mockk(relaxed = true)
        getRouteUseCase = mockk(relaxed = true)
        getFuelStationsInRouteUseCase = mockk(relaxed = true)
        defaultDispatcher = StandardTestDispatcher()

        // Default mocks to prevent initialization issues
        every { getFiltersUseCase() } returns flowOf(emptyList())
        coEvery { getCurrentLocationUseCase() } returns null
    }

    private fun createViewModel(): StationMapViewModel {
        return StationMapViewModel(
            fuelStationByLocation = fuelStationByLocation,
            getUserDataUseCase = getUserDataUseCase,
            getLocationPlaceUseCase = getLocationPlaceUseCase,
            getCurrentLocationUseCase = getCurrentLocationUseCase,
            getFiltersUseCase = getFiltersUseCase,
            saveFilterUseCase = saveFilterUseCase,
            getRouteUseCase = getRouteUseCase,
            getFuelStationsInRouteUseCase = getFuelStationsInRouteUseCase,
            defaultDispatcher = defaultDispatcher,
        )
    }

    @Nested
    @DisplayName("Section 4: Map Centering Tests (CRITICAL - Zoom Fix)")
    inner class MapCenteringTests {

        @Test
        @DisplayName("GIVEN shouldCenterMap is true WHEN OnMapCentered event is sent THEN shouldCenterMap is reset to false")
        fun `markMapAsCentered resets shouldCenterMap flag`() = runTest {
            // Given
            val testLocation = createTestLocation()
            val testStations = listOf(createTestFuelStation())
            val testUserData = createTestUserData()

            coEvery { getCurrentLocationUseCase() } returns testLocation
            coEvery { fuelStationByLocation(any(), any(), any(), any()) } returns flowOf(testStations)
            every { getUserDataUseCase() } returns flowOf(testUserData)

            viewModel = createViewModel()

            viewModel.state.test {
                // Wait for initial state
                var currentState = awaitItem()

                // Trigger station loading which sets shouldCenterMap = true
                viewModel.handleEvent(StationMapEvent.GetStationByCurrentLocation)
                advanceUntilIdle()

                // Skip to state where shouldCenterMap = true
                currentState = expectMostRecentItem()
                assertTrue(currentState.shouldCenterMap, "shouldCenterMap should be true after loading stations")

                // When: OnMapCentered event is sent
                viewModel.handleEvent(StationMapEvent.OnMapCentered)

                // Then: shouldCenterMap should be false
                currentState = awaitItem()
                assertFalse(currentState.shouldCenterMap, "shouldCenterMap should be false after OnMapCentered")

                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        @DisplayName("GIVEN userLocationToCenter has value WHEN OnUserLocationCentered event is sent THEN userLocationToCenter is reset to null (CRITICAL - Zoom Fix)")
        fun `markUserLocationCentered resets userLocationToCenter flag`() = runTest {
            // Given
            val testLocation = createTestLocation(latitude = 40.5, longitude = -3.5)
            val testRoute = createTestRoute()

            coEvery { getCurrentLocationUseCase() } returns testLocation

            viewModel = createViewModel()

            viewModel.state.test {
                // Set route in state first
                viewModel.handleEvent(
                    StationMapEvent.StartRoute(
                        originId = null,
                        destinationId = null,
                        destinationName = "Test Destination",
                    ),
                )

                // Mock route response
                val originLocation = createTestLocation()
                val destinationLocation = createTestLocation(latitude = 41.0, longitude = -3.0)
                coEvery { getCurrentLocationUseCase() } returnsMany listOf(originLocation, destinationLocation)
                coEvery { getRouteUseCase(any(), any()) } returns flowOf(testRoute)
                coEvery { getFuelStationsInRouteUseCase(any(), any()) } returns emptyList()
                every { getUserDataUseCase() } returns flowOf(createTestUserData())

                advanceUntilIdle()
                var currentState = expectMostRecentItem()

                // Now route is active, trigger location centering
                coEvery { getCurrentLocationUseCase() } returns testLocation
                viewModel.handleEvent(StationMapEvent.GetStationByCurrentLocation)
                advanceUntilIdle()

                currentState = awaitItem()
                assertNotNull(currentState.userLocationToCenter, "userLocationToCenter should be set")

                // When: OnUserLocationCentered event is sent
                viewModel.handleEvent(StationMapEvent.OnUserLocationCentered)

                // Then: userLocationToCenter should be null
                currentState = awaitItem()
                assertNull(currentState.userLocationToCenter, "userLocationToCenter should be null after OnUserLocationCentered")

                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        @DisplayName("GIVEN route is active WHEN user clicks location button THEN userLocationToCenter is updated and loading stays false (CRITICAL - Zoom Fix)")
        fun `getStationByCurrentLocation when route is active centers on location`() = runTest {
            // Given: Route is active
            val testLocation = createTestLocation(latitude = 40.5, longitude = -3.5)
            val testRoute = createTestRoute()
            val originLocation = createTestLocation()
            val destinationLocation = createTestLocation(latitude = 41.0, longitude = -3.0)

            coEvery { getCurrentLocationUseCase() } returnsMany listOf(originLocation, destinationLocation)
            coEvery { getRouteUseCase(any(), any()) } returns flowOf(testRoute)
            coEvery { getFuelStationsInRouteUseCase(any(), any()) } returns emptyList()
            every { getUserDataUseCase() } returns flowOf(createTestUserData())

            viewModel = createViewModel()

            viewModel.state.test {
                // Start a route
                viewModel.handleEvent(
                    StationMapEvent.StartRoute(
                        originId = null,
                        destinationId = null,
                        destinationName = "Test Destination",
                    ),
                )
                advanceUntilIdle()

                var currentState = expectMostRecentItem()
                assertNotNull(currentState.route, "Route should be active")

                // When: User clicks location button
                coEvery { getCurrentLocationUseCase() } returns testLocation
                viewModel.handleEvent(StationMapEvent.GetStationByCurrentLocation)
                advanceUntilIdle()

                // Then
                currentState = awaitItem()
                assertNotNull(currentState.userLocationToCenter, "userLocationToCenter should be set")
                assertEquals(40.5, currentState.userLocationToCenter?.latitude ?: 0.0, 0.01)
                assertEquals(-3.5, currentState.userLocationToCenter?.longitude ?: 0.0, 0.01)
                assertFalse(currentState.loading, "loading should remain false")
                // mapStations should not change
                assertTrue(currentState.mapStations.isEmpty(), "mapStations should not be reloaded")

                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        @DisplayName("GIVEN route is active WHEN centerMapOnLocation is called THEN shouldCenterMap is NOT changed (CRITICAL - Distinguishes two centering types)")
        fun `centerMapOnLocation does not set shouldCenterMap`() = runTest {
            // Given: Route is active
            val testLocation = createTestLocation(latitude = 40.5, longitude = -3.5)
            val testRoute = createTestRoute()
            val originLocation = createTestLocation()
            val destinationLocation = createTestLocation(latitude = 41.0, longitude = -3.0)

            coEvery { getCurrentLocationUseCase() } returnsMany listOf(originLocation, destinationLocation)
            coEvery { getRouteUseCase(any(), any()) } returns flowOf(testRoute)
            coEvery { getFuelStationsInRouteUseCase(any(), any()) } returns emptyList()
            every { getUserDataUseCase() } returns flowOf(createTestUserData())

            viewModel = createViewModel()

            viewModel.state.test {
                // Start a route
                viewModel.handleEvent(
                    StationMapEvent.StartRoute(
                        originId = null,
                        destinationId = null,
                        destinationName = "Test Destination",
                    ),
                )
                advanceUntilIdle()

                var currentState = expectMostRecentItem()
                val initialShouldCenterMap = currentState.shouldCenterMap

                // When: centerMapOnLocation is triggered via GetStationByCurrentLocation with active route
                coEvery { getCurrentLocationUseCase() } returns testLocation
                viewModel.handleEvent(StationMapEvent.GetStationByCurrentLocation)
                advanceUntilIdle()

                // Then
                currentState = awaitItem()
                assertEquals(
                    initialShouldCenterMap,
                    currentState.shouldCenterMap,
                    "shouldCenterMap should NOT change when centering on user location with active route",
                )
                assertNotNull(currentState.userLocationToCenter, "Only userLocationToCenter should be set")

                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        @DisplayName("GIVEN no route is active WHEN stations are loaded THEN shouldCenterMap is true and userLocationToCenter is null (CRITICAL - Normal behavior vs route)")
        fun `normal station loading sets shouldCenterMap not userLocationToCenter`() = runTest {
            // Given: No route active
            val testLocation = createTestLocation()
            val testStations = listOf(createTestFuelStation())
            val testUserData = createTestUserData()

            coEvery { getCurrentLocationUseCase() } returns testLocation
            coEvery { fuelStationByLocation(any(), any(), any(), any()) } returns flowOf(testStations)
            every { getUserDataUseCase() } returns flowOf(testUserData)

            viewModel = createViewModel()

            viewModel.state.test {
                // Wait for initial state
                awaitItem()

                // When: Load stations by current location (no route)
                viewModel.handleEvent(StationMapEvent.GetStationByCurrentLocation)
                advanceUntilIdle()

                // Then
                val currentState = expectMostRecentItem()
                assertTrue(currentState.shouldCenterMap, "shouldCenterMap should be true")
                assertNull(currentState.userLocationToCenter, "userLocationToCenter should remain null")
                assertTrue(currentState.mapStations.isNotEmpty(), "Stations should be loaded")

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    @DisplayName("Section 3: Route Management Tests")
    inner class RouteManagementTests {

        @Test
        @DisplayName("GIVEN route is active WHEN user cancels route THEN route is cleared and stations are reloaded")
        fun `cancelRoute clears route and reloads stations`() = runTest {
            // Given: Route is active
            val testRoute = createTestRoute()
            val originLocation = createTestLocation()
            val destinationLocation = createTestLocation(latitude = 41.0, longitude = -3.0)
            val testStations = listOf(createTestFuelStation())

            coEvery { getCurrentLocationUseCase() } returnsMany listOf(originLocation, destinationLocation)
            coEvery { getRouteUseCase(any(), any()) } returns flowOf(testRoute)
            coEvery { getFuelStationsInRouteUseCase(any(), any()) } returns emptyList()
            every { getUserDataUseCase() } returns flowOf(createTestUserData())

            viewModel = createViewModel()

            viewModel.state.test {
                // Start route
                viewModel.handleEvent(
                    StationMapEvent.StartRoute(
                        originId = null,
                        destinationId = null,
                        destinationName = "Test Destination",
                    ),
                )
                advanceUntilIdle()

                var currentState = expectMostRecentItem()
                assertNotNull(currentState.route, "Route should be active")

                // When: Cancel route
                coEvery { getCurrentLocationUseCase() } returns originLocation
                coEvery { fuelStationByLocation(any(), any(), any(), any()) } returns flowOf(testStations)
                viewModel.handleEvent(StationMapEvent.CancelRoute)
                advanceUntilIdle()

                // Then
                currentState = expectMostRecentItem()
                assertNull(currentState.route, "Route should be null")
                assertNull(currentState.routeDestinationName, "Route destination name should be null")
                assertTrue(currentState.mapStations.isNotEmpty(), "Stations should be reloaded")

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    @DisplayName("Section 5: Tab Switching Tests")
    inner class TabSwitchingTests {

        @Test
        @DisplayName("GIVEN stations are loaded WHEN tab changes to DISTANCE THEN listStations are sorted by distance")
        fun `changeTab to DISTANCE sorts by distance ascending`() = runTest {
            // Given: Stations loaded
            val testLocation = createTestLocation()
            val testStations = listOf(
                createTestFuelStation(id = 1, distance = 300f),
                createTestFuelStation(id = 2, distance = 100f),
                createTestFuelStation(id = 3, distance = 200f),
            )
            val testUserData = createTestUserData(fuelSelection = FuelType.DIESEL)

            coEvery { getCurrentLocationUseCase() } returns testLocation
            coEvery { fuelStationByLocation(any(), any(), any(), any()) } returns flowOf(testStations)
            every { getUserDataUseCase() } returns flowOf(testUserData)

            viewModel = createViewModel()

            viewModel.state.test {
                awaitItem() // Initial state

                // Load stations
                viewModel.handleEvent(StationMapEvent.GetStationByCurrentLocation)
                advanceUntilIdle()

                var currentState = expectMostRecentItem()
                assertEquals(3, currentState.mapStations.size)

                // When: Change to DISTANCE tab
                viewModel.handleEvent(StationMapEvent.ChangeTab(selected = StationSortTab.DISTANCE))
                advanceUntilIdle()

                // Then: Stations sorted by distance
                currentState = expectMostRecentItem()
                assertEquals(2, currentState.listStations[0].fuelStation.idServiceStation, "First should be id=2 (100f)")
                assertEquals(3, currentState.listStations[1].fuelStation.idServiceStation, "Second should be id=3 (200f)")
                assertEquals(1, currentState.listStations[2].fuelStation.idServiceStation, "Third should be id=1 (300f)")

                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}
