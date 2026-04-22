package com.gasguru.core.domain.search

import com.gasguru.core.domain.fakes.FakeOfflineRecentSearchRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class InsertRecentSearchQueryUseCaseTest {

    private lateinit var sut: InsertRecentSearchQueryUseCase
    private lateinit var fakeSearchRepository: FakeOfflineRecentSearchRepository

    @BeforeTest
    fun setUp() {
        fakeSearchRepository = FakeOfflineRecentSearchRepository()
        sut = InsertRecentSearchQueryUseCase(recentSearchRepository = fakeSearchRepository)
    }

    @Test
    fun insertsSearchQueryWithCorrectPlaceIdAndName() = runTest {
        sut(placeId = "place_123", name = "Madrid")

        assertEquals(1, fakeSearchRepository.insertedRecentSearches.size)
        val inserted = fakeSearchRepository.insertedRecentSearches.first()
        assertEquals("place_123", inserted.id)
        assertEquals("Madrid", inserted.name)
    }

    @Test
    fun insertingMultipleQueriesTracksAll() = runTest {
        sut(placeId = "p1", name = "Madrid")
        sut(placeId = "p2", name = "Barcelona")

        assertEquals(2, fakeSearchRepository.insertedRecentSearches.size)
    }
}
