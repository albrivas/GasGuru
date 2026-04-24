package com.gasguru.core.data.di

import com.gasguru.core.common.KoinQualifiers
import com.gasguru.core.data.repository.geocoder.GeocoderAddress
import com.gasguru.core.data.repository.geocoder.GeocoderAddressImpl
import com.gasguru.core.data.repository.location.LocationTracker
import com.gasguru.core.data.repository.location.LocationTrackerRepository
import com.gasguru.core.data.repository.places.PlacesRepository
import com.gasguru.core.data.repository.places.PlacesRepositoryImp
import com.gasguru.core.data.repository.route.RoutesRepository
import com.gasguru.core.data.repository.route.RoutesRepositoryImpl
import com.gasguru.core.data.util.ConnectivityManagerNetworkMonitor
import com.gasguru.core.data.util.NetworkMonitor
import com.gasguru.core.network.datasource.PlacesDataSource
import com.gasguru.core.network.datasource.PlacesDataSourceImp
import com.gasguru.core.network.datasource.RoutesDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val androidDataModule = module {
    single<LocationTracker> {
        LocationTrackerRepository(
            locationClient = get(),
            context = androidContext(),
        )
    }

    single<GeocoderAddress> {
        GeocoderAddressImpl(
            context = androidContext(),
            ioDispatcher = get(named(KoinQualifiers.IO_DISPATCHER)),
        )
    }

    single<NetworkMonitor> {
        ConnectivityManagerNetworkMonitor(
            context = androidContext(),
            ioDispatcher = get(named(KoinQualifiers.IO_DISPATCHER)),
        )
    }

    single<PlacesDataSource> { PlacesDataSourceImp(placesClient = get()) }

    single<PlacesRepository> { PlacesRepositoryImp(placesDataSource = get()) }

    single<RoutesRepository> {
        RoutesRepositoryImpl(
            routesDataSource = get(),
            ioDispatcher = get(named(KoinQualifiers.IO_DISPATCHER)),
            defaultDispatcher = get(named(KoinQualifiers.DEFAULT_DISPATCHER)),
        )
    }
}
