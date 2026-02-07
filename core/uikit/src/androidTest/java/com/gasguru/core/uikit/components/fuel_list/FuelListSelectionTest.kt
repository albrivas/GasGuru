package com.gasguru.core.uikit.components.fuel_list

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.gasguru.core.testing.BaseTest
import com.gasguru.core.uikit.R
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.utils.BackgroundColorKey
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class FuelListSelectionTest : BaseTest() {

    private val testFuelItems = listOf(
        FuelItemModel(
            iconRes = R.drawable.ic_gasoline_95,
            nameRes = R.string.preview_fuel_type,
        ),
        FuelItemModel(
            iconRes = R.drawable.ic_gasoline_98,
            nameRes = R.string.preview_fuel_type_2,
        ),
        FuelItemModel(
            iconRes = R.drawable.ic_diesel,
            nameRes = R.string.preview_fuel_type_3,
        ),
    )

    @Test
    @DisplayName("GIVEN a list of fuels WHEN rendered THEN displays all items")
    fun displaysAllItems() = extension.use {
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
    @DisplayName("GIVEN an empty list WHEN rendered THEN no items exist")
    fun displaysNoItemsWhenEmpty() = extension.use {
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

        // Verify that the first item doesn't exist (empty list)
        assert(
            runCatching {
                onNodeWithTag("list_item_0").assertIsDisplayed()
            }.isFailure
        )
    }

    @Test
    @DisplayName("GIVEN a list with selected item WHEN rendered THEN selected item has highlighted background")
    fun selectedItemHasHighlightedBackground() = extension.use {
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
    @DisplayName("GIVEN a list WHEN item is clicked THEN callback is invoked with correct fuel")
    fun itemClickInvokesCallbackWithCorrectFuel() = extension.use {
        var selectedFuelId: Int? = null

        setContent {
            MyApplicationTheme {
                FuelListSelection(
                    model = FuelListSelectionModel(
                        list = testFuelItems,
                        selected = null,
                        onItemSelected = { fuelId ->
                            selectedFuelId = fuelId
                        },
                    ),
                )
            }
        }

        onNodeWithTag("list_item_1").performClick()

        assert(selectedFuelId == testFuelItems[1].nameRes)
    }

    @Test
    @DisplayName("GIVEN a list WHEN different items clicked THEN selection updates correctly")
    fun selectionUpdatesCorrectly() = extension.use {
        var selectedFuelId: Int? = null

        setContent {
            MyApplicationTheme {
                FuelListSelection(
                    model = FuelListSelectionModel(
                        list = testFuelItems,
                        selected = selectedFuelId,
                        onItemSelected = { fuelId ->
                            selectedFuelId = fuelId
                        },
                    ),
                )
            }
        }

        onNodeWithTag("list_item_0").performClick()
        assert(selectedFuelId == testFuelItems[0].nameRes)

        onNodeWithTag("list_item_2").performClick()
        assert(selectedFuelId == testFuelItems[2].nameRes)
    }

    @Test
    @DisplayName("GIVEN a single item list WHEN rendered THEN item is clickable")
    fun singleItemIsClickable() = extension.use {
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

        assert(clicked)
    }
}

private fun hasBackgroundColor(expectedColor: Color) =
    SemanticsMatcher.expectValue(BackgroundColorKey, expectedColor)