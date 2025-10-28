package com.gasguru.core.data.repository.alerts

import com.gasguru.core.database.dao.PriceAlertDao
import com.gasguru.core.database.model.PriceAlertEntity
import com.gasguru.core.supabase.SupabaseManager
import javax.inject.Inject

class PriceAlertRepositoryImpl @Inject constructor(
    private val priceAlertDao: PriceAlertDao,
    private val supabaseManager: SupabaseManager
) : PriceAlertRepository {

    override suspend fun addPriceAlert(stationId: Int) {
        priceAlertDao.addPriceAlert(PriceAlertEntity(stationId = stationId))
        supabaseManager.addPriceAlert(stationId = stationId)
    }

    override suspend fun removePriceAlert(stationId: Int) {
        priceAlertDao.removePriceAlert(stationId = stationId)
        supabaseManager.removePriceAlert(stationId = stationId)
    }
}