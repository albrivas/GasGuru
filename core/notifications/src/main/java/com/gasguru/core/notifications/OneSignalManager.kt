package com.gasguru.core.notifications

interface OneSignalManager {
    suspend fun enablePriceNotificationAlert(enable: Boolean)
    suspend fun getPlayerId(): String?
}
