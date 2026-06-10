package com.gasguru.core.ui.mapper

import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.ui.generated.resources.Res
import com.gasguru.core.ui.generated.resources.theme_mode_dark
import com.gasguru.core.ui.generated.resources.theme_mode_light
import com.gasguru.core.ui.generated.resources.theme_mode_system
import com.gasguru.core.ui.models.ThemeModeUi
import com.gasguru.core.uikit.components.icon.ThemeModeIcons

fun ThemeMode.toUi(): ThemeModeUi = when (this) {
    ThemeMode.DARK -> ThemeModeUi(mode = this, titleRes = Res.string.theme_mode_dark, iconRes = ThemeModeIcons.Dark)
    ThemeMode.LIGHT -> ThemeModeUi(mode = this, titleRes = Res.string.theme_mode_light, iconRes = ThemeModeIcons.Light)
    ThemeMode.SYSTEM -> ThemeModeUi(
        mode = this,
        titleRes = Res.string.theme_mode_system,
        iconRes = ThemeModeIcons.System
    )
}
