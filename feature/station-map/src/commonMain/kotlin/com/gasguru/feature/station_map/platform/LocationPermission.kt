package com.gasguru.feature.station_map.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

@Stable
class LocationPermissionState(
    val isGranted: Boolean,
    val isDenied: Boolean,
    val requestPermission: () -> Unit,
    val openSettings: () -> Unit,
)

@Composable
expect fun rememberLocationPermissionState(): LocationPermissionState
