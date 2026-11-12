@file:Suppress(
    "LongParameterList", // Actual del expect en commonMain; firma fija.
    "MaximumLineLength", // Línea con expresión Compose larga; partirla rompe legibilidad.
    "MaxLineLength",
)

package com.gasguru.feature.station_map.platform

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.gasguru.core.common.centerOnLocation
import com.gasguru.core.common.centerOnMap
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.ui.getPrice
import com.gasguru.core.ui.models.FuelStationUiModel
import com.gasguru.core.ui.toColor
import com.gasguru.core.uikit.components.loading.GasGuruLoading
import com.gasguru.core.uikit.components.loading.GasGuruLoadingModel
import com.gasguru.core.uikit.components.marker.CustomLocationMarker
import com.gasguru.core.uikit.components.marker.CustomLocationMarkerModel
import com.gasguru.core.uikit.components.marker.StationMarker
import com.gasguru.core.uikit.components.marker.StationMarkerModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.utils.maestroTestTag
import com.gasguru.feature.station_map.BuildConfig
import com.gasguru.feature.station_map.generated.resources.Res
import com.gasguru.feature.station_map.generated.resources.custom_location_marker_description
import com.gasguru.feature.station_map.ui.model.GeoBounds
import com.gasguru.feature.station_map.ui.models.RouteUiModel
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import org.jetbrains.compose.resources.stringResource
import com.google.android.gms.maps.model.LatLng as GoogleLatLng

private const val CUSTOM_PIN_Z_INDEX = 10f

@Composable
actual fun PlatformMapView(
    stations: List<FuelStationUiModel>,
    route: RouteUiModel?,
    selectedStationId: Int,
    userSelectedFuelType: FuelType?,
    loading: Boolean,
    isLocationPermissionGranted: Boolean,
    mapBounds: GeoBounds?,
    shouldCenterMap: Boolean,
    userLocationToCenter: LatLng?,
    customPinLocation: LatLng?,
    onStationClick: (Int) -> Unit,
    onMapCentered: () -> Unit,
    onUserLocationCentered: () -> Unit,
    onCameraIdle: (bounds: GeoBounds, movedByUser: Boolean) -> Unit,
    onMapLongClick: (location: LatLng) -> Unit,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(GoogleLatLng(40.0, -4.0), 5.5f)
    }

    val currentOnCameraIdle by rememberUpdatedState(onCameraIdle)
    val currentOnMapLongClick by rememberUpdatedState(onMapLongClick)
    val customPinDescription = stringResource(resource = Res.string.custom_location_marker_description)
    LaunchedEffect(cameraState) {
        snapshotFlow { cameraState.isMoving }.collect { isMoving ->
            if (isMoving) return@collect
            val visibleBounds = cameraState.projection?.visibleRegion?.latLngBounds ?: return@collect
            currentOnCameraIdle(
                visibleBounds.toGeoBounds(),
                cameraState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE,
            )
        }
    }

    LaunchedEffect(mapBounds, shouldCenterMap) {
        if (mapBounds != null && shouldCenterMap) {
            cameraState.centerOnMap(bounds = mapBounds.toGoogleLatLngBounds(), padding = 60)
            onMapCentered()
        }
    }

    LaunchedEffect(userLocationToCenter) {
        if (userLocationToCenter != null) {
            cameraState.centerOnLocation(location = userLocationToCenter.toGoogleLatLng())
            onUserLocationCentered()
        }
    }

    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false,
                compassEnabled = false,
                mapToolbarEnabled = false,
            )
        )
    }
    val mapProperties = remember(isLocationPermissionGranted) {
        MapProperties(isMyLocationEnabled = isLocationPermissionGranted)
    }

    Box(modifier = modifier) {
        if (loading) {
            GasGuruLoading(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GasGuruTheme.colors.neutralBlack.copy(alpha = 0.5f))
                    .zIndex(1f)
                    .maestroTestTag("loading_map"),
                model = GasGuruLoadingModel(color = GasGuruTheme.colors.primary800),
            )
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            googleMapOptionsFactory = { GoogleMapOptions().mapId(BuildConfig.googleStyleId) },
            uiSettings = uiSettings,
            properties = mapProperties,
            contentPadding = PaddingValues(bottom = 60.dp),
            mapColorScheme = if (GasGuruTheme.colors.isDark) ComposeMapColorScheme.DARK else ComposeMapColorScheme.LIGHT,
            onMapLongClick = { pressedLocation ->
                currentOnMapLongClick(
                    LatLng(latitude = pressedLocation.latitude, longitude = pressedLocation.longitude),
                )
            },
        ) {
            customPinLocation?.let { pinLocation ->
                val pinState = remember(pinLocation) { MarkerState(position = pinLocation.toGoogleLatLng()) }
                MarkerComposable(
                    keys = arrayOf(pinLocation),
                    state = pinState,
                    anchor = Offset(x = 0.5f, y = 1f),
                    zIndex = CUSTOM_PIN_Z_INDEX,
                    contentDescription = customPinDescription,
                ) {
                    CustomLocationMarker(model = CustomLocationMarkerModel(contentDescription = null))
                }
            }
            route?.let {
                Polyline(
                    points = it.route.map { point -> point.toGoogleLatLng() },
                    width = 20f,
                    jointType = JointType.ROUND,
                    color = GasGuruTheme.colors.primary900,
                )
            }
            stations.forEach { station ->
                val priceCategoryColor = station.fuelStation.priceCategory.toColor()
                val markerState = remember(station.fuelStation.idServiceStation) {
                    MarkerState(position = station.fuelStation.location.toGoogleLatLng())
                }
                val isSelected = selectedStationId == station.fuelStation.idServiceStation
                val price by remember(userSelectedFuelType, station) {
                    derivedStateOf {
                        userSelectedFuelType.getPrice(
                            context = context,
                            fuelStation = station.fuelStation,
                        )
                    }
                }
                val color by remember(station) { derivedStateOf { priceCategoryColor } }

                MarkerComposable(
                    keys = arrayOf(station.fuelStation.idServiceStation, price, color),
                    state = markerState,
                    onClick = {
                        onStationClick(station.fuelStation.idServiceStation)
                        false
                    },
                    contentDescription = "Marker ${station.fuelStation.brandStationName}",
                ) {
                    StationMarker(
                        model = StationMarkerModel(
                            icon = station.brandIcon,
                            price = price,
                            color = color,
                            isSelected = isSelected,
                        )
                    )
                }
            }
        }
    }
}
