/*
 * File: DaoModule.kt
 * Project: FuelPump
 * Module: FuelPump.core.database.main
 * Last modified: 1/4/23, 9:21 PM
 *
 * Created by albertorivas on 1/5/23, 12:13 AM
 * Copyright © 2023 Alberto Rivas. All rights reserved.
 *
 */

package com.albrivas.fuelpump.core.database.di

import com.albrivas.fuelpump.core.database.AppDatabase
import com.albrivas.fuelpump.core.database.dao.FuelStationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    fun provideFuelStationDao(appDatabase: AppDatabase): FuelStationDao {
        return appDatabase.fuelStationDao()
    }
}