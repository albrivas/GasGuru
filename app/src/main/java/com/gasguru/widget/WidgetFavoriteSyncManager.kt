package com.gasguru.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.glance.appwidget.updateAll
import com.gasguru.core.domain.fuelstation.GetFavoriteStationsWithoutDistanceUseCase
import com.gasguru.feature.widget.ui.FavoriteStationsWidget
import com.gasguru.feature.widget.ui.FavoriteStationsWidgetReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import com.gasguru.feature.widget.R as WidgetR

class WidgetFavoriteSyncManager(
    private val context: Context,
    private val getFavoriteStationsWithoutDistanceUseCase: GetFavoriteStationsWithoutDistanceUseCase,
    private val applicationScope: CoroutineScope,
) {
    fun observe() {
        applicationScope.launch {
            getFavoriteStationsWithoutDistanceUseCase()
                .drop(1)
                .catch { }
                .collect { FavoriteStationsWidget().updateAll(context) }
        }
    }

    fun setupPreview() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            applicationScope.launch(Dispatchers.IO) {
                val provider = ComponentName(context, FavoriteStationsWidgetReceiver::class.java)
                val preview = RemoteViews(context.packageName, WidgetR.layout.widget_preview)
                AppWidgetManager.getInstance(context).setWidgetPreview(
                    provider,
                    AppWidgetProviderInfo.WIDGET_CATEGORY_HOME_SCREEN,
                    preview,
                )
            }
        }
    }
}
