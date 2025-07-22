package com.gasguru.feature.profile.ui

import app.cash.turbine.test
import com.gasguru.core.domain.fuelstation.SaveFuelSelectionUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.UserData
import com.gasguru.core.testing.CoroutinesTestExtension
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(
    CoroutinesTestExtension::class,
    MockKExtension::class
)
class ProfileViewModelTest {

    @MockK
    private lateinit var getUserData: GetUserDataUseCase

    @MockK
    private lateinit var saveFuelSelectionUseCase: SaveFuelSelectionUseCase

    private lateinit var sut: ProfileViewModel

    private lateinit var mockUserData: UserData

    @BeforeEach
    fun setUp() {
        mockUserData = UserData()
        coEvery { getUserData.invoke() } returns flowOf(mockUserData)
        sut = ProfileViewModel(getUserData, saveFuelSelectionUseCase)
    }

    @Test
    @DisplayName("GIVEN userdata use case WHEN collected THEN emit Loading and then Success")
    fun getUserDataSuccess() = runTest {
        sut.userData.test {
            Assertions.assertEquals(ProfileUiState.Loading, awaitItem())
            Assertions.assertEquals(ProfileUiState.Success(mockUserData), awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("GIVEN fuel selection event WHEN handleEvents THEN useCase is called with correct fuel")
    fun saveSelectionFuel() = runTest {
        val fuelType = FuelType.GASOLINE_95

        coEvery { saveFuelSelectionUseCase(fuelType = fuelType) } just Runs

        sut.handleEvents(ProfileEvents.Fuel(fuelType))

        advanceUntilIdle()

        coVerify { saveFuelSelectionUseCase(fuelType = fuelType) }
    }

}