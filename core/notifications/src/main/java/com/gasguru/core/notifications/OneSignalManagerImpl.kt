package com.gasguru.core.notifications

import com.onesignal.OneSignal
import javax.inject.Inject

class OneSignalManagerImpl @Inject constructor() : OneSignalManager {

    companion object {
        private const val ENABLE_STATIONS_ALERTS = "enable_stations_alerts"
    }

    override suspend fun enablePriceNotificationAlert(enable: Boolean) {
        OneSignal.User.addTag(ENABLE_STATIONS_ALERTS, "$enable")
    }

    override suspend fun getPlayerId(): String? {
        return OneSignal.User.pushSubscription.id
    }
}
