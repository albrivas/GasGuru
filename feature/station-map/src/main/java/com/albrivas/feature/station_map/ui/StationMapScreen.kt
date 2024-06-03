package com.albrivas.feature.station_map.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings

@Composable
fun StationMapScreenRoute(viewModel: StationMapViewModel = hiltViewModel()) {
    StationMapScreen()
}

@Composable
internal fun StationMapScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            //cameraPositionState = cameraState,
            contentPadding = PaddingValues(bottom = 74.dp),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = true,
                zoomControlsEnabled = false,
                compassEnabled = false,
            ),
            properties = MapProperties(
                //isMyLocationEnabled = context.hasLocationPermission() && context.isLocationEnabled()
            ),
            onMyLocationButtonClick = {
                true
            },
            onMapLoaded = {}
        ) {

        }
    }
}

@Preview
@Composable
private fun StationMapScreenPreview() {
    StationMapScreen()
}