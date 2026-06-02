package com.gasguru.core.data.di

import com.gasguru.core.common.KoinQualifiers
import com.gasguru.core.data.repository.geocoder.CLGeocoderAddress
import com.gasguru.core.data.repository.geocoder.GeocoderAddress
import com.gasguru.core.data.repository.location.LocationTracker
import com.gasguru.core.data.repository.location.LocationTrackerIos
import com.gasguru.core.data.repository.places.PlacesRepository
import com.gasguru.core.data.repository.places.PlacesRepositoryIos
import com.gasguru.core.data.util.NWPathMonitorNetworkMonitor
import com.gasguru.core.data.util.NetworkMonitor
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun iosDataModule() = module {
    single<LocationTracker> {
        LocationTrackerIos(ioDispatcher = get<CoroutineDispatcher>(named(KoinQualifiers.IO_DISPATCHER)))
    }
    single<GeocoderAddress> {
        CLGeocoderAddress(ioDispatcher = get<CoroutineDispatcher>(named(KoinQualifiers.IO_DISPATCHER)))
    }
    single<NetworkMonitor> {
        NWPathMonitorNetworkMonitor(ioDispatcher = get<CoroutineDispatcher>(named(KoinQualifiers.IO_DISPATCHER)))
    }
    single<PlacesRepository> { PlacesRepositoryIos() }
    single<String>(named(KoinQualifiers.GOOGLE_API_KEY)) { "" }
}
