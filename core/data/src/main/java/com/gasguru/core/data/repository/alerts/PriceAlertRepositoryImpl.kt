package com.gasguru.core.data.repository.alerts

import com.gasguru.core.data.util.NetworkMonitor
import com.gasguru.core.database.dao.PriceAlertDao
import com.gasguru.core.database.dao.UserDataDao
import com.gasguru.core.database.model.ModificationType
import com.gasguru.core.database.model.PriceAlertEntity
import com.gasguru.core.notifications.OneSignalManager
import com.gasguru.core.supabase.SupabaseManager
import kotlinx.coroutines.flow.first
class PriceAlertRepositoryImpl constructor(
    private val priceAlertDao: PriceAlertDao,
    private val supabaseManager: SupabaseManager,
    private val networkMonitor: NetworkMonitor,
    private val oneSignalManager: OneSignalManager,
    private val userDataDao: UserDataDao,
) : PriceAlertRepository {

    override suspend fun addPriceAlert(stationId: Int, lastNotifiedPrice: Double) {
        enableNotificationsIfFirstAlert()

        priceAlertDao.insert(
            PriceAlertEntity(
                stationId = stationId,
                lastNotifiedPrice = lastNotifiedPrice,
                typeModification = ModificationType.INSERT,
                isSynced = false
            )
        )

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
        val existingAlert = priceAlertDao.getByStationId(stationId = stationId) ?: return

        if (!existingAlert.isSynced) {
            // case 1: Without sync → Remove local
            priceAlertDao.deleteByStationId(stationId = stationId)
        } else if (networkMonitor.isOnline.first()) {
            // Case 2: Synced + online → Remove local and server
            priceAlertDao.deleteByStationId(stationId = stationId)
            supabaseManager.removePriceAlert(stationId = stationId)
        } else {
            // Case 3: Synced + offline → mark as DELETE pending
            priceAlertDao.insert(
                PriceAlertEntity(
                    stationId = stationId,
                    lastNotifiedPrice = existingAlert.lastNotifiedPrice,
                    typeModification = ModificationType.DELETE,
                    isSynced = false
                )
            )
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

            val pendingInserts = priceAlertDao.getPendingInserts()
            pendingInserts.forEach { alert ->
                supabaseManager.addPriceAlert(
                    stationId = alert.stationId,
                    onesignalPlayerId = playerId,
                    fuelType = fuelType,
                    lastNotifiedPrice = alert.lastNotifiedPrice,
                )
                priceAlertDao.markAsSynced(stationId = alert.stationId)
            }

            val pendingDeletes = priceAlertDao.getPendingDeletes()
            pendingDeletes.forEach { alert ->
                supabaseManager.removePriceAlert(stationId = alert.stationId)
                priceAlertDao.deleteByStationId(stationId = alert.stationId)
            }

            true
        } catch (_: Exception) {
            false
        }
    }
}
