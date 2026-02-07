package com.gasguru.core.uikit.components.selectedItem

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.gasguru.core.testing.BaseTest
import com.gasguru.core.uikit.R
import com.gasguru.core.uikit.theme.MyApplicationTheme
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class SelectedItemTest : BaseTest() {

    @Test
    @DisplayName("GIVEN an unselected item WHEN rendered THEN displays all elements correctly")
    fun unselectedItemDisplaysCorrectly() = extension.use {
        setContent {
            MyApplicationTheme {
                SelectedItem(
                    model = SelectedItemModel(
                        title = R.string.preview_fuel_type,
                        isSelected = false,
                        image = R.drawable.ic_gasoline_95,
                    ),
                )
            }
        }

        onNodeWithText(getStringResource(R.string.preview_fuel_type)).assertIsDisplayed()
        onNodeWithTag("radio_button_${R.string.preview_fuel_type}")
            .assertIsDisplayed()
            .assertIsNotSelected()
    }

    @Test
    @DisplayName("GIVEN a selected item WHEN rendered THEN displays selected state correctly")
    fun selectedItemDisplaysCorrectly() = extension.use {
        setContent {
            MyApplicationTheme {
                SelectedItem(
                    model = SelectedItemModel(
                        title = R.string.preview_fuel_type,
                        isSelected = true,
                        image = R.drawable.ic_gasoline_95,
                    ),
                )
            }
        }

        onNodeWithText(getStringResource(R.string.preview_fuel_type)).assertIsDisplayed()
        onNodeWithTag("radio_button_${R.string.preview_fuel_type}")
            .assertIsDisplayed()
            .assertIsSelected()
    }

    @Test
    @DisplayName("GIVEN an item WHEN clicked THEN callback is invoked")
    fun itemClickInvokesCallback() = extension.use {
        var clickedModel: SelectedItemModel? = null

        setContent {
            MyApplicationTheme {
                SelectedItem(
                    model = SelectedItemModel(
                        title = R.string.preview_fuel_type,
                        isSelected = false,
                        image = R.drawable.ic_gasoline_95,
                        onItemSelected = { model ->
                            clickedModel = model
                        },
                    ),
                )
            }
        }

        onNodeWithText(getStringResource(R.string.preview_fuel_type)).performClick()

        assert(clickedModel != null)
        assert(clickedModel?.title == R.string.preview_fuel_type)
    }

    @Test
    @DisplayName("GIVEN an item WHEN radio button clicked THEN callback is invoked")
    fun radioButtonClickInvokesCallback() = extension.use {
        var clickedModel: SelectedItemModel? = null

        setContent {
            MyApplicationTheme {
                SelectedItem(
                    model = SelectedItemModel(
                        title = R.string.preview_fuel_type,
                        isSelected = false,
                        image = R.drawable.ic_gasoline_95,
                        onItemSelected = { model ->
                            clickedModel = model
                        },
                    ),
                )
            }
        }

        onNodeWithTag("radio_button_${R.string.preview_fuel_type}").performClick()

        assert(clickedModel != null)
        assert(clickedModel?.title == R.string.preview_fuel_type)
    }
}