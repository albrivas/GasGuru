@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.gasguru.core.data.repository.route

import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.Route
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import platform.CoreLocation.CLLocationCoordinate2D
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSDateComponentsFormatter
import platform.Foundation.NSDateComponentsFormatterUnitsStyleAbbreviated
import platform.Foundation.NSMakeRange
import platform.MapKit.MKDistanceFormatter
import platform.MapKit.MKDistanceFormatterUnitStyleAbbreviated
import platform.MapKit.MKPolyline
import platform.MapKit.MKRoute

internal fun MKRoute.toDomainRoute(): Route = Route(
    route = polyline.toLatLngList(),
    distanceText = mkDistanceFormatter.stringFromDistance(distance),
    durationText = mkDurationFormatter.stringFromTimeInterval(expectedTravelTime) ?: "",
)

private val mkDistanceFormatter = MKDistanceFormatter().apply {
    unitStyle = MKDistanceFormatterUnitStyleAbbreviated
}

private val mkDurationFormatter = NSDateComponentsFormatter().apply {
    allowedUnits = NSCalendarUnitHour or NSCalendarUnitMinute
    unitsStyle = NSDateComponentsFormatterUnitsStyleAbbreviated
}

private fun MKPolyline.toLatLngList(): List<LatLng> = memScoped {
    val count = pointCount.toInt()
    if (count == 0) return@memScoped emptyList()
    val coordsBuffer = allocArray<CLLocationCoordinate2D>(count)
    getCoordinates(coordsBuffer, NSMakeRange(0u, count.toULong()))
    List(count) { index ->
        LatLng(
            latitude = coordsBuffer[index].latitude,
            longitude = coordsBuffer[index].longitude,
        )
    }
}
