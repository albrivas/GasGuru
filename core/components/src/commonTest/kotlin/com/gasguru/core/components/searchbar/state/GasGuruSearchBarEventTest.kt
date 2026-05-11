package com.gasguru.core.components.searchbar.state

import com.gasguru.core.model.data.SearchPlace
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("GasGuruSearchBarEvent")
class GasGuruSearchBarEventTest {

    @Test
    @DisplayName(
        """
        GIVEN a search query string
        WHEN UpdateSearchQuery event is created
        THEN it carries the exact query
        """,
    )
    fun updateSearchQueryCarriesQuery() {
        val query = "gasolinera"

        val event = GasGuruSearchBarEvent.UpdateSearchQuery(query = query)

        assertEquals(query, event.query)
    }

    @Test
    @DisplayName(
        """
        GIVEN two UpdateSearchQuery events with the same query
        WHEN compared
        THEN they are equal
        """,
    )
    fun updateSearchQueryEquality() {
        val eventA = GasGuruSearchBarEvent.UpdateSearchQuery(query = "Madrid")
        val eventB = GasGuruSearchBarEvent.UpdateSearchQuery(query = "Madrid")

        assertEquals(eventA, eventB)
    }

    @Test
    @DisplayName(
        """
        GIVEN the user wants to clear recent searches
        WHEN ClearRecentSearches event is created
        THEN it equals another ClearRecentSearches instance
        """,
    )
    fun clearRecentSearchesEquality() {
        val eventA: GasGuruSearchBarEvent = GasGuruSearchBarEvent.ClearRecentSearches
        val eventB: GasGuruSearchBarEvent = GasGuruSearchBarEvent.ClearRecentSearches

        assertEquals(eventA, eventB)
    }

    @Test
    @DisplayName(
        """
        GIVEN a selected place
        WHEN InsertRecentSearch event is created
        THEN it carries the exact search place
        """,
    )
    fun insertRecentSearchCarriesPlace() {
        val place = SearchPlace(name = "Valencia", id = "42")

        val event = GasGuruSearchBarEvent.InsertRecentSearch(searchQuery = place)

        assertEquals(place.name, event.searchQuery.name)
        assertEquals(place.id, event.searchQuery.id)
    }

    @Test
    @DisplayName(
        """
        GIVEN two InsertRecentSearch events with the same place
        WHEN compared
        THEN they are equal
        """,
    )
    fun insertRecentSearchEquality() {
        val place = SearchPlace(name = "Bilbao", id = "7")

        val eventA = GasGuruSearchBarEvent.InsertRecentSearch(searchQuery = place)
        val eventB = GasGuruSearchBarEvent.InsertRecentSearch(searchQuery = place)

        assertEquals(eventA, eventB)
    }
}
