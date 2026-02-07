package com.gasguru.core.uikit.components.selectedItem

import androidx.annotation.StringRes

data class SelectedItemModel(
    @StringRes val title: Int,
    val isSelected: Boolean,
    val image: Int,
    val onItemSelected: (SelectedItemModel) -> Unit = {},
)