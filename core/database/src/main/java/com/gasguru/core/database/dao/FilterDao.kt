package com.gasguru.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gasguru.core.database.model.FilterEntity
import com.gasguru.core.model.data.FilterType
import kotlinx.coroutines.flow.Flow

@Dao
interface FilterDao {
    @Query("SELECT * FROM filter")
    fun getFilters(): Flow<List<FilterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilter(filter: FilterEntity)

    @Query("UPDATE filter SET selection = :newSelection WHERE type = :filterType")
    suspend fun updateFilterByType(filterType: FilterType, newSelection: List<String>)

    @Query("SELECT COUNT(*) FROM filter WHERE type = :filterType")
    suspend fun isFilterExist(filterType: FilterType): Int
}
