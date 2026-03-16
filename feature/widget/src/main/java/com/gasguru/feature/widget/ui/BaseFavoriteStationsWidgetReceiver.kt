package com.gasguru.feature.widget.ui

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.gasguru.feature.widget.worker.StationSyncWorker
import java.util.concurrent.TimeUnit

abstract class BaseFavoriteStationsWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = FavoriteStationsWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        WorkManager.getInstance(context).enqueue(
            OneTimeWorkRequestBuilder<StationSyncWorker>()
                .setConstraints(networkConstraints())
                .build()
        )
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            uniqueWorkName = StationSyncWorker.WORK_NAME,
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
            request = PeriodicWorkRequestBuilder<StationSyncWorker>(
                repeatInterval = 30,
                repeatIntervalTimeUnit = TimeUnit.MINUTES,
            )
                .setConstraints(networkConstraints())
                .build(),
        )
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        val manager = AppWidgetManager.getInstance(context)
        val largeIds = manager.getAppWidgetIds(
            ComponentName(context, FavoriteStationsWidgetReceiver::class.java)
        )
        val smallIds = manager.getAppWidgetIds(
            ComponentName(context, FavoriteStationsWidgetSmallReceiver::class.java)
        )
        if (largeIds.isEmpty() && smallIds.isEmpty()) {
            WorkManager.getInstance(context).cancelUniqueWork(StationSyncWorker.WORK_NAME)
        }
    }

    private fun networkConstraints(): Constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
}
