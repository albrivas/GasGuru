package com.gasguru.composeApp.bridge

import com.gasguru.navigation.deeplink.DeepLinkStateHolder
import kotlin.test.Test
import kotlin.test.assertEquals

class IosBridgeImplTest {

    @Test
    fun handlePushTap_setsPendingStationId() {
        val holder = DeepLinkStateHolder()
        val bridge: IosBridge = IosBridgeImpl(deepLinkStateHolder = holder)

        bridge.handlePushTap(stationId = 42)

        assertEquals(42, holder.pendingStationId.value)
    }
}
