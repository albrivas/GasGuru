package com.gasguru.core.notifications

class OneSignalManagerIos : OneSignalManager {

    override suspend fun enablePriceNotificationAlert(enable: Boolean) {
        // TODO Phase 4c V2: integrate OneSignal iOS SDK via SPM/cinterop
    }

    override suspend fun getPlayerId(): String? {
        // TODO Phase 4c V2: return OneSignal iOS player ID
        return null
    }
}
