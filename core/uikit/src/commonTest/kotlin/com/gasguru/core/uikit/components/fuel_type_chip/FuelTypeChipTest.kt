package com.gasguru.core.uikit.components.fuel_type_chip

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import com.gasguru.core.uikit.theme.MyApplicationTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class FuelTypeChipTest {

    private val previewFuelType = "Gasoline 95"
    private val defaultModel = FuelTypeChipModel(nameRes = previewFuelType)

    @Test
    fun fuelNameTextIsDisplayed() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                FuelTypeChip(model = defaultModel)
            }
        }

        onNodeWithTag("fuel_type_chip_name").assertIsDisplayed()
        onNodeWithText(previewFuelType).assertIsDisplayed()
    }

    @Test
    fun iconAndNameAreBothVisible() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                FuelTypeChip(model = defaultModel)
            }
        }

        onNodeWithTag("fuel_type_chip_name").assertIsDisplayed()
    }

    @Test
    fun componentDisplaysCorrectlyInDarkTheme() = runComposeUiTest {
        setContent {
            MyApplicationTheme(darkTheme = true) {
                FuelTypeChip(model = defaultModel)
            }
        }

        onNodeWithTag("fuel_type_chip_name").assertIsDisplayed()
    }
}
