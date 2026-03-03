package com.gasguru.core.uikit.components.tank_cost_card

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.gasguru.core.testing.BaseTest
import com.gasguru.core.uikit.R
import com.gasguru.core.uikit.components.fuel_type_chip.FuelTypeChipModel
import com.gasguru.core.uikit.theme.MyApplicationTheme
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TankCostCardTest : BaseTest() {

    private fun defaultModel(onEditClick: () -> Unit = {}) = TankCostCardModel(
        fuelTypeChip = FuelTypeChipModel(
            nameRes = R.string.preview_fuel_type,
        ),
        totalCost = "86.72 €",
        litres = "55.6 L",
        pricePerLitre = "1.559 €/l",
        vehicleName = "Golf VIII",
        onEditClick = onEditClick,
    )

    @Test
    @DisplayName("GIVEN card model WHEN rendered THEN total cost is displayed")
    fun totalCostIsDisplayed() = extension.use {
        setContent {
            MyApplicationTheme {
                TankCostCard(model = defaultModel())
            }
        }

        onNodeWithTag("tank_cost_price").assertIsDisplayed()
    }

    @Test
    @DisplayName("GIVEN card model WHEN rendered THEN litres are displayed")
    fun litresAreDisplayed() = extension.use {
        setContent {
            MyApplicationTheme {
                TankCostCard(model = defaultModel())
            }
        }

        onNodeWithTag("tank_cost_litres").assertIsDisplayed()
    }

    @Test
    @DisplayName("GIVEN card model WHEN rendered THEN price per litre is displayed")
    fun pricePerLitreIsDisplayed() = extension.use {
        setContent {
            MyApplicationTheme {
                TankCostCard(model = defaultModel())
            }
        }

        onNodeWithTag("tank_cost_price_per_litre").assertIsDisplayed()
    }

    @Test
    @DisplayName("GIVEN card model WHEN rendered THEN vehicle name is displayed")
    fun vehicleNameIsDisplayed() = extension.use {
        setContent {
            MyApplicationTheme {
                TankCostCard(model = defaultModel())
            }
        }

        onNodeWithTag("tank_cost_vehicle_name").assertIsDisplayed()
    }

    @Test
    @DisplayName("GIVEN card model WHEN rendered THEN fuel type chip is shown")
    fun fuelTypeChipIsShown() = extension.use {
        setContent {
            MyApplicationTheme {
                TankCostCard(model = defaultModel())
            }
        }

        onNodeWithTag("fuel_type_chip_name").assertIsDisplayed()
    }

    @Test
    @DisplayName("GIVEN card model WHEN edit button clicked THEN onEditClick callback is invoked")
    fun editButtonClickInvokesCallback() = extension.use {
        var editClicked = false

        setContent {
            MyApplicationTheme {
                TankCostCard(model = defaultModel(onEditClick = { editClicked = true }))
            }
        }

        onNodeWithTag("tank_cost_edit_button").performClick()

        assertTrue(editClicked)
    }

    @Test
    @DisplayName("GIVEN card model WHEN rendered THEN full_tank_cost label is shown")
    fun fullTankCostLabelIsShown() = extension.use {
        setContent {
            MyApplicationTheme {
                TankCostCard(model = defaultModel())
            }
        }

        onNodeWithText(getStringResource(R.string.full_tank_cost)).assertIsDisplayed()
    }
}
