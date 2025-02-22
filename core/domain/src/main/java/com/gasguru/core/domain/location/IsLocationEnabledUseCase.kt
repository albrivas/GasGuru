package com.gasguru.core.domain.location

import com.gasguru.core.data.repository.LocationTracker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsLocationEnabledUseCase @Inject constructor(
    private val locationTracker: LocationTracker
) {
    operator fun invoke(): Flow<Boolean> = locationTracker.isLocationEnabled
}
