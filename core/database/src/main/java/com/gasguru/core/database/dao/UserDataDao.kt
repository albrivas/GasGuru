package com.gasguru.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.gasguru.core.database.model.FavoriteStationCrossRef
import com.gasguru.core.database.model.UserDataEntity
import com.gasguru.core.database.model.UserWithFavoriteStationsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDataDao {
    @Query("SELECT id FROM `user-data` LIMIT 1")
    suspend fun getUserId(): Long

    @Query("SELECT * FROM `user-data` LIMIT 1")
    fun getUserData(): Flow<UserDataEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserData(userData: UserDataEntity)

    @Transaction
    @Query("SELECT * FROM `user-data` WHERE id = :userId")
    fun getUserWithFavoriteStations(userId: Long): Flow<UserWithFavoriteStationsEntity>

    @Upsert
    suspend fun insertFavoriteStationCrossRef(crossRef: FavoriteStationCrossRef)

    @Delete
    suspend fun deleteFavoriteStationCrossRef(crossRef: FavoriteStationCrossRef)
}
