package com.gasguru.core.notifications

import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("OneSignalManager", exact = true)
interface OneSignalManager {
    suspend fun enablePriceNotificationAlert(enable: Boolean)
    suspend fun getPlayerId(): String?
}
