package com.gasguru.core.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.gasguru.core.model.data.LatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.android.gms.maps.model.LatLng as GoogleLatLng

fun Location?.toLatLng(): GoogleLatLng {
    return if (this != null) {
        GoogleLatLng(latitude, longitude)
    } else {
        GoogleLatLng(0.0, 0.0)
    }
}

fun LatLng?.toLocation() = Location("").apply {
    this@toLocation?.let { location ->
        latitude = location.latitude
        longitude = location.longitude
    }
}

fun Int.dpToPx() = (this * Resources.getSystem().displayMetrics.density).toInt()

suspend fun CameraPositionState.centerOnMap(bounds: LatLngBounds, padding: Int) =
    animate(
        update = CameraUpdateFactory.newLatLngBounds(bounds, padding.dpToPx()),
        durationMs = 500
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
