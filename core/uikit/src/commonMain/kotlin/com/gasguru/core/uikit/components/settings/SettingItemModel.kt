package com.gasguru.core.uikit.components.settings

import org.jetbrains.compose.resources.DrawableResource

data class SettingItemModel(
    val title: String,
    val selection: String,
    val icon: DrawableResource,
    val onClick: () -> Unit = {},
)
