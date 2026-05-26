package com.gasguru.feature.profile.ui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.ui.generated.resources.Res
import com.gasguru.core.ui.generated.resources.gasoline_95
import com.gasguru.core.ui.mapper.toUi
import com.gasguru.core.uikit.components.icon.VehicleTypeIcons
import com.gasguru.core.uikit.components.vehicle_item.VehicleItemCardModel
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews

class ProfileContentUiPreviewParameterProvider : PreviewParameterProvider<ProfileContentUi> {
    override val values = sequenceOf(
        ProfileContentUi(
            themeUi = ThemeMode.SYSTEM.toUi(),
            allThemesUi = ThemeMode.entries.map { it.toUi() },
            vehicles = listOf(
                VehicleItemCardModel(
                    id = 1L,
                    name = "Golf VIII",
                    vehicleTypeIconRes = VehicleTypeIcons.Car,
                    fuelTypeTranslationRes = Res.string.gasoline_95,
                    tankCapacityLitres = 55,
                    isSelected = true,
                ),
                VehicleItemCardModel(
                    id = 2L,
                    name = "Honda CB500",
                    vehicleTypeIconRes = VehicleTypeIcons.Motorcycle,
                    fuelTypeTranslationRes = Res.string.gasoline_95,
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
                    vehicleTypeIconRes = VehicleTypeIcons.Car,
                    fuelTypeTranslationRes = Res.string.gasoline_95,
                    tankCapacityLitres = 55,
                    isSelected = false,
                ),
                VehicleItemCardModel(
                    id = 2L,
                    name = "Honda CB500",
                    vehicleTypeIconRes = VehicleTypeIcons.Motorcycle,
                    fuelTypeTranslationRes = Res.string.gasoline_95,
                    tankCapacityLitres = 18,
                    isSelected = false,
                ),
            ),
        ),
    )
}

@Composable
@ThemePreviews
private fun ProfileScreenLoadingPreview() {
    MyApplicationTheme {
        ProfileScreen(uiState = ProfileUiState.Loading, event = {})
    }
}
