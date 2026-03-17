package com.gasguru.worker

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.domain.fuelstation.GetFuelStationUseCase
import com.gasguru.feature.widget.ui.FavoriteStationsWidget
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StationSyncWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params), KoinComponent {

    private val getFuelStationUseCase: GetFuelStationUseCase by inject()
    private val analyticsHelper: AnalyticsHelper by inject()

    override suspend fun doWork(): Result {
        analyticsHelper.logEvent(event = AnalyticsEvent(type = AnalyticsEvent.Types.STATION_SYNC_WORKER_STARTED))
        return try {
            getFuelStationUseCase.getFuelInAllStations()
            FavoriteStationsWidget().updateAll(applicationContext)
            analyticsHelper.logEvent(event = AnalyticsEvent(type = AnalyticsEvent.Types.STATION_SYNC_WORKER_COMPLETED))
            Result.success()
        } catch (exception: Exception) {
            analyticsHelper.logEvent(event = AnalyticsEvent(type = AnalyticsEvent.Types.STATION_SYNC_WORKER_RETRIED))
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "gasguru_station_sync"
    }
}
