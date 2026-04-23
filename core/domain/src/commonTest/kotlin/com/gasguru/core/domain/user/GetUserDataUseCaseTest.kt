package com.gasguru.core.domain.user

import app.cash.turbine.test
import com.gasguru.core.domain.fakes.FakeUserDataRepository
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.model.data.UserData
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserDataUseCaseTest {

    private lateinit var sut: GetUserDataUseCase
    private lateinit var fakeUserDataRepository: FakeUserDataRepository

    @BeforeTest
    fun setUp() {
        fakeUserDataRepository = FakeUserDataRepository()
        sut = GetUserDataUseCase(userDataRepository = fakeUserDataRepository)
    }

    @Test
    fun returnsDefaultUserData() = runTest {
        sut().test {
            val result = awaitItem()
            assertEquals(UserData(), result)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun returnsUpdatedUserData() = runTest {
        val customUserData = UserData(themeMode = ThemeMode.DARK, isOnboardingSuccess = true)
        fakeUserDataRepository.setUserData(customUserData)

        sut().test {
            val result = awaitItem()
            assertEquals(ThemeMode.DARK, result.themeMode)
            assertEquals(true, result.isOnboardingSuccess)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
