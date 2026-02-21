package com.gasguru.core.domain.location

import com.gasguru.core.data.repository.location.LocationTracker
import com.gasguru.core.model.data.LatLng

class GetCurrentLocationUseCase(
    private val locationTracker: LocationTracker
) {
    suspend operator fun invoke(): LatLng? = locationTracker.getCurrentLocation()
}
