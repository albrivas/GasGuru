package com.gasguru.core.domain.location

import com.gasguru.core.model.data.LatLng
import com.gasguru.core.data.repository.location.LocationTracker
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val locationTracker: LocationTracker
) {
    suspend operator fun invoke(): LatLng? = locationTracker.getCurrentLocation()
}
