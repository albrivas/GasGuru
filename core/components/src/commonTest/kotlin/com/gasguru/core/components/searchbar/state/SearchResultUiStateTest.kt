package com.gasguru.core.components.searchbar.state

import com.gasguru.core.model.data.SearchPlace
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("SearchResultUiState")
class SearchResultUiStateTest {

    @Test
    @DisplayName(
        """
        GIVEN no query entered
        WHEN EmptyQuery state is created
        THEN it is an instance of SearchResultUiState
        """,
    )
    fun emptyQueryIsSearchResultUiState() {
        val state: SearchResultUiState = SearchResultUiState.EmptyQuery

        assertInstanceOf(SearchResultUiState::class.java, state)
    }

    @Test
    @DisplayName(
        """
        GIVEN a search that returned no results
        WHEN EmptySearchResult state is created
        THEN it equals another EmptySearchResult instance
        """,
    )
    fun emptySearchResultEquality() {
        val stateA: SearchResultUiState = SearchResultUiState.EmptySearchResult
        val stateB: SearchResultUiState = SearchResultUiState.EmptySearchResult

        assertEquals(stateA, stateB)
    }

    @Test
    @DisplayName(
        """
        GIVEN a list of matching places
        WHEN Success state is created
        THEN it carries the exact places list
        """,
    )
    fun successCarriesPlaces() {
        val places = listOf(
            SearchPlace(name = "Barcelona", id = "1"),
            SearchPlace(name = "Madrid", id = "2"),
        )

        val state = SearchResultUiState.Success(places = places)

        assertEquals(2, state.places.size)
        assertEquals("Barcelona", state.places[0].name)
        assertEquals("Madrid", state.places[1].name)
    }

    @Test
    @DisplayName(
        """
        GIVEN a search with no matching places
        WHEN Success state is created with empty list
        THEN places list is empty
        """,
    )
    fun successWithEmptyListHasNoPlaces() {
        val state = SearchResultUiState.Success(places = emptyList())

        assertTrue(state.places.isEmpty())
    }

    @Test
    @DisplayName(
        """
        GIVEN a search in progress
        WHEN Loading state is created
        THEN it equals another Loading instance
        """,
    )
    fun loadingEquality() {
        val stateA: SearchResultUiState = SearchResultUiState.Loading
        val stateB: SearchResultUiState = SearchResultUiState.Loading

        assertEquals(stateA, stateB)
    }

    @Test
    @DisplayName(
        """
        GIVEN a failed search request
        WHEN LoadFailed state is created
        THEN it equals another LoadFailed instance
        """,
    )
    fun loadFailedEquality() {
        val stateA: SearchResultUiState = SearchResultUiState.LoadFailed
        val stateB: SearchResultUiState = SearchResultUiState.LoadFailed

        assertEquals(stateA, stateB)
    }

    @Test
    @DisplayName(
        """
        GIVEN two Success states with same places
        WHEN compared
        THEN they are equal
        """,
    )
    fun successStatesWithSamePlacesAreEqual() {
        val places = listOf(SearchPlace(name = "Sevilla", id = "3"))

        val stateA = SearchResultUiState.Success(places = places)
        val stateB = SearchResultUiState.Success(places = places)

        assertEquals(stateA, stateB)
    }
}
