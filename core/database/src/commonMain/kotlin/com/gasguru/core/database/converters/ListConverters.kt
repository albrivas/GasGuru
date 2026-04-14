package com.gasguru.core.database.converters

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class ListConverters {
    @TypeConverter
    fun fromList(list: List<String>): String = Json.encodeToString(list)

    @TypeConverter
    fun toList(data: String): List<String> =
        try {
            Json.decodeFromString(data)
        } catch (_: Exception) {
            emptyList()
        }
}
