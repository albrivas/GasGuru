package com.gasguru.core.data.repository.geocoder

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import platform.CoreLocation.CLGeocoder
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLPlacemark

@OptIn(ExperimentalForeignApi::class)
class GeocoderAddressIos(
    private val ioDispatcher: CoroutineDispatcher,
) : GeocoderAddress {
    override fun getAddressFromLocation(latitude: Double, longitude: Double): Flow<String?> =
        callbackFlow<String?> {
            val geocoder = CLGeocoder()
            val location = CLLocation(latitude = latitude, longitude = longitude)

            geocoder.reverseGeocodeLocation(location) { placemarks, error ->
                if (error != null || placemarks == null) {
                    trySend(null)
                } else {
                    val placemark = placemarks.firstOrNull() as? CLPlacemark
                    trySend(placemark?.formatAddress())
                }
                close()
            }

            awaitClose { geocoder.cancelGeocode() }
        }.flowOn(ioDispatcher)

    private fun CLPlacemark.formatAddress(): String? {
        val streetLine = listOfNotNull(thoroughfare, subThoroughfare)
            .joinToString(" ")
            .ifEmpty { null }
        return listOfNotNull(streetLine, locality, postalCode)
            .joinToString(", ")
            .ifEmpty { null }
    }
}
