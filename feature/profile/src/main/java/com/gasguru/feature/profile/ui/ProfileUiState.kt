package com.gasguru.feature.profile.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.ui.R
import com.gasguru.core.ui.ThemeModeUi
import com.gasguru.core.ui.toUi

@Immutable
data class ProfileContentUi(
    val fuelTranslation: Int,
    val themeUi: ThemeModeUi,
    val allThemesUi: List<ThemeModeUi>
)

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val content: ProfileContentUi) : ProfileUiState
    data object LoadFailed : ProfileUiState
}

class ProfileContentUiPreviewParameterProvider : PreviewParameterProvider<ProfileContentUi> {
    override val values = sequenceOf(
        ProfileContentUi(
            fuelTranslation = R.string.gasoline_95,
            themeUi = ThemeMode.SYSTEM.toUi(),
            allThemesUi = ThemeMode.entries.map { it.toUi() }
        ),
        ProfileContentUi(
            fuelTranslation = R.string.diesel,
            themeUi = ThemeMode.DARK.toUi(),
            allThemesUi = ThemeMode.entries.map { it.toUi() }
        )
    )
}
