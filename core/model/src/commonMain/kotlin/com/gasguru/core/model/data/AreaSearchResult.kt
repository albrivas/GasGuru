package com.gasguru.core.model.data

data class AreaSearchResult(
    val stations: List<FuelStation>,
    val isTruncated: Boolean,
)
