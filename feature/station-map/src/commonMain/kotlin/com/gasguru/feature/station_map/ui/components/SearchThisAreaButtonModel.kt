package com.gasguru.feature.station_map.ui.components

import androidx.compose.runtime.Stable

@Stable
data class SearchThisAreaButtonModel(
    val text: String,
    val isLoading: Boolean,
    val onClick: () -> Unit,
)
