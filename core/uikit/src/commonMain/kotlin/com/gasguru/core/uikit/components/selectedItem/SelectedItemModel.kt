package com.gasguru.core.uikit.components.selectedItem

import org.jetbrains.compose.resources.DrawableResource

data class SelectedItemModel(
    val title: String,
    val isSelected: Boolean,
    val image: DrawableResource,
    val onItemSelected: (SelectedItemModel) -> Unit = {},
)
