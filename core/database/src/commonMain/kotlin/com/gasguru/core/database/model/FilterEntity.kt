package com.gasguru.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gasguru.core.model.data.Filter
import com.gasguru.core.model.data.FilterType

@Entity(
    tableName = "filter"
)
data class FilterEntity(
    @PrimaryKey
    val type: FilterType,
    val selection: List<String>,
)

fun FilterEntity.asExternalModel() = Filter(
    type = type,
    selection = selection
)

fun List<FilterEntity>.asExternalModel() = map {
    it.asExternalModel()
}
