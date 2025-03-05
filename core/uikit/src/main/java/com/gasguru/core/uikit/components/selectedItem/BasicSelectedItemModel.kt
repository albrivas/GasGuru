package com.gasguru.core.uikit.components.selectedItem

import androidx.annotation.StringRes

data class BasicSelectedItemModel(
    @StringRes val title: Int,
    val isSelected: Boolean,
    val isRoundedItem: Boolean = true,
    val image: Int,
    val onItemSelected: (BasicSelectedItemModel) -> Unit = {},
)
