package com.albrivas.fuelpump.core.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState

fun Location?.toLatLng(): LatLng {
    return if (this != null) {
        LatLng(latitude, longitude)
    } else {
        LatLng(40.4165, -3.70256) //Madrid coordinates
    }
}

suspend fun CameraPositionState.centerOnLocation(location: LatLng, zoomLevel: Float) =
    animate(
        update = CameraUpdateFactory.newLatLngZoom(location, zoomLevel),
        durationMs = 800
    )

fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.isLocationEnabled(): Boolean {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
            locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
}