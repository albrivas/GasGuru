package com.gasguru.auto.ui

import android.Manifest
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import com.gasguru.auto.di.CarScreenEntryPoint
import com.gasguru.core.domain.location.GetCurrentLocationUseCase
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FuelStationScreen(carContext: CarContext) : Screen(carContext) {

    private var hasLocationPermission = false
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var currentLocationText: String = "Loading location..."


    private val getCurrentLocationUseCase: GetCurrentLocationUseCase by lazy {
        EntryPointAccessors.fromApplication(
            carContext.applicationContext,
            CarScreenEntryPoint::class.java
        ).getCurrentLocationUseCase()
    }

    init {
        carContext.requestPermissions(
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) { granted, _ ->
            if (granted.contains(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            ) {
                hasLocationPermission = true
                coroutineScope.launch {
                    getCurrentLocationUseCase()?.let {
                        val latitude = it.latitude
                        val longitude = it.longitude
                        currentLocationText = "Latitude: $latitude, Longitude: $longitude"
                    }
                    invalidate()
                }
            }
        }
    }

    override fun onGetTemplate(): Template {
        return PaneTemplate.Builder(
            Pane.Builder()
                .addRow(
                    Row.Builder().setTitle(currentLocationText).build()
                )
                .build()
        ).build()
    }
}