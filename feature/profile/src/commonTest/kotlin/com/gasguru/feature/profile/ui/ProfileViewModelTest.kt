package com.gasguru.feature.profile.ui

import app.cash.turbine.test
import com.gasguru.core.analytics.NoOpAnalyticsHelper
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.domain.user.SaveThemeModeUseCase
import com.gasguru.core.domain.vehicle.DeleteVehicleUseCase
import com.gasguru.core.domain.vehicle.GetVehicleByIdUseCase
import com.gasguru.core.domain.vehicle.SaveVehicleUseCase
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.model.data.UserData
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.VehicleType
import com.gasguru.core.testing.CoroutineTest
import com.gasguru.core.testing.fakes.data.user.FakeUserDataRepository
import com.gasguru.core.testing.fakes.data.vehicle.FakeVehicleRepository
import com.gasguru.core.testing.fakes.navigation.FakeNavigationManager
import com.gasguru.core.ui.mapper.toUi
import com.gasguru.navigation.manager.NavigationDestination
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProfileViewModelTest : CoroutineTest() {

    private lateinit var fakeUserDataRepository: FakeUserDataRepository
    private lateinit var fakeVehicleRepository: FakeVehicleRepository
    private lateinit var fakeNavigationManager: FakeNavigationManager

    private lateinit var sut: ProfileViewModel

    private val defaultVehicles = listOf(
        Vehicle(
            id = 1L,
            userId = 0L,
            fuelType = FuelType.GASOLINE_95,
            name = "Golf VIII",
            tankCapacity = 55,
            vehicleType = VehicleType.CAR,
            isPrincipal = true
        ),
        Vehicle(
            id = 2L,
            userId = 0L,
            fuelType = FuelType.GASOLINE_95,
            name = "Honda CB500",
            tankCapacity = 18,
            vehicleType = VehicleType.MOTORCYCLE,
            isPrincipal = false
        ),
    )

    @BeforeTest
    fun setUp() {
        fakeUserDataRepository = FakeUserDataRepository(
            initialUserData = UserData(vehicles = defaultVehicles)
        )
        fakeVehicleRepository = FakeVehicleRepository(initialVehicles = defaultVehicles)
        fakeNavigationManager = FakeNavigationManager()
        sut = ProfileViewModel(
            getUserData = GetUserDataUseCase(fakeUserDataRepository),
            saveThemeModeUseCase = SaveThemeModeUseCase(fakeUserDataRepository),
            deleteVehicleUseCase = DeleteVehicleUseCase(fakeVehicleRepository),
            getVehicleByIdUseCase = GetVehicleByIdUseCase(fakeVehicleRepository),
            saveVehicleUseCase = SaveVehicleUseCase(fakeVehicleRepository),
            navigationManager = fakeNavigationManager,
            analyticsHelper = NoOpAnalyticsHelper(),
        )
    }

    @Test
    fun `GIVEN repository with two vehicles WHEN userData flow is collected THEN emits Loading then Success with correct theme and vehicle count`() = runTest {
        sut.userData.test {
            assertEquals(ProfileUiState.Loading, awaitItem())

            val successState = awaitItem() as ProfileUiState.Success
            assertEquals(ThemeMode.SYSTEM, successState.content.themeUi.mode)
            assertEquals(ThemeMode.entries.size, successState.content.allThemesUi.size)
            assertEquals(2, successState.content.vehicles.size)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `GIVEN two vehicles where first is principal WHEN userData emits Success THEN only the principal vehicle is marked as selected`() = runTest {
        sut.userData.test {
            awaitItem() // Loading
            val successState = awaitItem() as ProfileUiState.Success

            val selectedVehicle = successState.content.vehicles.first { it.isSelected }
            assertEquals(1L, selectedVehicle.id)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `GIVEN two vehicles where second is not principal WHEN userData emits Success THEN the non-principal vehicle is not selected`() = runTest {
        sut.userData.test {
            awaitItem() // Loading
            val successState = awaitItem() as ProfileUiState.Success

            val nonSelectedVehicles = successState.content.vehicles.filter { !it.isSelected }
            assertEquals(1, nonSelectedVehicles.size)
            assertEquals(2L, nonSelectedVehicles.first().id)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `GIVEN vehicles ordered with non-principal first in repository WHEN userData emits Success THEN principal vehicle appears first in the list`() = runTest {
        val repositoryWithReversedOrder = FakeUserDataRepository(
            initialUserData = UserData(
                vehicles = listOf(
                    Vehicle(
                        id = 2L,
                        userId = 0L,
                        fuelType = FuelType.GASOLINE_95,
                        name = "Honda CB500",
                        tankCapacity = 18,
                        vehicleType = VehicleType.MOTORCYCLE,
                        isPrincipal = false
                    ),
                    Vehicle(
                        id = 1L,
                        userId = 0L,
                        fuelType = FuelType.GASOLINE_95,
                        name = "Golf VIII",
                        tankCapacity = 55,
                        vehicleType = VehicleType.CAR,
                        isPrincipal = true
                    ),
                ),
            )
        )
        val viewModel = ProfileViewModel(
            getUserData = GetUserDataUseCase(repositoryWithReversedOrder),

            saveThemeModeUseCase = SaveThemeModeUseCase(repositoryWithReversedOrder),
            deleteVehicleUseCase = DeleteVehicleUseCase(fakeVehicleRepository),
            getVehicleByIdUseCase = GetVehicleByIdUseCase(fakeVehicleRepository),
            saveVehicleUseCase = SaveVehicleUseCase(fakeVehicleRepository),
            navigationManager = fakeNavigationManager,
            analyticsHelper = NoOpAnalyticsHelper(),
        )

        viewModel.userData.test {
            awaitItem() // Loading
            val successState = awaitItem() as ProfileUiState.Success

            assertEquals(1L, successState.content.vehicles.first().id)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `GIVEN profile screen WHEN AddVehicle event is sent THEN navigation manager receives AddVehicle destination`() = runTest {
        sut.handleEvents(ProfileEvents.AddVehicle)

        assertEquals<List<NavigationDestination>>(
            listOf(NavigationDestination.AddVehicle),
            fakeNavigationManager.navigatedDestinations
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GIVEN profile screen WHEN Theme event is sent with LIGHT mode THEN repository saves LIGHT theme mode`() = runTest {
        val themeMode = ThemeMode.LIGHT

        sut.handleEvents(ProfileEvents.Theme(themeMode.toUi()))

        advanceUntilIdle()

        assertEquals(listOf(themeMode), fakeUserDataRepository.updatedThemeModes)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GIVEN userData in Success state WHEN Theme event is sent with DARK mode THEN ui state reflects DARK theme`() = runTest {
        sut.userData.test {
            assertEquals(ProfileUiState.Loading, awaitItem())
            awaitItem()

            sut.handleEvents(ProfileEvents.Theme(ThemeMode.DARK.toUi()))
            advanceUntilIdle()

            val updated = awaitItem() as ProfileUiState.Success
            assertEquals(ThemeMode.DARK, updated.content.themeUi.mode)
            cancelAndConsumeRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GIVEN two vehicles WHEN DeleteVehicle event is sent for the non-principal vehicle THEN repository deletes that vehicle id`() = runTest {
        sut.userData.test {
            awaitItem() // Loading
            awaitItem() // Success

            sut.handleEvents(ProfileEvents.DeleteVehicle(vehicleId = 2L))
            advanceUntilIdle()

            assertEquals(listOf(2L), fakeVehicleRepository.deletedVehicleIds)
            cancelAndConsumeRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GIVEN exactly two vehicles where first is principal WHEN DeleteVehicle event is sent for the principal THEN the remaining vehicle is promoted to principal`() = runTest {
        sut.userData.test {
            awaitItem() // Loading
            awaitItem() // Success

            sut.handleEvents(ProfileEvents.DeleteVehicle(vehicleId = 1L))
            advanceUntilIdle()

            val promotedVehicle = fakeVehicleRepository.getVehicleById(vehicleId = 2L)
            assertTrue(promotedVehicle?.isPrincipal == true)
            assertEquals(listOf(1L), fakeVehicleRepository.deletedVehicleIds)
            cancelAndConsumeRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GIVEN only one vehicle WHEN DeleteVehicle event is sent for that vehicle THEN no vehicle is deleted`() = runTest {
        val singleVehicleRepository = FakeVehicleRepository(
            initialVehicles = listOf(
                Vehicle(
                    id = 1L,
                    userId = 0L,
                    fuelType = FuelType.GASOLINE_95,
                    name = "Golf VIII",
                    tankCapacity = 55,
                    vehicleType = VehicleType.CAR,
                    isPrincipal = true
                ),
            )
        )
        val singleVehicleViewModel = ProfileViewModel(
            getUserData = GetUserDataUseCase(
                FakeUserDataRepository(
                    initialUserData = UserData(
                        vehicles = listOf(
                            Vehicle(
                                id = 1L,
                                userId = 0L,
                                fuelType = FuelType.GASOLINE_95,
                                name = "Golf VIII",
                                tankCapacity = 55,
                                vehicleType = VehicleType.CAR,
                                isPrincipal = true
                            ),
                        )
                    )
                )
            ),

            saveThemeModeUseCase = SaveThemeModeUseCase(fakeUserDataRepository),
            deleteVehicleUseCase = DeleteVehicleUseCase(singleVehicleRepository),
            getVehicleByIdUseCase = GetVehicleByIdUseCase(singleVehicleRepository),
            saveVehicleUseCase = SaveVehicleUseCase(singleVehicleRepository),
            navigationManager = fakeNavigationManager,
            analyticsHelper = NoOpAnalyticsHelper(),
        )

        singleVehicleViewModel.userData.test {
            awaitItem() // Loading
            awaitItem() // Success

            singleVehicleViewModel.handleEvents(ProfileEvents.DeleteVehicle(vehicleId = 1L))
            advanceUntilIdle()

            assertTrue(singleVehicleRepository.deletedVehicleIds.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GIVEN three vehicles where first is principal WHEN DeleteVehicle event is sent for the principal THEN no remaining vehicle is auto-promoted to principal`() = runTest {
        val threeVehicles = listOf(
            Vehicle(
                id = 1L,
                userId = 0L,
                fuelType = FuelType.GASOLINE_95,
                name = "Golf VIII",
                tankCapacity = 55,
                vehicleType = VehicleType.CAR,
                isPrincipal = true
            ),
            Vehicle(
                id = 2L,
                userId = 0L,
                fuelType = FuelType.GASOLINE_95,
                name = "Honda CB500",
                tankCapacity = 18,
                vehicleType = VehicleType.MOTORCYCLE,
                isPrincipal = false
            ),
            Vehicle(
                id = 3L,
                userId = 0L,
                fuelType = FuelType.GASOLINE_95,
                name = "Seat Ibiza",
                tankCapacity = 40,
                vehicleType = VehicleType.CAR,
                isPrincipal = false
            ),
        )
        val threeVehicleRepository = FakeVehicleRepository(initialVehicles = threeVehicles)
        val threeVehicleViewModel = ProfileViewModel(
            getUserData = GetUserDataUseCase(
                FakeUserDataRepository(initialUserData = UserData(vehicles = threeVehicles))
            ),

            saveThemeModeUseCase = SaveThemeModeUseCase(fakeUserDataRepository),
            deleteVehicleUseCase = DeleteVehicleUseCase(threeVehicleRepository),
            getVehicleByIdUseCase = GetVehicleByIdUseCase(threeVehicleRepository),
            saveVehicleUseCase = SaveVehicleUseCase(threeVehicleRepository),
            navigationManager = fakeNavigationManager,
            analyticsHelper = NoOpAnalyticsHelper(),
        )

        threeVehicleViewModel.userData.test {
            awaitItem() // Loading
            awaitItem() // Success

            threeVehicleViewModel.handleEvents(ProfileEvents.DeleteVehicle(vehicleId = 1L))
            advanceUntilIdle()

            assertEquals(listOf(1L), threeVehicleRepository.deletedVehicleIds)
            val vehicle2 = threeVehicleRepository.getVehicleById(vehicleId = 2L)
            val vehicle3 = threeVehicleRepository.getVehicleById(vehicleId = 3L)
            assertFalse(vehicle2?.isPrincipal == true)
            assertFalse(vehicle3?.isPrincipal == true)
            cancelAndConsumeRemainingEvents()
        }
    }
}
