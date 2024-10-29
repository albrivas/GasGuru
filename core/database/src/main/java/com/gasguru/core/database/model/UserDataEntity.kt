package com.gasguru.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.UserData

@Entity(
    tableName = "user-data"
)
data class UserDataEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long = 0,
    val fuelSelection: FuelType,
)

fun UserDataEntity.asExternalModel() = UserData(
    fuelSelection = fuelSelection,
)
