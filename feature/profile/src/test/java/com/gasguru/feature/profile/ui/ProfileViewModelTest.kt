package com.gasguru.feature.profile.ui

import app.cash.turbine.test
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.domain.user.SaveThemeModeUseCase
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.model.data.UserData
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.VehicleType
import com.gasguru.core.testing.CoroutinesTestExtension
import com.gasguru.core.testing.fakes.data.user.FakeUserDataRepository
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
    private lateinit var fakeNavigationManager: FakeNavigationManager

    private lateinit var sut: ProfileViewModel

    @BeforeEach
    fun setUp() {
        fakeUserDataRepository = FakeUserDataRepository(
            initialUserData = UserData(
                vehicles = listOf(
                    Vehicle(id = 1L, userId = 0L, fuelType = FuelType.GASOLINE_95, name = "Golf VIII", tankCapacity = 55, vehicleType = VehicleType.CAR, isPrincipal = true),
                    Vehicle(id = 2L, userId = 0L, fuelType = FuelType.GASOLINE_95, name = "Honda CB500", tankCapacity = 18, vehicleType = VehicleType.MOTORCYCLE, isPrincipal = false),
                ),
            )
        )
        fakeNavigationManager = FakeNavigationManager()
        sut = ProfileViewModel(
            getUserData = GetUserDataUseCase(fakeUserDataRepository),
            saveThemeModeUseCase = SaveThemeModeUseCase(fakeUserDataRepository),
            navigationManager = fakeNavigationManager,
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
                    Vehicle(id = 2L, userId = 0L, fuelType = FuelType.GASOLINE_95, name = "Honda CB500", tankCapacity = 18, vehicleType = VehicleType.MOTORCYCLE, isPrincipal = false),
                    Vehicle(id = 1L, userId = 0L, fuelType = FuelType.GASOLINE_95, name = "Golf VIII", tankCapacity = 55, vehicleType = VehicleType.CAR, isPrincipal = true),
                ),
            )
        )
        val viewModel = ProfileViewModel(
            getUserData = GetUserDataUseCase(repositoryWithReversedOrder),
            saveThemeModeUseCase = SaveThemeModeUseCase(repositoryWithReversedOrder),
            navigationManager = fakeNavigationManager,
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
}
