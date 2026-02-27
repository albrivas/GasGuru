package com.gasguru.feature.onboarding_welcome.viewmodel

import app.cash.turbine.test
import com.gasguru.core.domain.vehicle.SaveDefaultVehicleCapacityUseCase
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.UserData
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.testing.CoroutinesTestExtension
import com.gasguru.core.testing.fakes.data.user.FakeUserDataRepository
import com.gasguru.core.testing.fakes.data.vehicle.FakeVehicleRepository
import com.gasguru.core.testing.fakes.navigation.FakeNavigationManager
import com.gasguru.feature.onboarding_welcome.ui.CapacityTankEvent
import com.gasguru.navigation.manager.NavigationDestination
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
class CapacityTankViewModelTest {

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
    )

    @BeforeEach
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
        )
    }

    @Test
    @DisplayName("GIVEN initial state WHEN collecting uiState THEN selectedCapacity is null, showPicker is false and isContinueEnabled is false")
    fun initialStateIsEmpty() = runTest {
        sut.uiState.test {
            val state = awaitItem()
            assertNull(state.selectedCapacity)
            assertFalse(state.showPicker)
            assertFalse(state.isContinueEnabled)
        }
    }

    @Test
    @DisplayName("GIVEN initial state WHEN collecting uiState THEN commonValues contains expected items")
    fun initialStateHasExpectedCommonValues() = runTest {
        sut.uiState.test {
            val state = awaitItem()
            assertEquals(listOf(40, 45, 50, 55, 60, 70), state.commonValues)
        }
    }

    @Test
    @DisplayName("GIVEN initial state WHEN SelectCommonValue event THEN selectedCapacity is updated and isContinueEnabled is true")
    fun selectCommonValueUpdatesSelectedCapacity() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = CapacityTankEvent.SelectCommonValue(value = 50))
            val state = awaitItem()

            assertEquals(50, state.selectedCapacity)
            assertTrue(state.isContinueEnabled)
        }
    }

    @Test
    @DisplayName("GIVEN no capacity selected WHEN OpenPicker event THEN showPicker is true and pickerValue is PICKER_MIN")
    fun openPickerWithNoSelectionSetsPickerValueToMin() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = CapacityTankEvent.OpenPicker)
            val state = awaitItem()

            assertTrue(state.showPicker)
            assertEquals(CapacityTankUiState.PICKER_MIN, state.pickerValue)
        }
    }

    @Test
    @DisplayName("GIVEN capacity selected WHEN OpenPicker event THEN showPicker is true and pickerValue matches selected capacity")
    fun openPickerWithSelectionSetsPickerValueToSelectedCapacity() = runTest {
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
    @DisplayName("GIVEN picker open WHEN ClosePicker event THEN showPicker is false")
    fun closePickerHidesPicker() = runTest {
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
    @DisplayName("GIVEN picker open WHEN ConfirmPickerValue event THEN selectedCapacity is updated and showPicker is false")
    fun confirmPickerValueUpdatesCapacityAndClosesPicker() = runTest {
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
    @DisplayName("GIVEN capacity selected WHEN Continue event THEN save use case is called with correct capacity and navigates to Home")
    fun continueWithSelectedCapacitySavesAndNavigatesToHome() = runTest {
        sut.handleEvent(event = CapacityTankEvent.SelectCommonValue(value = 55))

        sut.handleEvent(event = CapacityTankEvent.Continue)

        assertEquals(listOf(defaultVehicle.id to 55), fakeVehicleRepository.updatedTankCapacities)
        assertEquals(1, fakeNavigationManager.navigatedDestinations.size)
        assertEquals(NavigationDestination.Home, fakeNavigationManager.navigatedDestinations.first())
    }

    @Test
    @DisplayName("GIVEN no capacity selected WHEN Continue event THEN does not save or navigate")
    fun continueWithNoSelectionDoesNotSaveOrNavigate() = runTest {
        sut.handleEvent(event = CapacityTankEvent.Continue)

        assertTrue(fakeVehicleRepository.updatedTankCapacities.isEmpty())
        assertTrue(fakeNavigationManager.navigatedDestinations.isEmpty())
    }
}
