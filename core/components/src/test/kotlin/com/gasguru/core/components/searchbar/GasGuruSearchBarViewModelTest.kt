package com.gasguru.core.components.searchbar

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.gasguru.core.components.searchbar.state.GasGuruSearchBarEvent
import com.gasguru.core.components.searchbar.state.SearchResultUiState
import com.gasguru.core.domain.places.GetPlacesUseCase
import com.gasguru.core.domain.search.ClearRecentSearchQueriesUseCase
import com.gasguru.core.domain.search.GetRecentSearchQueryUseCase
import com.gasguru.core.domain.search.InsertRecentSearchQueryUseCase
import com.gasguru.core.model.data.RecentSearchQuery
import com.gasguru.core.model.data.SearchPlace
import com.gasguru.core.testing.CoroutinesTestExtension
import com.gasguru.core.testing.fakes.data.places.FakePlacesRepository
import com.gasguru.core.testing.fakes.data.search.FakeOfflineRecentSearchRepository
import com.gasguru.core.ui.RecentSearchQueriesUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
class GasGuruSearchBarViewModelTest {

    private lateinit var sut: GasGuruSearchBarViewModel
    private lateinit var placesRepository: FakePlacesRepository
    private lateinit var recentSearchRepository: FakeOfflineRecentSearchRepository

    @BeforeEach
    fun setUp() {
        placesRepository = FakePlacesRepository().apply {
            setPlacesForQuery(
                query = "ba",
                places = listOf(
                    SearchPlace(name = "Barcelona", id = "1"),
                    SearchPlace(name = "Barakaldo", id = "2")
                )
            )
            setPlacesForQuery(query = "zz", places = emptyList())
        }

        recentSearchRepository = FakeOfflineRecentSearchRepository(
            initialQueries = listOf(
                RecentSearchQuery("Madrid", "10"),
                RecentSearchQuery("Valencia", "11")
            )
        )

        sut = GasGuruSearchBarViewModel(
            savedStateHandle = SavedStateHandle(),
            getPlacesUseCase = GetPlacesUseCase(placesRepository),
            clearRecentSearchQueriesUseCase = ClearRecentSearchQueriesUseCase(recentSearchRepository),
            insertRecentSearchQueryUseCase = InsertRecentSearchQueryUseCase(recentSearchRepository),
            getRecentSearchQueryUseCase = GetRecentSearchQueryUseCase(recentSearchRepository)
        )
    }

    @Test
    @DisplayName("GIVEN view model WHEN observing search query THEN starts empty")
    fun searchQueryStartsEmpty() = runTest {
        sut.searchQuery.test {
            assertEquals("", awaitItem())
        }
    }

    @Test
    @DisplayName("GIVEN empty query WHEN observing results THEN emits EmptyQuery")
    fun emitsEmptyQueryState() = runTest {
        sut.searchResultUiState.test {
            assertEquals(SearchResultUiState.Loading, awaitItem())
            assertEquals(SearchResultUiState.EmptyQuery, awaitItem())
        }
    }

    @Test
    @DisplayName("GIVEN short query WHEN updating search query THEN emits EmptyQuery")
    fun shortQueryEmitsEmptyQuery() = runTest {
        sut.searchResultUiState.test {
            assertEquals(SearchResultUiState.Loading, awaitItem())
            assertEquals(SearchResultUiState.EmptyQuery, awaitItem())

            sut.handleEvent(GasGuruSearchBarEvent.UpdateSearchQuery("ba"))
            assertTrue(awaitItem() is SearchResultUiState.Success)

            sut.handleEvent(GasGuruSearchBarEvent.UpdateSearchQuery("b"))
            assertEquals(SearchResultUiState.EmptyQuery, awaitItem())
        }
    }

    @Test
    @DisplayName("GIVEN results WHEN updating query THEN emits Success with places")
    fun emitsSuccessWithPlaces() = runTest {
        sut.searchResultUiState.test {
            assertEquals(SearchResultUiState.Loading, awaitItem())
            assertEquals(SearchResultUiState.EmptyQuery, awaitItem())

            sut.handleEvent(GasGuruSearchBarEvent.UpdateSearchQuery("ba"))

            val state = awaitItem() as SearchResultUiState.Success
            assertEquals(2, state.places.size)
            assertEquals("Barcelona", state.places[0].name)
        }
    }

    @Test
    @DisplayName("GIVEN no results WHEN updating query THEN emits EmptySearchResult")
    fun emitsEmptySearchResult() = runTest {
        sut.searchResultUiState.test {
            assertEquals(SearchResultUiState.Loading, awaitItem())
            assertEquals(SearchResultUiState.EmptyQuery, awaitItem())

            sut.handleEvent(GasGuruSearchBarEvent.UpdateSearchQuery("zz"))

            assertEquals(SearchResultUiState.EmptySearchResult, awaitItem())
        }
    }

    @Test
    @DisplayName("GIVEN recent queries WHEN observing state THEN emits Success with queries")
    fun emitsRecentSearchQueries() = runTest {
        sut.recentSearchQueriesUiState.test {
            assertEquals(RecentSearchQueriesUiState.Loading, awaitItem())
            val success = awaitItem() as RecentSearchQueriesUiState.Success
            assertEquals(2, success.recentQueries.size)
            assertEquals("Madrid", success.recentQueries[0].name)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("GIVEN clear event WHEN handling THEN clears recent searches")
    fun clearsRecentSearches() = runTest {
        sut.handleEvent(GasGuruSearchBarEvent.ClearRecentSearches)

        advanceUntilIdle()

        assertEquals(1, recentSearchRepository.clearRecentSearchesCalls)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("GIVEN insert event WHEN handling THEN inserts recent search")
    fun insertsRecentSearch() = runTest {
        val place = SearchPlace(name = "Sevilla", id = "99")
        val expectedQuery = RecentSearchQuery(name = "Sevilla", id = "99")

        sut.handleEvent(GasGuruSearchBarEvent.InsertRecentSearch(place))

        advanceUntilIdle()

        assertTrue(recentSearchRepository.insertedRecentSearches.contains(expectedQuery))
    }
}
