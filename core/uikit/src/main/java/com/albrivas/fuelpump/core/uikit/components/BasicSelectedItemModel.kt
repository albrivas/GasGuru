package com.albrivas.fuelpump.core.uikit.components

import androidx.annotation.StringRes

data class BasicSelectedItemModel(
    @StringRes val title: Int,
    val isSelected: Boolean,
)
