package com.gasguru.core.testing.fakes.data.alerts

import com.gasguru.core.data.repository.alerts.PriceAlertRepository

class FakePriceAlertRepository : PriceAlertRepository {
    val addedAlerts = mutableListOf<Pair<Int, Double>>()
    val removedAlerts = mutableListOf<Int>()

    override suspend fun addPriceAlert(stationId: Int, lastNotifiedPrice: Double) {
        addedAlerts.add(stationId to lastNotifiedPrice)
    }

    override suspend fun removePriceAlert(stationId: Int) {
        removedAlerts.add(stationId)
    }

    override suspend fun sync(): Boolean = true

    override suspend fun hasPendingSync(): Boolean = false
}
