package com.gasguru.core.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.uikit.R as RUikit

@Immutable
data class ThemeModeUi(
    val mode: ThemeMode,
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int
)

class ThemeModeUiPreviewParameterProvider : PreviewParameterProvider<ThemeModeUi> {
    override val values = sequenceOf(
        ThemeMode.SYSTEM.toUi(),
        ThemeMode.LIGHT.toUi(),
        ThemeMode.DARK.toUi()
    )
}

fun ThemeMode.toUi(): ThemeModeUi = when (this) {
    ThemeMode.DARK -> ThemeModeUi(this, R.string.theme_mode_dark, RUikit.drawable.ic_dark_mode)
    ThemeMode.LIGHT -> ThemeModeUi(this, R.string.theme_mode_light, RUikit.drawable.ic_light_mode)
    ThemeMode.SYSTEM -> ThemeModeUi(this, R.string.theme_mode_system, RUikit.drawable.ic_system_mode)
}
