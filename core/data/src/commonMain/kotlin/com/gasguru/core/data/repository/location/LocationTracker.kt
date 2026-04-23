package com.gasguru.core.data.repository.location

import com.gasguru.core.model.data.LatLng
import kotlinx.coroutines.flow.Flow

interface LocationTracker {
    suspend fun getCurrentLocation(): LatLng?
    val isLocationEnabled: Flow<Boolean>
    val getCurrentLocationFlow: Flow<LatLng?>
    val getLastKnownLocation: Flow<LatLng?>
}
