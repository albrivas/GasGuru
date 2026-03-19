package com.gasguru.feature.widget.ui

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WidgetStationClickCallback : ActionCallback, KoinComponent {

    private val analyticsHelper: AnalyticsHelper by inject()

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val stationId = parameters[stationIdKey] ?: return
        analyticsHelper.logEvent(
            event = AnalyticsEvent(
                type = AnalyticsEvent.Types.WIDGET_STATION_TAPPED,
                extras = listOf(
                    AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.STATION_ID, value = stationId.toString()),
                ),
            ),
        )
        val intent = Intent().apply {
            setClassName(context.packageName, "com.gasguru.MainActivity")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("station_id", stationId.toString())
        }
        context.startActivity(intent)
    }

    companion object {
        val stationIdKey = ActionParameters.Key<Int>("station_id")
    }
}
