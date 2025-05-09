package com.gasguru.auto.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarLocation
import androidx.car.app.model.ItemList
import androidx.car.app.model.Metadata
import androidx.car.app.model.Place
import androidx.car.app.model.PlaceListMapTemplate
import androidx.car.app.model.PlaceMarker
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.gasguru.auto.di.CarScreenEntryPoint
import com.gasguru.core.domain.FuelStationByLocationUseCase
import com.gasguru.core.domain.GetUserDataUseCase
import com.gasguru.core.domain.location.GetCurrentLocationUseCase
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.OpeningHours
import com.gasguru.core.ui.getPrice
import com.gasguru.core.uikit.theme.Primary600
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class MapAutomotiveScreen(carContext: CarContext) : Screen(carContext) {

    private var hasLocationPermission = false
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var carUiState = CarUiState(
        loading = true
    )

    private val getCurrentLocationUseCase: GetCurrentLocationUseCase by lazy {
        EntryPointAccessors.fromApplication(
            carContext.applicationContext,
            CarScreenEntryPoint::class.java
        ).getCurrentLocationUseCase()
    }

    private val getFuelStationByLocation: FuelStationByLocationUseCase by lazy {
        EntryPointAccessors.fromApplication(
            carContext.applicationContext,
            CarScreenEntryPoint::class.java
        ).getFuelStationByLocation()
    }

    private val getUserDataUseCase: GetUserDataUseCase by lazy {
        EntryPointAccessors.fromApplication(
            carContext.applicationContext,
            CarScreenEntryPoint::class.java
        ).getUserDataUseCase()
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
                updateStationList()
            }
        }
    }

    private fun updateStationList() {
        coroutineScope.launch {
            getCurrentLocationUseCase()?.let { location ->
                combine(
                    getUserDataUseCase(),
                    getFuelStationByLocation(
                        userLocation = location,
                        maxStations = 25,
                        brands = emptyList(),
                        schedule = OpeningHours.NONE
                    )
                ) { userData, stations ->
                    carUiState = CarUiState(
                        loading = false,
                        stations = stations,
                        selectedFuel = userData.fuelSelection
                    )
                    invalidate()
                }.launchIn(coroutineScope)
            }
        }
    }

    private fun cafeRow(station: FuelStation, selectedFuel: FuelType?): Row {
        return Row.Builder()
            .setTitle(
                "${station.formatName()} - ${
                    selectedFuel?.getPrice(
                        carContext,
                        station
                    ) ?: ""
                }"
            )
            .setMetadata(
                Metadata.Builder()
                    .setPlace(
                        Place.Builder(
                            CarLocation.create(
                                station.location.latitude,
                                station.location.longitude
                            )
                        ).setMarker(
                            PlaceMarker.Builder().setColor(
                                CarColor.createCustom(
                                    Color.White.toArgb(),
                                    Primary600.toArgb(),
                                )
                            ).build()
                        )
                            .build()
                    ).build()
            )
            .setBrowsable(true)
            .setOnClickListener {
                navigateToStation(station.location.latitude, station.location.longitude)
            }
            .addText(station.formatDirection())
            .addText(station.formatDistance())
            .build()
    }

    private fun navigateToStation(latitude: Double, longitude: Double) {
        val intent = Intent().apply {
            action = CarContext.ACTION_NAVIGATE
            data = Uri.parse("geo:$latitude,$longitude")
        }
        carContext.startCarApp(intent)
        screenManager.pop()
    }

    override fun onGetTemplate(): Template {
        val builder = PlaceListMapTemplate
            .Builder()
            .setTitle("GasGuru Finder")
            .setHeaderAction(Action.APP_ICON)

        builder.setLoading(carUiState.loading)

        if (!carUiState.loading) {
            builder.setLoading(false)
            val items = ItemList.Builder()

            carUiState.stations.forEach { station ->
                items.addItem(cafeRow(station, carUiState.selectedFuel))
            }

            builder.setItemList(items.build())
        }

        if (hasLocationPermission) {
            builder.setCurrentLocationEnabled(true)
        }

        builder.setOnContentRefreshListener { updateStationList() }

        return builder.build()
    }
}
