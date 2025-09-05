package com.gasguru.auto.ui.favoritestation

import android.content.Intent
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
import androidx.compose.ui.graphics.toArgb
import androidx.core.net.toUri
import com.gasguru.auto.common.getAutomotiveThemeColor
import com.gasguru.auto.di.CarScreenEntryPoint
import com.gasguru.core.domain.fuelstation.GetFavoriteStationsUseCase
import com.gasguru.core.domain.location.GetCurrentLocationUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.getPrice
import com.gasguru.core.ui.models.FuelStationUiModel
import com.gasguru.core.ui.toUiModel
import com.gasguru.core.uikit.theme.GasGuruColors
import dagger.hilt.android.EntryPointAccessors
import com.gasguru.core.ui.R as CoreUiR
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class FavoriteStationsScreen(carContext: CarContext) : Screen(carContext) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var uiState = FavoriteStationsUiState(
        loading = true
    )

    private val getCurrentLocationUseCase: GetCurrentLocationUseCase by lazy {
        EntryPointAccessors.fromApplication(
            carContext.applicationContext,
            CarScreenEntryPoint::class.java
        ).getCurrentLocationUseCase()
    }

    private val getFavoriteStationsUseCase: GetFavoriteStationsUseCase by lazy {
        EntryPointAccessors.fromApplication(
            carContext.applicationContext,
            CarScreenEntryPoint::class.java
        ).getFavoriteStationsUseCase()
    }

    private val getUserDataUseCase: GetUserDataUseCase by lazy {
        EntryPointAccessors.fromApplication(
            carContext.applicationContext,
            CarScreenEntryPoint::class.java
        ).getUserDataUseCase()
    }

    init {
        updateStationList()
    }

    private fun updateStationList() {
        coroutineScope.launch {
            val location = getCurrentLocationUseCase()
            if (location != null) {
                combine(
                    getUserDataUseCase(),
                    getFavoriteStationsUseCase(userLocation = location)
                ) { userData, userWithFavorites ->
                    uiState = FavoriteStationsUiState(
                        loading = false,
                        stations = userWithFavorites.favoriteStations.map { it.toUiModel() },
                        selectedFuel = userData.fuelSelection
                    )
                    invalidate()
                }.launchIn(coroutineScope)
            } else {
                coroutineScope.launch {
                    getUserDataUseCase().collect { userData ->
                        uiState = FavoriteStationsUiState(
                            loading = false,
                            stations = emptyList(),
                            selectedFuel = userData.fuelSelection
                        )
                        invalidate()
                    }
                }
            }
        }
    }

    private fun stationRow(stationModel: FuelStationUiModel, selectedFuel: FuelType?, theme: GasGuruColors): Row {
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

    override fun onGetTemplate(): Template {
        val builder = PlaceListMapTemplate
            .Builder()
            .setTitle(carContext.getString(CoreUiR.string.favorites))
            .setHeaderAction(Action.BACK)

        builder.setLoading(uiState.loading)
        val theme = carContext.getAutomotiveThemeColor()

        if (!uiState.loading) {
            builder.setLoading(false)
            val items = ItemList.Builder()

            uiState.stations.forEach { station ->
                items.addItem(stationRow(station, uiState.selectedFuel, theme))
            }

            builder.setItemList(items.build())
        }

        builder.setCurrentLocationEnabled(true)
        builder.setOnContentRefreshListener { updateStationList() }

        return builder.build()
    }
}