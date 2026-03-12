package com.gasguru.feature.profile.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.ui.R
import com.gasguru.core.ui.mapper.toUi
import com.gasguru.core.ui.models.ThemeModeUi
import com.gasguru.core.uikit.components.vehicle_item.VehicleItemCardModel
import com.gasguru.core.uikit.R as RUikit

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

class ProfileContentUiPreviewParameterProvider : PreviewParameterProvider<ProfileContentUi> {
    override val values = sequenceOf(
        ProfileContentUi(
            themeUi = ThemeMode.SYSTEM.toUi(),
            allThemesUi = ThemeMode.entries.map { it.toUi() },
            vehicles = listOf(
                VehicleItemCardModel(
                    id = 1L,
                    name = "Golf VIII",
                    vehicleTypeIconRes = RUikit.drawable.ic_vehicle_car,
                    fuelTypeTranslationRes = R.string.gasoline_95,
                    tankCapacityLitres = 55,
                    isSelected = true,
                ),
                VehicleItemCardModel(
                    id = 2L,
                    name = "Honda CB500",
                    vehicleTypeIconRes = RUikit.drawable.ic_vehicle_motorcycle,
                    fuelTypeTranslationRes = R.string.gasoline_95,
                    tankCapacityLitres = 18,
                    isSelected = false,
                ),
            ),
        ),
        ProfileContentUi(
            themeUi = ThemeMode.DARK.toUi(),
            allThemesUi = ThemeMode.entries.map { it.toUi() },
            vehicles = listOf(
                VehicleItemCardModel(
                    id = 1L,
                    name = "Golf VIII",
                    vehicleTypeIconRes = RUikit.drawable.ic_vehicle_car,
                    fuelTypeTranslationRes = R.string.gasoline_95,
                    tankCapacityLitres = 55,
                    isSelected = false,
                ),
                VehicleItemCardModel(
                    id = 2L,
                    name = "Honda CB500",
                    vehicleTypeIconRes = RUikit.drawable.ic_vehicle_motorcycle,
                    fuelTypeTranslationRes = R.string.gasoline_95,
                    tankCapacityLitres = 18,
                    isSelected = false,
                ),
            ),
        ),
    )
}
