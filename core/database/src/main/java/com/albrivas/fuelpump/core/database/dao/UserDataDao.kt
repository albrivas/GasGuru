package com.albrivas.fuelpump.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.albrivas.fuelpump.core.database.model.UserDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDataDao {
    @Query("SELECT * FROM `user-data` LIMIT 1")
    fun getUserData(): Flow<UserDataEntity>

    @Upsert
    suspend fun insertUserData(user: UserDataEntity)
}