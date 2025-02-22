package com.gasguru.core.domain

import com.gasguru.core.data.repository.GeocoderAddress
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAddressFromLocationUseCase @Inject constructor(
    private val geocoderAddress: GeocoderAddress,
) {
    operator fun invoke(latitude: Double, longitude: Double): Flow<String?> =
        geocoderAddress.getAddressFromLocation(
            latitude = latitude,
            longitude = longitude
        )
}
