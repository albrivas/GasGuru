package com.albrivas.fuelpump.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.albrivas.fuelpump.core.data.repository.FuelStationRepository
import com.albrivas.fuelpump.core.data.repository.OfflineFuelStationRepository
import com.albrivas.fuelpump.core.data.repository.OfflineUserDataRepository
import com.albrivas.fuelpump.core.data.repository.UserDataRepository
import com.albrivas.fuelpump.core.network.datasource.RemoteDataSource
import com.albrivas.fuelpump.core.network.datasource.RemoteDataSourceImp

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindsFuelStationRepository(
        fuelStationRepository: OfflineFuelStationRepository
    ): FuelStationRepository

    @Binds
    fun bindRemoteDataSourceImp(
        remoteDataSource: RemoteDataSourceImp
    ): RemoteDataSource

    @Binds
    fun bindUserDataRepository(
        userDataRepository: OfflineUserDataRepository
    ): UserDataRepository

}
