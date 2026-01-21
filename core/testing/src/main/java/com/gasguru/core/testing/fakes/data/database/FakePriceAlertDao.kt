package com.gasguru.core.testing.fakes.data.database

import com.gasguru.core.database.dao.PriceAlertDao
import com.gasguru.core.database.model.ModificationType
import com.gasguru.core.database.model.PriceAlertEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakePriceAlertDao(
    initialAlerts: List<PriceAlertEntity> = emptyList(),
) : PriceAlertDao {

    private val alertsFlow = MutableStateFlow(initialAlerts)

    override suspend fun insert(priceAlert: PriceAlertEntity) {
        alertsFlow.update { alerts ->
            alerts.filterNot { it.stationId == priceAlert.stationId } + priceAlert
        }
    }

    override suspend fun getPendingInserts(): List<PriceAlertEntity> =
        alertsFlow.value.filter { it.typeModification == ModificationType.INSERT && !it.isSynced }

    override suspend fun getPendingDeletes(): List<PriceAlertEntity> =
        alertsFlow.value.filter { it.typeModification == ModificationType.DELETE && !it.isSynced }

    override suspend fun markAsSynced(stationId: Int) {
        alertsFlow.update { alerts ->
            alerts.map { alert ->
                if (alert.stationId == stationId) alert.copy(isSynced = true) else alert
            }
        }
    }

    override suspend fun hasPendingSync(): Boolean =
        alertsFlow.value.any { !it.isSynced }

    override suspend fun getActiveAlertsCount(): Int {
        val deletes = alertsFlow.value
            .filter { it.typeModification == ModificationType.DELETE && !it.isSynced }
            .map { it.stationId }
            .toSet()
        return alertsFlow.value.count {
            it.typeModification == ModificationType.INSERT && it.stationId !in deletes
        }
    }

    override fun getAllPriceAlerts(): Flow<List<PriceAlertEntity>> = alertsFlow

    override suspend fun getByStationId(stationId: Int): PriceAlertEntity? =
        alertsFlow.value.firstOrNull { it.stationId == stationId }

    override suspend fun deleteByStationId(stationId: Int) {
        alertsFlow.update { alerts -> alerts.filterNot { it.stationId == stationId } }
    }

    fun setAlerts(alerts: List<PriceAlertEntity>) {
        alertsFlow.value = alerts
    }
}
