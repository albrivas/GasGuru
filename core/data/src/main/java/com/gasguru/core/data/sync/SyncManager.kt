package com.gasguru.core.data.sync

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.data.repository.alerts.PriceAlertRepository
import com.gasguru.core.data.util.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SyncManager(
    private val networkMonitor: NetworkMonitor,
    private val priceAlertRepository: PriceAlertRepository,
    private val scope: CoroutineScope,
    private val analyticsHelper: AnalyticsHelper,
) {

    fun execute() {
        networkMonitor.isOnline
            .distinctUntilChanged()
            .onEach { isOnline ->
                if (isOnline) {
                    analyticsHelper.logEvent(event = AnalyticsEvent(type = AnalyticsEvent.Types.CAME_ONLINE))
                    syncAllPending()
                } else {
                    analyticsHelper.logEvent(event = AnalyticsEvent(type = AnalyticsEvent.Types.WENT_OFFLINE))
                }
            }
            .launchIn(scope)
    }

    private suspend fun syncAllPending() {
        if (priceAlertRepository.hasPendingSync()) {
            priceAlertRepository.sync()
        }
    }
}
