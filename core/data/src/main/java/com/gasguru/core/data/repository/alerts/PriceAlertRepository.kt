package com.gasguru.core.data.repository.alerts

interface PriceAlertRepository {
    suspend fun addPriceAlert(stationId: Int)
    suspend fun removePriceAlert(stationId: Int)
}