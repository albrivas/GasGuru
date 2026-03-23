package com.gasguru.feature.widget.worker

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
class WidgetMetricsWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {

    private val analyticsHelper: AnalyticsHelper by inject()

    override suspend fun doWork(): Result {
        val manager = AppWidgetManager.getInstance(applicationContext)
        val endTime = System.currentTimeMillis()
        val startTime = endTime - TimeUnit.HOURS.toMillis(1)

        manager.queryAppWidgetEvents(startTime, endTime).forEach { event ->
            analyticsHelper.logEvent(
                AnalyticsEvent(
                    type = AnalyticsEvent.Types.WIDGET_METRICS_REPORTED,
                    extras = listOf(
                        AnalyticsEvent.Param(
                            key = AnalyticsEvent.ParamKeys.WIDGET_ID,
                            value = event.appWidgetId.toString(),
                        ),
                        AnalyticsEvent.Param(
                            key = AnalyticsEvent.ParamKeys.WIDGET_VISIBLE_DURATION_MS,
                            value = event.visibleDuration.toMillis().toString(),
                        ),
                        AnalyticsEvent.Param(
                            key = AnalyticsEvent.ParamKeys.WIDGET_CLICK_COUNT,
                            value = event.clickedIds.size.toString(),
                        ),
                    ),
                )
            )
        }
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "widget_metrics_worker"
    }
}
