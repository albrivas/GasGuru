package com.gasguru.data.fakes

import com.gasguru.core.data.repository.alerts.PriceAlertRepository

class FakePriceAlertRepository(
    var pendingSync: Boolean = false,
) : PriceAlertRepository {

    var syncCalled: Boolean = false
    var syncResult: Boolean = true

    override suspend fun addPriceAlert(stationId: Int, lastNotifiedPrice: Double) = Unit

    override suspend fun removePriceAlert(stationId: Int) = Unit

    override suspend fun sync(): Boolean {
        syncCalled = true
        return syncResult
    }

    override suspend fun hasPendingSync(): Boolean = pendingSync
}
