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
import com.gasguru.core.testing.CoroutinesTestExtension
import com.gasguru.core.testing.fakes.data.user.FakeUserDataRepository
import com.gasguru.core.testing.fakes.data.vehicle.FakeVehicleRepository
import com.gasguru.core.testing.fakes.navigation.FakeNavigationManager
import com.gasguru.core.ui.mapper.toUi
import com.gasguru.navigation.manager.NavigationDestination
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(
    CoroutinesTestExtension::class,
)
class ProfileViewModelTest {

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

    @BeforeEach
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
    @DisplayName("GIVEN userdata use case WHEN collected THEN emit Loading and then Success with vehicles")
    fun getUserDataSuccess() = runTest {
        sut.userData.test {
            Assertions.assertEquals(ProfileUiState.Loading, awaitItem())

            val successState = awaitItem() as ProfileUiState.Success
            Assertions.assertEquals(ThemeMode.SYSTEM, successState.content.themeUi.mode)
            Assertions.assertEquals(ThemeMode.entries.size, successState.content.allThemesUi.size)
            Assertions.assertEquals(2, successState.content.vehicles.size)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    @DisplayName("GIVEN vehicles with isPrincipal true WHEN collected THEN principal vehicle is isSelected true")
    fun principalVehicleIsSelected() = runTest {
        sut.userData.test {
            awaitItem() // Loading
            val successState = awaitItem() as ProfileUiState.Success

            val selectedVehicle = successState.content.vehicles.first { it.isSelected }
            Assertions.assertEquals(1L, selectedVehicle.id)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    @DisplayName(
        "GIVEN vehicles with isPrincipal false WHEN collected THEN non-principal vehicles are isSelected false"
    )
    fun nonPrincipalVehiclesAreNotSelected() = runTest {
        sut.userData.test {
            awaitItem() // Loading
            val successState = awaitItem() as ProfileUiState.Success

            val nonSelectedVehicles = successState.content.vehicles.filter { !it.isSelected }
            Assertions.assertEquals(1, nonSelectedVehicles.size)
            Assertions.assertEquals(2L, nonSelectedVehicles.first().id)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN vehicles where principal is not the first in the list
        WHEN userData is collected
        THEN principal vehicle is first in the result list
        """
    )
    fun principalVehicleIsFirstInList() = runTest {
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

            Assertions.assertEquals(1L, successState.content.vehicles.first().id)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    @DisplayName("GIVEN add vehicle event WHEN handleEvents THEN navigates to AddVehicle destination")
    fun addVehicleNavigatesToAddVehicleScreen() = runTest {
        sut.handleEvents(ProfileEvents.AddVehicle)

        Assertions.assertEquals(listOf(NavigationDestination.AddVehicle), fakeNavigationManager.navigatedDestinations)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("GIVEN theme event WHEN handleEvents THEN useCase is called with correct theme")
    fun saveThemeMode() = runTest {
        val themeMode = ThemeMode.LIGHT

        sut.handleEvents(ProfileEvents.Theme(themeMode.toUi()))

        advanceUntilIdle()

        Assertions.assertEquals(listOf(themeMode), fakeUserDataRepository.updatedThemeModes)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("GIVEN theme change WHEN handleEvents THEN updates userData state")
    fun themeUpdatesUiState() = runTest {
        sut.userData.test {
            Assertions.assertEquals(ProfileUiState.Loading, awaitItem())
            awaitItem()

            sut.handleEvents(ProfileEvents.Theme(ThemeMode.DARK.toUi()))
            advanceUntilIdle()

            val updated = awaitItem() as ProfileUiState.Success
            Assertions.assertEquals(ThemeMode.DARK, updated.content.themeUi.mode)
            cancelAndConsumeRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName(
        """
        GIVEN two vehicles
        WHEN delete non-principal vehicle
        THEN deleteVehicleUseCase is called with correct id
        """
    )
    fun deleteNonPrincipalVehicleCallsUseCase() = runTest {
        sut.userData.test {
            awaitItem() // Loading
            awaitItem() // Success

            sut.handleEvents(ProfileEvents.DeleteVehicle(vehicleId = 2L))
            advanceUntilIdle()

            Assertions.assertEquals(listOf(2L), fakeVehicleRepository.deletedVehicleIds)
            cancelAndConsumeRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName(
        """
        GIVEN two vehicles where vehicle 1 is principal
        WHEN delete principal vehicle
        THEN remaining vehicle is promoted to principal and deleted vehicle is removed
        """
    )
    fun deletePrincipalVehicleWithTwoVehiclesPromotesRemainingToPrincipal() = runTest {
        sut.userData.test {
            awaitItem() // Loading
            awaitItem() // Success

            sut.handleEvents(ProfileEvents.DeleteVehicle(vehicleId = 1L))
            advanceUntilIdle()

            val promotedVehicle = fakeVehicleRepository.getVehicleById(vehicleId = 2L)
            Assertions.assertTrue(promotedVehicle?.isPrincipal == true)
            Assertions.assertEquals(listOf(1L), fakeVehicleRepository.deletedVehicleIds)
            cancelAndConsumeRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName(
        """
        GIVEN one vehicle
        WHEN delete that vehicle
        THEN nothing is deleted (guard prevents it)
        """
    )
    fun deleteWithSingleVehicleDoesNothing() = runTest {
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

            Assertions.assertTrue(singleVehicleRepository.deletedVehicleIds.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName(
        """
        GIVEN three vehicles where vehicle 1 is principal
        WHEN delete principal vehicle
        THEN vehicle is deleted but no auto-promotion happens
        """
    )
    fun deletePrincipalVehicleWithThreeVehiclesDoesNotAutopromote() = runTest {
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

            Assertions.assertEquals(listOf(1L), threeVehicleRepository.deletedVehicleIds)
            val vehicle2 = threeVehicleRepository.getVehicleById(vehicleId = 2L)
            val vehicle3 = threeVehicleRepository.getVehicleById(vehicleId = 3L)
            Assertions.assertFalse(vehicle2?.isPrincipal == true)
            Assertions.assertFalse(vehicle3?.isPrincipal == true)
            cancelAndConsumeRemainingEvents()
        }
    }
}
