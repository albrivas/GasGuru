package com.gasguru.feature.station_map.platform

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import com.gasguru.core.model.data.LatLng
import com.gasguru.feature.station_map.ui.model.GeoBounds
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.useContents
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Pixmap
import platform.CoreGraphics.CGBitmapContextCreate
import platform.CoreGraphics.CGBitmapContextCreateImage
import platform.CoreGraphics.CGBitmapContextGetBytesPerRow
import platform.CoreGraphics.CGBitmapContextGetData
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGColorSpaceRelease
import platform.CoreGraphics.CGContextRelease
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGImageRef
import platform.CoreGraphics.CGImageRelease
import platform.CoreGraphics.kCGBitmapByteOrder32Big
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCoordinateRegionMake
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKCoordinateSpanMake
import platform.MapKit.MKMapView
import platform.MapKit.MKPolyline
import platform.UIKit.UIImage
import platform.UIKit.UIImageOrientation
import platform.UIKit.UIScreen
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
internal fun MKMapView.visibleGeoBounds(): GeoBounds? = region.useContents {
    if (span.latitudeDelta <= 0.0 || span.longitudeDelta <= 0.0) {
        null
    } else {
        GeoBounds(
            southwest = LatLng(
                latitude = center.latitude - span.latitudeDelta / 2.0,
                longitude = center.longitude - span.longitudeDelta / 2.0,
            ),
            northeast = LatLng(
                latitude = center.latitude + span.latitudeDelta / 2.0,
                longitude = center.longitude + span.longitudeDelta / 2.0,
            ),
        )
    }
}

internal class CameraIdleTracker(
    private val gestureDetector: MapUserGestureDetector,
    private val onCameraIdle: (bounds: GeoBounds, movedByUser: Boolean) -> Unit,
) {
    fun regionDidChange(mapView: MKMapView) {
        val movedByUser = gestureDetector.consumePendingUserGesture()
        mapView.visibleGeoBounds()?.let { visibleBounds ->
            onCameraIdle(visibleBounds, movedByUser)
        }
    }
}

// Skia writes straight into CoreGraphics-owned memory: a PNG round-trip froze the main thread and a
// ByteArray copy filled the Kotlin heap (~150 KB per marker), triggering full GC pauses.
@OptIn(ExperimentalForeignApi::class)
internal fun ImageBitmap.toUIImage(): UIImage? {
    val cgImage = asSkiaBitmap().peekPixels()?.toCGImage(width = width, height = height) ?: return null
    return UIImage.imageWithCGImage(
        cgImage = cgImage,
        scale = UIScreen.mainScreen.scale,
        orientation = UIImageOrientation.UIImageOrientationUp,
    ).also { CGImageRelease(cgImage) }
}

@OptIn(ExperimentalForeignApi::class)
private fun Pixmap.toCGImage(width: Int, height: Int): CGImageRef? {
    val colorSpace = CGColorSpaceCreateDeviceRGB()
    val bitmapContext = CGBitmapContextCreate(
        data = null,
        width = width.toULong(),
        height = height.toULong(),
        bitsPerComponent = BITS_PER_COMPONENT,
        bytesPerRow = (width * RGBA_BYTES_PER_PIXEL).toULong(),
        space = colorSpace,
        bitmapInfo = CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value or kCGBitmapByteOrder32Big,
    )
    CGColorSpaceRelease(colorSpace)
    val contextPixels = CGBitmapContextGetData(bitmapContext)
    val isCopied = contextPixels != null && readPixels(
        info = ImageInfo(
            width = width,
            height = height,
            colorType = ColorType.RGBA_8888,
            alphaType = ColorAlphaType.PREMUL,
        ),
        addr = contextPixels.rawValue,
        rowBytes = CGBitmapContextGetBytesPerRow(bitmapContext).toInt(),
    )
    val cgImage = if (isCopied) CGBitmapContextCreateImage(bitmapContext) else null
    CGContextRelease(bitmapContext)
    return cgImage
}

private const val RGBA_BYTES_PER_PIXEL = 4
private const val BITS_PER_COMPONENT = 8uL

@OptIn(ExperimentalForeignApi::class)
internal fun createMKPolyline(points: List<LatLng>): MKPolyline = memScoped {
    val coordsBuffer = allocArray<CLLocationCoordinate2D>(points.size) { index ->
        latitude = points[index].latitude
        longitude = points[index].longitude
    }
    MKPolyline.polylineWithCoordinates(coordsBuffer, points.size.toULong())
}
