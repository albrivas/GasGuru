package com.gasguru.feature.station_map.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberLocationPermissionState(): LocationPermissionState = remember {
    LocationPermissionState(
        isGranted = true,
        isDenied = false,
        requestPermission = {},
        openSettings = {},
    )
}
