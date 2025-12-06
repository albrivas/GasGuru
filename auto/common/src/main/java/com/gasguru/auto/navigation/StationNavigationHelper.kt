package com.gasguru.auto.navigation

import android.content.Intent
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.core.net.toUri

object StationNavigationHelper {

    fun navigateToStation(
        carContext: CarContext,
        latitude: Double,
        longitude: Double,
        finishAfterNavigation: Boolean = false
    ) {
        val intent = Intent().apply {
            action = CarContext.ACTION_NAVIGATE
            data = "geo:$latitude,$longitude".toUri()
        }
        carContext.startCarApp(intent)

        if (finishAfterNavigation) {
            carContext.finishCarApp()
        }
    }

    fun navigateToStationAndPopScreen(
        screen: Screen,
        latitude: Double,
        longitude: Double
    ) {
        navigateToStation(
            carContext = screen.carContext,
            latitude = latitude,
            longitude = longitude
        )
        screen.screenManager.pop()
    }
}
