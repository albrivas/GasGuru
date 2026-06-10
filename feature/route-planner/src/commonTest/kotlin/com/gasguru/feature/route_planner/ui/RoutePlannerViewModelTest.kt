package com.gasguru.feature.route_planner.ui

import app.cash.turbine.test
import com.gasguru.core.analytics.NoOpAnalyticsHelper
import com.gasguru.core.domain.search.ClearRecentSearchQueriesUseCase
import com.gasguru.core.domain.search.GetRecentSearchQueryUseCase
import com.gasguru.core.model.data.RecentSearchQuery
import com.gasguru.core.testing.CoroutineTest
import com.gasguru.core.testing.fakes.data.search.FakeOfflineRecentSearchRepository
import com.gasguru.core.ui.RecentSearchQueriesUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RoutePlannerViewModelTest : CoroutineTest() {

    private lateinit var sut: RoutePlannerViewModel
    private lateinit var clearRecentSearchQueriesUseCase: ClearRecentSearchQueriesUseCase
    private lateinit var getRecentSearchQueryUseCase: GetRecentSearchQueryUseCase
    private lateinit var recentSearchRepository: FakeOfflineRecentSearchRepository

    @BeforeTest
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
            getRecentSearchQueryUseCase = getRecentSearchQueryUseCase,
            analyticsHelper = NoOpAnalyticsHelper(),
        )
    }

    @Test
    fun `GIVEN a fresh ViewModel WHEN state is observed THEN initial state has empty queries and START as current input`() = runTest {
        sut.state.test {
            val initialState = awaitItem()

            assertEquals(RoutePlannerUiState(), initialState)
            assertTrue(initialState.startQuery.isEmpty)
            assertTrue(initialState.endQuery.isEmpty)
            assertEquals(InputField.START, initialState.currentInput)
        }
    }

    @Test
    fun `GIVEN a fresh ViewModel WHEN isRouteEnabled is observed THEN initial value is false`() = runTest {
        sut.isRouteEnabled.test {
            assertEquals(false, awaitItem())
        }
    }

    @Test
    fun `GIVEN start and end queries are both filled WHEN isRouteEnabled is observed THEN value changes to true`() = runTest {
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
    fun `GIVEN START is the current input field WHEN ChangeCurrentInput event with END is sent THEN state reflects END as the current input`() = runTest {
        sut.state.test {
            awaitItem()

            sut.handleEvent(RoutePlannerUiEvent.ChangeCurrentInput(InputField.END))

            assertEquals(InputField.END, awaitItem().currentInput)
        }
    }

    @Test
    fun `GIVEN current input is START WHEN SelectPlace event is sent THEN start query is updated and end query remains empty`() = runTest {
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
    fun `GIVEN current input is changed to END WHEN SelectPlace event is sent THEN end query is updated and start query remains empty`() = runTest {
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
    fun `GIVEN both queries are empty WHEN SelectCurrentLocation event is sent THEN start query is set to current location and end query remains empty`() = runTest {
        sut.state.test {
            awaitItem()

            sut.handleEvent(RoutePlannerUiEvent.SelectCurrentLocation)

            val state = awaitItem()
            assertTrue(state.startQuery.isCurrentLocation)
            assertTrue(state.endQuery.isEmpty)
        }
    }

    @Test
    fun `GIVEN start query is already filled WHEN SelectCurrentLocation event is sent THEN end query is set to current location`() = runTest {
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
    fun `GIVEN both queries are empty WHEN SelectRecentPlace event is sent THEN start query is filled with the recent place`() = runTest {
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
    fun `GIVEN start query has been filled WHEN ClearStartDestinationField event is sent THEN start query becomes empty`() = runTest {
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
    fun `GIVEN both start and end queries are filled WHEN ChangeDestinations event is sent THEN start and end queries are swapped`() = runTest {
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
    fun `GIVEN recent searches exist WHEN ClearRecentSearches event is sent THEN repository clear method is called once`() = runTest {
        sut.handleEvent(RoutePlannerUiEvent.ClearRecentSearches)

        advanceUntilIdle()

        assertEquals(1, recentSearchRepository.clearRecentSearchesCalls)
    }

    @Test
    fun `GIVEN repository has two recent queries WHEN recentSearchQueriesUiState flow is collected THEN emits Loading then Success with both queries`() = runTest {
        sut.recentSearchQueriesUiState.test {
            assertEquals(RecentSearchQueriesUiState.Loading, awaitItem())

            val successState = awaitItem() as RecentSearchQueriesUiState.Success
            assertEquals(2, successState.recentQueries.size)
            assertEquals("Barcelona", successState.recentQueries[0].name)
            assertEquals("Madrid", successState.recentQueries[1].name)
        }
    }

    @Test
    fun `GIVEN route is enabled with both queries filled WHEN start query is cleared THEN isRouteEnabled reverts to false`() = runTest {
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
