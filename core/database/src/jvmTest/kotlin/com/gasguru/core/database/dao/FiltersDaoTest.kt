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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName(
    """
    GIVEN a FilterDao
    WHEN performing filter operations
    THEN the results are correct
    """
)
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
        GIVEN a filter is inserted
        WHEN getting all filters
        THEN returns that filter
        """
    )
    fun getAllFilters() = runTest {
        val filter = FilterEntity(type = FilterType.BRAND, selection = listOf("Repsol"))
        filterDao.insertFilter(filter)

        val result = filterDao.getFilters().first()

        assertEquals(result.first().type, filter.type)
    }

    @Test
    @DisplayName(
        """
        GIVEN a filter is inserted with one selection
        WHEN updating the filter selection
        THEN the new selection is persisted
        """
    )
    fun updateFilter() = runTest {
        val filter = FilterEntity(type = FilterType.BRAND, selection = listOf("Repsol"))
        filterDao.insertFilter(filter)
        filterDao.updateFilterByType(filterType = FilterType.BRAND, newSelection = listOf("Cepsa"))

        val result = filterDao.getFilters().first()

        assertEquals(result.first().selection, listOf("Cepsa"))
    }

    @Test
    @DisplayName(
        """
        GIVEN a filter was inserted
        WHEN checking if it exists
        THEN returns count of 1
        """
    )
    fun filterExist() = runTest {
        val filter = FilterEntity(type = FilterType.BRAND, selection = listOf("Repsol"))
        filterDao.insertFilter(filter)

        val result = filterDao.isFilterExist(filterType = FilterType.BRAND)

        assertEquals(result, 1)
    }

    @Test
    @DisplayName(
        """
        GIVEN empty database
        WHEN checking if filter exists
        THEN returns count of 0
        """
    )
    fun filterNotExist() = runTest {
        val result = filterDao.isFilterExist(filterType = FilterType.BRAND)

        assertEquals(result, 0)
    }

    @Test
    @DisplayName(
        """
        GIVEN empty database
        WHEN getting filters
        THEN returns empty list
        """
    )
    fun emptyFilters() = runTest {
        val result = filterDao.getFilters()

        assertEquals(result.first(), emptyList<FilterEntity>())
    }

    @Test
    @DisplayName(
        """
        GIVEN no filter exists for the given type
        WHEN updating filter by type
        THEN no filter is created
        """
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
        """
        GIVEN one filter of type BRAND is inserted
        WHEN getting all filters
        THEN returns one filter with correct type
        """
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
        """
        GIVEN a filter is inserted then another with same type is inserted
        WHEN getting all filters
        THEN only the second filter exists with its selection
        """
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
        """
        GIVEN a filter with empty selection is inserted
        WHEN getting all filters
        THEN the empty selection is persisted
        """
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
        """
        GIVEN a filter was inserted and updated
        WHEN checking if it exists
        THEN returns count of 1
        """
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
