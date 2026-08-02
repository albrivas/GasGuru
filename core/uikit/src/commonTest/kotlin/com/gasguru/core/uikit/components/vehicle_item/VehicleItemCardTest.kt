package com.gasguru.core.uikit.components.vehicle_item

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.gasguru.core.uikit.generated.resources.Res
import com.gasguru.core.uikit.generated.resources.ic_vehicle_car
import com.gasguru.core.uikit.generated.resources.preview_fuel_type
import com.gasguru.core.uikit.theme.MyApplicationTheme
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class VehicleItemCardTest {

    private fun defaultModel(isSelected: Boolean) = VehicleItemCardModel(
        id = 1L,
        name = "Golf VIII",
        vehicleTypeIconRes = Res.drawable.ic_vehicle_car,
        fuelTypeTranslationRes = Res.string.preview_fuel_type,
        tankCapacityLitres = 55,
        isSelected = isSelected,
    )

    @Test
    fun chevronIsShownByDefault() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                VehicleItemCard(model = defaultModel(isSelected = true))
            }
        }

        onNodeWithTag("vehicle_item_chevron").assertIsDisplayed()
    }

    @Test
    fun chevronIsHiddenWhenShowChevronIsFalse() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                VehicleItemCard(model = defaultModel(isSelected = true), showChevron = false)
            }
        }

        onNodeWithTag("vehicle_item_chevron").assertDoesNotExist()
    }

    @Test
    fun chevronIsHiddenForUnselectedVehicleWhenShowChevronIsFalse() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                VehicleItemCard(model = defaultModel(isSelected = false), showChevron = false)
            }
        }

        onNodeWithTag("vehicle_item_chevron").assertDoesNotExist()
    }

    @Test
    fun clickInvokesCallback() = runComposeUiTest {
        var clicked = false

        setContent {
            MyApplicationTheme {
                VehicleItemCard(model = defaultModel(isSelected = true), onClick = { clicked = true })
            }
        }

        onNodeWithText("Golf VIII").performClick()

        assertTrue(clicked)
    }
}
