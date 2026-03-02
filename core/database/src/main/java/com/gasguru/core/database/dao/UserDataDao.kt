package com.gasguru.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gasguru.core.database.model.UserDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDataDao {
    @Query("SELECT * FROM `user-data` LIMIT 1")
    fun getUserData(): Flow<UserDataEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserData(userData: UserDataEntity): Long

    @Update
    suspend fun updateUserData(userData: UserDataEntity)
}
