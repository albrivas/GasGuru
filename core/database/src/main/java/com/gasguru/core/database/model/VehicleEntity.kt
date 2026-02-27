package com.gasguru.core.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.Vehicle

@Entity(
    tableName = "vehicles",
    foreignKeys = [
        ForeignKey(
            entity = UserDataEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["userId"])],
)
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val name: String?,
    val fuelType: FuelType,
    val tankCapacity: Int,
)

fun VehicleEntity.asExternalModel() = Vehicle(
    id = id,
    userId = userId,
    name = name,
    fuelType = fuelType,
    tankCapacity = tankCapacity,
)