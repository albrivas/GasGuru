package com.gasguru.auto.ui.favoritestation

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.PlaceListMapTemplate
import androidx.car.app.model.Template
import com.gasguru.auto.common.getAutomotiveThemeColor
import com.gasguru.auto.di.CarScreenEntryPoint
import com.gasguru.auto.navigation.StationNavigationHelper
import com.gasguru.auto.ui.component.StationRowComponent
import com.gasguru.core.domain.fuelstation.GetFavoriteStationsUseCase
import com.gasguru.core.domain.location.GetCurrentLocationUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.ui.mapper.toUiModel
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import com.gasguru.core.ui.R as CoreUiR

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
                items.addItem(
                    StationRowComponent.createStationRow(
                        stationModel = station,
                        selectedFuel = uiState.selectedFuel,
                        theme = theme,
                        carContext = carContext
                    ) { lat, lng ->
                        StationNavigationHelper.navigateToStationAndPopScreen(
                            screen = this@FavoriteStationsScreen,
                            latitude = lat,
                            longitude = lng
                        )
                    }
                )
            }

            builder.setItemList(items.build())
        }

        builder.setCurrentLocationEnabled(true)
        builder.setOnContentRefreshListener { updateStationList() }

        return builder.build()
    }
}
