package com.gasguru.core.data.repository.alerts

import com.gasguru.core.data.util.NetworkMonitor
import com.gasguru.core.database.dao.PriceAlertDao
import com.gasguru.core.database.dao.UserDataDao
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
    private val userDataDao: UserDataDao,
) : PriceAlertRepository {

    override suspend fun addPriceAlert(stationId: Int, lastNotifiedPrice: Double) {
        enableNotificationsIfFirstAlert()

        priceAlertDao.addPriceAlert(PriceAlertEntity(stationId = stationId, lastNotifiedPrice = lastNotifiedPrice, isSynced = false))

        if (networkMonitor.isOnline.first()) {
            val playerId = oneSignalManager.getPlayerId().orEmpty()
            val userData = userDataDao.getUserData().first()
            val fuelType = userData?.fuelSelection?.name.orEmpty()

            supabaseManager.addPriceAlert(
                stationId = stationId,
                onesignalPlayerId = playerId,
                fuelType = fuelType,
                lastNotifiedPrice = lastNotifiedPrice,
            )
            priceAlertDao.markAsSynced(stationId = stationId)
        }
    }

    private suspend fun enableNotificationsIfFirstAlert() {
        val hasExistingAlerts = priceAlertDao.getActiveAlertsCount() > 0
        if (!hasExistingAlerts) {
            oneSignalManager.enablePriceNotificationAlert(enable = true)
        }
    }

    override suspend fun removePriceAlert(stationId: Int) {
        priceAlertDao.markAsDeleted(stationId = stationId)

        if (networkMonitor.isOnline.first()) {
            supabaseManager.removePriceAlert(stationId = stationId)
            priceAlertDao.cleanupSyncedDeletes()
        }

        disableNotificationsIfNoAlerts()
    }

    private suspend fun disableNotificationsIfNoAlerts() {
        val remainingAlerts = priceAlertDao.getActiveAlertsCount()
        if (remainingAlerts == 0) {
            oneSignalManager.enablePriceNotificationAlert(enable = false)
        }
    }

    override suspend fun hasPendingSync(): Boolean {
        return priceAlertDao.hasPendingSync()
    }

    override suspend fun sync(): Boolean {
        return try {
            val playerId = oneSignalManager.getPlayerId().orEmpty()
            val userData = userDataDao.getUserData().first()
            val fuelType = userData?.fuelSelection?.name.orEmpty()

            val pendingAdds = priceAlertDao.getPendingAddAlerts()
            pendingAdds.forEach { alert ->
                supabaseManager.addPriceAlert(
                    stationId = alert.stationId,
                    onesignalPlayerId = playerId,
                    fuelType = fuelType,
                    lastNotifiedPrice = alert.lastNotifiedPrice,
                )
                priceAlertDao.markAsSynced(stationId = alert.stationId)
            }

            val pendingDeletes = priceAlertDao.getPendingDeleteAlerts()
            pendingDeletes.forEach { alert ->
                supabaseManager.removePriceAlert(stationId = alert.stationId)
            }

            priceAlertDao.cleanupSyncedDeletes()
            true
        } catch (_: Exception) {
            false
        }
    }
}
