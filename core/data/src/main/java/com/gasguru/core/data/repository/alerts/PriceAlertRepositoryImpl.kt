package com.gasguru.core.data.repository.alerts

import com.gasguru.core.data.util.NetworkMonitor
import com.gasguru.core.database.dao.PriceAlertDao
import com.gasguru.core.database.model.PriceAlertEntity
import com.gasguru.core.notifications.OneSignalManager
import com.gasguru.core.supabase.SupabaseManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PriceAlertRepositoryImpl @Inject constructor(
    private val priceAlertDao: PriceAlertDao,
    private val supabaseManager: SupabaseManager,
    private val networkMonitor: NetworkMonitor,
    private val oneSignalManager: OneSignalManager,
) : PriceAlertRepository {

    override suspend fun addPriceAlert(stationId: Int) {
        enableNotificationsIfFirstAlert()
        
        priceAlertDao.addPriceAlert(PriceAlertEntity(stationId = stationId, isSynced = false))
        
        if (networkMonitor.isOnline.first()) {
            supabaseManager.addPriceAlert(stationId = stationId)
            priceAlertDao.markAsSynced(stationId = stationId)
        }
    }

    private suspend fun enableNotificationsIfFirstAlert() {
        val hasExistingAlerts = priceAlertDao.getTotalAlertsCount() > 0
        if (!hasExistingAlerts) {
            oneSignalManager.enablePriceNotificationAlert(enable = true)
        }
    }

    override suspend fun removePriceAlert(stationId: Int) {
        priceAlertDao.removePriceAlert(stationId = stationId)
        
        disableNotificationsIfNoAlerts()
        
        if (networkMonitor.isOnline.first()) {
            supabaseManager.removePriceAlert(stationId = stationId)
        }
    }

    private suspend fun disableNotificationsIfNoAlerts() {
        val remainingAlerts = priceAlertDao.getTotalAlertsCount()
        if (remainingAlerts == 0) {
            oneSignalManager.enablePriceNotificationAlert(enable = false)
        }
    }
}