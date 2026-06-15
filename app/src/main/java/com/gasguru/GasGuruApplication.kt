package com.gasguru

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.gasguru.composeApp.di.initKoin
import com.gasguru.core.data.sync.SyncManager
import com.gasguru.core.notifications.NotificationService
import com.gasguru.di.appModule
import com.gasguru.di.remoteDataSourceModule
import com.gasguru.widget.WidgetFavoriteSyncManager
import com.gasguru.worker.StationSyncWorker
import com.microsoft.clarity.Clarity
import com.microsoft.clarity.ClarityConfig
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit
import com.microsoft.clarity.models.LogLevel as ClarityLogLevel

class GasGuruApplication : Application() {

    private val syncManager: SyncManager by inject()
    private val pushNotificationService: NotificationService by inject()
    private val widgetFavoriteSyncManager: WidgetFavoriteSyncManager by inject()

    override fun onCreate() {
        super.onCreate()
        initKoin(
            application = this,
            platformModules = listOf(remoteDataSourceModule(), appModule()),
            enableDebug = BuildConfig.DEBUG,
        )
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
        pushNotificationService.start()
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
}
