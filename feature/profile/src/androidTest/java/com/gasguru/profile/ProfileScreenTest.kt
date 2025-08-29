package com.gasguru.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.testing.BaseTest
import com.gasguru.core.ui.models.toUi
import com.gasguru.feature.profile.R
import com.gasguru.feature.profile.ui.ProfileContentUi
import com.gasguru.feature.profile.ui.ProfileScreen
import com.gasguru.feature.profile.ui.ProfileUiState
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProfileScreenTest : BaseTest() {

    @Test
    @DisplayName("Setting fuel")
    fun itemSettingFuel(): Unit = extension.use {
        setContent {
            ProfileScreen(uiState = ProfileUiState.Loading, event = {})
        }

        onNodeWithTag("loading").assertIsDisplayed()
    }

    @Test
    @DisplayName("Show bottom sheet when user clicks on setting fuel")
    fun showDialog(): Unit = extension.use {
        setContent {
            ProfileScreen(
                uiState = ProfileUiState.Success(
                    ProfileContentUi(
                        fuelTranslation = com.gasguru.core.ui.R.string.gasoline_95,
                        themeUi = ThemeMode.SYSTEM.toUi(),
                        allThemesUi = ThemeMode.entries.map { it.toUi() }
                    )
                ),
                event = {})
        }

        onNodeWithTag("fuel_setting_item").performClick()
        onNodeWithTag("bottom_sheet_fuel").assertIsDisplayed()
    }

    @Test
    @DisplayName("Show the info app version")
    fun showAppVersionInfo(): Unit = extension.use {
        setContent {
            ProfileScreen(
                uiState = ProfileUiState.Success(
                    ProfileContentUi(
                        fuelTranslation = com.gasguru.core.ui.R.string.gasoline_95,
                        themeUi = ThemeMode.SYSTEM.toUi(),
                        allThemesUi = ThemeMode.entries.map { it.toUi() }
                    )),
                event = {})
        }

        onNodeWithText(getStringResource(id = R.string.version, "1.0.0 (12)")).isDisplayed()
    }
}