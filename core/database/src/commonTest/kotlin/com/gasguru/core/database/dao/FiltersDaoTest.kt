package com.gasguru.core.database.dao

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.gasguru.core.database.GasGuruDatabase
import com.gasguru.core.database.model.FilterEntity
import com.gasguru.core.model.data.FilterType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FiltersDaoTest {

    private lateinit var filterDao: FilterDao
    private lateinit var db: GasGuruDatabase

    @BeforeTest
    fun createDb() {
        db = Room.databaseBuilder<GasGuruDatabase>(name = ":memory:")
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        filterDao = db.filterDao()
    }

    @AfterTest
    fun closeDb() = db.close()

    @Test
    fun getAllFilters() = runTest {
        val filter = FilterEntity(type = FilterType.BRAND, selection = listOf("Repsol"))
        filterDao.insertFilter(filter)

        val result = filterDao.getFilters().first()

        assertEquals(result.first().type, filter.type)
    }

    @Test
    fun updateFilter() = runTest {
        val filter = FilterEntity(type = FilterType.BRAND, selection = listOf("Repsol"))
        filterDao.insertFilter(filter)
        filterDao.updateFilterByType(filterType = FilterType.BRAND, newSelection = listOf("Cepsa"))

        val result = filterDao.getFilters().first()

        assertEquals(result.first().selection, listOf("Cepsa"))
    }

    @Test
    fun filterExist() = runTest {
        val filter = FilterEntity(type = FilterType.BRAND, selection = listOf("Repsol"))
        filterDao.insertFilter(filter)

        val result = filterDao.isFilterExist(filterType = FilterType.BRAND)

        assertEquals(result, 1)
    }

    @Test
    fun filterNotExist() = runTest {
        val result = filterDao.isFilterExist(filterType = FilterType.BRAND)

        assertEquals(result, 0)
    }

    @Test
    fun emptyFilters() = runTest {
        val result = filterDao.getFilters()

        assertEquals(result.first(), emptyList<FilterEntity>())
    }

    @Test
    fun updateFilterByType_whenFilterDoesNotExist_doesNotCreateFilter() = runTest {
        filterDao.updateFilterByType(
            filterType = FilterType.BRAND,
            newSelection = listOf("Repsol"),
        )

        val result = filterDao.getFilters().first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun getFilters_withMultipleFilterTypes_returnsAll() = runTest {
        val brandFilter = FilterEntity(type = FilterType.BRAND, selection = listOf("Repsol"))
        filterDao.insertFilter(brandFilter)

        val allFilters = filterDao.getFilters().first()

        assertEquals(1, allFilters.size)
        assertEquals(FilterType.BRAND, allFilters.first().type)
    }

    @Test
    fun insertFilter_duplicateType_replacesExistingFilter() = runTest {
        val first = FilterEntity(type = FilterType.BRAND, selection = listOf("Repsol"))
        val second = FilterEntity(type = FilterType.BRAND, selection = listOf("Cepsa", "Galp"))
        filterDao.insertFilter(first)
        filterDao.insertFilter(second)

        val result = filterDao.getFilters().first()

        assertEquals(1, result.size)
        assertEquals(listOf("Cepsa", "Galp"), result.first().selection)
    }

    @Test
    fun insertFilter_withEmptySelection_persistsEmptyList() = runTest {
        val filter = FilterEntity(type = FilterType.BRAND, selection = emptyList())
        filterDao.insertFilter(filter)

        val result = filterDao.getFilters().first()

        assertEquals(1, result.size)
        assertTrue(result.first().selection.isEmpty())
    }

    @Test
    fun isFilterExist_afterUpdate_returnsOne() = runTest {
        filterDao.insertFilter(FilterEntity(type = FilterType.BRAND, selection = listOf("Repsol")))
        filterDao.updateFilterByType(
            filterType = FilterType.BRAND,
            newSelection = listOf("Cepsa", "Galp", "BP"),
        )

        val count = filterDao.isFilterExist(filterType = FilterType.BRAND)

        assertEquals(1, count)
    }
}
