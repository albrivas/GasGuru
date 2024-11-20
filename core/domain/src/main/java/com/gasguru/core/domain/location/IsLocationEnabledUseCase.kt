package com.gasguru.core.domain.location

import com.gasguru.core.data.repository.LocationTracker
import javax.inject.Inject

class IsLocationEnabledUseCase @Inject constructor(
    private val locationTracker: LocationTracker
) {
    suspend operator fun invoke(): Boolean = locationTracker.isLocationEnabled()
}
