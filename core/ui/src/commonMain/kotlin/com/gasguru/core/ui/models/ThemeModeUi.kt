package com.gasguru.core.ui.models

import androidx.compose.runtime.Immutable
import com.gasguru.core.model.data.ThemeMode
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

@Immutable
data class ThemeModeUi(
    val mode: ThemeMode,
    val titleRes: StringResource,
    val iconRes: DrawableResource,
) {
    val id: Int get() = mode.id
}
