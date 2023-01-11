/*
 * File: AppDatabase.kt
 * Project: FuelPump
 * Module: FuelPump.core.database.main
 * Last modified: 1/4/23, 9:20 PM
 *
 * Created by albertorivas on 1/5/23, 12:13 AM
 * Copyright © 2023 Alberto Rivas. All rights reserved.
 *
 */

package com.albrivas.fuelpump.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.albrivas.fuelpump.core.database.dao.FuelStationDao
import com.albrivas.fuelpump.core.database.model.FuelStationEntity

@Database(entities = [FuelStationEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fuelStationDao(): FuelStationDao
}
