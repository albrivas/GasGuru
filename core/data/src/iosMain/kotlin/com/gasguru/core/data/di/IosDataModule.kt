package com.gasguru.core.data.di

import com.gasguru.core.common.KoinQualifiers
import com.gasguru.core.data.repository.geocoder.GeocoderAddress
import com.gasguru.core.data.repository.geocoder.GeocoderAddressIos
import com.gasguru.core.data.repository.location.LocationTracker
import com.gasguru.core.data.repository.location.LocationTrackerIos
import com.gasguru.core.data.repository.places.PlacesRepository
import com.gasguru.core.data.repository.places.PlacesRepositoryIos
import com.gasguru.core.data.util.NetworkMonitor
import com.gasguru.core.data.util.NetworkMonitorIos
import org.koin.core.qualifier.named
import org.koin.dsl.module

// Módulo iOS: wiring de stubs V1. Para iOS V2, reemplazar con implementaciones nativas.
// No incluye RoutesRepository (feature de rutas pendiente para V2).
val iosDataModule = module {
    single<LocationTracker> { LocationTrackerIos() }
    single<GeocoderAddress> { GeocoderAddressIos() }
    single<NetworkMonitor> { NetworkMonitorIos() }
    single<PlacesRepository> { PlacesRepositoryIos() }
    single<String>(named(KoinQualifiers.GOOGLE_API_KEY)) { "" }
}
