package com.gasguru.core.uikit.components.alert

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOff
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.gasguru.core.uikit.theme.MyApplicationTheme
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class GasGuruAlertDialogTest {

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
    fun showsAllElements() = runComposeUiTest {
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
    fun callsPrimaryButtonClick() = runComposeUiTest {
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
    fun callsSecondaryButtonClick() = runComposeUiTest {
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
    fun showsOnlyPrimaryButton() = runComposeUiTest {
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
