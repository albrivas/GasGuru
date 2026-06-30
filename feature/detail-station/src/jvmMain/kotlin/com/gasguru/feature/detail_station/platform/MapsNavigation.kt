package com.gasguru.feature.detail_station.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gasguru.core.model.data.LatLng

@Composable
actual fun rememberNavigateToMapsAction(stationName: String): (LatLng) -> Unit =
    remember { { _ -> } }
