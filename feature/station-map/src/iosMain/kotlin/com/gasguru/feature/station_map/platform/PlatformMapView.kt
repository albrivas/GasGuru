@file:Suppress("LongParameterList") // Actual del expect en commonMain; firma fija.
@file:OptIn(ExperimentalForeignApi::class)

package com.gasguru.feature.station_map.platform

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import androidx.compose.ui.zIndex
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.PriceCategory
import com.gasguru.core.ui.models.FuelStationUiModel
import com.gasguru.core.uikit.components.loading.GasGuruLoading
import com.gasguru.core.uikit.components.loading.GasGuruLoadingModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.feature.station_map.ui.model.GeoBounds
import com.gasguru.feature.station_map.ui.models.RouteUiModel
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2D
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.MapKit.MKMarkerAnnotationView
import platform.MapKit.MKOverlayProtocol
import platform.MapKit.MKOverlayRenderer
import platform.MapKit.MKPolyline
import platform.MapKit.MKPolylineRenderer
import platform.MapKit.addOverlay
import platform.MapKit.removeOverlay
import platform.UIKit.UIColor
import platform.darwin.NSObject

private const val STATION_MARKER_REUSE_ID = "station_marker"

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
    onStationClick: (Int) -> Unit,
    onMapCentered: () -> Unit,
    onUserLocationCentered: () -> Unit,
    modifier: Modifier,
) {
    val currentOnStationClick by rememberUpdatedState(onStationClick)
    val currentOnMapCentered by rememberUpdatedState(onMapCentered)
    val currentOnUserLocationCentered by rememberUpdatedState(onUserLocationCentered)

    val mapDelegate = remember {
        object : NSObject(), MKMapViewDelegateProtocol {
            var lastRoute: RouteUiModel? = null
            var currentPolyline: MKPolyline? = null

            override fun mapView(
                mapView: MKMapView,
                didSelectAnnotationView: MKAnnotationView,
            ) {
                val annotation = didSelectAnnotationView.annotation
                if (annotation is StationAnnotation) {
                    currentOnStationClick(annotation.stationId)
                }
            }

            override fun mapView(
                mapView: MKMapView,
                rendererForOverlay: MKOverlayProtocol,
            ): MKOverlayRenderer {
                if (rendererForOverlay is MKPolyline) {
                    return MKPolylineRenderer(overlay = rendererForOverlay).apply {
                        strokeColor = UIColor(red = 0.0, green = 0.48, blue = 1.0, alpha = 1.0)
                        lineWidth = 6.0
                    }
                }
                return MKOverlayRenderer(overlay = rendererForOverlay)
            }

            override fun mapView(
                mapView: MKMapView,
                viewForAnnotation: MKAnnotationProtocol,
            ): MKAnnotationView? {
                if (viewForAnnotation !is StationAnnotation) return null
                val view = (
                    mapView.dequeueReusableAnnotationViewWithIdentifier(
                        identifier = STATION_MARKER_REUSE_ID,
                    ) as? MKMarkerAnnotationView
                    )
                    ?: MKMarkerAnnotationView(
                        annotation = viewForAnnotation,
                        reuseIdentifier = STATION_MARKER_REUSE_ID,
                    )
                view.annotation = viewForAnnotation
                view.canShowCallout = false
                view.markerTintColor = viewForAnnotation.priceCategory.toMarkerColor()
                return view
            }
        }
    }

    val mapView = remember {
        MKMapView().apply {
            delegate = mapDelegate
            showsCompass = false
            showsScale = false
            showsTraffic = false
        }
    }

    LaunchedEffect(mapBounds, shouldCenterMap) {
        if (mapBounds != null && shouldCenterMap) {
            mapView.setRegion(region = mapBounds.toMKCoordinateRegion(), animated = true)
            currentOnMapCentered()
        }
    }

    LaunchedEffect(userLocationToCenter) {
        if (userLocationToCenter != null) {
            mapView.setRegion(
                region = userLocationToCenter.toMKCoordinateRegionCentered(),
                animated = true,
            )
            currentOnUserLocationCentered()
        }
    }

    DisposableEffect(Unit) {
        onDispose { mapView.delegate = null }
    }

    Box(modifier = modifier) {
        UIKitView<MKMapView>(
            factory = { mapView },
            update = { mv ->
                mv.showsUserLocation = isLocationPermissionGranted

                // Diff annotations: add new, remove stale
                val newStationIds = stations.map { it.fuelStation.idServiceStation }.toSet()
                val existingAnnotations = mv.annotations.filterIsInstance<StationAnnotation>()
                val existingIds = existingAnnotations.map { it.stationId }.toSet()

                existingAnnotations
                    .filter { it.stationId !in newStationIds }
                    .forEach { mv.removeAnnotation(it) }

                stations
                    .filter { it.fuelStation.idServiceStation !in existingIds }
                    .forEach { station ->
                        mv.addAnnotation(
                            StationAnnotation(
                                stationId = station.fuelStation.idServiceStation,
                                priceCategory = station.fuelStation.priceCategory,
                                storedCoordinate = station.fuelStation.location
                                    .toCLLocationCoordinate2D(),
                                stationTitle = station.fuelStation.brandStationName,
                            )
                        )
                    }

                // Replace polyline overlay only when the route reference changes
                if (route !== mapDelegate.lastRoute) {
                    mapDelegate.currentPolyline?.let { mv.removeOverlay(overlay = it) }
                    mapDelegate.currentPolyline = null
                    val routePoints = route?.route
                    if (routePoints != null && routePoints.isNotEmpty()) {
                        val newPolyline = createMKPolyline(routePoints)
                        mv.addOverlay(overlay = newPolyline)
                        mapDelegate.currentPolyline = newPolyline
                    }
                    mapDelegate.lastRoute = route
                }
            },
            modifier = Modifier.fillMaxSize(),
            properties = UIKitInteropProperties(
                interactionMode = UIKitInteropInteractionMode.NonCooperative,
                isNativeAccessibilityEnabled = false,
            ),
        )

        if (loading) {
            GasGuruLoading(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GasGuruTheme.colors.neutralBlack.copy(alpha = 0.5f))
                    .zIndex(1f),
                model = GasGuruLoadingModel(color = GasGuruTheme.colors.primary800),
            )
        }
    }
}

private class StationAnnotation(
    val stationId: Int,
    val priceCategory: PriceCategory,
    private val storedCoordinate: CValue<CLLocationCoordinate2D>,
    private val stationTitle: String?,
) : NSObject(), MKAnnotationProtocol {

    override fun coordinate(): CValue<CLLocationCoordinate2D> = storedCoordinate
    override fun title(): String? = stationTitle
    override fun subtitle(): String? = null
}

private fun PriceCategory.toMarkerColor(): UIColor = when (this) {
    PriceCategory.CHEAP -> UIColor(red = 0.2, green = 0.78, blue = 0.35, alpha = 1.0)
    PriceCategory.NORMAL -> UIColor(red = 1.0, green = 0.58, blue = 0.0, alpha = 1.0)
    PriceCategory.EXPENSIVE -> UIColor(red = 1.0, green = 0.23, blue = 0.19, alpha = 1.0)
    PriceCategory.NONE -> UIColor(red = 0.56, green = 0.56, blue = 0.58, alpha = 1.0)
}
