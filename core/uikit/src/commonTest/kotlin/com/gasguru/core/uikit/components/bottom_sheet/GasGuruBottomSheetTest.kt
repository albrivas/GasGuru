package com.gasguru.core.uikit.components.bottom_sheet

import androidx.compose.material3.Text
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.utils.TestTags
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class GasGuruBottomSheetTest {

    @Test
    fun showsTitleAndSubtitleWhenProvided() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                GasGuruBottomSheet(
                    model = GasGuruBottomSheetModel(
                        title = "Capacidad del depósito",
                        subtitle = "Entre 1 y 999 litros",
                        onDismiss = {},
                    ),
                ) {}
            }
        }

        onNodeWithText("Capacidad del depósito").assertExists()
        onNodeWithText("Entre 1 y 999 litros").assertExists()
    }

    @Test
    fun doesNotShowSubtitleWhenNull() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                GasGuruBottomSheet(
                    model = GasGuruBottomSheetModel(
                        title = "Cambiar vehículo",
                        onDismiss = {},
                    ),
                ) {}
            }
        }

        onNodeWithTag(TestTags.BottomSheet.SHEET).assertExists()
        onNodeWithText("Cambiar vehículo").assertExists()
    }

    @Test
    fun callsOnDismissWhenCloseIconClicked() = runComposeUiTest {
        var dismissed = false

        setContent {
            MyApplicationTheme {
                GasGuruBottomSheet(
                    model = GasGuruBottomSheetModel(
                        title = "Cambiar vehículo",
                        onDismiss = { dismissed = true },
                    ),
                ) {}
            }
        }

        onNodeWithTag(TestTags.BottomSheet.CLOSE, useUnmergedTree = true).performClick()
        waitForIdle()

        assertTrue(dismissed)
    }

    @Test
    fun clickingActionButtonInvokesOnClickThenOnDismiss() = runComposeUiTest {
        var clicked = false
        var dismissed = false

        setContent {
            MyApplicationTheme {
                GasGuruBottomSheet(
                    model = GasGuruBottomSheetModel(
                        title = "Tema",
                        onDismiss = { dismissed = true },
                        actionButton = GasGuruBottomSheetAction(
                            text = "Guardar",
                            onClick = { clicked = true },
                        ),
                    ),
                ) {}
            }
        }

        onNodeWithText("Guardar").performClick()
        waitForIdle()

        assertTrue(clicked)
        assertTrue(dismissed)
    }

    @Test
    fun doesNotShowActionButtonWhenNull() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                GasGuruBottomSheet(
                    model = GasGuruBottomSheetModel(
                        title = "Cambiar vehículo",
                        onDismiss = {},
                    ),
                ) {
                    Text(text = "Contenido")
                }
            }
        }

        onNodeWithText("Guardar").assertDoesNotExist()
        onNodeWithText("Contenido").assertExists()
    }
}
