package com.gasguru.feature.detail_station.platform

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.gasguru.core.model.data.LatLng

@SuppressLint("QueryPermissionsNeeded")
@Composable
actual fun rememberNavigateToMapsAction(stationName: String): (LatLng) -> Unit {
    val context = LocalContext.current
    return { location ->
        val lat = location.latitude
        val lng = location.longitude
        val intents = mutableListOf<Intent>()
        intents.add(
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("google.navigation:q=$lat,$lng")
                setPackage("com.google.android.apps.maps")
            },
        )
        val wazeIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("waze://?ll=$lat,$lng&navigate=yes")
            setPackage("com.waze")
        }
        if (wazeIntent.resolveActivity(context.packageManager) != null) {
            intents.add(wazeIntent)
        }
        val chooser = Intent.createChooser(intents.removeAt(0), null).apply {
            putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toTypedArray())
        }
        context.startActivity(chooser)
    }
}
