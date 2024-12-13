package com.gasguru.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.gasguru.core.database.GasGuruDatabase
import com.gasguru.core.database.model.FilterEntity
import com.gasguru.core.model.data.FilterType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class FiltersDaoTest {

    private lateinit var filterDao: FilterDao
    private lateinit var db: GasGuruDatabase

    @BeforeEach
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            GasGuruDatabase::class.java
        ).build()
        filterDao = db.filterDao()
    }

    @AfterEach
    fun closeDb() = db.close()


    @Test
    @DisplayName("Retrieves all filters")
    fun getAllFilters() = runTest {
        val filter =
            FilterEntity(type = FilterType.BRAND, selection = listOf("Repsol"))
        filterDao.insertFilter(filter)

        val result = filterDao.getFilters().first()

        Assertions.assertEquals(result.first().type, filter.type)
    }

    @Test
    @DisplayName("Update filter")
    fun updateFilter() = runTest {
        val filter = FilterEntity(
            type = FilterType.BRAND,
            selection = listOf("Repsol")
        )
        filterDao.insertFilter(filter)
        filterDao.updateFilterByType(filterType = FilterType.BRAND, newSelection = listOf("Cepsa"))

        val result = filterDao.getFilters().first()

        Assertions.assertEquals(result.first().selection, listOf("Cepsa"))
    }

    @Test
    @DisplayName("Filter exist")
    fun filterExist() = runTest {
        val filter = FilterEntity(
            type = FilterType.BRAND,
            selection = listOf("Repsol")
        )
        filterDao.insertFilter(filter)

        val result = filterDao.isFilterExist(filterType = FilterType.BRAND)

        Assertions.assertEquals(result, 1)
    }

    @Test
    @DisplayName("Filter not exist")
    fun filterNotExist() = runTest {
        val result = filterDao.isFilterExist(filterType = FilterType.BRAND)

        Assertions.assertEquals(result, 0)
    }

    @Test
    @DisplayName("Empty filters")
    fun emptyFilters() = runTest {
        val result = filterDao.getFilters()

        Assertions.assertEquals(result.first(), emptyList<FilterEntity>())
    }
}