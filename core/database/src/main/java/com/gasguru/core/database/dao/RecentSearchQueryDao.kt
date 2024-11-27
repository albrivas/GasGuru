package com.gasguru.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.gasguru.core.database.model.RecentSearchQueryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentSearchQueryDao {
    @Query(value = "SELECT * FROM `recent-search-queries` ORDER BY id DESC LIMIT :limit")
    fun getRecentSearchQueryEntities(limit: Int): Flow<List<RecentSearchQueryEntity>>

    @Upsert
    suspend fun insertOrReplaceRecentSearchQuery(recentPlace: RecentSearchQueryEntity)

    @Query(value = "DELETE FROM `recent-search-queries`")
    suspend fun clearRecentSearchQueries()
}
