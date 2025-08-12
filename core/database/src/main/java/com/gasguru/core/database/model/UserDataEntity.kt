package com.gasguru.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.model.data.UserData

@Entity(
    tableName = "user-data"
)
data class UserDataEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long = 0,
    val fuelSelection: FuelType,
    @ColumnInfo(defaultValue = "0")
    val lastUpdate: Long,
    @ColumnInfo(defaultValue = "0")
    val isOnboardingSuccess: Boolean,
    @ColumnInfo(defaultValue = "SYSTEM")
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)

fun UserDataEntity.asExternalModel() = UserData(
    fuelSelection = fuelSelection,
    lastUpdate = lastUpdate,
    isOnboardingSuccess = isOnboardingSuccess,
    themeMode = themeMode
)
