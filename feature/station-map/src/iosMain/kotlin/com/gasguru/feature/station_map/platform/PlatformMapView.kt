@file:Suppress("LongParameterList") // Actual del expect en commonMain; firma fija.

package com.gasguru.feature.station_map.platform

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.ui.models.FuelStationUiModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.feature.station_map.ui.model.GeoBounds
import com.gasguru.feature.station_map.ui.models.RouteUiModel

@Composable
actual fun PlatformMapView(
    stations: List<FuelStationUiModel>,
    route: RouteUiModel?,
    selectedStationId: Int,
    userSelectedFuelType: FuelType?,
    loading: Boolean,
    isLocationPermissionGranted: Boolean,
    mapBounds: GeoBounds?,
    shouldCenterMap: Boolean,
    userLocationToCenter: LatLng?,
    onStationClick: (Int) -> Unit,
    onMapCentered: () -> Unit,
    onUserLocationCentered: () -> Unit,
    modifier: Modifier,
) {
    Box(modifier = modifier.fillMaxSize().background(GasGuruTheme.colors.surface))
    // V2: UIKitView { MKMapView() } con MKAnnotations por estación
}
