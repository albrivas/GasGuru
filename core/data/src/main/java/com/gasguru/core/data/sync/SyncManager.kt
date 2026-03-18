package com.gasguru.core.data.sync

import com.gasguru.core.data.repository.alerts.PriceAlertRepository
import com.gasguru.core.data.util.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SyncManager(
    private val networkMonitor: NetworkMonitor,
    private val priceAlertRepository: PriceAlertRepository,
    private val scope: CoroutineScope,
) {

    fun execute() {
        networkMonitor.isOnline
            .onEach { isOnline ->
                if (isOnline) syncAllPending()
            }
            .launchIn(scope)
    }

    private suspend fun syncAllPending() {
        if (priceAlertRepository.hasPendingSync()) {
            priceAlertRepository.sync()
        }
    }
}
