package com.albrivas.fuelpump.feature.onboarding_welcome

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.albrivas.fuelpump.core.testing.BaseTest
import com.albrivas.fuelpump.feature.onboarding.R
import com.albrivas.fuelpump.feature.onboarding_welcome.ui.OnboardingWelcomeScreen
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class OnboardingWelcomeTest: BaseTest() {

    @Test
    @DisplayName("User accept the location permission")
    fun testLocationPermissionGranted(): Unit = extension.use {
        setContent {
            OnboardingWelcomeScreen(isPermissionGranted = true)
        }

        onNodeWithTag("button_next").assertIsEnabled()
    }

    @Test
    @DisplayName("User deny the location permission")
    fun testLocationPermissionNotGranted(): Unit = extension.use {
        setContent {
            OnboardingWelcomeScreen(isPermissionGranted = false)
        }

        onNodeWithTag("button_next").assertIsNotEnabled()
    }

    @Test
    @DisplayName("Text displayed components")
    fun checkDisplayedComponents(): Unit = extension.use {
        setContent {
            OnboardingWelcomeScreen(isPermissionGranted = false)
        }

        val title =  testContext.getString(R.string.welcome)
        val descriptionOne = testContext.getString(R.string.welcome_text)
        val descriptionTwo = testContext.getString(R.string.welcome_permission)
        onNodeWithText(title).assertIsDisplayed()
        onNodeWithText(descriptionOne).assertIsDisplayed()
        onNodeWithText(descriptionTwo).assertIsDisplayed()
    }
}
