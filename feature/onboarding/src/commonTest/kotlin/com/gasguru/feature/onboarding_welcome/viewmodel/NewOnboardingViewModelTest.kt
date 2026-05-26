package com.gasguru.feature.onboarding_welcome.viewmodel

import app.cash.turbine.test
import com.gasguru.core.analytics.NoOpAnalyticsHelper
import com.gasguru.core.testing.CoroutineTest
import com.gasguru.core.testing.fakes.navigation.FakeNavigationManager
import com.gasguru.feature.onboarding_welcome.ui.NewOnboardingEvent
import com.gasguru.feature.onboarding_welcome.ui.OnboardingPageUiModel
import com.gasguru.navigation.manager.NavigationDestination
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NewOnboardingViewModelTest : CoroutineTest() {

    private lateinit var sut: NewOnboardingViewModel
    private lateinit var fakeNavigationManager: FakeNavigationManager

    @BeforeTest
    fun setUp() {
        fakeNavigationManager = FakeNavigationManager()
        sut = NewOnboardingViewModel(navigationManager = fakeNavigationManager, analyticsHelper = NoOpAnalyticsHelper())
    }

    @Test
    fun `GIVEN a fresh ViewModel WHEN uiState is observed THEN current page is 0 isFirstPage is true and skip button is shown`() = runTest {
        sut.uiState.test {
            val state = awaitItem()
            assertEquals(0, state.currentPage)
            assertTrue(state.isFirstPage)
            assertTrue(state.showSkipButton)
        }
    }

    @Test
    fun `GIVEN current page is 0 WHEN NextPage event is sent THEN current page increments to 1`() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = NewOnboardingEvent.NextPage)
            val state = awaitItem()
            assertEquals(1, state.currentPage)
        }
    }

    @Test
    fun `GIVEN current page is 1 WHEN PreviousPage event is sent THEN current page decrements to 0`() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = NewOnboardingEvent.NextPage)
            awaitItem()

            sut.handleEvent(event = NewOnboardingEvent.PreviousPage)
            val state = awaitItem()
            assertEquals(0, state.currentPage)
        }
    }

    @Test
    fun `GIVEN current page is 0 WHEN PreviousPage event is sent THEN page does not change and no new state is emitted`() = runTest {
        sut.uiState.test {
            val initial = awaitItem()
            assertEquals(0, initial.currentPage)

            sut.handleEvent(event = NewOnboardingEvent.PreviousPage)
            expectNoEvents()
        }
    }

    @Test
    fun `GIVEN current page is the last onboarding page WHEN NextPage event is sent THEN navigation goes to OnboardingFuelPreferences`() = runTest {
        val lastPageIndex = OnboardingPageUiModel.entries.size - 1
        repeat(lastPageIndex) {
            sut.handleEvent(event = NewOnboardingEvent.NextPage)
        }

        sut.handleEvent(event = NewOnboardingEvent.NextPage)

        assertEquals(1, fakeNavigationManager.navigatedDestinations.size)
        assertEquals(
            NavigationDestination.OnboardingFuelPreferences,
            fakeNavigationManager.navigatedDestinations.first(),
        )
    }

    @Test
    fun `GIVEN onboarding is in progress WHEN Skip event is sent THEN navigation goes to OnboardingFuelPreferences`() = runTest {
        sut.handleEvent(event = NewOnboardingEvent.Skip)

        assertEquals(1, fakeNavigationManager.navigatedDestinations.size)
        assertEquals(
            NavigationDestination.OnboardingFuelPreferences,
            fakeNavigationManager.navigatedDestinations.first(),
        )
    }

    @Test
    fun `GIVEN onboarding is on page 0 WHEN PageChanged event is sent with page 2 THEN current page updates to 2`() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = NewOnboardingEvent.PageChanged(page = 2))
            val state = awaitItem()
            assertEquals(2, state.currentPage)
        }
    }

    @Test
    fun `GIVEN onboarding is on page 0 WHEN PageChanged event is sent with negative or out-of-bounds page THEN page does not change`() = runTest {
        sut.uiState.test {
            val initial = awaitItem()
            assertEquals(0, initial.currentPage)

            sut.handleEvent(event = NewOnboardingEvent.PageChanged(page = -1))
            sut.handleEvent(event = NewOnboardingEvent.PageChanged(page = OnboardingPageUiModel.entries.size))
            expectNoEvents()
        }
    }

    @Test
    fun `GIVEN onboarding navigates to the last page WHEN PageChanged event is sent with that page THEN isLastPage is true and skip button is hidden`() = runTest {
        sut.uiState.test {
            awaitItem()

            val lastPageIndex = OnboardingPageUiModel.entries.size - 1
            sut.handleEvent(event = NewOnboardingEvent.PageChanged(page = lastPageIndex))
            val state = awaitItem()
            assertTrue(state.isLastPage)
            assertEquals(false, state.showSkipButton)
        }
    }
}
