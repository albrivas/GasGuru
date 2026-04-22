package com.gasguru.core.domain.search

import com.gasguru.core.domain.fakes.FakeOfflineRecentSearchRepository
import com.gasguru.core.model.data.RecentSearchQuery
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ClearRecentSearchQueriesUseCaseTest {

    private lateinit var sut: ClearRecentSearchQueriesUseCase
    private lateinit var fakeSearchRepository: FakeOfflineRecentSearchRepository

    @BeforeTest
    fun setUp() {
        fakeSearchRepository = FakeOfflineRecentSearchRepository(
            initialQueries = listOf(
                RecentSearchQuery(name = "Madrid", id = "q1"),
                RecentSearchQuery(name = "Barcelona", id = "q2"),
            ),
        )
        sut = ClearRecentSearchQueriesUseCase(recentSearchRepository = fakeSearchRepository)
    }

    @Test
    fun clearsDelegatesToRepository() = runTest {
        sut()

        assertEquals(1, fakeSearchRepository.clearRecentSearchesCalls)
    }

    @Test
    fun clearingMultipleTimesIncrementsCount() = runTest {
        sut()
        sut()

        assertEquals(2, fakeSearchRepository.clearRecentSearchesCalls)
    }
}
