package com.gasguru.composeApp.di

import com.gasguru.composeApp.bridge.IosBridge
import com.gasguru.core.common.coroutineModule
import com.gasguru.core.components.searchbar.di.searchBarModule
import com.gasguru.core.data.di.commonDataModule
import com.gasguru.core.data.di.iosDataModule
import com.gasguru.core.data.sync.SyncManager
import com.gasguru.core.database.di.daoModule
import com.gasguru.core.database.di.databaseModule
import com.gasguru.core.domain.di.domainModule
import com.gasguru.core.supabase.datasource.RemoteDataSource
import com.gasguru.core.supabase.datasource.SupabaseRemoteDataSource
import com.gasguru.core.supabase.di.supabaseModule
import com.gasguru.di.appShellModule
import com.gasguru.feature.detail_station.di.detailStationModule
import com.gasguru.feature.favorite_list_station.di.favoriteListStationModule
import com.gasguru.feature.onboarding_welcome.di.onboardingModule
import com.gasguru.feature.profile.di.profileModule
import com.gasguru.feature.route_planner.di.routePlannerModule
import com.gasguru.feature.station_map.di.stationMapModule
import com.gasguru.feature.vehicle.di.vehicleModule
import com.gasguru.navigation.di.navigationModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

// Called from Swift as KoinInitKt.doInitKoin(platformModules:).
// Returns IosBridge — the single contract between Swift and KMP internals.
fun initKoin(platformModules: List<Module>): IosBridge {
    val koin = startKoin {
        modules(
            platformModules + listOf(
                coroutineModule,
                databaseModule,
                daoModule,
                supabaseModule,
                module { single<RemoteDataSource> { get<SupabaseRemoteDataSource>() } },
                iosDataModule(),
                commonDataModule(),
                domainModule(),
                navigationModule(),
                appShellModule(),
                detailStationModule(),
                favoriteListStationModule(),
                onboardingModule(),
                profileModule(),
                routePlannerModule(),
                stationMapModule(),
                vehicleModule(),
                searchBarModule(),
            ),
        )
    }.koin
    koin.get<SyncManager>().execute()
    return koin.get<IosBridge>()
}
