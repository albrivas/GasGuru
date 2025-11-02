package com.gasguru.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "price_alerts")
data class PriceAlertEntity(
    @PrimaryKey
    val stationId: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val lastNotifiedPrice: Double,
    val isDeleted: Boolean = false,
    val isSynced: Boolean = false
)