package com.gasguru.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "filter"
)
data class FilterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val type: String,
    val selection: List<String>
)
