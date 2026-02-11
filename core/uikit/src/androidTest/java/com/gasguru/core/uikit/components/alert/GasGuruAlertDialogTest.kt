package com.gasguru.core.uikit.components.alert

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOff
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.gasguru.core.testing.BaseTest
import com.gasguru.core.uikit.theme.MyApplicationTheme
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class GasGuruAlertDialogTest : BaseTest() {

    private val testModel = GasGuruAlertDialogModel(
        icon = Icons.Outlined.LocationOff,
        iconTint = Color(0xFFF59E0B),
        iconBackgroundColor = Color(0xFFFFFBEB),
        title = "Test title",
        description = "Test description",
        primaryButtonText = "Primary",
        secondaryButtonText = "Secondary",
    )

    @Test
    @DisplayName("GIVEN alert dialog WHEN rendered THEN shows title, description and buttons")
    fun showsAllElements() = extension.use {
        setContent {
            MyApplicationTheme {
                GasGuruAlertDialog(
                    model = testModel,
                    onPrimaryButtonClick = {},
                    onSecondaryButtonClick = {},
                )
            }
        }

        onNodeWithText("Test title").assertIsDisplayed()
        onNodeWithText("Test description").assertIsDisplayed()
        onNodeWithText("Primary").assertIsDisplayed()
        onNodeWithText("Secondary").assertIsDisplayed()
    }

    @Test
    @DisplayName("GIVEN alert dialog WHEN primary button clicked THEN calls onPrimaryButtonClick")
    fun callsPrimaryButtonClick() = extension.use {
        var primaryClicked = false

        setContent {
            MyApplicationTheme {
                GasGuruAlertDialog(
                    model = testModel,
                    onPrimaryButtonClick = { primaryClicked = true },
                    onSecondaryButtonClick = {},
                )
            }
        }

        onNodeWithText("Primary").performClick()

        assertTrue(primaryClicked)
    }

    @Test
    @DisplayName("GIVEN alert dialog WHEN secondary button clicked THEN calls onSecondaryButtonClick")
    fun callsSecondaryButtonClick() = extension.use {
        var secondaryClicked = false

        setContent {
            MyApplicationTheme {
                GasGuruAlertDialog(
                    model = testModel,
                    onPrimaryButtonClick = {},
                    onSecondaryButtonClick = { secondaryClicked = true },
                )
            }
        }

        onNodeWithText("Secondary").performClick()

        assertTrue(secondaryClicked)
    }

    @Test
    @DisplayName("GIVEN alert dialog without secondary text WHEN rendered THEN shows only primary button")
    fun showsOnlyPrimaryButton() = extension.use {
        val modelWithoutSecondary = testModel.copy(secondaryButtonText = null)

        setContent {
            MyApplicationTheme {
                GasGuruAlertDialog(
                    model = modelWithoutSecondary,
                    onPrimaryButtonClick = {},
                )
            }
        }

        onNodeWithText("Test title").assertIsDisplayed()
        onNodeWithText("Test description").assertIsDisplayed()
        onNodeWithText("Primary").assertIsDisplayed()
        onNodeWithText("Secondary").assertDoesNotExist()
    }
}