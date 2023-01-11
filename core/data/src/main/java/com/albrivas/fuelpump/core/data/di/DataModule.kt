package com.albrivas.fuelpump.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.albrivas.fuelpump.core.data.repository.FuelStationRepository
import com.albrivas.fuelpump.core.data.repository.OfflineFuelStationRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsTaskRepository(
        fuelStationRepository: OfflineFuelStationRepository
    ): FuelStationRepository
}
