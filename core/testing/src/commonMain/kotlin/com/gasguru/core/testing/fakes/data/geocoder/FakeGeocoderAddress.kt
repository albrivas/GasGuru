package com.gasguru.core.testing.fakes.data.geocoder

import com.gasguru.core.data.repository.geocoder.GeocoderAddress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeGeocoderAddress(
    var address: String? = null,
    var shouldThrow: Boolean = false,
) : GeocoderAddress {
    override fun getAddressFromLocation(latitude: Double, longitude: Double): Flow<String?> = flow {
        if (shouldThrow) {
            throw IllegalStateException("Geocoder error")
        }
        emit(address)
    }
}
