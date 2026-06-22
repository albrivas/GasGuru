package com.gasguru.core.uikit.components.fuel_list

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.gasguru.core.uikit.generated.resources.Res
import com.gasguru.core.uikit.generated.resources.ic_diesel
import com.gasguru.core.uikit.generated.resources.ic_gasoline_95
import com.gasguru.core.uikit.generated.resources.ic_gasoline_98
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.utils.BackgroundColorKey
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class FuelListSelectionTest {

    // Literales de los strings preview_fuel_type / _2 / _3 (datos del modelo, no renderizados internamente)
    private val testFuelItems = listOf(
        FuelItemModel(iconRes = Res.drawable.ic_gasoline_95, nameRes = "Gasoline 95"),
        FuelItemModel(iconRes = Res.drawable.ic_gasoline_98, nameRes = "Gasoline 98"),
        FuelItemModel(iconRes = Res.drawable.ic_diesel, nameRes = "Diesel"),
    )

    @Test
    fun displaysAllItems() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                FuelListSelection(
                    model = FuelListSelectionModel(
                        list = testFuelItems,
                        selected = null,
                        onItemSelected = {},
                    ),
                )
            }
        }

        onNodeWithTag("list_item_0").assertIsDisplayed()
        onNodeWithTag("list_item_1").assertIsDisplayed()
        onNodeWithTag("list_item_2").assertIsDisplayed()
    }

    @Test
    fun displaysNoItemsWhenEmpty() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                FuelListSelection(
                    model = FuelListSelectionModel(
                        list = emptyList(),
                        selected = null,
                        onItemSelected = {},
                    ),
                )
            }
        }

        assertTrue(
            runCatching {
                onNodeWithTag("list_item_0").assertIsDisplayed()
            }.isFailure,
        )
    }

    @Test
    fun selectedItemHasHighlightedBackground() = runComposeUiTest {
        val selectedIndex = 1
        val selectedItem = testFuelItems[selectedIndex].nameRes
        var selectedBackgroundColor = Color.Unspecified
        var unselectedBackgroundColor = Color.Unspecified

        setContent {
            MyApplicationTheme {
                selectedBackgroundColor = GasGuruTheme.colors.accentGreen.copy(alpha = 0.2f)
                unselectedBackgroundColor = GasGuruTheme.colors.neutral200
                FuelListSelection(
                    model = FuelListSelectionModel(
                        list = testFuelItems,
                        selected = selectedItem,
                        onItemSelected = {},
                    ),
                )
            }
        }

        onNodeWithTag("list_item_$selectedIndex", useUnmergedTree = true)
            .assert(hasBackgroundColor(selectedBackgroundColor))

        onNodeWithTag("list_item_0", useUnmergedTree = true)
            .assert(hasBackgroundColor(unselectedBackgroundColor))

        onNodeWithTag("radio_button_$selectedItem").assertIsSelected()
    }

    @Test
    fun itemClickInvokesCallbackWithCorrectFuel() = runComposeUiTest {
        var selectedFuelName: String? = null

        setContent {
            MyApplicationTheme {
                FuelListSelection(
                    model = FuelListSelectionModel(
                        list = testFuelItems,
                        selected = null,
                        onItemSelected = { fuelName ->
                            selectedFuelName = fuelName
                        },
                    ),
                )
            }
        }

        onNodeWithTag("list_item_1").performClick()

        assertTrue(selectedFuelName == testFuelItems[1].nameRes)
    }

    @Test
    fun selectionUpdatesCorrectly() = runComposeUiTest {
        var selectedFuelName: String? = null

        setContent {
            MyApplicationTheme {
                FuelListSelection(
                    model = FuelListSelectionModel(
                        list = testFuelItems,
                        selected = selectedFuelName,
                        onItemSelected = { fuelName ->
                            selectedFuelName = fuelName
                        },
                    ),
                )
            }
        }

        onNodeWithTag("list_item_0").performClick()
        assertTrue(selectedFuelName == testFuelItems[0].nameRes)

        onNodeWithTag("list_item_2").performClick()
        assertTrue(selectedFuelName == testFuelItems[2].nameRes)
    }

    @Test
    fun singleItemIsClickable() = runComposeUiTest {
        var clicked = false

        setContent {
            MyApplicationTheme {
                FuelListSelection(
                    model = FuelListSelectionModel(
                        list = listOf(testFuelItems[0]),
                        selected = null,
                        onItemSelected = { clicked = true },
                    ),
                )
            }
        }

        onNodeWithTag("list_item_0").performClick()

        assertTrue(clicked)
    }
}

private fun hasBackgroundColor(expectedColor: Color) =
    SemanticsMatcher.expectValue(BackgroundColorKey, expectedColor)
