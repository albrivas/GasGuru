package com.gasguru.core.data.di

import android.content.Context
import com.gasguru.core.data.BuildConfig
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ProvidesDataModule {

    @Provides
    @Singleton
    fun providesFusedLocationProviderClient(
        @ApplicationContext application: Context
    ): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    @Provides
    @Singleton
    @Named("google_api_key")
    fun provideGoogleApiKey(): String = BuildConfig.googleApiKey
}
