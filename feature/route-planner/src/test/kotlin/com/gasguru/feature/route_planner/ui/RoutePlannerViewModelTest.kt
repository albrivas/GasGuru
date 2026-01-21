package com.gasguru.feature.route_planner.ui

import app.cash.turbine.test
import com.gasguru.core.domain.search.ClearRecentSearchQueriesUseCase
import com.gasguru.core.domain.search.GetRecentSearchQueryUseCase
import com.gasguru.core.model.data.RecentSearchQuery
import com.gasguru.core.testing.CoroutinesTestExtension
import com.gasguru.core.testing.fakes.data.search.FakeOfflineRecentSearchRepository
import com.gasguru.core.ui.RecentSearchQueriesUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    private lateinit var sut: RoutePlannerViewModel
    private lateinit var clearRecentSearchQueriesUseCase: ClearRecentSearchQueriesUseCase
    private lateinit var getRecentSearchQueryUseCase: GetRecentSearchQueryUseCase
    private lateinit var recentSearchRepository: FakeOfflineRecentSearchRepository

    @BeforeEach
    fun setUp() {
        recentSearchRepository = FakeOfflineRecentSearchRepository()
        clearRecentSearchQueriesUseCase = ClearRecentSearchQueriesUseCase(recentSearchRepository)
        getRecentSearchQueryUseCase = GetRecentSearchQueryUseCase(recentSearchRepository)

        recentSearchRepository.setRecentSearchQueries(
            listOf(
                RecentSearchQuery("Barcelona", "1"),
                RecentSearchQuery("Madrid", "2")
            )
        )

        sut = RoutePlannerViewModel(
            clearRecentSearchQueriesUseCase = clearRecentSearchQueriesUseCase,
            getRecentSearchQueryUseCase = getRecentSearchQueryUseCase
        )
    }

    @Test
    @DisplayName("GIVEN initial state WHEN accessing state THEN should have empty queries and START as current input")
    fun initialState() = runTest {
        sut.state.test {
            val initialState = awaitItem()

            assertEquals(RoutePlannerUiState(), initialState)
            assertTrue(initialState.startQuery.isEmpty)
            assertTrue(initialState.endQuery.isEmpty)
            assertEquals(InputField.START, initialState.currentInput)
        }
    }

    @Test
    @DisplayName("GIVEN initial state WHEN checking route enabled THEN should be disabled")
    fun routeDisabledInitially() = runTest {
        sut.isRouteEnabled.test {
            assertEquals(false, awaitItem())
        }
    }

    @Test
    @DisplayName("GIVEN empty queries WHEN both start and end queries are filled THEN should enable route")
    fun enablesRouteWhenBothQueriesFilled() = runTest {
        sut.isRouteEnabled.test {
            assertEquals(false, awaitItem())

            sut.handleEvent(
                RoutePlannerUiEvent.SelectPlace(placeId = "1", placeName = "Barcelona")
            )
            expectNoEvents()

            sut.handleEvent(RoutePlannerUiEvent.ChangeCurrentInput(InputField.END))
            sut.handleEvent(
                RoutePlannerUiEvent.SelectPlace(placeId = "2", placeName = "Madrid")
            )
            assertEquals(true, awaitItem())
        }
    }

    @Test
    @DisplayName("GIVEN initial state WHEN changing current input THEN should update current input field")
    fun updatesCurrentInputField() = runTest {
        sut.state.test {
            awaitItem()

            sut.handleEvent(RoutePlannerUiEvent.ChangeCurrentInput(InputField.END))

            assertEquals(InputField.END, awaitItem().currentInput)
        }
    }

    @Test
    @DisplayName("GIVEN current input is START WHEN selecting place THEN should update start query")
    fun updatesStartQueryWhenCurrentInputIsStart() = runTest {
        sut.state.test {
            awaitItem()

            sut.handleEvent(
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
    @DisplayName("GIVEN current input is END WHEN selecting place THEN should update end query")
    fun updatesEndQueryWhenCurrentInputIsEnd() = runTest {
        sut.state.test {
            awaitItem()

            sut.handleEvent(RoutePlannerUiEvent.ChangeCurrentInput(InputField.END))
            awaitItem()

            sut.handleEvent(
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
    @DisplayName("GIVEN empty queries WHEN selecting current location THEN should fill start query first")
    fun fillsEmptyStartQueryWhenSelectingCurrentLocation() = runTest {
        sut.state.test {
            awaitItem()

            sut.handleEvent(RoutePlannerUiEvent.SelectCurrentLocation)

            val state = awaitItem()
            assertTrue(state.startQuery.isCurrentLocation)
            assertTrue(state.endQuery.isEmpty)
        }
    }

    @Test
    @DisplayName("GIVEN start query filled WHEN selecting current location THEN should fill end query")
    fun fillsEmptyEndQueryWhenStartIsNotEmpty() = runTest {
        sut.state.test {
            awaitItem()

            sut.handleEvent(
                RoutePlannerUiEvent.SelectPlace(placeId = "123", placeName = "Barcelona")
            )
            awaitItem()

            sut.handleEvent(RoutePlannerUiEvent.SelectCurrentLocation)

            val state = awaitItem()
            assertEquals("Barcelona", state.startQuery.name)
            assertTrue(state.endQuery.isCurrentLocation)
        }
    }

    @Test
    @DisplayName("GIVEN empty queries WHEN selecting recent place THEN should fill start query first")
    fun fillsEmptyStartQueryWhenSelectingRecentPlace() = runTest {
        sut.state.test {
            awaitItem()

            sut.handleEvent(
                RoutePlannerUiEvent.SelectRecentPlace(placeId = "789", placeName = "Valencia")
            )

            val state = awaitItem()
            assertEquals("Valencia", state.startQuery.name)
            assertEquals("789", state.startQuery.id)
            assertTrue(state.endQuery.isEmpty)
        }
    }

    @Test
    @DisplayName("GIVEN filled start query WHEN clearing start field THEN should clear start query")
    fun clearsStartQuery() = runTest {
        sut.state.test {
            awaitItem()

            sut.handleEvent(
                RoutePlannerUiEvent.SelectPlace(placeId = "123", placeName = "Barcelona")
            )
            awaitItem()

            sut.handleEvent(RoutePlannerUiEvent.ClearStartDestinationField)

            assertTrue(awaitItem().startQuery.isEmpty)
        }
    }

    @Test
    @DisplayName("GIVEN both queries filled WHEN swapping destinations THEN should swap start and end queries")
    fun swapsStartAndEndQueries() = runTest {
        sut.state.test {
            awaitItem()

            sut.handleEvent(
                RoutePlannerUiEvent.SelectPlace(placeId = "123", placeName = "Barcelona")
            )
            awaitItem()

            sut.handleEvent(RoutePlannerUiEvent.ChangeCurrentInput(InputField.END))
            awaitItem()

            sut.handleEvent(
                RoutePlannerUiEvent.SelectPlace(placeId = "456", placeName = "Madrid")
            )
            awaitItem()

            sut.handleEvent(RoutePlannerUiEvent.ChangeDestinations)

            val state = awaitItem()
            assertEquals("Madrid", state.startQuery.name)
            assertEquals("456", state.startQuery.id)
            assertEquals("Barcelona", state.endQuery.name)
            assertEquals("123", state.endQuery.id)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("GIVEN clear recent event WHEN handling event THEN should call clear recent searches use case")
    fun callsClearRecentSearchesUseCase() = runTest {
        sut.handleEvent(RoutePlannerUiEvent.ClearRecentSearches)

        advanceUntilIdle()

        assertEquals(1, recentSearchRepository.clearRecentSearchesCalls)
    }

    @Test
    @DisplayName(
        "GIVEN use case returns recent queries WHEN collecting state THEN should emit Success state with recent queries"
    )
    fun emitsSuccessStateWithRecentQueries() = runTest {
        sut.recentSearchQueriesUiState.test {
            assertEquals(RecentSearchQueriesUiState.Loading, awaitItem())

            val successState = awaitItem() as RecentSearchQueriesUiState.Success
            assertEquals(2, successState.recentQueries.size)
            assertEquals("Barcelona", successState.recentQueries[0].name)
            assertEquals("Madrid", successState.recentQueries[1].name)
        }
    }

    @Test
    @DisplayName("GIVEN empty queries WHEN filling and clearing queries THEN should update route enabled state")
    fun updatesRouteEnabledStateWhenQueriesChange() = runTest {
        sut.isRouteEnabled.test {
            assertEquals(false, awaitItem())

            sut.handleEvent(
                RoutePlannerUiEvent.SelectPlace(placeId = "123", placeName = "Barcelona")
            )
            expectNoEvents()

            sut.handleEvent(RoutePlannerUiEvent.ChangeCurrentInput(InputField.END))
            sut.handleEvent(
                RoutePlannerUiEvent.SelectPlace(placeId = "456", placeName = "Madrid")
            )
            assertEquals(true, awaitItem())

            sut.handleEvent(RoutePlannerUiEvent.ClearStartDestinationField)
            assertEquals(false, awaitItem())
        }
    }
}
