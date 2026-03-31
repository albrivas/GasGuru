package com.gasguru.feature.widget.ui

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import com.gasguru.core.domain.fuelstation.GetFavoriteStationsWithoutDistanceUseCase
import com.gasguru.core.model.data.principalVehicle
import com.gasguru.feature.widget.model.toWidgetItemModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FavoriteStationsWidget : GlanceAppWidget(), KoinComponent {

    override val sizeMode = SizeMode.Responsive(
        setOf(
            DpSize(width = 250.dp, height = 110.dp),
            DpSize(width = 250.dp, height = 200.dp),
        )
    )

    private val getFavoriteStationsWithoutDistanceUseCase: GetFavoriteStationsWithoutDistanceUseCase by inject()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val stationsFlow = getFavoriteStationsWithoutDistanceUseCase()
            .map { data ->
                val fuelType = data.user.principalVehicle().fuelType
                data.favoriteStations
                    .sortedBy { station -> fuelType.extractPrice(station) }
                    .map { station -> station.toWidgetItemModel(fuelType = fuelType) }
            }
            .catch { emit(emptyList()) }

        provideContent {
            val stations by stationsFlow.collectAsState(initial = null)
            val isCompact = LocalSize.current.height < 150.dp
            FavoriteStationsWidgetContent(
                stations = stations,
                isCompact = isCompact,
            )
        }
    }
}
