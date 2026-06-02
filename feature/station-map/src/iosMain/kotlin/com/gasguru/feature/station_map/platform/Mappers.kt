package com.gasguru.feature.station_map.platform

import com.gasguru.core.model.data.LatLng
import com.gasguru.feature.station_map.ui.model.GeoBounds
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCoordinateRegionMake
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKCoordinateSpanMake
import platform.MapKit.MKPolyline
import kotlin.math.abs

@OptIn(ExperimentalForeignApi::class)
internal fun LatLng.toCLLocationCoordinate2D(): CValue<CLLocationCoordinate2D> =
    CLLocationCoordinate2DMake(latitude, longitude)

@OptIn(ExperimentalForeignApi::class)
internal fun GeoBounds.toMKCoordinateRegion(): CValue<platform.MapKit.MKCoordinateRegion> {
    val centerLat = (southwest.latitude + northeast.latitude) / 2.0
    val centerLng = (southwest.longitude + northeast.longitude) / 2.0
    val spanLat = abs(northeast.latitude - southwest.latitude) * 1.4
    val spanLng = abs(northeast.longitude - southwest.longitude) * 1.4
    return MKCoordinateRegionMake(
        CLLocationCoordinate2DMake(centerLat, centerLng),
        MKCoordinateSpanMake(spanLat, spanLng),
    )
}

@OptIn(ExperimentalForeignApi::class)
internal fun LatLng.toMKCoordinateRegionCentered(): CValue<platform.MapKit.MKCoordinateRegion> =
    MKCoordinateRegionMakeWithDistance(toCLLocationCoordinate2D(), 1500.0, 1500.0)

@OptIn(ExperimentalForeignApi::class)
internal fun createMKPolyline(points: List<LatLng>): MKPolyline = memScoped {
    val coordsBuffer = allocArray<CLLocationCoordinate2D>(points.size) { index ->
        latitude = points[index].latitude
        longitude = points[index].longitude
    }
    MKPolyline.polylineWithCoordinates(coordsBuffer, points.size.toULong())
}
