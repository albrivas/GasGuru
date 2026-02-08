package com.gasguru.feature.onboarding_welcome.viewmodel

import app.cash.turbine.test
import com.gasguru.core.testing.CoroutinesTestExtension
import com.gasguru.core.testing.fakes.navigation.FakeNavigationManager
import com.gasguru.feature.onboarding_welcome.ui.NewOnboardingEvent
import com.gasguru.feature.onboarding_welcome.ui.OnboardingPage
import com.gasguru.navigation.manager.NavigationDestination
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
class NewOnboardingViewModelTest {

    private lateinit var sut: NewOnboardingViewModel
    private lateinit var fakeNavigationManager: FakeNavigationManager

    @BeforeEach
    fun setUp() {
        fakeNavigationManager = FakeNavigationManager()
        sut = NewOnboardingViewModel(navigationManager = fakeNavigationManager)
    }

    @Test
    @DisplayName("GIVEN initial state WHEN collecting uiState THEN currentPage is 0")
    fun initialStateIsFirstPage() = runTest {
        sut.uiState.test {
            val state = awaitItem()
            assertEquals(0, state.currentPage)
            assertTrue(state.isFirstPage)
            assertTrue(state.showSkipButton)
        }
    }

    @Test
    @DisplayName("GIVEN first page WHEN NextPage event THEN currentPage is 1")
    fun nextPageIncrementsCurrentPage() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = NewOnboardingEvent.NextPage)
            val state = awaitItem()
            assertEquals(1, state.currentPage)
        }
    }

    @Test
    @DisplayName("GIVEN second page WHEN PreviousPage event THEN currentPage is 0")
    fun previousPageDecrementsCurrentPage() = runTest {
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
    @DisplayName("GIVEN first page WHEN PreviousPage event THEN currentPage stays 0")
    fun previousPageOnFirstPageDoesNothing() = runTest {
        sut.uiState.test {
            val initial = awaitItem()
            assertEquals(0, initial.currentPage)

            sut.handleEvent(event = NewOnboardingEvent.PreviousPage)
            expectNoEvents()
        }
    }

    @Test
    @DisplayName("GIVEN last page WHEN NextPage event THEN navigates to fuel preferences")
    fun nextPageOnLastPageNavigatesToFuelPreferences() = runTest {
        val lastPageIndex = OnboardingPage.entries.size - 1
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
    @DisplayName("GIVEN any page WHEN Skip event THEN navigates to fuel preferences")
    fun skipNavigatesToFuelPreferences() = runTest {
        sut.handleEvent(event = NewOnboardingEvent.Skip)

        assertEquals(1, fakeNavigationManager.navigatedDestinations.size)
        assertEquals(
            NavigationDestination.OnboardingFuelPreferences,
            fakeNavigationManager.navigatedDestinations.first(),
        )
    }

    @Test
    @DisplayName("GIVEN valid page WHEN PageChanged event THEN currentPage updates")
    fun pageChangedUpdatesCurrentPage() = runTest {
        sut.uiState.test {
            awaitItem()

            sut.handleEvent(event = NewOnboardingEvent.PageChanged(page = 2))
            val state = awaitItem()
            assertEquals(2, state.currentPage)
        }
    }

    @Test
    @DisplayName("GIVEN invalid page WHEN PageChanged event THEN currentPage stays the same")
    fun pageChangedWithInvalidPageDoesNothing() = runTest {
        sut.uiState.test {
            val initial = awaitItem()
            assertEquals(0, initial.currentPage)

            sut.handleEvent(event = NewOnboardingEvent.PageChanged(page = -1))
            sut.handleEvent(event = NewOnboardingEvent.PageChanged(page = OnboardingPage.entries.size))
            expectNoEvents()
        }
    }

    @Test
    @DisplayName("GIVEN last page WHEN collecting uiState THEN showSkipButton is false")
    fun lastPageHidesSkipButton() = runTest {
        sut.uiState.test {
            awaitItem()

            val lastPageIndex = OnboardingPage.entries.size - 1
            sut.handleEvent(event = NewOnboardingEvent.PageChanged(page = lastPageIndex))
            val state = awaitItem()
            assertTrue(state.isLastPage)
            assertEquals(false, state.showSkipButton)
        }
    }
}