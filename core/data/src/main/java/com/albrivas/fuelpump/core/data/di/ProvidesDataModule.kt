package com.albrivas.fuelpump.core.data.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProvidesDataModule {

    @Provides
    @Singleton
    fun providesFusedLocationProviderClient(
        @ApplicationContext application: Context
    ): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)
}