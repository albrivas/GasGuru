package com.gasguru.core.network.di

import android.content.Context
import com.gasguru.core.network.BuildConfig
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlacesModule {

    private fun providePlaces(@ApplicationContext context: Context) {
        Places.initialize(context, BuildConfig.googleApiKey)
    }

    @Provides
    @Singleton
    fun providePlacesClient(@ApplicationContext context: Context): PlacesClient {
        providePlaces(context)
        return Places.createClient(context)
    }
}
