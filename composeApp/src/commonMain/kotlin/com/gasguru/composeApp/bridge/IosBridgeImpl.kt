package com.gasguru.composeApp.bridge

import com.gasguru.navigation.deeplink.DeepLinkStateHolder

class IosBridgeImpl(
    private val deepLinkStateHolder: DeepLinkStateHolder,
) : IosBridge {

    override fun handlePushTap(stationId: Int) {
        deepLinkStateHolder.setPendingStationId(stationId = stationId)
    }
}
