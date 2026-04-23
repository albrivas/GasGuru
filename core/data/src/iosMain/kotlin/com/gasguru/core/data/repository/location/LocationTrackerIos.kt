package com.gasguru.core.data.repository.location

import com.gasguru.core.model.data.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

// V1 stub. Para iOS V2: implementar con CLLocationManager (CoreLocation).
class LocationTrackerIos : LocationTracker {
    override suspend fun getCurrentLocation(): LatLng? = null
    override val isLocationEnabled: Flow<Boolean> = flowOf(false)
    override val getCurrentLocationFlow: Flow<LatLng?> = flowOf(null)
    override val getLastKnownLocation: Flow<LatLng?> = flowOf(null)
}
