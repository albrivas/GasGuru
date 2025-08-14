package com.gasguru.core.components.searchbar

import com.gasguru.core.model.data.SearchPlace

data class GasGuruSearchBarModel(
    val onActiveChange: (Boolean) -> Unit = {},
    val onPlaceSelected: (SearchPlace) -> Unit = {},
    val onRecentSearchClicked: (SearchPlace) -> Unit = {},
    val modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    val onHeight: (Int) -> Unit = {},
)
