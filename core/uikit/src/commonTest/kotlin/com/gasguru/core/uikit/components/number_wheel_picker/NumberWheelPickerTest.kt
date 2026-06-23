package com.gasguru.core.uikit.components.number_wheel_picker

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.theme.MyApplicationTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class NumberWheelPickerTest {

    private val defaultModifier = Modifier
        .height(156.dp)
        .fillMaxWidth()

    @Test
    fun initialValueIsDisplayed() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                NumberWheelPicker(
                    model = NumberWheelPickerModel(
                        min = 40,
                        max = 60,
                        initialValue = 50,
                        onValueChanged = {},
                    ),
                    modifier = defaultModifier,
                )
            }
        }

        onNodeWithText("50").assertIsDisplayed()
    }

    @Test
    fun onValueChangedCalledWithInitialValue() = runComposeUiTest {
        val capturedValues = mutableListOf<Int>()

        setContent {
            MyApplicationTheme {
                NumberWheelPicker(
                    model = NumberWheelPickerModel(
                        min = 40,
                        max = 60,
                        initialValue = 50,
                        onValueChanged = { capturedValues.add(it) },
                    ),
                    modifier = defaultModifier,
                )
            }
        }

        assertTrue(capturedValues.isNotEmpty())
        assertEquals(50, capturedValues.last())
    }

    @Test
    fun minBoundaryValueIsDisplayed() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                NumberWheelPicker(
                    model = NumberWheelPickerModel(
                        min = 40,
                        max = 60,
                        initialValue = 40,
                        onValueChanged = {},
                    ),
                    modifier = defaultModifier,
                )
            }
        }

        onNodeWithText("40").assertIsDisplayed()
    }

    @Test
    fun maxBoundaryValueIsDisplayed() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                NumberWheelPicker(
                    model = NumberWheelPickerModel(
                        min = 40,
                        max = 60,
                        initialValue = 60,
                        onValueChanged = {},
                    ),
                    modifier = defaultModifier,
                )
            }
        }

        onNodeWithText("60").assertIsDisplayed()
    }
}
