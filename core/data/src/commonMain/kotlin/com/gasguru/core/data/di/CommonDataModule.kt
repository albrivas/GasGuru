package com.gasguru.core.data.di

import com.gasguru.core.common.KoinQualifiers
import com.gasguru.core.data.repository.alerts.PriceAlertRepository
import com.gasguru.core.data.repository.alerts.PriceAlertRepositoryImpl
import com.gasguru.core.data.repository.filter.FilterRepository
import com.gasguru.core.data.repository.filter.FilterRepositoryImpl
import com.gasguru.core.data.repository.maps.GoogleStaticMapRepository
import com.gasguru.core.data.repository.maps.StaticMapRepository
import com.gasguru.core.data.repository.search.OfflineRecentSearchRepository
import com.gasguru.core.data.repository.search.OfflineRecentSearchRepositoryImp
import com.gasguru.core.data.repository.stations.FuelStationRepository
import com.gasguru.core.data.repository.stations.OfflineFuelStationRepository
import com.gasguru.core.data.repository.user.OfflineUserDataRepository
import com.gasguru.core.data.repository.user.UserDataRepository
import com.gasguru.core.data.repository.vehicle.OfflineVehicleRepository
import com.gasguru.core.data.repository.vehicle.VehicleRepository
import com.gasguru.core.data.sync.SyncManager
import org.koin.core.qualifier.named
import org.koin.dsl.module

val commonDataModule = module {
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

    single<OfflineRecentSearchRepository> {
        OfflineRecentSearchRepositoryImp(recentSearchQueryDao = get())
    }

    single<FilterRepository> { FilterRepositoryImpl(dao = get()) }

    single<StaticMapRepository> {
        GoogleStaticMapRepository(apiKey = get(named(KoinQualifiers.GOOGLE_API_KEY)))
    }

    single<PriceAlertRepository> {
        PriceAlertRepositoryImpl(
            priceAlertDao = get(),
            supabaseManager = get(),
            networkMonitor = get(),
            oneSignalManager = get(),
            vehicleDao = get(),
            analyticsHelper = get(),
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
