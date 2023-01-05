/*
 * File: FuelStationDao.kt
 * Project: FuelPump
 * Module: FuelPump.core.database.main
 * Last modified: 1/5/23, 12:05 AM
 *
 * Created by albertorivas on 1/5/23, 12:13 AM
 * Copyright © 2023 Alberto Rivas. All rights reserved.
 *
 */

package com.albrivas.fuelpump.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.albrivas.fuelpump.core.database.model.FuelStationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelStationDao {
    @Query("SELECT * FROM `fuel-station` ORDER BY locality")
    fun getFuelStations(): Flow<List<FuelStationEntity>>

    @Insert
    suspend fun insertFuelStation(items: List<FuelStationEntity>)
}