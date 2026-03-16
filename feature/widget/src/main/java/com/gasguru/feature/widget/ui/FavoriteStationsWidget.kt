package com.gasguru.feature.widget.ui

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import com.gasguru.core.domain.fuelstation.GetFavoriteStationsWithoutDistanceUseCase
import com.gasguru.core.model.data.principalVehicle
import com.gasguru.feature.widget.model.FavoriteWidgetItemModel
import com.gasguru.feature.widget.model.toWidgetItemModel
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FavoriteStationsWidget : GlanceAppWidget(), KoinComponent {

    private val getFavoriteStationsWithoutDistanceUseCase: GetFavoriteStationsWithoutDistanceUseCase by inject()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val stations = loadFavoriteStations()
        provideContent {
            FavoriteStationsWidgetContent(stations = stations)
        }
    }

    private suspend fun loadFavoriteStations(): List<FavoriteWidgetItemModel> {
        val data = getFavoriteStationsWithoutDistanceUseCase().first()
        val fuelType = data.user.principalVehicle().fuelType
        return data.favoriteStations
            .sortedBy { fuelStation -> fuelType.extractPrice(fuelStation) }
            .map { fuelStation -> fuelStation.toWidgetItemModel(fuelType = fuelType) }
    }
}
