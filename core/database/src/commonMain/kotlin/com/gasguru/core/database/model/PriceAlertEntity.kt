package com.gasguru.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Clock

@Entity(tableName = "price_alerts")
data class PriceAlertEntity(
    @PrimaryKey
    val stationId: Int,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val lastNotifiedPrice: Double,
    val typeModification: ModificationType = ModificationType.INSERT,
    val isSynced: Boolean = false,
)
