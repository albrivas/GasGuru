package com.gasguru.feature.widget.worker

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gasguru.core.domain.fuelstation.GetFuelStationUseCase
import com.gasguru.feature.widget.ui.FavoriteStationsWidget
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StationSyncWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params), KoinComponent {

    private val getFuelStationUseCase: GetFuelStationUseCase by inject()

    override suspend fun doWork(): Result {
        return try {
            getFuelStationUseCase.getFuelInAllStations()
            FavoriteStationsWidget().updateAll(applicationContext)
            Result.success()
        } catch (exception: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "gasguru_station_sync"
    }
}
