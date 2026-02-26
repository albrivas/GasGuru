package com.gasguru.feature.onboarding_welcome.ui

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.testing.BaseTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class OnboardingFuelPreferencesTest : BaseTest() {

    @Test
    @DisplayName("GIVEN a selected fuel type screen, WHEN the user doesn't select a fuel type, THEN the button is disabled")
    fun buttonDisabled(): Unit = extension.use {
        setContent {
            OnboardingFuelPreferences(
                fuelList = emptyList(),
                selectedFuel = null,
                navigateNext = {},
                onSelectedFuel = {},
                saveSelection = {},
            )
        }
        onNodeWithTag("button_next_onboarding").assertIsNotEnabled()
    }

    @Test
    @DisplayName("GIVEN a selected fuel type screen, WHEN the user select a fuel type, THEN the button is enabled")
    fun buttonEnabled(): Unit = extension.use {
        setContent {
            OnboardingFuelPreferences(
                fuelList = listOf(
                    FuelType.GASOLINE_95,
                    FuelType.GASOLINE_98,
                    FuelType.DIESEL,
                    FuelType.DIESEL_PLUS,
                ),
                selectedFuel = null,
                navigateNext = {},
                onSelectedFuel = {},
                saveSelection = {},
            )
        }
        onNodeWithTag("list_item_1").performClick()
        onNodeWithTag("button_next_onboarding").assertIsEnabled()
    }
}