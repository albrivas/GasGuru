package com.gasguru.core.database.converters

import androidx.room.TypeConverter
import com.gasguru.core.model.data.FuelType

internal class UserDataConverters {
    @TypeConverter
    fun fromFuelType(fuelType: FuelType): String {
        return fuelType.name
    }

    @TypeConverter
    fun toFuelType(value: String): FuelType {
        return enumValueOf(value)
    }
}
