package com.gasguru.core.domain.places

import app.cash.turbine.test
import com.gasguru.core.domain.fakes.FakePlacesRepository
import com.gasguru.core.model.data.SearchPlace
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetPlacesUseCaseTest {

    private lateinit var sut: GetPlacesUseCase
    private lateinit var fakePlacesRepository: FakePlacesRepository

    @BeforeTest
    fun setUp() {
        fakePlacesRepository = FakePlacesRepository()
        sut = GetPlacesUseCase(placesRepository = fakePlacesRepository)
    }

    @Test
    fun returnsEmptyListWhenNoMatchesFound() = runTest {
        sut(query = "nonexistent").test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun returnsPlacesForQuery() = runTest {
        val places = listOf(
            SearchPlace(name = "Madrid", id = "m1"),
            SearchPlace(name = "Malaga", id = "m2"),
        )
        fakePlacesRepository.setPlacesForQuery(query = "Ma", places = places)

        sut(query = "Ma").test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("Madrid", result.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun tracksRequestedQueries() = runTest {
        sut(query = "Madrid").test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(listOf("Madrid"), fakePlacesRepository.requestedQueries)
    }
}
