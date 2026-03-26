package com.gasguru.core.database.converters

import androidx.room.TypeConverter
import com.gasguru.core.model.data.FilterType

internal class FilterTypeConverter {
    @TypeConverter
    fun fromFilterType(filterType: FilterType): String {
        return filterType.name
    }

    @TypeConverter
    fun toFilterType(value: String): FilterType {
        return enumValueOf(value)
    }
}
