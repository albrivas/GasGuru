package com.albrivas.fuelpump.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.albrivas.fuelpump.core.data.repository.FuelStationRepository
import com.albrivas.fuelpump.core.data.repository.OfflineFuelStationRepository
import com.albrivas.fuelpump.core.database.AppDatabase
import com.albrivas.fuelpump.core.database.dao.FuelStationDao
import com.albrivas.fuelpump.core.network.datasource.RemoteDataSource
import com.albrivas.fuelpump.core.network.datasource.RemoteDataSourceImp
import dagger.Provides
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsFuelStationRepository(
        fuelStationRepository: OfflineFuelStationRepository
    ): FuelStationRepository

    @Singleton
    @Binds
    fun bindRemoteDataSourceImp(
        remoteDataSource: RemoteDataSourceImp
    ): RemoteDataSource

}
