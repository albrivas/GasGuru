package com.gasguru.core.database.converters

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ListConverters {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val listType = Types.newParameterizedType(List::class.java, String::class.javaObjectType)
    private val adapter = moshi.adapter<List<String>>(listType)

    @TypeConverter
    fun fromList(list: List<String>): String {
        return adapter.toJson(list)
    }

    @TypeConverter
    fun toList(data: String): List<String> {
        return adapter.fromJson(data) ?: emptyList()
    }
}
