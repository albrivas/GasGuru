package com.gasguru.core.uikit.components.tank_cost_card

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.gasguru.core.uikit.components.fuel_type_chip.FuelTypeChipModel
import com.gasguru.core.uikit.generated.resources.Res
import com.gasguru.core.uikit.generated.resources.full_tank_cost
import com.gasguru.core.uikit.theme.MyApplicationTheme
import org.jetbrains.compose.resources.stringResource
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class TankCostCardTest {

    // "Gasoline 95" — literal para el string preview_fuel_type que es dato del modelo
    private fun defaultModel(onEditClick: () -> Unit = {}) = TankCostCardModel(
        fuelTypeChip = FuelTypeChipModel(nameRes = "Gasoline 95"),
        totalCost = "86.72 €",
        litres = "55.6 L",
        pricePerLitre = "1.559 €/l",
        vehicleName = "Golf VIII",
        onEditClick = onEditClick,
    )

    @Test
    fun totalCostIsDisplayed() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                TankCostCard(model = defaultModel())
            }
        }

        onNodeWithTag("tank_cost_price").assertIsDisplayed()
    }

    @Test
    fun litresAreDisplayed() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                TankCostCard(model = defaultModel())
            }
        }

        onNodeWithTag("tank_cost_litres").assertIsDisplayed()
    }

    @Test
    fun pricePerLitreIsDisplayed() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                TankCostCard(model = defaultModel())
            }
        }

        onNodeWithTag("tank_cost_price_per_litre").assertIsDisplayed()
    }

    @Test
    fun vehicleNameIsDisplayed() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                TankCostCard(model = defaultModel())
            }
        }

        onNodeWithTag("tank_cost_vehicle_name").assertIsDisplayed()
    }

    @Test
    fun fuelTypeChipIsShown() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                TankCostCard(model = defaultModel())
            }
        }

        onNodeWithTag("fuel_type_chip_name").assertIsDisplayed()
    }

    @Test
    fun editButtonClickInvokesCallback() = runComposeUiTest {
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
    fun fullTankCostLabelIsShown() = runComposeUiTest {
        var fullTankCostLabel = ""

        setContent {
            fullTankCostLabel = stringResource(Res.string.full_tank_cost)
            MyApplicationTheme {
                TankCostCard(model = defaultModel())
            }
        }

        onNodeWithText(fullTankCostLabel).assertIsDisplayed()
    }
}
