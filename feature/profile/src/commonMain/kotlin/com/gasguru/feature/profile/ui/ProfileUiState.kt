package com.gasguru.feature.profile.ui

import androidx.compose.runtime.Immutable
import com.gasguru.core.ui.models.ThemeModeUi
import com.gasguru.core.uikit.components.vehicle_item.VehicleItemCardModel

@Immutable
data class ProfileContentUi(
    val themeUi: ThemeModeUi,
    val allThemesUi: List<ThemeModeUi>,
    val vehicles: List<VehicleItemCardModel> = emptyList(),
)

sealed class ProfileSheet {
    object None : ProfileSheet()
    object Theme : ProfileSheet()
}

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val content: ProfileContentUi) : ProfileUiState
    data object LoadFailed : ProfileUiState
}
