package com.gasguru.core.database.di

import com.gasguru.core.database.GasGuruDatabase
import com.gasguru.core.database.dao.FuelStationDao
import com.gasguru.core.database.dao.RecentSearchQueryDao
import com.gasguru.core.database.dao.UserDataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DaoModule {
    @Provides
    fun provideFuelStationDao(appDatabase: GasGuruDatabase): FuelStationDao =
        appDatabase.fuelStationDao()

    @Provides
    fun provideUserDataDao(appDatabase: GasGuruDatabase): UserDataDao =
        appDatabase.userDataDao()

    @Provides
    fun provideRecentDao(appDatabase: GasGuruDatabase): RecentSearchQueryDao =
        appDatabase.recentDao()
}
