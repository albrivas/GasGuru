package com.gasguru.feature.station_map.platform

import androidx.compose.runtime.Composable

@Composable
actual fun rememberLocationPermissionState(): LocationPermissionState = LocationPermissionState(
    isGranted = false,
    isDenied = false,
    requestPermission = {},
    openSettings = {},
)
