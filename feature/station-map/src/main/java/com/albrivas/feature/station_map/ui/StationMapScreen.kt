package com.albrivas.feature.station_map.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.albrivas.fuelpump.core.common.centerOnLocation
import com.albrivas.fuelpump.core.common.hasLocationPermission
import com.albrivas.fuelpump.core.common.isLocationEnabled
import com.albrivas.fuelpump.core.common.toLatLng
import com.albrivas.fuelpump.core.model.data.FuelStation
import com.albrivas.fuelpump.core.model.data.FuelType
import com.albrivas.fuelpump.core.ui.getPrice
import com.albrivas.fuelpump.core.ui.toBrandStationIcon
import com.albrivas.fuelpump.core.ui.toColor
import com.albrivas.fuelpump.core.uikit.components.marker.StationMarker
import com.albrivas.fuelpump.core.uikit.components.marker.StationMarkerModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun StationMapScreenRoute(viewModel: StationMapViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    StationMapScreen(
        getStations = viewModel::getStationsByLocation,
        stations = state.fuelStations,
        centerMap = state.centerMap,
        zoomLevel = state.zoomLevel,
        userSelectedFuelType = state.selectedType
    )
}

@Composable
internal fun StationMapScreen(
    getStations: () -> Unit,
    stations: List<FuelStation>,
    centerMap: LatLng,
    zoomLevel: Float,
    userSelectedFuelType: FuelType?,
) {
    val context = LocalContext.current
    val cameraState = rememberCameraPositionState()
    LaunchedEffect(key1 = centerMap) {
        cameraState.centerOnLocation(location = centerMap, zoomLevel = zoomLevel)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = true,
                zoomControlsEnabled = false,
                compassEnabled = false,
            ),
            properties = MapProperties(
                isMyLocationEnabled = context.hasLocationPermission() && context.isLocationEnabled()
            ),
            onMyLocationButtonClick = {
                true
            },
            onMapLoaded = getStations,
        ) {
            stations.forEach { station ->
                val state = MarkerState(position = station.location.toLatLng())
                MarkerComposable(state = state) {
                    StationMarker(
                        model = StationMarkerModel(
                            icon = station.brandStationBrandsType.toBrandStationIcon(),
                            price = "€${userSelectedFuelType.getPrice(station)}",
                            color = station.priceCategory.toColor()
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun StationMapScreenPreview() {
    StationMapScreen(
        getStations = {},
        stations = emptyList(),
        centerMap = LatLng(0.0, 0.0),
        zoomLevel = 15f,
        userSelectedFuelType = FuelType.GASOLINE_95
    )
}