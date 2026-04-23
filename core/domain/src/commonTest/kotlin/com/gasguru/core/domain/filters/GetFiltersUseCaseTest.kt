package com.gasguru.core.domain.filters

import app.cash.turbine.test
import com.gasguru.core.domain.fakes.FakeFilterRepository
import com.gasguru.core.model.data.Filter
import com.gasguru.core.model.data.FilterType
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetFiltersUseCaseTest {

    private lateinit var sut: GetFiltersUseCase
    private lateinit var fakeFilterRepository: FakeFilterRepository

    @BeforeTest
    fun setUp() {
        fakeFilterRepository = FakeFilterRepository()
        sut = GetFiltersUseCase(filterRepository = fakeFilterRepository)
    }

    @Test
    fun returnsEmptyFiltersInitially() = runTest {
        sut().test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun returnsFiltersWhenPresent() = runTest {
        val filters = listOf(
            Filter(type = FilterType.BRAND, selection = listOf("REPSOL")),
        )
        fakeFilterRepository.setFilters(filters)

        sut().test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals(FilterType.BRAND, result.first().type)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun emitsUpdatesWhenFiltersChange() = runTest {
        sut().test {
            assertTrue(awaitItem().isEmpty())

            fakeFilterRepository.setFilters(
                listOf(Filter(type = FilterType.BRAND, selection = listOf("CEPSA"))),
            )

            val updated = awaitItem()
            assertEquals(1, updated.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
