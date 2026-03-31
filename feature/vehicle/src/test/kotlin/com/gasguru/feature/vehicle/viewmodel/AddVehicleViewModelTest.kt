package com.gasguru.feature.vehicle.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.gasguru.core.analytics.NoOpAnalyticsHelper
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.domain.vehicle.GetVehicleByIdUseCase
import com.gasguru.core.domain.vehicle.SaveVehicleUseCase
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.UserData
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.VehicleType
import com.gasguru.core.testing.CoroutinesTestExtension
import com.gasguru.core.testing.fakes.data.user.FakeUserDataRepository
import com.gasguru.core.testing.fakes.data.vehicle.FakeVehicleRepository
import com.gasguru.core.testing.fakes.navigation.FakeNavigationManager
import com.gasguru.feature.vehicle.ui.AddVehicleEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
class AddVehicleViewModelTest {

    private lateinit var sut: AddVehicleViewModel
    private lateinit var fakeNavigationManager: FakeNavigationManager
    private lateinit var fakeVehicleRepository: FakeVehicleRepository
    private lateinit var fakeUserDataRepository: FakeUserDataRepository
    private lateinit var saveVehicleUseCase: SaveVehicleUseCase
    private lateinit var getVehicleByIdUseCase: GetVehicleByIdUseCase
    private lateinit var getUserDataUseCase: GetUserDataUseCase

    @BeforeEach
    fun setUp() {
        fakeNavigationManager = FakeNavigationManager()
        fakeVehicleRepository = FakeVehicleRepository()
        fakeUserDataRepository = FakeUserDataRepository(initialUserData = UserData(userId = 0L))
        saveVehicleUseCase = SaveVehicleUseCase(vehicleRepository = fakeVehicleRepository)
        getVehicleByIdUseCase = GetVehicleByIdUseCase(vehicleRepository = fakeVehicleRepository)
        getUserDataUseCase = GetUserDataUseCase(userDataRepository = fakeUserDataRepository)
        sut = buildViewModel()
    }

    private fun buildViewModel(): AddVehicleViewModel = AddVehicleViewModel(
        savedStateHandle = SavedStateHandle(),
        navigationManager = fakeNavigationManager,
        saveVehicleUseCase = saveVehicleUseCase,
        getVehicleByIdUseCase = getVehicleByIdUseCase,
        getUserDataUseCase = getUserDataUseCase,
        analyticsHelper = NoOpAnalyticsHelper(),
    )

    @Test
    @DisplayName(
        """
        GIVEN initial state
        WHEN collecting uiState
        THEN all fields have default empty values and isSaveEnabled is false
        """
    )
    fun initialStateHasEmptyDefaults() = runTest {
        sut.uiState.test {
            val state = awaitItem()
            assertNull(state.selectedVehicleType)
            assertEquals("", state.vehicleName)
            assertNull(state.selectedFuelType)
            assertNull(state.selectedCapacity)
            assertFalse(state.isMainVehicle)
            assertFalse(state.showCapacityPicker)
            assertFalse(state.isSaveEnabled)
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN initial state
        WHEN SelectVehicleType event
        THEN selectedVehicleType is updated
        """
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
        """
        GIVEN initial state
        WHEN UpdateVehicleName event
        THEN vehicleName is updated
        """
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
        """
        GIVEN initial state
        WHEN SelectFuelType event
        THEN selectedFuelType is updated
        """
    )
    fun selectFuelTypeUpdatesState() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.SelectFuelType(fuelType = FuelType.GASOLINE_95))
            val state = awaitItem()

            assertEquals(FuelType.GASOLINE_95, state.selectedFuelType)
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN no capacity selected
        WHEN OpenCapacityPicker event
        THEN showCapacityPicker is true and pickerValue is PICKER_MIN
        """
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
        """
        GIVEN capacity already selected
        WHEN OpenCapacityPicker event
        THEN pickerValue matches selected capacity
        """
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
        """
        GIVEN picker open
        WHEN CloseCapacityPicker event
        THEN showCapacityPicker is false
        """
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
        """
        GIVEN picker open
        WHEN ConfirmCapacityValue event
        THEN selectedCapacity is updated and picker is closed
        """
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
        """
        GIVEN isMainVehicle is false
        WHEN ToggleMainVehicle event
        THEN isMainVehicle becomes true
        """
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
        """
        GIVEN isMainVehicle is true
        WHEN ToggleMainVehicle event
        THEN isMainVehicle becomes false
        """
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
        """
        GIVEN no fuelType and no capacity
        THEN isSaveEnabled is false
        """
    )
    fun isSaveEnabledFalseWhenBothMissing() = runTest {
        sut.uiState.test {
            val state = awaitItem()
            assertFalse(state.isSaveEnabled)
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN fuelType selected but no capacity
        THEN isSaveEnabled is false
        """
    )
    fun isSaveEnabledFalseWhenOnlyFuelSelected() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.SelectFuelType(fuelType = FuelType.GASOLINE_95))
            val state = awaitItem()

            assertFalse(state.isSaveEnabled)
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN capacity selected but no fuelType
        THEN isSaveEnabled is false
        """
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
        """
        GIVEN fuelType and capacity both selected
        THEN isSaveEnabled is true
        """
    )
    fun isSaveEnabledTrueWhenBothSet() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.SelectFuelType(fuelType = FuelType.GASOLINE_95))
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.ConfirmCapacityValue(value = 50))
            val state = awaitItem()

            assertTrue(state.isSaveEnabled)
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN fuelType and capacity set
        WHEN SaveVehicle event
        THEN vehicle is saved with correct userId and navigation goes back
        """
    )
    fun saveVehiclePersistsVehicleWithCorrectUserIdAndNavigatesBack() = runTest {
        sut.handleEvent(event = AddVehicleEvent.SelectVehicleType(vehicleType = VehicleType.CAR))
        sut.handleEvent(event = AddVehicleEvent.UpdateVehicleName(name = "Golf VII"))
        sut.handleEvent(event = AddVehicleEvent.SelectFuelType(fuelType = FuelType.GASOLINE_95))
        sut.handleEvent(event = AddVehicleEvent.ConfirmCapacityValue(value = 55))
        sut.handleEvent(event = AddVehicleEvent.ToggleMainVehicle)

        sut.handleEvent(event = AddVehicleEvent.SaveVehicle)
        advanceUntilIdle()

        val savedVehicles = fakeVehicleRepository.getVehiclesForUser(userId = 0L)
        savedVehicles.test {
            val vehicles = awaitItem()
            assertEquals(1, vehicles.size)
            val savedVehicle = vehicles.first()
            assertEquals(0L, savedVehicle.userId)
            assertEquals("Golf VII", savedVehicle.name)
            assertEquals(FuelType.GASOLINE_95, savedVehicle.fuelType)
            assertEquals(55, savedVehicle.tankCapacity)
            assertEquals(VehicleType.CAR, savedVehicle.vehicleType)
            assertTrue(savedVehicle.isPrincipal)
        }
        assertTrue(fakeNavigationManager.navigateBackCalled)
    }

    @Test
    @DisplayName(
        """
        GIVEN fuelType and capacity set but no name
        WHEN SaveVehicle event
        THEN vehicle is saved with null name
        """
    )
    fun saveVehicleWithBlankNameStoresNullName() = runTest {
        sut.handleEvent(event = AddVehicleEvent.SelectFuelType(fuelType = FuelType.DIESEL))
        sut.handleEvent(event = AddVehicleEvent.ConfirmCapacityValue(value = 40))

        sut.handleEvent(event = AddVehicleEvent.SaveVehicle)
        advanceUntilIdle()

        val savedVehicles = fakeVehicleRepository.getVehiclesForUser(userId = 0L)
        savedVehicles.test {
            val vehicles = awaitItem()
            assertNull(vehicles.first().name)
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN no vehicleType selected
        WHEN SaveVehicle event
        THEN vehicle defaults to CAR type
        """
    )
    fun saveVehicleWithNoVehicleTypeDefaultsToCar() = runTest {
        sut.handleEvent(event = AddVehicleEvent.SelectFuelType(fuelType = FuelType.GASOLINE_95))
        sut.handleEvent(event = AddVehicleEvent.ConfirmCapacityValue(value = 50))

        sut.handleEvent(event = AddVehicleEvent.SaveVehicle)
        advanceUntilIdle()

        val savedVehicles = fakeVehicleRepository.getVehiclesForUser(userId = 0L)
        savedVehicles.test {
            val vehicles = awaitItem()
            assertEquals(VehicleType.CAR, vehicles.first().vehicleType)
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN any state
        WHEN Back event
        THEN navigationManager.navigateBack is called
        """
    )
    fun backEventCallsNavigateBack() = runTest {
        sut.handleEvent(event = AddVehicleEvent.Back)

        assertTrue(fakeNavigationManager.navigateBackCalled)
    }

    @Test
    @DisplayName(
        """
        GIVEN a vehicle exists in the repository
        WHEN the vehicle is loaded
        THEN state is pre-filled with vehicle data and isEditMode is true
        """
    )
    fun loadVehiclePreFillsStateAndSetsEditMode() = runTest {
        val existingVehicle = Vehicle(
            id = 5L,
            userId = 0L,
            name = "Golf VII",
            fuelType = FuelType.GASOLINE_95,
            tankCapacity = 55,
            vehicleType = VehicleType.CAR,
            isPrincipal = false,
        )
        fakeVehicleRepository.upsertVehicle(vehicle = existingVehicle)
        sut.onLoadVehicle(vehicleId = 5L)
        advanceUntilIdle()

        val state = sut.uiState.value
        assertTrue(state.isEditMode)
        assertEquals(5L, state.vehicleId)
        assertEquals("Golf VII", state.vehicleName)
        assertEquals(FuelType.GASOLINE_95, state.selectedFuelType)
        assertEquals(55, state.selectedCapacity)
        assertEquals(VehicleType.CAR, state.selectedVehicleType)
        assertFalse(state.isMainVehicle)
        assertFalse(state.isOriginallyPrincipal)
    }

    @Test
    @DisplayName(
        """
        GIVEN a principal vehicle exists in the repository
        WHEN the vehicle is loaded
        THEN isOriginallyPrincipal is true and isMainVehicle is true
        """
    )
    fun loadPrincipalVehicleSetsIsOriginallyPrincipalTrue() = runTest {
        val principalVehicle = Vehicle(
            id = 10L,
            userId = 0L,
            name = "Golf VII",
            fuelType = FuelType.GASOLINE_95,
            tankCapacity = 55,
            vehicleType = VehicleType.CAR,
            isPrincipal = true,
        )
        fakeVehicleRepository.upsertVehicle(vehicle = principalVehicle)
        sut.onLoadVehicle(vehicleId = 10L)
        advanceUntilIdle()

        val state = sut.uiState.value
        assertTrue(state.isMainVehicle)
        assertTrue(state.isOriginallyPrincipal)
    }

    @Test
    @DisplayName(
        """
        GIVEN a principal vehicle loaded in edit mode
        WHEN ToggleMainVehicle event is sent
        THEN isMainVehicle remains true
        """
    )
    fun toggleMainVehicleIsIgnoredForOriginallyPrincipalVehicle() = runTest {
        val principalVehicle = Vehicle(
            id = 10L,
            userId = 0L,
            name = "Golf VII",
            fuelType = FuelType.GASOLINE_95,
            tankCapacity = 55,
            vehicleType = VehicleType.CAR,
            isPrincipal = true,
        )
        fakeVehicleRepository.upsertVehicle(vehicle = principalVehicle)
        sut.onLoadVehicle(vehicleId = 10L)
        advanceUntilIdle()

        sut.uiState.test {
            awaitItem() // current state after load

            sut.handleEvent(event = AddVehicleEvent.ToggleMainVehicle)
            expectNoEvents()

            assertTrue(sut.uiState.value.isMainVehicle)
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN a vehicle is loaded in edit mode
        WHEN SaveVehicle event
        THEN vehicle is updated with same id
        """
    )
    fun saveVehicleInEditModeUpdatesExistingVehicle() = runTest {
        val existingVehicle = Vehicle(
            id = 5L,
            userId = 0L,
            name = "Golf VII",
            fuelType = FuelType.GASOLINE_95,
            tankCapacity = 55,
            vehicleType = VehicleType.CAR,
            isPrincipal = false,
        )
        fakeVehicleRepository.upsertVehicle(vehicle = existingVehicle)
        sut.onLoadVehicle(vehicleId = 5L)
        advanceUntilIdle()

        sut.handleEvent(event = AddVehicleEvent.UpdateVehicleName(name = "Golf VIII"))
        sut.handleEvent(event = AddVehicleEvent.SaveVehicle)
        advanceUntilIdle()

        val vehicles = fakeVehicleRepository.getVehiclesForUser(userId = 0L)
        vehicles.test {
            val vehicleList = awaitItem()
            val updatedVehicle = vehicleList.first { it.id == 5L }
            assertEquals("Golf VIII", updatedVehicle.name)
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN existing principal vehicle
        WHEN saving new vehicle with isPrincipal=true
        THEN only one vehicle is principal
        """
    )
    fun savingPrincipalVehicleClearsOtherPrincipals() = runTest {
        val existingPrincipal = Vehicle(
            id = 1L,
            userId = 0L,
            name = "Golf VII",
            fuelType = FuelType.GASOLINE_95,
            tankCapacity = 55,
            vehicleType = VehicleType.CAR,
            isPrincipal = true,
        )
        fakeVehicleRepository.upsertVehicle(vehicle = existingPrincipal)

        sut.handleEvent(event = AddVehicleEvent.SelectFuelType(fuelType = FuelType.DIESEL))
        sut.handleEvent(event = AddVehicleEvent.ConfirmCapacityValue(value = 40))
        sut.handleEvent(event = AddVehicleEvent.ToggleMainVehicle) // set as principal
        sut.handleEvent(event = AddVehicleEvent.SaveVehicle)
        advanceUntilIdle()

        val vehicles = fakeVehicleRepository.getVehiclesForUser(userId = 0L)
        vehicles.test {
            val vehicleList = awaitItem()
            assertEquals(1, vehicleList.count { it.isPrincipal })
        }
    }
}
