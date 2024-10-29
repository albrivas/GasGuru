package com.gasguru.core.data.repository

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationTracker {
    suspend fun getCurrentLocation(): Location?
    suspend fun isLocationEnabled(): Boolean
    val getCurrentLocationFlow: Flow<Location?>
    val getLastKnownLocation: Flow<Location?>
}
