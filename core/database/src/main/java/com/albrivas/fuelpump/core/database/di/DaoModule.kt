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

import com.albrivas.fuelpump.core.database.FuelPumpDatabase
import com.albrivas.fuelpump.core.database.dao.FuelStationDao
import com.albrivas.fuelpump.core.database.dao.RecentSearchQueryDao
import com.albrivas.fuelpump.core.database.dao.UserDataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class DaoModule {
    @Provides
    fun provideFuelStationDao(appDatabase: FuelPumpDatabase): FuelStationDao =
        appDatabase.fuelStationDao()

    @Provides
    fun provideUserDataDao(appDatabase: FuelPumpDatabase): UserDataDao =
        appDatabase.userDataDao()

    @Provides
    fun provideRecentDao(appDatabase: FuelPumpDatabase): RecentSearchQueryDao =
        appDatabase.recentDao()
}