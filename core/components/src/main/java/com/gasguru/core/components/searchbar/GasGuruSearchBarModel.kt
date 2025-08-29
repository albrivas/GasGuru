package com.gasguru.core.components.searchbar

import androidx.compose.ui.Modifier
import com.gasguru.core.model.data.SearchPlace

data class GasGuruSearchBarModel(
    val alwaysActive: Boolean = false,
    val onActiveChange: (Boolean) -> Unit = {},
    val onPlaceSelected: (SearchPlace) -> Unit = {},
    val onRecentSearchClicked: (SearchPlace) -> Unit = {},
    val onBackPressed: () -> Unit = {},
    val modifier: Modifier = Modifier,
    val onHeight: (Int) -> Unit = {},
)
