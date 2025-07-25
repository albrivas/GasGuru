package com.gasguru.core.data.repository.geocoder

import kotlinx.coroutines.flow.Flow

fun interface GeocoderAddress {
    fun getAddressFromLocation(latitude: Double, longitude: Double): Flow<String?>
}
