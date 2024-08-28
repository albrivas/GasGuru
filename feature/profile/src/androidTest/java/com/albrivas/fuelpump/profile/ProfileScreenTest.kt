package com.albrivas.fuelpump.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.albrivas.fuelpump.core.model.data.FuelType
import com.albrivas.fuelpump.core.model.data.UserData
import com.albrivas.fuelpump.core.testing.BaseTest
import com.albrivas.fuelpump.core.ui.translation
import com.albrivas.fuelpump.feature.profile.ProfileScreen
import com.albrivas.fuelpump.feature.profile.ProfileUiState
import org.junit.jupiter.api.DisplayName

import org.junit.jupiter.api.Test

class ProfileScreenTest : BaseTest() {

    @Test
    @DisplayName("Setting fuel")
    fun itemSettingFuel(): Unit = extension.use {
        setContent {
            ProfileScreen(uiState = ProfileUiState.Loading)
        }

        onNodeWithTag("loading").assertIsDisplayed()
    }

    @Test
    @DisplayName("Show dialog when user clicks on setting fuel")
    fun showDialog(): Unit = extension.use {
        setContent {
            ProfileScreen(uiState = ProfileUiState.Success(UserData(FuelType.GASOLINE_95)))
        }

        val gasoline = FuelType.GASOLINE_95.translation()
        onNodeWithTag("fuel_setting_item").performClick()
        onNodeWithTag("fuel_dialog").assertIsDisplayed()
        onNodeWithTag("radio_button_$gasoline").assertIsSelected()
    }
}