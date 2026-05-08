package com.gasguru.feature.detail_station.platform

import androidx.compose.runtime.Composable
import com.gasguru.core.model.data.LatLng

@Composable
expect fun rememberNavigateToMapsAction(): (LatLng) -> Unit
