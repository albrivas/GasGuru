package com.gasguru.core.domain.search

import app.cash.turbine.test
import com.gasguru.core.domain.fakes.FakeOfflineRecentSearchRepository
import com.gasguru.core.model.data.RecentSearchQuery
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetRecentSearchQueryUseCaseTest {

    private lateinit var sut: GetRecentSearchQueryUseCase
    private lateinit var fakeSearchRepository: FakeOfflineRecentSearchRepository

    @BeforeTest
    fun setUp() {
        fakeSearchRepository = FakeOfflineRecentSearchRepository()
        sut = GetRecentSearchQueryUseCase(recentSearchRepository = fakeSearchRepository)
    }

    @Test
    fun returnsEmptyListInitially() = runTest {
        sut().test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun returnsStoredQueries() = runTest {
        val queries = listOf(
            RecentSearchQuery(name = "Madrid", id = "q1"),
            RecentSearchQuery(name = "Barcelona", id = "q2"),
        )
        fakeSearchRepository.setRecentSearchQueries(queries)

        sut().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("Madrid", result.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun respectsLimitParam() = runTest {
        val queries = (1..5).map { RecentSearchQuery(name = "Place $it", id = "q$it") }
        fakeSearchRepository.setRecentSearchQueries(queries)

        sut(limit = 3).test {
            val result = awaitItem()
            assertEquals(3, result.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
