package com.gasguru.core.data.sync

import com.gasguru.core.common.ApplicationScope
import com.gasguru.core.data.util.NetworkMonitor
import com.gasguru.core.database.dao.PriceAlertDao
import com.gasguru.core.supabase.SupabaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val priceAlertDao: PriceAlertDao,
    private val supabaseManager: SupabaseManager,
    @ApplicationScope private val scope: CoroutineScope,
) {

    fun execute() {
        networkMonitor.isOnline
            .distinctUntilChanged()
            .filter { isOnline -> isOnline }
            .onEach { syncAllPending() }
            .launchIn(scope)
    }

    private suspend fun syncAllPending() {
        val pendingAlerts = priceAlertDao.getPendingSyncAlerts()
        pendingAlerts.forEach { alert ->
            supabaseManager.addPriceAlert(stationId = alert.stationId)
            priceAlertDao.markAsSynced(stationId = alert.stationId)
        }
    }
}