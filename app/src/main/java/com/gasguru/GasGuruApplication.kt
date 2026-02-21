package com.gasguru

import android.app.Application
import com.gasguru.core.common.coroutineModule
import com.gasguru.core.components.searchbar.di.searchBarModule
import com.gasguru.core.data.di.dataModule
import com.gasguru.core.data.di.dataProviderModule
import com.gasguru.core.data.sync.SyncManager
import com.gasguru.core.database.di.daoModule
import com.gasguru.core.database.di.databaseModule
import com.gasguru.core.domain.di.domainModule
import com.gasguru.core.network.di.networkModule
import com.gasguru.core.network.di.placesModule
import com.gasguru.core.notifications.PushNotificationService
import com.gasguru.core.notifications.di.notificationModule
import com.gasguru.core.supabase.di.supabaseModule
import com.gasguru.di.appModule
import com.gasguru.di.remoteDataSourceModule
import com.gasguru.feature.detail_station.di.detailStationModule
import com.gasguru.feature.favorite_list_station.di.favoriteListStationModule
import com.gasguru.feature.onboarding_welcome.di.onboardingModule
import com.gasguru.feature.profile.di.profileModule
import com.gasguru.feature.route_planner.di.routePlannerModule
import com.gasguru.feature.station_map.di.stationMapModule
import com.gasguru.navigation.di.navigationModule
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class GasGuruApplication : Application() {

    private val syncManager: SyncManager by inject()
    private val pushNotificationService: PushNotificationService by inject()

    override fun onCreate() {
        super.onCreate()
        initKoin()
        oneSignalSetUp()
        initPushNotifications()
        mixpanelSetUp()
        initSyncManager()
    }

    private fun oneSignalSetUp() {
        when (BuildConfig.DEBUG) {
            true -> OneSignal.Debug.logLevel = LogLevel.VERBOSE
            false -> OneSignal.Debug.logLevel = LogLevel.NONE
        }

        OneSignal.initWithContext(this, BuildConfig.onesignalAppId)
    }

    private fun mixpanelSetUp() {
        MixpanelAPI.getInstance(this, BuildConfig.mixpanelProjectToken, true)
    }

    private fun initPushNotifications() {
        pushNotificationService.init()
    }

    private fun initSyncManager() {
        syncManager.execute()
    }

    private fun initKoin() {
        startKoin {
            androidLogger(level = if (BuildConfig.DEBUG) Level.DEBUG else Level.ERROR)
            androidContext(this@GasGuruApplication)
            modules(
                coroutineModule,
                databaseModule,
                daoModule,
                networkModule,
                placesModule,
                supabaseModule,
                notificationModule,
                dataModule,
                dataProviderModule,
                domainModule,
                navigationModule,
                remoteDataSourceModule,
                appModule,
                stationMapModule,
                detailStationModule,
                favoriteListStationModule,
                profileModule,
                routePlannerModule,
                onboardingModule,
                searchBarModule,
            )
        }
    }
}
