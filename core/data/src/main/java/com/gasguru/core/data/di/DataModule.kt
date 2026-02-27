package com.gasguru.core.data.di

import com.gasguru.core.common.KoinQualifiers
import com.gasguru.core.data.repository.alerts.PriceAlertRepository
import com.gasguru.core.data.repository.alerts.PriceAlertRepositoryImpl
import com.gasguru.core.data.repository.filter.FilterRepository
import com.gasguru.core.data.repository.filter.FilterRepositoryImpl
import com.gasguru.core.data.repository.geocoder.GeocoderAddress
import com.gasguru.core.data.repository.geocoder.GeocoderAddressImpl
import com.gasguru.core.data.repository.location.LocationTracker
import com.gasguru.core.data.repository.location.LocationTrackerRepository
import com.gasguru.core.data.repository.maps.GoogleStaticMapRepository
import com.gasguru.core.data.repository.maps.StaticMapRepository
import com.gasguru.core.data.repository.places.PlacesRepository
import com.gasguru.core.data.repository.places.PlacesRepositoryImp
import com.gasguru.core.data.repository.route.RoutesRepository
import com.gasguru.core.data.repository.route.RoutesRepositoryImpl
import com.gasguru.core.data.repository.search.OfflineRecentSearchRepository
import com.gasguru.core.data.repository.search.OfflineRecentSearchRepositoryImp
import com.gasguru.core.data.repository.stations.FuelStationRepository
import com.gasguru.core.data.repository.stations.OfflineFuelStationRepository
import com.gasguru.core.data.repository.user.OfflineUserDataRepository
import com.gasguru.core.data.repository.user.UserDataRepository
import com.gasguru.core.data.repository.vehicle.OfflineVehicleRepository
import com.gasguru.core.data.repository.vehicle.VehicleRepository
import com.gasguru.core.data.sync.SyncManager
import com.gasguru.core.data.util.ConnectivityManagerNetworkMonitor
import com.gasguru.core.data.util.NetworkMonitor
import com.gasguru.core.network.datasource.PlacesDataSource
import com.gasguru.core.network.datasource.PlacesDataSourceImp
import com.gasguru.core.network.datasource.RoutesDataSource
import com.gasguru.core.network.datasource.RoutesDataSourceImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {
    single<UserDataRepository> {
        OfflineUserDataRepository(
            userDataDao = get(),
            favoriteStationDao = get(),
            vehicleDao = get(),
        )
    }

    single<VehicleRepository> {
        OfflineVehicleRepository(vehicleDao = get())
    }

    single<FuelStationRepository> {
        OfflineFuelStationRepository(
            fuelStationDao = get(),
            remoteDataSource = get(),
            defaultDispatcher = get(named(KoinQualifiers.DEFAULT_DISPATCHER)),
            ioDispatcher = get(named(KoinQualifiers.IO_DISPATCHER)),
            offlineUserDataRepository = get(),
            favoriteStationDao = get(),
            priceAlertDao = get(),
        )
    }

    single<LocationTracker> {
        LocationTrackerRepository(
            locationClient = get(),
            context = androidContext(),
        )
    }

    single<PlacesDataSource> { PlacesDataSourceImp(placesClient = get()) }

    single<PlacesRepository> { PlacesRepositoryImp(placesDataSource = get()) }

    single<OfflineRecentSearchRepository> {
        OfflineRecentSearchRepositoryImp(recentSearchQueryDao = get())
    }

    single<GeocoderAddress> {
        GeocoderAddressImpl(
            context = androidContext(),
            ioDispatcher = get(named(KoinQualifiers.IO_DISPATCHER)),
        )
    }

    single<FilterRepository> { FilterRepositoryImpl(dao = get()) }

    single<NetworkMonitor> {
        ConnectivityManagerNetworkMonitor(
            context = androidContext(),
            ioDispatcher = get(named(KoinQualifiers.IO_DISPATCHER)),
        )
    }

    single<StaticMapRepository> {
        GoogleStaticMapRepository(apiKey = get(named(KoinQualifiers.GOOGLE_API_KEY)))
    }

    single<RoutesDataSource> { RoutesDataSourceImpl(routeApiServices = get()) }

    single<RoutesRepository> {
        RoutesRepositoryImpl(
            routesDataSource = get(),
            ioDispatcher = get(named(KoinQualifiers.IO_DISPATCHER)),
            defaultDispatcher = get(named(KoinQualifiers.DEFAULT_DISPATCHER)),
        )
    }

    single<PriceAlertRepository> {
        PriceAlertRepositoryImpl(
            priceAlertDao = get(),
            supabaseManager = get(),
            networkMonitor = get(),
            oneSignalManager = get(),
            vehicleDao = get(),
        )
    }

    single {
        SyncManager(
            networkMonitor = get(),
            priceAlertRepository = get(),
            scope = get(named(KoinQualifiers.APPLICATION_SCOPE)),
        )
    }
}
