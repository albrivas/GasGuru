package com.gasguru.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.model.data.UserData

@Entity(
    tableName = "user-data"
)
data class UserDataEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long = 0,
    @ColumnInfo(defaultValue = "0")
    val lastUpdate: Long,
    @ColumnInfo(defaultValue = "0")
    val isOnboardingSuccess: Boolean,
    @ColumnInfo(defaultValue = "3")
    val themeModeId: Int = ThemeMode.SYSTEM.id,
)

fun UserDataEntity.asExternalModel() = UserData(
    lastUpdate = lastUpdate,
    isOnboardingSuccess = isOnboardingSuccess,
    themeMode = ThemeMode.fromId(themeModeId),
)
