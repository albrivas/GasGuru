package com.gasguru.auto.ui.mainmenu

import android.Manifest
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.PlaceListMapTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import com.gasguru.auto.common.R
import com.gasguru.auto.ui.favoritestation.FavoriteStationsScreen
import com.gasguru.auto.ui.nearbystation.NearbyStationsScreen
import com.gasguru.core.ui.R as CoreUiR

class MapAutomotiveScreen(carContext: CarContext) : Screen(carContext) {

    private var hasLocationPermission = false
    private var uiState = MainMenuUiState(
        permissionDenied = true
    )

    init {
        checkPermissions()
    }

    private fun createStationListOptions(): ItemList {
        val items = ItemList.Builder()

        items.addItem(
            Row.Builder()
                .setTitle(carContext.getString(CoreUiR.string.nearby_stations))
                .setBrowsable(true)
                .setOnClickListener {
                    screenManager.push(NearbyStationsScreen(carContext))
                }
                .build()
        )

        items.addItem(
            Row.Builder()
                .setTitle(carContext.getString(CoreUiR.string.favorites))
                .setBrowsable(true)
                .setOnClickListener {
                    screenManager.push(FavoriteStationsScreen(carContext))
                }
                .build()
        )

        return items.build()
    }

    private fun checkPermissions() {
        val hasLocationPermissions = carContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED

        if (hasLocationPermissions) {
            hasLocationPermission = true
            uiState = MainMenuUiState(permissionDenied = false)
            invalidate()
        } else {
            try {
                carContext.requestPermissions(
                    listOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                ) { granted, rejected ->
                    if (granted.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        hasLocationPermission = true
                        uiState = MainMenuUiState(permissionDenied = false)
                    } else {
                        uiState = MainMenuUiState()
                    }
                    invalidate()
                }
            } catch (_: SecurityException) {
                uiState = MainMenuUiState()
                invalidate()
            }
        }
    }

    override fun onGetTemplate(): Template {
        if (uiState.permissionDenied) {
            return MessageTemplate.Builder(carContext.getString(R.string.permission_required_message))
                .setTitle(carContext.getString(R.string.permission_required_title))
                .setHeaderAction(Action.APP_ICON)
                .addAction(
                    Action.Builder()
                        .setTitle(carContext.getString(R.string.grant_permissions))
                        .setOnClickListener {
                            checkPermissions()
                        }
                        .build()
                )
                .build()
        }

        if (uiState.needsOnboarding) {
            return MessageTemplate.Builder(carContext.getString(R.string.onboarding_required_message))
                .setTitle(carContext.getString(R.string.onboarding_required_title))
                .setHeaderAction(Action.APP_ICON)
                .addAction(
                    Action.Builder()
                        .setTitle(carContext.getString(R.string.complete_onboarding))
                        .setOnClickListener {
                            checkPermissions()
                        }
                        .build()
                )
                .build()
        }

        // Normal map template for when permissions are granted
        val builder = PlaceListMapTemplate
            .Builder()
            .setTitle(carContext.getString(R.string.app_title))
            .setHeaderAction(Action.APP_ICON)

        builder.setItemList(createStationListOptions())

        if (hasLocationPermission) {
            builder.setCurrentLocationEnabled(true)
        }

        return builder.build()
    }
}
