package com.gasguru.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.UserData
import com.gasguru.core.testing.BaseTest
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
                uiState = ProfileUiState.Success(UserData(FuelType.GASOLINE_95)),
                event = {})
        }

        onNodeWithTag("fuel_setting_item").performClick()
        onNodeWithTag("bottom_sheet_fuel").assertIsDisplayed()
    }
}