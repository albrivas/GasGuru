package com.gasguru.core.model.data

data class Filter(
    val type: FilterType,
    val selection: List<String>
)

enum class FilterType {
    BRAND, NEARBY, SCHEDULE
}
