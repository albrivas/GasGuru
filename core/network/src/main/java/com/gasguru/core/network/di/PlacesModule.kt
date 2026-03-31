package com.gasguru.core.network.di

import com.gasguru.core.network.BuildConfig
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val placesModule = module {
    single<PlacesClient> {
        Places.initialize(androidContext(), BuildConfig.googleApiKey)
        Places.createClient(androidContext())
    }
}
