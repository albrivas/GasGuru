package com.gasguru.feature.station_map.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gasguru.core.ui.LocalOpenLocationSettings
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.darwin.NSObject

@Composable
actual fun rememberLocationPermissionState(): LocationPermissionState {
    val openLocationSettings = LocalOpenLocationSettings.current
    val manager = remember { CLLocationManager() }
    var authStatus by remember { mutableStateOf(manager.authorizationStatus) }

    val delegate = remember {
        object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
                authStatus = manager.authorizationStatus
            }
        }
    }

    DisposableEffect(Unit) {
        manager.delegate = delegate
        onDispose { manager.delegate = null }
    }

    val isGranted = authStatus == kCLAuthorizationStatusAuthorizedWhenInUse ||
        authStatus == kCLAuthorizationStatusAuthorizedAlways
    val isDenied = authStatus == kCLAuthorizationStatusDenied ||
        authStatus == kCLAuthorizationStatusRestricted

    return LocationPermissionState(
        isGranted = isGranted,
        isDenied = isDenied,
        requestPermission = { manager.requestWhenInUseAuthorization() },
        openSettings = openLocationSettings,
    )
}
