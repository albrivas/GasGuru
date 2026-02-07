package com.gasguru.core.domain.location

import com.gasguru.core.data.repository.location.LocationTracker
import com.gasguru.core.model.data.LatLng
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentLocationFlowUseCase @Inject constructor(
    private val locationTracker: LocationTracker
) {
    operator fun invoke(): Flow<LatLng?> = locationTracker.getCurrentLocationFlow
}
