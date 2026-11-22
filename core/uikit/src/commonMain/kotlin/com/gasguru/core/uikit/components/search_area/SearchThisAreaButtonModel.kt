package com.gasguru.core.uikit.components.search_area

import androidx.compose.runtime.Stable

@Stable
data class SearchThisAreaButtonModel(
    val text: String,
    val isLoading: Boolean,
    val onClick: () -> Unit,
)
