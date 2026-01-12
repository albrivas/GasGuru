package com.gasguru.feature.station_map.ui

import com.gasguru.core.domain.filters.GetFiltersUseCase
import com.gasguru.core.domain.filters.SaveFilterUseCase
import com.gasguru.core.domain.fuelstation.FuelStationByLocationUseCase
import com.gasguru.core.domain.fuelstation.GetFuelStationsInRouteUseCase
import com.gasguru.core.domain.location.GetCurrentLocationUseCase
import com.gasguru.core.domain.places.GetLocationPlaceUseCase
import com.gasguru.core.domain.route.GetRouteUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.testing.CoroutinesTestExtension
import io.mockk.coEvery
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.jupiter.api.BeforeEach
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
        defaultDispatcher = UnconfinedTestDispatcher()

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
}
