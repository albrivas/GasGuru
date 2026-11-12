@file:Suppress("LongParameterList") // Actual del expect en commonMain; firma fija.
@file:OptIn(ExperimentalForeignApi::class)

package com.gasguru.feature.station_map.platform

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import androidx.compose.ui.zIndex
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.PriceCategory
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
import com.gasguru.feature.station_map.ui.model.GeoBounds
import com.gasguru.feature.station_map.ui.models.RouteUiModel
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCSignatureOverride
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocationCoordinate2D
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.MapKit.MKOverlayProtocol
import platform.MapKit.MKOverlayRenderer
import platform.MapKit.MKPolyline
import platform.MapKit.MKPolylineRenderer
import platform.MapKit.addOverlay
import platform.MapKit.removeOverlay
import platform.UIKit.UIColor
import platform.UIKit.UIImage
import platform.UIKit.UIUserInterfaceStyle
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
    customPinLocation: LatLng?,
    onStationClick: (Int) -> Unit,
    onMapCentered: () -> Unit,
    onUserLocationCentered: () -> Unit,
    onCameraIdle: (bounds: GeoBounds, movedByUser: Boolean) -> Unit,
    onMapLongClick: (location: LatLng) -> Unit,
    modifier: Modifier,
) {
    val currentOnStationClick by rememberUpdatedState(onStationClick)
    val currentOnMapCentered by rememberUpdatedState(onMapCentered)
    val currentOnUserLocationCentered by rememberUpdatedState(onUserLocationCentered)
    val currentOnCameraIdle by rememberUpdatedState(onCameraIdle)
    val currentOnMapLongClick by rememberUpdatedState(onMapLongClick)
    val isDark = GasGuruTheme.colors.isDark

    val markerImages = remember { mutableStateMapOf<Int, UIImage>() }

    val userGestureDetector = remember { MapUserGestureDetector() }
    val longPressHandler = remember {
        MapLongPressHandler(onLongPress = { pressedLocation -> currentOnMapLongClick(pressedLocation) })
    }
    var customPinImage by remember { mutableStateOf<UIImage?>(null) }

    val mapDelegate = remember {
        object : NSObject(), MKMapViewDelegateProtocol {
            var lastRoute: RouteUiModel? = null
            var currentPolyline: MKPolyline? = null
            val cameraIdleTracker = CameraIdleTracker(gestureDetector = userGestureDetector) { bounds, movedByUser ->
                currentOnCameraIdle(bounds, movedByUser)
            }

            @ObjCSignatureOverride
            override fun mapView(
                mapView: MKMapView,
                regionDidChangeAnimated: Boolean,
            ) {
                cameraIdleTracker.regionDidChange(mapView = mapView)
            }

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
            ): MKAnnotationView? = when (viewForAnnotation) {
                is CustomPinAnnotation -> mapView.customPinAnnotationView(
                    annotation = viewForAnnotation,
                    pinImage = customPinImage,
                )
                is StationAnnotation -> mapView.stationAnnotationView(
                    annotation = viewForAnnotation,
                    markerImage = markerImages[viewForAnnotation.stationId],
                )
                else -> null
            }
        }
    }

    val mapView = remember {
        MKMapView().apply {
            delegate = mapDelegate
            showsCompass = false
            showsScale = false
            showsTraffic = false
            userGestureDetector.attachTo(mapView = this)
            longPressHandler.attachTo(mapView = this)
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
        // Hidden area: renders each StationMarker composable and captures it to UIImage.
        // size(0.dp) + wrapContentSize(unbounded) keeps layout impact to zero.
        Box(
            modifier = Modifier
                .size(0.dp)
                .wrapContentSize(align = Alignment.TopStart, unbounded = true),
        ) {
            stations.forEach { station ->
                val isSelected = selectedStationId == station.fuelStation.idServiceStation
                StationMarkerCapture(
                    station = station,
                    isSelected = isSelected,
                    userSelectedFuelType = userSelectedFuelType,
                    onImageCaptured = { image ->
                        markerImages[station.fuelStation.idServiceStation] = image
                    },
                )
            }
            CustomPinCapture(onImageCaptured = { image -> customPinImage = image })
        }

        UIKitView<MKMapView>(
            factory = { mapView },
            update = { mv ->
                mv.overrideUserInterfaceStyle =
                    if (isDark) {
                        UIUserInterfaceStyle.UIUserInterfaceStyleDark
                    } else {
                        UIUserInterfaceStyle.UIUserInterfaceStyleLight
                    }
                mv.showsUserLocation = isLocationPermissionGranted

                mv.syncStationAnnotations(stations = stations, markerImages = markerImages)
                mv.syncCustomPin(pinLocation = customPinLocation, pinImage = customPinImage)

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

@Composable
private fun CustomPinCapture(onImageCaptured: (UIImage) -> Unit) {
    val graphicsLayer = rememberGraphicsLayer()
    Box(
        modifier = Modifier
            .wrapContentSize()
            .drawWithContent {
                graphicsLayer.record { this@drawWithContent.drawContent() }
            },
    ) {
        CustomLocationMarker(model = CustomLocationMarkerModel(contentDescription = null))
    }
    LaunchedEffect(Unit) {
        graphicsLayer.toImageBitmap().toUIImage()?.let(onImageCaptured)
    }
}

@Composable
private fun StationMarkerCapture(
    station: FuelStationUiModel,
    isSelected: Boolean,
    userSelectedFuelType: FuelType?,
    onImageCaptured: (UIImage) -> Unit,
) {
    val graphicsLayer = rememberGraphicsLayer()
    val price = userSelectedFuelType.getPrice(fuelStation = station.fuelStation)
    val color = station.fuelStation.priceCategory.toColor()

    Box(
        modifier = Modifier
            .wrapContentSize()
            .drawWithContent {
                graphicsLayer.record { this@drawWithContent.drawContent() }
            },
    ) {
        StationMarker(
            model = StationMarkerModel(
                icon = station.brandIcon,
                price = price,
                color = color,
                isSelected = isSelected,
            ),
        )
    }

    LaunchedEffect(station.fuelStation.idServiceStation, price, color, isSelected) {
        val imageBitmap = graphicsLayer.toImageBitmap()
        imageBitmap.toUIImage()?.let(onImageCaptured)
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

private fun MKMapView.stationAnnotationView(
    annotation: StationAnnotation,
    markerImage: UIImage?,
): MKAnnotationView {
    val view = dequeueReusableAnnotationViewWithIdentifier(identifier = STATION_MARKER_REUSE_ID)
        ?: MKAnnotationView(annotation = annotation, reuseIdentifier = STATION_MARKER_REUSE_ID)
    view.annotation = annotation
    view.canShowCallout = false
    val imgHeight = view.image?.size?.useContents { height } ?: 0.0
    view.centerOffset = platform.CoreGraphics.CGPointMake(0.0, -(imgHeight / 2.0))
    view.image = markerImage ?: annotation.priceCategory.toFallbackImage()
    return view
}

private fun PriceCategory.toFallbackImage(): UIImage? = null

// Diffs station annotations against the current list and refreshes on-screen views once their UIImage arrives.
private fun MKMapView.syncStationAnnotations(
    stations: List<FuelStationUiModel>,
    markerImages: Map<Int, UIImage>,
) {
    val newStationIds = stations.map { it.fuelStation.idServiceStation }.toSet()
    val existingAnnotations = annotations.filterIsInstance<StationAnnotation>()
    val existingIds = existingAnnotations.map { it.stationId }.toSet()

    existingAnnotations
        .filter { it.stationId !in newStationIds }
        .forEach { removeAnnotation(it) }

    stations
        .filter { it.fuelStation.idServiceStation !in existingIds }
        .forEach { station ->
            addAnnotation(
                StationAnnotation(
                    stationId = station.fuelStation.idServiceStation,
                    priceCategory = station.fuelStation.priceCategory,
                    storedCoordinate = station.fuelStation.location.toCLLocationCoordinate2D(),
                    stationTitle = station.fuelStation.brandStationName,
                ),
            )
        }

    existingAnnotations
        .filter { it.stationId in newStationIds }
        .forEach { annotation ->
            markerImages[annotation.stationId]?.let { image ->
                viewForAnnotation(annotation)?.let { view ->
                    view.image = image
                    val h = image.size.useContents { height }
                    view.centerOffset = platform.CoreGraphics.CGPointMake(0.0, -(h / 2.0))
                }
            }
        }
}
