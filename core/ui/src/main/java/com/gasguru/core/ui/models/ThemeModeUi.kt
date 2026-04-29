package com.gasguru.core.ui.models

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.gasguru.core.model.data.ThemeMode
import org.jetbrains.compose.resources.DrawableResource

@Immutable
data class ThemeModeUi(
    val mode: ThemeMode,
    @StringRes val titleRes: Int,
    val iconRes: DrawableResource
) {
    val id: Int get() = mode.id
}
