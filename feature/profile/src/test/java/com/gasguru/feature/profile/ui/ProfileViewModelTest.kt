package com.gasguru.feature.profile.ui

import app.cash.turbine.test
import com.gasguru.core.domain.fuelstation.SaveFuelSelectionUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.domain.user.SaveThemeModeUseCase
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.model.data.UserData
import com.gasguru.core.testing.CoroutinesTestExtension
import com.gasguru.core.testing.fakes.data.user.FakeUserDataRepository
import com.gasguru.core.ui.R
import com.gasguru.core.ui.models.FuelTypeUiModel
import com.gasguru.core.ui.models.toUi
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

    private lateinit var sut: ProfileViewModel

    private lateinit var mockUserData: UserData

    @BeforeEach
    fun setUp() {
        mockUserData = UserData()
        fakeUserDataRepository = FakeUserDataRepository(initialUserData = mockUserData)
        sut = ProfileViewModel(
            getUserData = GetUserDataUseCase(fakeUserDataRepository),
            saveFuelSelectionUseCase = SaveFuelSelectionUseCase(fakeUserDataRepository),
            saveThemeModeUseCase = SaveThemeModeUseCase(fakeUserDataRepository),
        )
    }

    @Test
    @DisplayName("GIVEN userdata use case WHEN collected THEN emit Loading and then Success")
    fun getUserDataSuccess() = runTest {
        sut.userData.test {
            Assertions.assertEquals(ProfileUiState.Loading, awaitItem())

            val successState = awaitItem() as ProfileUiState.Success
            Assertions.assertEquals(R.string.gasoline_95, successState.content.fuelTranslation)
            Assertions.assertEquals(ThemeMode.SYSTEM, successState.content.themeUi.mode)
            Assertions.assertEquals(ThemeMode.entries.size, successState.content.allThemesUi.size)

            cancelAndConsumeRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("GIVEN fuel selection event WHEN handleEvents THEN useCase is called with correct fuel")
    fun saveSelectionFuel() = runTest {
        val fuelType = FuelType.GASOLINE_95

        sut.handleEvents(ProfileEvents.Fuel(fuelType))

        advanceUntilIdle()

        Assertions.assertEquals(listOf(fuelType), fakeUserDataRepository.updatedFuelSelections)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("GIVEN fuel change WHEN handleEvents THEN updates userData state")
    fun fuelSelectionUpdatesUiState() = runTest {
        val viewModel = sut

        viewModel.userData.test {
            Assertions.assertEquals(ProfileUiState.Loading, awaitItem())
            awaitItem()

            viewModel.handleEvents(ProfileEvents.Fuel(FuelType.DIESEL))
            advanceUntilIdle()

            val updated = awaitItem() as ProfileUiState.Success
            val expectedTranslation = FuelTypeUiModel.fromFuelType(FuelType.DIESEL).translationRes
            Assertions.assertEquals(expectedTranslation, updated.content.fuelTranslation)
            cancelAndConsumeRemainingEvents()
        }
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
