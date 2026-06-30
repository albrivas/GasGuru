package com.gasguru.composeApp.bridge

import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("IosBridge", exact = true)
interface IosBridge {
    fun handlePushTap(stationId: Int)
    fun refreshStations(onComplete: (Boolean) -> Unit)
}
