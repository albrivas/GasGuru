package com.gasguru.core.data.repository.geocoder

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

// V1 stub. Para iOS V2: implementar con CLGeocoder (CoreLocation).
class GeocoderAddressIos : GeocoderAddress {
    override fun getAddressFromLocation(latitude: Double, longitude: Double): Flow<String?> =
        flowOf(null)
}
