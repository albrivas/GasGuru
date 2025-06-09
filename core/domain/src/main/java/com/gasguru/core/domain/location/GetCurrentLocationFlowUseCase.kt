package com.gasguru.core.domain.location

import android.location.Location
import com.gasguru.core.data.repository.location.LocationTracker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentLocationFlowUseCase @Inject constructor(
    private val locationTracker: LocationTracker
) {
    operator fun invoke(): Flow<Location?> = locationTracker.getCurrentLocationFlow
}
