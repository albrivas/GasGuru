package com.gasguru.core.ui.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.ui.R
import com.gasguru.core.uikit.R as RUikit

@Immutable
data class ThemeModeUi(
    val mode: ThemeMode,
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int
) {
    val id: Int get() = mode.id
}
