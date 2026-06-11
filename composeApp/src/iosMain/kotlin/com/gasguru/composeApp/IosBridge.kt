package com.gasguru.composeApp

import com.gasguru.navigation.deeplink.DeepLinkStateHolder

class IosBridge internal constructor(
    private val deepLinkStateHolder: DeepLinkStateHolder,
) {
    fun handlePushTap(stationId: Int) {
        deepLinkStateHolder.setPendingStationId(stationId = stationId)
    }
}
