package com.gasguru.auto.ui.mainmenu

import android.Manifest
import android.content.pm.PackageManager
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.ItemList
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.ParkedOnlyOnClickListener
import androidx.car.app.model.PlaceListMapTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.gasguru.auto.analytics.trackAutoFavoriteStationsOpened
import com.gasguru.auto.analytics.trackAutoNearbyStationsOpened
import com.gasguru.auto.common.R
import com.gasguru.auto.ui.favoritestation.FavoriteSortCriteriaScreen
import com.gasguru.auto.ui.nearbystation.NearbyStationsScreen
import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.domain.location.IsLocationEnabledUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.gasguru.core.ui.R as CoreUiR

class MapAutomotiveScreen(carContext: CarContext) : Screen(carContext), KoinComponent {

    private val analyticsHelper: AnalyticsHelper by inject()
    private val isLocationEnabledUseCase: IsLocationEnabledUseCase by inject()

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var locationEnabledJob: Job? = null

    private var hasLocationPermission = false
    private var uiState = MainMenuUiState(
        permissionDenied = true
    )

    init {
        lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    coroutineScope.cancel()
                }
            }
        )
        checkPermissions()
    }

    private fun createStationListOptions(): ItemList {
        val items = ItemList.Builder()

        items.addItem(
            Row.Builder()
                .setTitle(carContext.getString(CoreUiR.string.nearby_stations))
                .setBrowsable(true)
                .setOnClickListener {
                    analyticsHelper.trackAutoNearbyStationsOpened()
                    screenManager.push(NearbyStationsScreen(carContext))
                }
                .build()
        )

        items.addItem(
            Row.Builder()
                .setTitle(carContext.getString(CoreUiR.string.favorites))
                .setBrowsable(true)
                .setOnClickListener {
                    analyticsHelper.trackAutoFavoriteStationsOpened()
                    screenManager.push(FavoriteSortCriteriaScreen(carContext))
                }
                .build()
        )

        return items.build()
    }

    private fun checkPermissions() {
        hasLocationPermission = carContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

        if (hasLocationPermission) {
            observeLocationEnabled()
        } else {
            locationEnabledJob?.cancel()
            uiState = MainMenuUiState(permissionDenied = true)
            invalidate()
        }
    }

    private fun requestLocationPermission() {
        try {
            carContext.requestPermissions(
                listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) { granted, _ ->
                hasLocationPermission = granted.contains(Manifest.permission.ACCESS_FINE_LOCATION)
                if (hasLocationPermission) {
                    observeLocationEnabled()
                } else {
                    uiState = MainMenuUiState(permissionDenied = true)
                    invalidate()
                }
            }
        } catch (_: SecurityException) {
            uiState = MainMenuUiState(permissionDenied = true)
            invalidate()
        }
    }

    private fun observeLocationEnabled() {
        locationEnabledJob?.cancel()
        locationEnabledJob = coroutineScope.launch {
            isLocationEnabledUseCase().collect { locationEnabled ->
                uiState = MainMenuUiState(
                    permissionDenied = false,
                    locationDisabled = !locationEnabled
                )
                invalidate()
            }
        }
    }

    override fun onGetTemplate(): Template = when {
        uiState.permissionDenied -> buildPermissionDeniedTemplate()
        uiState.locationDisabled -> buildLocationDisabledTemplate()
        uiState.needsOnboarding -> buildOnboardingTemplate()
        else -> buildMapTemplate()
    }

    private fun buildPermissionDeniedTemplate(): Template =
        MessageTemplate.Builder(carContext.getString(R.string.permission_required_message))
            .setTitle(carContext.getString(R.string.permission_required_title))
            .setHeaderAction(Action.APP_ICON)
            .setIcon(CarIcon.ERROR)
            .addAction(
                Action.Builder()
                    .setTitle(carContext.getString(R.string.grant_permissions))
                    .setOnClickListener(ParkedOnlyOnClickListener.create { requestLocationPermission() })
                    .build()
            )
            .build()

    private fun buildLocationDisabledTemplate(): Template =
        MessageTemplate.Builder(carContext.getString(R.string.location_disabled_message))
            .setTitle(carContext.getString(R.string.location_disabled_title))
            .setHeaderAction(Action.APP_ICON)
            .setIcon(CarIcon.ERROR)
            .build()

    private fun buildOnboardingTemplate(): Template =
        MessageTemplate.Builder(carContext.getString(R.string.onboarding_required_message))
            .setTitle(carContext.getString(R.string.onboarding_required_title))
            .setHeaderAction(Action.APP_ICON)
            .addAction(
                Action.Builder()
                    .setTitle(carContext.getString(R.string.complete_onboarding))
                    .setOnClickListener(ParkedOnlyOnClickListener.create { checkPermissions() })
                    .build()
            )
            .build()

    private fun buildMapTemplate(): Template {
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
