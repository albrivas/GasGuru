package com.gasguru.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeepLinkManager @Inject constructor() {

    private val _deepLinkEvents = Channel<DeepLinkEvent>(Channel.UNLIMITED)
    val deepLinkEvents: Flow<DeepLinkEvent> = _deepLinkEvents.receiveAsFlow()

    fun navigateToDetailStation(stationId: Int) {
        _deepLinkEvents.trySend(DeepLinkEvent.NavigateToDetailStation(stationId))
    }
}

sealed interface DeepLinkEvent {
    data class NavigateToDetailStation(val stationId: Int) : DeepLinkEvent
}
