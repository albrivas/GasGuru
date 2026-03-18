package com.gasguru.core.testing.fakes.data.notifications

import com.gasguru.core.notifications.OneSignalManager

class FakeOneSignalManager(private val playerId: String? = "fake-player-id") : OneSignalManager {
    val enabledStates = mutableListOf<Boolean>()

    override suspend fun enablePriceNotificationAlert(enable: Boolean) {
        enabledStates.add(enable)
    }

    override suspend fun getPlayerId(): String? = playerId
}
