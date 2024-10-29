package com.gasguru.core.database.converters

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ListConverters {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val listType = Types.newParameterizedType(List::class.java, Integer::class.javaObjectType)
    private val adapter = moshi.adapter<List<Int>>(listType)

    @TypeConverter
    fun fromList(list: List<Int>): String {
        return adapter.toJson(list)
    }

    @TypeConverter
    fun toList(data: String): List<Int> {
        return adapter.fromJson(data) ?: emptyList()
    }
}
