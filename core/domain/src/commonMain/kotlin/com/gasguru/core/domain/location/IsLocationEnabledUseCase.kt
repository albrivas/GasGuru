package com.gasguru.core.domain.location

import com.gasguru.core.data.repository.location.LocationTracker
import kotlinx.coroutines.flow.Flow

class IsLocationEnabledUseCase(
    private val locationTracker: LocationTracker
) {
    operator fun invoke(): Flow<Boolean> = locationTracker.isLocationEnabled
}
