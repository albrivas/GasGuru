package com.gasguru.navigation.deeplink

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Holds pending deep link navigation state.
 * Used when a push notification is tapped but the app needs to load first.
 */
class DeepLinkStateHolder {

    private val _pendingStationId = MutableStateFlow<Int?>(null)
    val pendingStationId: StateFlow<Int?> = _pendingStationId.asStateFlow()

    fun setPendingStationId(stationId: Int) {
        _pendingStationId.value = stationId
    }

    fun clear() {
        _pendingStationId.value = null
    }
}
