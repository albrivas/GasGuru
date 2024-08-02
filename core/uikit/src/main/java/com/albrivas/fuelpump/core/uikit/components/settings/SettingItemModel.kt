package com.albrivas.fuelpump.core.uikit.components.settings

data class SettingItemModel(
    val title: String,
    val selection: String,
    val icon: Int,
    val onClick: () -> Unit = {}
)
