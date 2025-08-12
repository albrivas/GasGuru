package com.gasguru.core.database.converters

import androidx.room.TypeConverter
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.ThemeMode

internal class UserDataConverters {
    @TypeConverter
    fun fromFuelType(fuelType: FuelType): String {
        return fuelType.name
    }

    @TypeConverter
    fun toFuelType(value: String): FuelType {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromThemeMode(themeMode: ThemeMode): String {
        return themeMode.name
    }

    @TypeConverter
    fun toThemeMode(value: String): ThemeMode {
        return enumValueOf(value)
    }
}
