@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.gasguru.core.data.di

import cocoapods.GooglePlaces.GMSPlacesClient
import com.gasguru.core.common.KoinQualifiers
import com.gasguru.core.data.DataSecrets
import com.gasguru.core.data.repository.geocoder.CLGeocoderAddress
import com.gasguru.core.data.repository.geocoder.GeocoderAddress
import com.gasguru.core.data.repository.location.LocationTracker
import com.gasguru.core.data.repository.location.LocationTrackerIos
import com.gasguru.core.data.repository.places.PlacesRepository
import com.gasguru.core.data.repository.places.PlacesRepositoryIos
import com.gasguru.core.data.repository.route.RoutesRepository
import com.gasguru.core.data.util.NWPathMonitorNetworkMonitor
import com.gasguru.core.data.util.NetworkMonitor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOf
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
    single<String>(named(KoinQualifiers.GOOGLE_API_KEY)) { DataSecrets.GOOGLE_API_KEY }
    single<PlacesRepository> {
        GMSPlacesClient.provideAPIKey(DataSecrets.GOOGLE_API_KEY)
        PlacesRepositoryIos(ioDispatcher = get<CoroutineDispatcher>(named(KoinQualifiers.IO_DISPATCHER)))
    }
    // Routes API requires iOS-specific Ktor client + API key setup (pending Phase 9D).
    // No-op: the map and route-planner work, but no polyline is drawn on iOS yet.
    single<RoutesRepository> { RoutesRepository { _, _ -> flowOf(null) } }
}
