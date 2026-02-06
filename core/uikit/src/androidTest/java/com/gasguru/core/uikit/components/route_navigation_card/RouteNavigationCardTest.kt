package com.gasguru.core.uikit.components.route_navigation_card

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.gasguru.core.testing.BaseTest
import com.gasguru.core.uikit.theme.MyApplicationTheme
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class RouteNavigationCardTest : BaseTest() {

    @Test
    @DisplayName("GIVEN route with data WHEN rendered THEN shows destination, distance, duration and close button")
    fun showsRouteDataWhenLoaded() = extension.use {
        setContent {
            MyApplicationTheme {
                RouteNavigationCard(
                    model = RouteNavigationCardModel(
                        destination = "Calle Gran Vía, 28, Madrid",
                        stationCountText = "5 stations",
                        distance = "12,5 km",
                        duration = "25 min",
                        onClose = {},
                    ),
                )
            }
        }

        onNodeWithTag("route-destination", useUnmergedTree = true).assertIsDisplayed()
        onNodeWithTag("route-info", useUnmergedTree = true).assertIsDisplayed()
        onNodeWithTag("route-close-button", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    @DisplayName("GIVEN route loading WHEN rendered THEN shows loading message")
    fun showsLoadingMessageWhenCalculating() = extension.use {
        setContent {
            MyApplicationTheme {
                RouteNavigationCard(
                    model = RouteNavigationCardModel(
                        destination = "Calle Gran Vía, 28, Madrid",
                        stationCountText = "0 stations",
                        distance = null,
                        duration = null,
                        onClose = {},
                    ),
                )
            }
        }

        onNodeWithTag("route-destination", useUnmergedTree = true).assertIsDisplayed()
        onNodeWithTag("route-info", useUnmergedTree = true).assertIsDisplayed()
        onNodeWithTag("route-loading", useUnmergedTree = true).assertIsDisplayed()
        onNodeWithTag("route-close-button", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    @DisplayName("GIVEN close button WHEN clicked THEN calls onClose callback")
    fun callsOnCloseWhenButtonClicked() = extension.use {
        var closeClicked = false

        setContent {
            MyApplicationTheme {
                RouteNavigationCard(
                    model = RouteNavigationCardModel(
                        destination = "Calle Gran Vía, 28, Madrid",
                        stationCountText = "5 stations",
                        distance = "12,5 km",
                        duration = "25 min",
                        onClose = { closeClicked = true },
                    ),
                )
            }
        }

        onNodeWithTag("route-close-button", useUnmergedTree = true).performClick()

        assertTrue(closeClicked)
    }
}
