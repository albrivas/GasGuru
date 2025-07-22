package com.gasguru.core.data.repository.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationTracker {
    suspend fun getCurrentLocation(): Location?
    val isLocationEnabled: Flow<Boolean>
    val getCurrentLocationFlow: Flow<Location?>
    val getLastKnownLocation: Flow<Location?>
}
