package com.gasguru.core.common

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState

fun Location?.toLatLng(): LatLng {
    return if (this != null) {
        LatLng(latitude, longitude)
    } else {
        LatLng(0.0, 0.0)
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

fun startRoute(context: Context, location: Location) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(
            "https://www.google.com/maps/dir/?api=1" +
                "&destination=${location.latitude},${location.longitude}" +
                "&mode=driving"
        )
    }
    context.startActivity(intent, null)
}

fun generateStaticMapUrl(
    location: Location,
    zoom: Int,
    width: Int,
    height: Int,
    apiKey: String,
): String {
    val center = "${location.latitude},${location.longitude}"
    return "https://maps.googleapis.com/maps/api/staticmap?" +
        "center=$center" +
        "&zoom=$zoom" +
        "&size=${width}x$height" +
        "&markers=color:red%7Clabel:C%7C$center" +
        "&key=$apiKey"
}
