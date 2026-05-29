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
import com.gasguru.core.testing.CoroutineTest
import com.gasguru.core.testing.fakes.data.user.FakeUserDataRepository
import com.gasguru.core.testing.fakes.data.vehicle.FakeVehicleRepository
import com.gasguru.core.testing.fakes.navigation.FakeNavigationManager
import com.gasguru.feature.vehicle.ui.AddVehicleEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AddVehicleViewModelTest : CoroutineTest() {

    private lateinit var sut: AddVehicleViewModel
    private lateinit var fakeNavigationManager: FakeNavigationManager
    private lateinit var fakeVehicleRepository: FakeVehicleRepository
    private lateinit var fakeUserDataRepository: FakeUserDataRepository
    private lateinit var saveVehicleUseCase: SaveVehicleUseCase
    private lateinit var getVehicleByIdUseCase: GetVehicleByIdUseCase
    private lateinit var getUserDataUseCase: GetUserDataUseCase

    @BeforeTest
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
    fun `GIVEN a fresh ViewModel WHEN uiState is observed THEN all fields are null or empty and save is disabled`() = runTest {
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
    fun `GIVEN fresh state WHEN SelectVehicleType event is sent with CAR THEN selectedVehicleType is CAR`() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.SelectVehicleType(vehicleType = VehicleType.CAR))
            val state = awaitItem()

            assertEquals(VehicleType.CAR, state.selectedVehicleType)
        }
    }

    @Test
    fun `GIVEN fresh state WHEN UpdateVehicleName event is sent with Golf VII THEN vehicleName is Golf VII`() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.UpdateVehicleName(name = "Golf VII"))
            val state = awaitItem()

            assertEquals("Golf VII", state.vehicleName)
        }
    }

    @Test
    fun `GIVEN fresh state WHEN SelectFuelType event is sent with GASOLINE_95 THEN selectedFuelType is GASOLINE_95`() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.SelectFuelType(fuelType = FuelType.GASOLINE_95))
            val state = awaitItem()

            assertEquals(FuelType.GASOLINE_95, state.selectedFuelType)
        }
    }

    @Test
    fun `GIVEN no capacity is selected WHEN OpenCapacityPicker event is sent THEN picker is shown with the minimum picker value`() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.OpenCapacityPicker)
            val state = awaitItem()

            assertTrue(state.showCapacityPicker)
            assertEquals(AddVehicleUiState.PICKER_MIN, state.pickerValue)
        }
    }

    @Test
    fun `GIVEN capacity 60 was previously confirmed WHEN OpenCapacityPicker event is sent THEN picker is shown with 60 as the initial value`() = runTest {
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
    fun `GIVEN picker is open WHEN CloseCapacityPicker event is sent THEN showCapacityPicker becomes false`() = runTest {
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
    fun `GIVEN picker is open WHEN ConfirmCapacityValue event is sent with 75 THEN selectedCapacity is 75 and picker is closed`() = runTest {
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
    fun `GIVEN isMainVehicle is false WHEN ToggleMainVehicle event is sent THEN isMainVehicle becomes true`() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.ToggleMainVehicle)
            val state = awaitItem()

            assertTrue(state.isMainVehicle)
        }
    }

    @Test
    fun `GIVEN isMainVehicle was toggled to true WHEN ToggleMainVehicle event is sent again THEN isMainVehicle reverts to false`() = runTest {
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
    fun `GIVEN neither fuel type nor capacity is selected WHEN uiState is observed THEN isSaveEnabled is false`() = runTest {
        sut.uiState.test {
            val state = awaitItem()
            assertFalse(state.isSaveEnabled)
        }
    }

    @Test
    fun `GIVEN only fuel type is selected and capacity is missing WHEN uiState is observed THEN isSaveEnabled is false`() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.SelectFuelType(fuelType = FuelType.GASOLINE_95))
            val state = awaitItem()

            assertFalse(state.isSaveEnabled)
        }
    }

    @Test
    fun `GIVEN only capacity is selected and fuel type is missing WHEN uiState is observed THEN isSaveEnabled is false`() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = AddVehicleEvent.ConfirmCapacityValue(value = 50))
            val state = awaitItem()

            assertFalse(state.isSaveEnabled)
        }
    }

    @Test
    fun `GIVEN both fuel type and capacity are selected WHEN uiState is observed THEN isSaveEnabled is true`() = runTest {
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
    fun `GIVEN all vehicle fields are filled WHEN SaveVehicle event is sent THEN vehicle is persisted with correct user id and navigation goes back`() = runTest {
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
    fun `GIVEN vehicle name is left blank WHEN SaveVehicle event is sent THEN vehicle is saved with null name`() = runTest {
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
    fun `GIVEN vehicle type is not selected WHEN SaveVehicle event is sent THEN vehicle is saved with CAR as the default vehicle type`() = runTest {
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
    fun `GIVEN the add vehicle screen is open WHEN Back event is sent THEN navigation manager navigates back`() = runTest {
        sut.handleEvent(event = AddVehicleEvent.Back)

        assertTrue(fakeNavigationManager.navigateBackCalled)
    }

    @Test
    fun `GIVEN an existing vehicle in repository WHEN onLoadVehicle is called with its id THEN state is pre-filled with vehicle data and edit mode is active`() = runTest {
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
    fun `GIVEN a principal vehicle in repository WHEN onLoadVehicle is called with its id THEN isMainVehicle and isOriginallyPrincipal are both true`() = runTest {
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
    fun `GIVEN an originally principal vehicle is loaded WHEN ToggleMainVehicle event is sent THEN the toggle is ignored and isMainVehicle remains true`() = runTest {
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
    fun `GIVEN a vehicle is loaded in edit mode WHEN vehicle name is updated and SaveVehicle event is sent THEN existing vehicle is updated in repository`() = runTest {
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
    fun `GIVEN an existing principal vehicle WHEN a new vehicle is saved as principal THEN only one vehicle remains principal in the repository`() = runTest {
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
