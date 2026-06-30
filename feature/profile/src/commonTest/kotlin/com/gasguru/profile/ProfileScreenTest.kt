package com.gasguru.profile

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.ui.mapper.toUi
import com.gasguru.core.uikit.components.vehicle_item.VehicleItemCardModel
import com.gasguru.feature.profile.ui.ProfileContentUi
import com.gasguru.feature.profile.ui.ProfileEvents
import com.gasguru.feature.profile.ui.ProfileScreen
import com.gasguru.feature.profile.ui.ProfileUiState
import com.gasguru.core.ui.generated.resources.Res as CoreUiRes
import com.gasguru.core.ui.generated.resources.gasoline_95
import com.gasguru.core.uikit.generated.resources.Res
import com.gasguru.core.uikit.generated.resources.ic_vehicle_car
import com.gasguru.feature.profile.generated.resources.Res as ProfileRes
import com.gasguru.feature.profile.generated.resources.version
import org.jetbrains.compose.resources.stringResource
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ProfileScreenTest {

    private val defaultContent = ProfileContentUi(
        themeUi = ThemeMode.SYSTEM.toUi(),
        allThemesUi = ThemeMode.entries.map { it.toUi() },
    )

    @Test
    fun showsLoadingIndicator() = runComposeUiTest {
        setContent {
            ProfileScreen(uiState = ProfileUiState.Loading, event = {})
        }

        onNodeWithTag("loading").assertIsDisplayed()
    }

    @Test
    fun showThemeSheetOnThemeSettingClick() = runComposeUiTest {
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
    fun showAppVersionInfo() = runComposeUiTest {
        // En jvmMain, getAppVersion() devuelve "0.0.0 (0)" (stub no-op del target JVM).
        var versionText = ""

        setContent {
            versionText = stringResource(ProfileRes.string.version, "0.0.0 (0)")
            ProfileScreen(
                uiState = ProfileUiState.Success(content = defaultContent),
                event = {},
            )
        }

        onNodeWithText(versionText).isDisplayed()
    }

    @Test
    fun clickVehicleItemTriggersEditEvent() = runComposeUiTest {
        var editedVehicleId: Long? = null
        val contentWithVehicle = defaultContent.copy(
            vehicles = listOf(
                VehicleItemCardModel(
                    id = 42L,
                    name = "Golf VII",
                    vehicleTypeIconRes = Res.drawable.ic_vehicle_car,
                    fuelTypeTranslationRes = CoreUiRes.string.gasoline_95,
                    tankCapacityLitres = 55,
                    isSelected = true,
                ),
            ),
        )

        setContent {
            ProfileScreen(
                uiState = ProfileUiState.Success(content = contentWithVehicle),
                event = { profileEvent ->
                    if (profileEvent is ProfileEvents.EditVehicle) {
                        editedVehicleId = profileEvent.vehicleId
                    }
                },
            )
        }

        onNodeWithText("Golf VII").performClick()
        assertTrue(editedVehicleId == 42L)
    }
}
