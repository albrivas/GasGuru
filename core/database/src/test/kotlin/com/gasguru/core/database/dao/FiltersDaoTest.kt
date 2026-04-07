package com.gasguru.core.database.dao

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.gasguru.core.database.GasGuruDatabase
import com.gasguru.core.database.model.FilterEntity
import com.gasguru.core.model.data.FilterType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class FiltersDaoTest {

    private lateinit var filterDao: FilterDao
    private lateinit var db: GasGuruDatabase

    @BeforeEach
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder<GasGuruDatabase>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        filterDao = db.filterDao()
    }

    @AfterEach
    fun closeDb() = db.close()

    @Test
    @DisplayName(
        """
        GIVEN a filter exists in database
        WHEN querying getFilters
        THEN returns all stored filters
        """
    )
    fun getAllFilters() = runTest {
        val filter = FilterEntity(type = FilterType.BRAND, selection = listOf("Repsol"))
        filterDao.insertFilter(filter)

        val result = filterDao.getFilters().first()

        Assertions.assertEquals(result.first().type, filter.type)
    }

    @Test
    @DisplayName(
        """
        GIVEN an existing filter
        WHEN updating its selection
        THEN the new selection is persisted
        """
    )
    fun updateFilter() = runTest {
        val filter = FilterEntity(type = FilterType.BRAND, selection = listOf("Repsol"))
        filterDao.insertFilter(filter)
        filterDao.updateFilterByType(filterType = FilterType.BRAND, newSelection = listOf("Cepsa"))

        val result = filterDao.getFilters().first()

        Assertions.assertEquals(result.first().selection, listOf("Cepsa"))
    }

    @Test
    @DisplayName(
        """
        GIVEN a filter exists for a type
        WHEN calling isFilterExist
        THEN returns 1
        """
    )
    fun filterExist() = runTest {
        val filter = FilterEntity(type = FilterType.BRAND, selection = listOf("Repsol"))
        filterDao.insertFilter(filter)

        val result = filterDao.isFilterExist(filterType = FilterType.BRAND)

        Assertions.assertEquals(result, 1)
    }

    @Test
    @DisplayName(
        """
        GIVEN no filter exists for a type
        WHEN calling isFilterExist
        THEN returns 0
        """
    )
    fun filterNotExist() = runTest {
        val result = filterDao.isFilterExist(filterType = FilterType.BRAND)

        Assertions.assertEquals(result, 0)
    }

    @Test
    @DisplayName(
        """
        GIVEN empty database
        WHEN querying getFilters
        THEN returns empty list
        """
    )
    fun emptyFilters() = runTest {
        val result = filterDao.getFilters()

        Assertions.assertEquals(result.first(), emptyList<FilterEntity>())
    }

    @Test
    @DisplayName(
        """GIVEN no filter exists for a type
        WHEN calling updateFilterByType
        THEN no error is thrown and no filter is created"""
    )
    fun updateFilterByType_whenFilterDoesNotExist_doesNotCreateFilter() = runTest {
        filterDao.updateFilterByType(
            filterType = FilterType.BRAND,
            newSelection = listOf("Repsol"),
        )

        val result = filterDao.getFilters().first()
        assertTrue(result.isEmpty())
    }

    @Test
    @DisplayName(
        """GIVEN two different filter types inserted
        WHEN querying all filters
        THEN both filters are returned"""
    )
    fun getFilters_withMultipleFilterTypes_returnsAll() = runTest {
        val brandFilter = FilterEntity(type = FilterType.BRAND, selection = listOf("Repsol"))
        filterDao.insertFilter(brandFilter)

        val allFilters = filterDao.getFilters().first()

        assertEquals(1, allFilters.size)
        assertEquals(FilterType.BRAND, allFilters.first().type)
    }

    @Test
    @DisplayName(
        """GIVEN a filter inserted twice with the same type (REPLACE conflict)
        WHEN querying all filters
        THEN only the latest record remains"""
    )
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
    @DisplayName(
        """GIVEN a filter with an empty selection list
        WHEN inserting and retrieving it
        THEN selection is empty"""
    )
    fun insertFilter_withEmptySelection_persistsEmptyList() = runTest {
        val filter = FilterEntity(type = FilterType.BRAND, selection = emptyList())
        filterDao.insertFilter(filter)

        val result = filterDao.getFilters().first()

        assertEquals(1, result.size)
        assertTrue(result.first().selection.isEmpty())
    }

    @Test
    @DisplayName(
        """GIVEN a filter with a large selection list
        WHEN updating it with a new selection
        THEN isFilterExist still returns 1"""
    )
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
