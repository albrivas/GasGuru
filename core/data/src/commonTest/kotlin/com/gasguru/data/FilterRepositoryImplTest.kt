package com.gasguru.data

import app.cash.turbine.test
import com.gasguru.core.data.repository.filter.FilterRepositoryImpl
import com.gasguru.core.database.model.FilterEntity
import com.gasguru.core.model.data.FilterType
import com.gasguru.data.fakes.FakeFilterDao
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class FilterRepositoryImplTest {

    private lateinit var fakeFilterDao: FakeFilterDao
    private lateinit var sut: FilterRepositoryImpl

    @BeforeTest
    fun setUp() {
        fakeFilterDao = FakeFilterDao()
        sut = FilterRepositoryImpl(dao = fakeFilterDao)
    }

    // region insertOrUpdateFilter

    @Test
    fun insertOrUpdateFilter_whenFilterDoesNotExist_insertsNewFilter() = runTest {
        sut.insertOrUpdateFilter(filterType = FilterType.NEARBY, selection = listOf("10"))

        sut.getFilters.test {
            val filters = awaitItem()
            assertEquals(1, filters.size)
            assertEquals(FilterType.NEARBY, filters.first().type)
            assertEquals(listOf("10"), filters.first().selection)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertOrUpdateFilter_whenFilterExists_updatesSelectionOnly() = runTest {
        fakeFilterDao.insertFilter(FilterEntity(type = FilterType.NEARBY, selection = listOf("5")))

        sut.insertOrUpdateFilter(filterType = FilterType.NEARBY, selection = listOf("20"))

        sut.getFilters.test {
            val filters = awaitItem()
            assertEquals(1, filters.size)
            assertEquals(listOf("20"), filters.first().selection)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertOrUpdateFilter_withTwoDifferentTypes_insertsBoth() = runTest {
        sut.insertOrUpdateFilter(filterType = FilterType.NEARBY, selection = listOf("10"))
        sut.insertOrUpdateFilter(filterType = FilterType.SCHEDULE, selection = listOf("open"))

        sut.getFilters.test {
            val filters = awaitItem()
            assertEquals(2, filters.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // endregion

    // region getFilters

    @Test
    fun getFilters_withExistingFilters_returnsMappedList() = runTest {
        fakeFilterDao.insertFilter(FilterEntity(type = FilterType.NEARBY, selection = listOf("10")))
        fakeFilterDao.insertFilter(FilterEntity(type = FilterType.SCHEDULE, selection = listOf("open")))

        sut.getFilters.test {
            val filters = awaitItem()
            assertEquals(2, filters.size)
            assertEquals(FilterType.NEARBY, filters[0].type)
            assertEquals(FilterType.SCHEDULE, filters[1].type)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getFilters_whenEmpty_returnsEmptyList() = runTest {
        sut.getFilters.test {
            val filters = awaitItem()
            assertEquals(0, filters.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // endregion
}
