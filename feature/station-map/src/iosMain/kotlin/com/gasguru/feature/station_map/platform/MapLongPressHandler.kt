package com.gasguru.feature.station_map.platform

import com.gasguru.core.model.data.LatLng
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGPointMake
import platform.CoreLocation.CLLocationCoordinate2D
import platform.Foundation.NSSelectorFromString
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKAnnotationViewZPriorityMax
import platform.MapKit.MKFeatureDisplayPriorityRequired
import platform.MapKit.MKMapView
import platform.UIKit.UIGestureRecognizerStateBegan
import platform.UIKit.UIImage
import platform.UIKit.UILongPressGestureRecognizer
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
internal class MapLongPressHandler(
    private val onLongPress: (location: LatLng) -> Unit,
) : NSObject() {

    fun attachTo(mapView: MKMapView) {
        mapView.addGestureRecognizer(
            UILongPressGestureRecognizer(target = this, action = NSSelectorFromString("onLongPress:")),
        )
    }

    @OptIn(BetaInteropApi::class)
    @ObjCAction
    fun onLongPress(recognizer: UILongPressGestureRecognizer) {
        if (recognizer.state != UIGestureRecognizerStateBegan) return
        val mapView = recognizer.view as? MKMapView ?: return
        val pressedPoint = recognizer.locationInView(view = mapView)
        val pressedLocation = mapView.convertPoint(point = pressedPoint, toCoordinateFromView = mapView)
            .useContents { LatLng(latitude = latitude, longitude = longitude) }
        onLongPress(pressedLocation)
    }
}

@OptIn(ExperimentalForeignApi::class)
internal class CustomPinAnnotation(
    val location: LatLng,
) : NSObject(), MKAnnotationProtocol {

    override fun coordinate(): CValue<CLLocationCoordinate2D> = location.toCLLocationCoordinate2D()
    override fun title(): String? = null
    override fun subtitle(): String? = null
}

private const val CUSTOM_PIN_REUSE_ID = "custom_location_pin"

@OptIn(ExperimentalForeignApi::class)
internal fun MKMapView.customPinAnnotationView(
    annotation: CustomPinAnnotation,
    pinImage: UIImage?,
): MKAnnotationView {
    val pinView = dequeueReusableAnnotationViewWithIdentifier(identifier = CUSTOM_PIN_REUSE_ID)
        ?: MKAnnotationView(annotation = annotation, reuseIdentifier = CUSTOM_PIN_REUSE_ID)
    pinView.annotation = annotation
    pinView.canShowCallout = false
    pinView.displayPriority = MKFeatureDisplayPriorityRequired
    pinView.zPriority = MKAnnotationViewZPriorityMax
    pinView.applyPinImage(pinImage = pinImage)
    return pinView
}

// Keeps a single custom pin annotation in sync with the requested location.
@OptIn(ExperimentalForeignApi::class)
internal fun MKMapView.syncCustomPin(pinLocation: LatLng?, pinImage: UIImage?) {
    val existingPins = annotations.filterIsInstance<CustomPinAnnotation>()
    existingPins
        .filter { existingPin -> existingPin.location != pinLocation }
        .forEach { stalePin -> removeAnnotation(stalePin) }
    if (pinLocation != null && existingPins.none { existingPin -> existingPin.location == pinLocation }) {
        addAnnotation(CustomPinAnnotation(location = pinLocation))
    }
    annotations.filterIsInstance<CustomPinAnnotation>().forEach { currentPin ->
        viewForAnnotation(currentPin)?.applyPinImage(pinImage = pinImage)
    }
}

// The pin tip is the bottom-center of the image, so lift the view by half its height.
@OptIn(ExperimentalForeignApi::class)
private fun MKAnnotationView.applyPinImage(pinImage: UIImage?) {
    image = pinImage
    val pinHeight = pinImage?.size?.useContents { height } ?: 0.0
    centerOffset = CGPointMake(0.0, -(pinHeight / 2.0))
}
