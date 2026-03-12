package com.gasguru.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.testing.BaseTest
import com.gasguru.core.ui.mapper.toUi
import com.gasguru.core.uikit.components.vehicle_item.VehicleItemCardModel
import com.gasguru.feature.profile.R
import com.gasguru.feature.profile.ui.ProfileContentUi
import com.gasguru.feature.profile.ui.ProfileScreen
import com.gasguru.feature.profile.ui.ProfileUiState
import com.gasguru.core.uikit.R as RUikit
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProfileScreenTest : BaseTest() {

    private val defaultContent = ProfileContentUi(
        themeUi = ThemeMode.SYSTEM.toUi(),
        allThemesUi = ThemeMode.entries.map { it.toUi() },
    )

    @Test
    @DisplayName("Show loading indicator when state is Loading")
    fun showsLoadingIndicator(): Unit = extension.use {
        setContent {
            ProfileScreen(uiState = ProfileUiState.Loading, event = {})
        }

        onNodeWithTag("loading").assertIsDisplayed()
    }

    @Test
    @DisplayName("Show theme bottom sheet when user clicks on theme setting")
    fun showThemeSheetOnThemeSettingClick(): Unit = extension.use {
        setContent {
            ProfileScreen(
                uiState = ProfileUiState.Success(content = defaultContent),
                event = {},
            )
        }

        onNodeWithTag("theme_setting_item").performClick()
        onNodeWithTag("bottom_sheet").assertIsDisplayed()
    }

    @Test
    @DisplayName("Show the app version info")
    fun showAppVersionInfo(): Unit = extension.use {
        setContent {
            ProfileScreen(
                uiState = ProfileUiState.Success(content = defaultContent),
                event = {},
            )
        }

        onNodeWithText(getStringResource(id = R.string.version, "1.0.0 (12)")).isDisplayed()
    }

    @Test
    @DisplayName("Trigger EditVehicle event when clicking a vehicle item")
    fun clickVehicleItemTriggersEditEvent(): Unit = extension.use {
        var editedVehicleId: Long? = null
        val contentWithVehicle = defaultContent.copy(
            vehicles = listOf(
                VehicleItemCardModel(
                    id = 42L,
                    name = "Golf VII",
                    vehicleTypeIconRes = RUikit.drawable.ic_vehicle_car,
                    fuelTypeTranslationRes = com.gasguru.core.ui.R.string.gasoline_95,
                    tankCapacityLitres = 55,
                    isSelected = true,
                ),
            ),
        )

        setContent {
            ProfileScreen(
                uiState = ProfileUiState.Success(content = contentWithVehicle),
                event = { profileEvent ->
                    if (profileEvent is com.gasguru.feature.profile.ui.ProfileEvents.EditVehicle) {
                        editedVehicleId = profileEvent.vehicleId
                    }
                },
            )
        }

        onNodeWithText("Golf VII").performClick()
        assert(editedVehicleId == 42L)
    }
}
