package com.gasguru.core.data.repository.alerts

import com.gasguru.core.data.sync.Syncable

interface PriceAlertRepository : Syncable {
    suspend fun addPriceAlert(stationId: Int, lastNotifiedPrice: Double)
    suspend fun removePriceAlert(stationId: Int)
}
