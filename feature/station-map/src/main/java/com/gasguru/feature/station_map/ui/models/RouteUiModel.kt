package com.gasguru.feature.station_map.ui.models

import androidx.compose.runtime.Stable
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.Route

@Stable
data class RouteUiModel(
    val route: List<LatLng>,
    val distanceText: String,
    val durationText: String,
)

fun Route.toUiModel(): RouteUiModel = RouteUiModel(
    route = route,
    distanceText = distanceText,
    durationText = durationText,
)
