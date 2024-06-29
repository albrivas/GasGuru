package com.albrivas.fuelpump.core.uikit.components.table

data class FuelPriceTableModel(
    val headers: Pair<String, String>,
    val rows: List<Pair<String, Double>>
)