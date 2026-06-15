package com.gasguru.composeApp.di

import android.app.Application
import com.gasguru.core.analytics.di.analyticsModule
import com.gasguru.core.common.coroutineModule
import com.gasguru.core.components.searchbar.di.searchBarModule
import com.gasguru.core.data.di.androidDataModule
import com.gasguru.core.data.di.commonDataModule
import com.gasguru.core.data.di.dataProviderModule
import com.gasguru.core.database.di.daoModule
import com.gasguru.core.database.di.databaseModule
import com.gasguru.core.domain.di.domainModule
import com.gasguru.core.network.di.networkModule
import com.gasguru.core.network.di.placesModule
import com.gasguru.core.notifications.di.notificationModule
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
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module

// Called from GasGuruApplication.initKoin(). platformModules covers flavor-specific
// (remoteDataSourceModule) and app-specific (appModule with Widget + MixpanelAPI) bindings.
fun initKoin(
    application: Application,
    platformModules: List<Module> = emptyList(),
    enableDebug: Boolean = false,
) {
    startKoin {
        androidLogger(level = if (enableDebug) Level.DEBUG else Level.ERROR)
        androidContext(application)
        modules(
            platformModules + listOf(
                analyticsModule,
                coroutineModule,
                databaseModule,
                daoModule,
                networkModule(),
                placesModule(),
                supabaseModule,
                notificationModule,
                commonDataModule(),
                androidDataModule(),
                dataProviderModule(),
                domainModule(),
                navigationModule(),
                appShellModule(),
                stationMapModule(),
                detailStationModule(),
                favoriteListStationModule(),
                profileModule(),
                routePlannerModule(),
                onboardingModule(),
                vehicleModule(),
                searchBarModule(),
            ),
        )
    }
}
