package com.gasguru.feature.vehicle.viewmodel

import app.cash.turbine.test
import com.gasguru.core.model.data.VehicleType
import com.gasguru.core.testing.CoroutinesTestExtension
import com.gasguru.core.testing.fakes.navigation.FakeNavigationManager
import com.gasguru.core.ui.models.FuelTypeUiModel
import com.gasguru.feature.vehicle.ui.AddVehicleEvent
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
class AddVehicleViewModelTest {

    private lateinit var sut: AddVehicleViewModel
    private lateinit var fakeNavigationManager: FakeNavigationManager

    @BeforeEach
    fun setUp() {
        fakeNavigationManager = FakeNavigationManager()
        sut = AddVehicleViewModel(navigationManager = fakeNavigationManager)
    }

    @Test
    @DisplayName(
        "GIVEN initial state WHEN collecting uiState THEN all fields have default empty values and isSaveEnabled is false"
    )
    fun initialStateHasEmptyDefaults() = runTest {
        sut.uiState.test {
            val state = awaitItem()
            assertNull(state.selectedVehicleType)
            assertEquals("", state.vehicleName)
            assertNull(state.selectedFuelTypeNameRes)
            assertNull(state.selectedCapacity)
            assertFalse(state.isMainVehicle)
            assertFalse(state.showCapacityPicker)
            assertFalse(state.isSaveEnabled)
        }
    }

    @Test
    @DisplayName(
        "GIVEN initial state WHEN SelectVehicleType event THEN selectedVehicleType is updated"
    )
    fun selectVehicleTypeUpdatesState() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.SelectVehicleType(vehicleType = VehicleType.CAR))
            val state = awaitItem()

            assertEquals(VehicleType.CAR, state.selectedVehicleType)
        }
    }

    @Test
    @DisplayName(
        "GIVEN initial state WHEN UpdateVehicleName event THEN vehicleName is updated"
    )
    fun updateVehicleNameUpdatesState() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.UpdateVehicleName(name = "Golf VII"))
            val state = awaitItem()

            assertEquals("Golf VII", state.vehicleName)
        }
    }

    @Test
    @DisplayName(
        "GIVEN initial state WHEN SelectFuelType event THEN selectedFuelTypeNameRes is updated"
    )
    fun selectFuelTypeUpdatesState() = runTest {
        val fuelNameRes = FuelTypeUiModel.ALL_FUELS.first().translationRes
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.SelectFuelType(fuelTypeNameRes = fuelNameRes))
            val state = awaitItem()

            assertEquals(fuelNameRes, state.selectedFuelTypeNameRes)
        }
    }

    @Test
    @DisplayName(
        "GIVEN no capacity selected WHEN OpenCapacityPicker event THEN showCapacityPicker is true and pickerValue is PICKER_MIN"
    )
    fun openPickerWithNoSelectionUsesMin() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.OpenCapacityPicker)
            val state = awaitItem()

            assertTrue(state.showCapacityPicker)
            assertEquals(AddVehicleUiState.PICKER_MIN, state.pickerValue)
        }
    }

    @Test
    @DisplayName(
        "GIVEN capacity already selected WHEN OpenCapacityPicker event THEN pickerValue matches selected capacity"
    )
    fun openPickerWithSelectionUsesSelectedValue() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.ConfirmCapacityValue(value = 60))
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.OpenCapacityPicker)
            val state = awaitItem()

            assertTrue(state.showCapacityPicker)
            assertEquals(60, state.pickerValue)
        }
    }

    @Test
    @DisplayName(
        "GIVEN picker open WHEN CloseCapacityPicker event THEN showCapacityPicker is false"
    )
    fun closePickerHidesPicker() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.OpenCapacityPicker)
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.CloseCapacityPicker)
            val state = awaitItem()

            assertFalse(state.showCapacityPicker)
        }
    }

    @Test
    @DisplayName(
        "GIVEN picker open WHEN ConfirmCapacityValue event THEN selectedCapacity is updated and picker is closed"
    )
    fun confirmCapacityValueUpdatesCapacityAndClosesPicker() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.OpenCapacityPicker)
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.ConfirmCapacityValue(value = 75))
            val state = awaitItem()

            assertEquals(75, state.selectedCapacity)
            assertFalse(state.showCapacityPicker)
        }
    }

    @Test
    @DisplayName(
        "GIVEN isMainVehicle is false WHEN ToggleMainVehicle event THEN isMainVehicle becomes true"
    )
    fun toggleMainVehicleFlipsFromFalseToTrue() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.ToggleMainVehicle)
            val state = awaitItem()

            assertTrue(state.isMainVehicle)
        }
    }

    @Test
    @DisplayName(
        "GIVEN isMainVehicle is true WHEN ToggleMainVehicle event THEN isMainVehicle becomes false"
    )
    fun toggleMainVehicleFlipsFromTrueToFalse() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.ToggleMainVehicle)
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.ToggleMainVehicle)
            val state = awaitItem()

            assertFalse(state.isMainVehicle)
        }
    }

    @Test
    @DisplayName(
        "GIVEN no fuelType and no capacity THEN isSaveEnabled is false"
    )
    fun isSaveEnabledFalseWhenBothMissing() = runTest {
        sut.uiState.test {
            val state = awaitItem()
            assertFalse(state.isSaveEnabled)
        }
    }

    @Test
    @DisplayName(
        "GIVEN fuelType selected but no capacity THEN isSaveEnabled is false"
    )
    fun isSaveEnabledFalseWhenOnlyFuelSelected() = runTest {
        val fuelNameRes = FuelTypeUiModel.ALL_FUELS.first().translationRes
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.SelectFuelType(fuelTypeNameRes = fuelNameRes))
            val state = awaitItem()

            assertFalse(state.isSaveEnabled)
        }
    }

    @Test
    @DisplayName(
        "GIVEN capacity selected but no fuelType THEN isSaveEnabled is false"
    )
    fun isSaveEnabledFalseWhenOnlyCapacitySelected() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.ConfirmCapacityValue(value = 50))
            val state = awaitItem()

            assertFalse(state.isSaveEnabled)
        }
    }

    @Test
    @DisplayName(
        "GIVEN fuelType and capacity both selected THEN isSaveEnabled is true"
    )
    fun isSaveEnabledTrueWhenBothSet() = runTest {
        val fuelNameRes = FuelTypeUiModel.ALL_FUELS.first().translationRes
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.SelectFuelType(fuelTypeNameRes = fuelNameRes))
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.ConfirmCapacityValue(value = 50))
            val state = awaitItem()

            assertTrue(state.isSaveEnabled)
        }
    }

    @Test
    @DisplayName(
        "GIVEN any state WHEN SaveVehicle event THEN no state change occurs and no crash"
    )
    fun saveVehicleIsNoOp() = runTest {
        sut.uiState.test {
            val initialState = awaitItem()

            sut.handleEvent(event = AddVehicleEvent.SaveVehicle)

            expectNoEvents()
            assertEquals(initialState, sut.uiState.value)
        }
    }

    @Test
    @DisplayName(
        "GIVEN any state WHEN Back event THEN navigationManager.navigateBack is called"
    )
    fun backEventCallsNavigateBack() = runTest {
        sut.handleEvent(event = AddVehicleEvent.Back)

        assertTrue(fakeNavigationManager.navigateBackCalled)
    }
}
