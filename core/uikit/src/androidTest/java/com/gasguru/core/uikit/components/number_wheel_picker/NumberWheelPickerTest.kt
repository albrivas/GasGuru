package com.gasguru.core.uikit.components.number_wheel_picker

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.gasguru.core.testing.BaseTest
import com.gasguru.core.uikit.theme.MyApplicationTheme
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class NumberWheelPickerTest : BaseTest() {

    private val defaultModifier = Modifier
        .height(156.dp)
        .fillMaxWidth()

    @Test
    @DisplayName("GIVEN a valid model WHEN rendered THEN initial value text is displayed")
    fun initialValueIsDisplayed() = extension.use {
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
    @DisplayName("GIVEN a valid model WHEN initial composition THEN onValueChanged is called with initial value")
    fun onValueChangedCalledWithInitialValue() = extension.use {
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
    @DisplayName("GIVEN min equal to max WHEN creating model THEN throws IllegalArgumentException")
    fun modelThrowsWhenMinEqualsMax() {
        assertThrows(IllegalArgumentException::class.java) {
            NumberWheelPickerModel(
                min = 40,
                max = 40,
                initialValue = 40,
                onValueChanged = {},
            )
        }
    }

    @Test
    @DisplayName("GIVEN min greater than max WHEN creating model THEN throws IllegalArgumentException")
    fun modelThrowsWhenMinGreaterThanMax() {
        assertThrows(IllegalArgumentException::class.java) {
            NumberWheelPickerModel(
                min = 60,
                max = 40,
                initialValue = 50,
                onValueChanged = {},
            )
        }
    }

    @Test
    @DisplayName("GIVEN initialValue below min WHEN creating model THEN throws IllegalArgumentException")
    fun modelThrowsWhenInitialValueBelowMin() {
        assertThrows(IllegalArgumentException::class.java) {
            NumberWheelPickerModel(
                min = 40,
                max = 60,
                initialValue = 30,
                onValueChanged = {},
            )
        }
    }

    @Test
    @DisplayName("GIVEN initialValue above max WHEN creating model THEN throws IllegalArgumentException")
    fun modelThrowsWhenInitialValueAboveMax() {
        assertThrows(IllegalArgumentException::class.java) {
            NumberWheelPickerModel(
                min = 40,
                max = 60,
                initialValue = 70,
                onValueChanged = {},
            )
        }
    }

    @Test
    @DisplayName("GIVEN min equals initialValue WHEN rendered THEN boundary value is displayed")
    fun minBoundaryValueIsDisplayed() = extension.use {
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
    @DisplayName("GIVEN max equals initialValue WHEN rendered THEN boundary value is displayed")
    fun maxBoundaryValueIsDisplayed() = extension.use {
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
