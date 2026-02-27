package com.gasguru.core.domain.alerts

import com.gasguru.core.data.repository.alerts.PriceAlertRepository

class RemovePriceAlertUseCase(
    private val priceAlertRepository: PriceAlertRepository,
) {
    suspend operator fun invoke(stationId: Int): Unit =
        priceAlertRepository.removePriceAlert(stationId = stationId)
}
