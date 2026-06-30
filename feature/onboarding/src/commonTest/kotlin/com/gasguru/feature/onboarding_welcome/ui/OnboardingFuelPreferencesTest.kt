package com.gasguru.feature.onboarding_welcome.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import com.gasguru.core.model.data.FuelType
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class OnboardingFuelPreferencesTest {

    @Test
    fun buttonDisabled() = runComposeUiTest {
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
    fun buttonEnabled() = runComposeUiTest {
        setContent {
            OnboardingFuelPreferences(
                fuelList = listOf(
                    FuelType.GASOLINE_95,
                    FuelType.GASOLINE_98,
                    FuelType.DIESEL,
                    FuelType.DIESEL_PLUS,
                ),
                selectedFuel = FuelType.GASOLINE_95,
                navigateNext = {},
                onSelectedFuel = {},
                saveSelection = {},
            )
        }

        onNodeWithTag("button_next_onboarding").assertIsEnabled()
    }
}
