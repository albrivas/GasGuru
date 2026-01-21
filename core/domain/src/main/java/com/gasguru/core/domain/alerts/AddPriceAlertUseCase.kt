package com.gasguru.core.domain.alerts

import com.gasguru.core.data.repository.alerts.PriceAlertRepository
import javax.inject.Inject

class AddPriceAlertUseCase @Inject constructor(
    private val priceAlertRepository: PriceAlertRepository,
) {
    suspend operator fun invoke(stationId: Int, lastNotifiedPrice: Double): Unit =
        priceAlertRepository.addPriceAlert(stationId = stationId, lastNotifiedPrice = lastNotifiedPrice)
}
