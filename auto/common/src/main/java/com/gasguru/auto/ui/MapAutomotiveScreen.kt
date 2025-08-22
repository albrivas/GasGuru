package com.gasguru.auto.ui

import android.Manifest
import android.content.Intent
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarLocation
import androidx.car.app.model.ItemList
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Metadata
import androidx.car.app.model.Place
import androidx.car.app.model.PlaceListMapTemplate
import androidx.car.app.model.PlaceMarker
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.compose.ui.graphics.toArgb
import androidx.core.net.toUri
import com.gasguru.auto.common.R
import com.gasguru.auto.common.getAutomotiveThemeColor
import com.gasguru.auto.di.CarScreenEntryPoint
import com.gasguru.core.domain.fuelstation.FuelStationByLocationUseCase
import com.gasguru.core.domain.location.GetCurrentLocationUseCase
import com.gasguru.core.domain.location.IsLocationEnabledUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.OpeningHours
import com.gasguru.core.ui.getPrice
import com.gasguru.core.ui.models.FuelStationUiModel
import com.gasguru.core.ui.toUiModel
import com.gasguru.core.uikit.theme.GasGuruColors
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
        loading = true,
        permissionDenied = true
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

    private val isLocationEnabledUseCase: IsLocationEnabledUseCase by lazy {
        EntryPointAccessors.fromApplication(
            carContext.applicationContext,
            CarScreenEntryPoint::class.java
        ).isLocationEnabledUseCase()
    }

    init {
        checkPermissions()
    }

    private fun updateStationList() {
        coroutineScope.launch {
            val location = getCurrentLocationUseCase()
            if (location != null) {
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
                        stations = stations.map { it.toUiModel() },
                        selectedFuel = userData.fuelSelection,
                        permissionDenied = false,
                        needsOnboarding = !userData.isOnboardingSuccess,
                        locationDisabled = false
                    )
                    invalidate()
                }.launchIn(coroutineScope)
            } else {
                // Location is null, could be because GPS is disabled
                coroutineScope.launch {
                    getUserDataUseCase().collect { userData ->
                        carUiState = CarUiState(
                            loading = false,
                            stations = emptyList(),
                            selectedFuel = userData.fuelSelection,
                            permissionDenied = false,
                            needsOnboarding = !userData.isOnboardingSuccess,
                            locationDisabled = true
                        )
                        invalidate()
                    }
                }
            }
        }
    }

    private fun cafeRow(stationModel: FuelStationUiModel, selectedFuel: FuelType?, theme: GasGuruColors): Row {
        return Row.Builder()
            .setTitle(
                "${stationModel.formattedName} - ${
                    selectedFuel?.getPrice(carContext, stationModel.fuelStation) ?: ""
                }"
            )
            .setMetadata(
                Metadata.Builder()
                    .setPlace(
                        Place.Builder(
                            CarLocation.create(
                                stationModel.fuelStation.location.latitude,
                                stationModel.fuelStation.location.longitude
                            )
                        ).setMarker(
                            PlaceMarker.Builder().setColor(
                                CarColor.createCustom(
                                    theme.neutralWhite.toArgb(),
                                    theme.primary600.toArgb(),
                                )
                            ).build()
                        )
                            .build()
                    ).build()
            )
            .setBrowsable(true)
            .setOnClickListener {
                navigateToStation(
                    latitude = stationModel.fuelStation.location.latitude,
                    longitude = stationModel.fuelStation.location.longitude
                )
            }
            .addText(stationModel.formattedDirection)
            .addText(stationModel.formattedDistance)
            .build()
    }

    private fun navigateToStation(latitude: Double, longitude: Double) {
        val intent = Intent().apply {
            action = CarContext.ACTION_NAVIGATE
            data = "geo:$latitude,$longitude".toUri()
        }
        carContext.startCarApp(intent)
        screenManager.pop()
    }

    private fun checkPermissions() {
        val hasLocationPermissions = carContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED

        if (hasLocationPermissions) {
            hasLocationPermission = true
            carUiState = CarUiState(loading = true, permissionDenied = false)
            updateStationList()
            startLocationStateMonitoring()
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
                        carUiState = CarUiState(loading = true, permissionDenied = false)
                        updateStationList()
                    } else {
                        carUiState = CarUiState()
                    }
                    invalidate()
                }
            } catch (_: SecurityException) {
                carUiState = CarUiState()
                invalidate()
            }
        }
    }

    private fun startLocationStateMonitoring() {
        coroutineScope.launch {
            isLocationEnabledUseCase().collect { isEnabled ->
                if (!carUiState.permissionDenied && !carUiState.loading) {
                    val currentLocationDisabled = carUiState.locationDisabled
                    if (currentLocationDisabled == isEnabled) {
                        carUiState = carUiState.copy(locationDisabled = !isEnabled)
                        invalidate()
                    }
                }
            }
        }
    }

    override fun onGetTemplate(): Template {
        if (carUiState.permissionDenied) {
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

        if (carUiState.needsOnboarding) {
            return MessageTemplate.Builder(carContext.getString(R.string.onboarding_required_message))
                .setTitle(carContext.getString(R.string.onboarding_required_title))
                .setHeaderAction(Action.APP_ICON)
                .addAction(
                    Action.Builder()
                        .setTitle(carContext.getString(R.string.complete_onboarding))
                        .setOnClickListener {
                            updateStationList()
                        }
                        .build()
                )
                .build()
        }

        if (carUiState.locationDisabled) {
            return MessageTemplate.Builder(carContext.getString(R.string.location_disabled_message))
                .setTitle(carContext.getString(R.string.location_disabled_title))
                .setHeaderAction(Action.APP_ICON)
                .addAction(
                    Action.Builder()
                        .setTitle(carContext.getString(R.string.enable_location))
                        .setOnClickListener {
                            updateStationList()
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

        builder.setLoading(carUiState.loading)
        val theme = carContext.getAutomotiveThemeColor()

        if (!carUiState.loading) {
            builder.setLoading(false)
            val items = ItemList.Builder()

            carUiState.stations.forEach { station ->
                items.addItem(cafeRow(station, carUiState.selectedFuel, theme))
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
