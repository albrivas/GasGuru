package com.gasguru.core.uikit.components.fuel_type_chip

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.gasguru.core.testing.BaseTest
import com.gasguru.core.uikit.R
import com.gasguru.core.uikit.theme.MyApplicationTheme
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class FuelTypeChipTest : BaseTest() {

    private val defaultModel = FuelTypeChipModel(
        iconRes = R.drawable.ic_gasoline_95,
        nameRes = R.string.preview_fuel_type,
    )

    @Test
    @DisplayName("GIVEN chip model WHEN rendered THEN icon is displayed")
    fun iconIsDisplayed() = extension.use {
        setContent {
            MyApplicationTheme {
                FuelTypeChip(model = defaultModel)
            }
        }

        onNodeWithTag("fuel_type_chip_icon").assertIsDisplayed()
    }

    @Test
    @DisplayName("GIVEN chip model WHEN rendered THEN fuel name text is displayed")
    fun fuelNameTextIsDisplayed() = extension.use {
        setContent {
            MyApplicationTheme {
                FuelTypeChip(model = defaultModel)
            }
        }

        onNodeWithTag("fuel_type_chip_name").assertIsDisplayed()
        onNodeWithText(getStringResource(R.string.preview_fuel_type)).assertIsDisplayed()
    }

    @Test
    @DisplayName("GIVEN chip model WHEN rendered THEN icon and name are both visible simultaneously")
    fun iconAndNameAreBothVisible() = extension.use {
        setContent {
            MyApplicationTheme {
                FuelTypeChip(model = defaultModel)
            }
        }

        onNodeWithTag("fuel_type_chip_icon").assertIsDisplayed()
        onNodeWithTag("fuel_type_chip_name").assertIsDisplayed()
    }

    @Test
    @DisplayName("GIVEN chip model WHEN rendered in dark theme THEN component displays correctly")
    fun componentDisplaysCorrectlyInDarkTheme() = extension.use {
        setContent {
            MyApplicationTheme(darkTheme = true) {
                FuelTypeChip(model = defaultModel)
            }
        }

        onNodeWithTag("fuel_type_chip_icon").assertIsDisplayed()
        onNodeWithTag("fuel_type_chip_name").assertIsDisplayed()
    }
}
