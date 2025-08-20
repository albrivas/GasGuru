package com.gasguru.feature.route_planner.ui

import app.cash.turbine.test
import com.gasguru.core.domain.search.ClearRecentSearchQueriesUseCase
import com.gasguru.core.domain.search.GetRecentSearchQueryUseCase
import com.gasguru.core.model.data.RecentSearchQuery
import com.gasguru.core.testing.CoroutinesTestExtension
import com.gasguru.core.ui.RecentSearchQueriesUiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
class RoutePlannerViewModelTest {

    private lateinit var viewModel: RoutePlannerViewModel
    private lateinit var clearRecentSearchQueriesUseCase: ClearRecentSearchQueriesUseCase
    private lateinit var getRecentSearchQueryUseCase: GetRecentSearchQueryUseCase

    @BeforeEach
    fun setUp() {
        clearRecentSearchQueriesUseCase = mockk(relaxed = true)
        getRecentSearchQueryUseCase = mockk()

        coEvery { getRecentSearchQueryUseCase() } returns flowOf(
            listOf(
                RecentSearchQuery("Barcelona", "1"),
                RecentSearchQuery("Madrid", "2")
            )
        )

        viewModel = RoutePlannerViewModel(
            clearRecentSearchQueriesUseCase = clearRecentSearchQueriesUseCase,
            getRecentSearchQueryUseCase = getRecentSearchQueryUseCase
        )
    }

    @Test
    @DisplayName("Should have empty queries and START as current input initially")
    fun `should have empty queries and START as current input initially`() = runTest {
        viewModel.state.test {
            val initialState = awaitItem()

            assertEquals(RoutePlannerUiState(), initialState)
            assertTrue(initialState.startQuery.isEmpty)
            assertTrue(initialState.endQuery.isEmpty)
            assertEquals(InputField.START, initialState.currentInput)
        }
    }

    @Test
    @DisplayName("Should have route disabled initially")
    fun `should have route disabled initially`() = runTest {
        viewModel.isRouteEnabled.test {
            assertEquals(false, awaitItem())
        }
    }

    @Test
    @DisplayName("Should enable route when both queries are filled")
    fun `should enable route when both queries are filled`() = runTest {
        viewModel.isRouteEnabled.test {
            assertEquals(false, awaitItem())

            viewModel.handleEvent(
                RoutePlannerUiEvent.SelectPlace(placeId = "1", placeName = "Barcelona")
            )
            expectNoEvents()

            viewModel.handleEvent(RoutePlannerUiEvent.ChangeCurrentInput(InputField.END))
            viewModel.handleEvent(
                RoutePlannerUiEvent.SelectPlace(placeId = "2", placeName = "Madrid")
            )
            assertEquals(true, awaitItem())
        }
    }

    @Test
    @DisplayName("Should update current input field")
    fun `should update current input field`() = runTest {
        viewModel.state.test {
            awaitItem()

            viewModel.handleEvent(RoutePlannerUiEvent.ChangeCurrentInput(InputField.END))

            assertEquals(InputField.END, awaitItem().currentInput)
        }
    }

    @Test
    @DisplayName("Should update start query when current input is START")
    fun `should update start query when current input is START`() = runTest {
        viewModel.state.test {
            awaitItem()

            viewModel.handleEvent(
                RoutePlannerUiEvent.SelectPlace(placeId = "123", placeName = "Barcelona")
            )

            val state = awaitItem()
            assertEquals("Barcelona", state.startQuery.name)
            assertEquals("123", state.startQuery.id)
            assertFalse(state.startQuery.isCurrentLocation)
            assertTrue(state.endQuery.isEmpty)
        }
    }

    @Test
    @DisplayName("Should update end query when current input is END")
    fun `should update end query when current input is END`() = runTest {
        viewModel.state.test {
            awaitItem()

            viewModel.handleEvent(RoutePlannerUiEvent.ChangeCurrentInput(InputField.END))
            awaitItem()

            viewModel.handleEvent(
                RoutePlannerUiEvent.SelectPlace(placeId = "456", placeName = "Madrid")
            )

            val state = awaitItem()
            assertEquals("Madrid", state.endQuery.name)
            assertEquals("456", state.endQuery.id)
            assertFalse(state.endQuery.isCurrentLocation)
            assertTrue(state.startQuery.isEmpty)
        }
    }

    @Test
    @DisplayName("Should fill empty start query first when selecting current location")
    fun `should fill empty start query first when selecting current location`() = runTest {
        viewModel.state.test {
            awaitItem()

            viewModel.handleEvent(RoutePlannerUiEvent.SelectCurrentLocation)

            val state = awaitItem()
            assertTrue(state.startQuery.isCurrentLocation)
            assertTrue(state.endQuery.isEmpty)
        }
    }

    @Test
    @DisplayName("Should fill empty end query when start query is not empty and selecting current location")
    fun `should fill empty end query when start query is not empty and selecting current location`() =
        runTest {
            viewModel.state.test {
                awaitItem()

                viewModel.handleEvent(
                    RoutePlannerUiEvent.SelectPlace(placeId = "123", placeName = "Barcelona")
                )
                awaitItem()

                viewModel.handleEvent(RoutePlannerUiEvent.SelectCurrentLocation)

                val state = awaitItem()
                assertEquals("Barcelona", state.startQuery.name)
                assertTrue(state.endQuery.isCurrentLocation)
            }
        }

    @Test
    @DisplayName("Should fill empty start query first when selecting recent place")
    fun `should fill empty start query first when selecting recent place`() = runTest {
        viewModel.state.test {
            awaitItem()

            viewModel.handleEvent(
                RoutePlannerUiEvent.SelectRecentPlace(placeId = "789", placeName = "Valencia")
            )

            val state = awaitItem()
            assertEquals("Valencia", state.startQuery.name)
            assertEquals("789", state.startQuery.id)
            assertTrue(state.endQuery.isEmpty)
        }
    }

    @Test
    @DisplayName("Should clear start query")
    fun `should clear start query`() = runTest {
        viewModel.state.test {
            awaitItem()

            viewModel.handleEvent(
                RoutePlannerUiEvent.SelectPlace(placeId = "123", placeName = "Barcelona")
            )
            awaitItem()

            viewModel.handleEvent(RoutePlannerUiEvent.ClearStartDestinationField)

            assertTrue(awaitItem().startQuery.isEmpty)
        }
    }

    @Test
    @DisplayName("Should swap start and end queries")
    fun `should swap start and end queries`() = runTest {
        viewModel.state.test {
            awaitItem()

            viewModel.handleEvent(
                RoutePlannerUiEvent.SelectPlace(placeId = "123", placeName = "Barcelona")
            )
            awaitItem()

            viewModel.handleEvent(RoutePlannerUiEvent.ChangeCurrentInput(InputField.END))
            awaitItem()

            viewModel.handleEvent(
                RoutePlannerUiEvent.SelectPlace(placeId = "456", placeName = "Madrid")
            )
            awaitItem()

            viewModel.handleEvent(RoutePlannerUiEvent.ChangeDestinations)

            val state = awaitItem()
            assertEquals("Madrid", state.startQuery.name)
            assertEquals("456", state.startQuery.id)
            assertEquals("Barcelona", state.endQuery.name)
            assertEquals("123", state.endQuery.id)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("Should call clear recent searches use case")
    fun `should call clear recent searches use case`() = runTest {
        viewModel.handleEvent(RoutePlannerUiEvent.ClearRecentSearches)

        advanceUntilIdle()

        coVerify { clearRecentSearchQueriesUseCase() }
    }

    @Test
    @DisplayName("Should emit Success state with recent queries")
    fun `should emit Success state with recent queries`() = runTest {
        viewModel.recentSearchQueriesUiState.test {
            assertEquals(RecentSearchQueriesUiState.Loading, awaitItem())

            val successState = awaitItem() as RecentSearchQueriesUiState.Success
            assertEquals(2, successState.recentQueries.size)
            assertEquals("Barcelona", successState.recentQueries[0].name)
            assertEquals("Madrid", successState.recentQueries[1].name)
        }
    }

    @Test
    @DisplayName("Should update route enabled state when queries change")
    fun `should update route enabled state when queries change`() = runTest {
        viewModel.isRouteEnabled.test {
            assertEquals(false, awaitItem())

            viewModel.handleEvent(
                RoutePlannerUiEvent.SelectPlace(placeId = "123", placeName = "Barcelona")
            )
            expectNoEvents()

            viewModel.handleEvent(RoutePlannerUiEvent.ChangeCurrentInput(InputField.END))
            viewModel.handleEvent(
                RoutePlannerUiEvent.SelectPlace(placeId = "456", placeName = "Madrid")
            )
            assertEquals(true, awaitItem())

            viewModel.handleEvent(RoutePlannerUiEvent.ClearStartDestinationField)
            assertEquals(false, awaitItem())
        }
    }
}
