package com.gasguru.feature.widget.ui

import android.content.Context
import android.os.Build
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.gasguru.feature.widget.worker.WidgetMetricsWorker
import java.util.concurrent.TimeUnit

abstract class BaseFavoriteStationsWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = FavoriteStationsWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WidgetMetricsWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<WidgetMetricsWorker>(1, TimeUnit.HOURS).build(),
            )
        }
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            WorkManager.getInstance(context).cancelUniqueWork(WidgetMetricsWorker.WORK_NAME)
        }
    }
}
