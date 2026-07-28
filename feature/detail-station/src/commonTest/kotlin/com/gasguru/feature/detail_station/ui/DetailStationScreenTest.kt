package com.gasguru.feature.detail_station.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.VehicleType
import com.gasguru.core.model.data.previewFuelStationDomain
import com.gasguru.core.ui.IconTintKey
import com.gasguru.core.ui.mapper.toUiModel
import com.gasguru.core.uikit.components.vehicle_item.VehicleItemCardModel
import com.gasguru.core.uikit.generated.resources.Res
import com.gasguru.core.uikit.generated.resources.ic_vehicle_car
import com.gasguru.core.uikit.generated.resources.ic_vehicle_motorcycle
import com.gasguru.core.uikit.generated.resources.preview_fuel_type
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.utils.TestTags
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class DetailStationScreenTest {

    @Test
    fun markFavoriteStation() = runComposeUiTest {
        val initialStation = previewFuelStationDomain()
        var station by mutableStateOf(initialStation)
        var black = Color.Unspecified
        var red = Color.Unspecified

        setContent {
            black = GasGuruTheme.colors.neutralBlack
            red = GasGuruTheme.colors.accentRed
            DetailStationScreen(
                uiState = DetailStationUiState.Success(
                    stationModel = station.toUiModel(),
                    address = null,
                    isOpen = false,
                ),
                lastUpdate = 0,
                staticMapUrl = "",
                onEvent = {
                    station = station.copy(isFavorite = true)
                },
                onBack = {},
            )
        }

        onNodeWithTag("icon_favorite", useUnmergedTree = true)
            .assert(hasIconTint(black))

        onNodeWithTag("button_favorite").performClick()
        waitForIdle()

        onNodeWithTag("icon_favorite", useUnmergedTree = true)
            .assert(hasIconTint(red))
    }

    @Test
    fun changeVehicleButtonOpensSheetAndSelectingVehicleEmitsSelectVehicleEvent() = runComposeUiTest {
        val initialStation = previewFuelStationDomain()
        var selectedVehicleId: Long? = null

        setContent {
            DetailStationScreen(
                uiState = DetailStationUiState.Success(
                    stationModel = initialStation.toUiModel(),
                    address = null,
                    isOpen = false,
                ),
                lastUpdate = 0,
                staticMapUrl = "",
                vehicle = Vehicle(
                    id = 1L,
                    name = "Golf VIII",
                    fuelType = FuelType.GASOLINE_95,
                    tankCapacity = 50,
                    vehicleType = VehicleType.CAR,
                    isPrincipal = true,
                ),
                vehicles = listOf(
                    VehicleItemCardModel(
                        id = 1L,
                        name = "Golf VIII",
                        vehicleTypeIconRes = Res.drawable.ic_vehicle_car,
                        fuelTypeTranslationRes = Res.string.preview_fuel_type,
                        tankCapacityLitres = 50,
                        isSelected = true,
                    ),
                    VehicleItemCardModel(
                        id = 2L,
                        name = "Honda CB500",
                        vehicleTypeIconRes = Res.drawable.ic_vehicle_motorcycle,
                        fuelTypeTranslationRes = Res.string.preview_fuel_type,
                        tankCapacityLitres = 18,
                        isSelected = false,
                    ),
                ),
                onEvent = { event ->
                    if (event is DetailStationEvent.SelectVehicle) {
                        selectedVehicleId = event.vehicleId
                    }
                },
                onBack = {},
            )
        }

        onNodeWithTag(TestTags.DetailStation.CHANGE_VEHICLE).performClick()
        waitForIdle()

        onNodeWithTag(TestTags.DetailStation.vehiclePickerItem(2L)).performClick()
        waitForIdle()

        assertEquals(2L, selectedVehicleId)
    }

    private fun hasIconTint(expectedColor: Color) =
        SemanticsMatcher.expectValue(IconTintKey, expectedColor)
}
