package com.gasguru.core.testing.fakes.data.location

import com.gasguru.core.data.repository.location.LocationTracker
import com.gasguru.core.model.data.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeLocationTracker(
    isLocationEnabled: Boolean = true,
    lastKnownLocation: LatLng? = null,
) : LocationTracker {

    private val isLocationEnabledFlow = MutableStateFlow(isLocationEnabled)
    private val lastKnownLocationFlow = MutableStateFlow(lastKnownLocation)
    private val currentLocationFlow = MutableStateFlow(lastKnownLocation)

    override val isLocationEnabled: Flow<Boolean> = isLocationEnabledFlow
    override val getLastKnownLocation: Flow<LatLng?> = lastKnownLocationFlow
    override val getCurrentLocationFlow: Flow<LatLng?> = currentLocationFlow

    override suspend fun getCurrentLocation(): LatLng? = currentLocationFlow.value

    fun setLocationEnabled(enabled: Boolean) {
        isLocationEnabledFlow.value = enabled
    }

    fun setLastKnownLocation(location: LatLng?) {
        lastKnownLocationFlow.value = location
        currentLocationFlow.value = location
    }
}
