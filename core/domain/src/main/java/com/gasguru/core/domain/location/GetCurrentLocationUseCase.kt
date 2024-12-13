package com.gasguru.core.domain.location

import android.location.Location
import com.gasguru.core.data.repository.LocationTracker
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val locationTracker: LocationTracker
) {
    suspend operator fun invoke(): Location? = locationTracker.getCurrentLocation()
}
