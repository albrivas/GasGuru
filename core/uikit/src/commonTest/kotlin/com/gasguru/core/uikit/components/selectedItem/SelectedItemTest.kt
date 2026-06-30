package com.gasguru.core.uikit.components.selectedItem

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.gasguru.core.uikit.generated.resources.Res
import com.gasguru.core.uikit.generated.resources.ic_gasoline_95
import com.gasguru.core.uikit.theme.MyApplicationTheme
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class SelectedItemTest {

    // "Gasoline 95" — literal de preview_fuel_type, dato del modelo
    private val previewFuelType = "Gasoline 95"

    @Test
    fun unselectedItemDisplaysCorrectly() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                SelectedItem(
                    model = SelectedItemModel(
                        title = previewFuelType,
                        isSelected = false,
                        image = Res.drawable.ic_gasoline_95,
                    ),
                )
            }
        }

        onNodeWithText(previewFuelType).assertIsDisplayed()
        onNodeWithTag("radio_button_$previewFuelType")
            .assertIsDisplayed()
            .assertIsNotSelected()
    }

    @Test
    fun selectedItemDisplaysCorrectly() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                SelectedItem(
                    model = SelectedItemModel(
                        title = previewFuelType,
                        isSelected = true,
                        image = Res.drawable.ic_gasoline_95,
                    ),
                )
            }
        }

        onNodeWithText(previewFuelType).assertIsDisplayed()
        onNodeWithTag("radio_button_$previewFuelType")
            .assertIsDisplayed()
            .assertIsSelected()
    }

    @Test
    fun itemClickInvokesCallback() = runComposeUiTest {
        var clickedModel: SelectedItemModel? = null

        setContent {
            MyApplicationTheme {
                SelectedItem(
                    model = SelectedItemModel(
                        title = previewFuelType,
                        isSelected = false,
                        image = Res.drawable.ic_gasoline_95,
                        onItemSelected = { model ->
                            clickedModel = model
                        },
                    ),
                )
            }
        }

        onNodeWithText(previewFuelType).performClick()

        assertTrue(clickedModel != null)
        assertTrue(clickedModel?.title == previewFuelType)
    }

    @Test
    fun radioButtonClickInvokesCallback() = runComposeUiTest {
        var clickedModel: SelectedItemModel? = null

        setContent {
            MyApplicationTheme {
                SelectedItem(
                    model = SelectedItemModel(
                        title = previewFuelType,
                        isSelected = false,
                        image = Res.drawable.ic_gasoline_95,
                        onItemSelected = { model ->
                            clickedModel = model
                        },
                    ),
                )
            }
        }

        onNodeWithTag("radio_button_$previewFuelType").performClick()

        assertTrue(clickedModel != null)
        assertTrue(clickedModel?.title == previewFuelType)
    }
}
