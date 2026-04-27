package com.gasguru.core.ui.mapper

import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.ui.R
import com.gasguru.core.ui.models.ThemeModeUi
import com.gasguru.core.uikit.components.icon.ThemeModeIcons

/**
 * Maps [ThemeMode] domain model to [ThemeModeUi].
 *
 * @receiver ThemeMode domain model
 * @return ThemeModeUi UI representation with icon and string resources
 */
fun ThemeMode.toUi(): ThemeModeUi = when (this) {
    ThemeMode.DARK -> ThemeModeUi(this, R.string.theme_mode_dark, ThemeModeIcons.Dark)
    ThemeMode.LIGHT -> ThemeModeUi(this, R.string.theme_mode_light, ThemeModeIcons.Light)
    ThemeMode.SYSTEM -> ThemeModeUi(this, R.string.theme_mode_system, ThemeModeIcons.System)
}
