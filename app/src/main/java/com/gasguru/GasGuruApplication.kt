package com.gasguru

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.gasguru.core.analytics.di.analyticsModule
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
import com.gasguru.core.notifications.NotificationService
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
import com.gasguru.feature.vehicle.di.vehicleModule
import com.gasguru.navigation.di.navigationModule
import com.gasguru.widget.WidgetFavoriteSyncManager
import com.gasguru.worker.StationSyncWorker
import com.microsoft.clarity.Clarity
import com.microsoft.clarity.ClarityConfig
import com.microsoft.clarity.models.LogLevel as ClarityLogLevel
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.util.concurrent.TimeUnit

class GasGuruApplication : Application() {

    private val syncManager: SyncManager by inject()
    private val pushNotificationService: NotificationService by inject()
    private val widgetFavoriteSyncManager: WidgetFavoriteSyncManager by inject()

    override fun onCreate() {
        super.onCreate()
        initKoin()
        oneSignalSetUp()
        initPushNotifications()
        mixpanelSetUp()
        claritySetUp()
        initSyncManager()
        initStationSync()
        widgetFavoriteSyncManager.observe()
        widgetFavoriteSyncManager.setupPreview()
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

    private fun claritySetUp() {
        val config = ClarityConfig(
            projectId = BuildConfig.clarityProjectId,
            logLevel = if (BuildConfig.DEBUG) ClarityLogLevel.Verbose else ClarityLogLevel.None,
        )
        Clarity.initialize(applicationContext, config)
    }

    private fun initPushNotifications() {
        pushNotificationService.init()
    }

    private fun initSyncManager() {
        syncManager.execute()
    }

    private fun initStationSync() {
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            StationSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<StationSyncWorker>(30, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints(requiredNetworkType = NetworkType.CONNECTED),
                )
                .build(),
        )
    }

    private fun initKoin() {
        startKoin {
            androidLogger(level = if (BuildConfig.DEBUG) Level.DEBUG else Level.ERROR)
            androidContext(this@GasGuruApplication)
            modules(
                analyticsModule,
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
                vehicleModule,
                searchBarModule,
            )
        }
    }
}
