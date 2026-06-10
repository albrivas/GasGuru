package com.gasguru.feature.onboarding_welcome.viewmodel

import app.cash.turbine.test
import com.gasguru.core.analytics.NoOpAnalyticsHelper
import com.gasguru.core.domain.vehicle.SaveDefaultVehicleCapacityUseCase
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.UserData
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.VehicleType
import com.gasguru.core.testing.CoroutineTest
import com.gasguru.core.testing.fakes.data.user.FakeUserDataRepository
import com.gasguru.core.testing.fakes.data.vehicle.FakeVehicleRepository
import com.gasguru.core.testing.fakes.navigation.FakeNavigationManager
import com.gasguru.feature.onboarding_welcome.ui.CapacityTankEvent
import com.gasguru.navigation.manager.NavigationDestination
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CapacityTankViewModelTest : CoroutineTest() {

    private lateinit var sut: CapacityTankViewModel
    private lateinit var fakeNavigationManager: FakeNavigationManager
    private lateinit var fakeUserDataRepository: FakeUserDataRepository
    private lateinit var fakeVehicleRepository: FakeVehicleRepository

    private val defaultVehicle = Vehicle(
        id = 1L,
        userId = 0L,
        name = null,
        fuelType = FuelType.GASOLINE_95,
        tankCapacity = 50,
        vehicleType = VehicleType.CAR,
        isPrincipal = true,
    )

    @BeforeTest
    fun setUp() {
        fakeNavigationManager = FakeNavigationManager()
        fakeUserDataRepository = FakeUserDataRepository(
            initialUserData = UserData(vehicles = listOf(defaultVehicle)),
        )
        fakeVehicleRepository = FakeVehicleRepository(
            initialVehicles = listOf(defaultVehicle),
        )
        val saveDefaultVehicleCapacityUseCase = SaveDefaultVehicleCapacityUseCase(
            vehicleRepository = fakeVehicleRepository,
            userDataRepository = fakeUserDataRepository,
        )
        sut = CapacityTankViewModel(
            navigationManager = fakeNavigationManager,
            saveDefaultVehicleCapacityUseCase = saveDefaultVehicleCapacityUseCase,
            analyticsHelper = NoOpAnalyticsHelper(),
        )
    }

    @Test
    fun `GIVEN a fresh ViewModel WHEN uiState is observed THEN selectedCapacity is null showPicker is false and isContinueEnabled is false`() = runTest {
        sut.uiState.test {
            val state = awaitItem()
            assertNull(state.selectedCapacity)
            assertFalse(state.showPicker)
            assertFalse(state.isContinueEnabled)
        }
    }

    @Test
    fun `GIVEN a fresh ViewModel WHEN uiState is observed THEN commonValues contains the expected preset capacities`() = runTest {
        sut.uiState.test {
            val state = awaitItem()
            assertEquals(listOf(40, 45, 50, 55, 60, 70), state.commonValues)
        }
    }

    @Test
    fun `GIVEN fresh state WHEN SelectCommonValue event is sent with 50 THEN selectedCapacity is 50 and isContinueEnabled is true`() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = CapacityTankEvent.SelectCommonValue(value = 50))
            val state = awaitItem()

            assertEquals(50, state.selectedCapacity)
            assertTrue(state.isContinueEnabled)
        }
    }

    @Test
    fun `GIVEN no capacity is selected WHEN OpenPicker event is sent THEN picker is shown with the minimum value`() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = CapacityTankEvent.OpenPicker)
            val state = awaitItem()

            assertTrue(state.showPicker)
            assertEquals(CapacityTankUiState.PICKER_MIN, state.pickerValue)
        }
    }

    @Test
    fun `GIVEN capacity 60 is already selected WHEN OpenPicker event is sent THEN picker is shown with 60 as the initial value`() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = CapacityTankEvent.SelectCommonValue(value = 60))
            awaitItem()

            sut.handleEvent(event = CapacityTankEvent.OpenPicker)
            val state = awaitItem()

            assertTrue(state.showPicker)
            assertEquals(60, state.pickerValue)
        }
    }

    @Test
    fun `GIVEN picker is open WHEN ClosePicker event is sent THEN showPicker becomes false`() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = CapacityTankEvent.OpenPicker)
            awaitItem()

            sut.handleEvent(event = CapacityTankEvent.ClosePicker)
            val state = awaitItem()

            assertFalse(state.showPicker)
        }
    }

    @Test
    fun `GIVEN picker is open WHEN ConfirmPickerValue event is sent with 75 THEN selectedCapacity is 75 and picker is closed`() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = CapacityTankEvent.OpenPicker)
            awaitItem()

            sut.handleEvent(event = CapacityTankEvent.ConfirmPickerValue(value = 75))
            val state = awaitItem()

            assertEquals(75, state.selectedCapacity)
            assertFalse(state.showPicker)
        }
    }

    @Test
    fun `GIVEN a capacity of 55 is selected WHEN Continue event is sent THEN vehicle capacity is saved and navigation goes to Home`() = runTest {
        sut.handleEvent(event = CapacityTankEvent.SelectCommonValue(value = 55))

        sut.handleEvent(event = CapacityTankEvent.Continue)
        advanceUntilIdle()

        assertEquals(listOf(defaultVehicle.id to 55), fakeVehicleRepository.updatedTankCapacities)
        assertEquals(1, fakeNavigationManager.navigatedDestinations.size)
        assertEquals(NavigationDestination.Home, fakeNavigationManager.navigatedDestinations.first())
    }

    @Test
    fun `GIVEN no capacity is selected WHEN Continue event is sent THEN nothing is saved and no navigation occurs`() = runTest {
        sut.handleEvent(event = CapacityTankEvent.Continue)

        assertTrue(fakeVehicleRepository.updatedTankCapacities.isEmpty())
        assertTrue(fakeNavigationManager.navigatedDestinations.isEmpty())
    }
}
