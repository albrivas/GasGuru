package com.gasguru.core.uikit.components.route_navigation_card

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.gasguru.core.uikit.theme.MyApplicationTheme
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class RouteNavigationCardTest {

    @Test
    fun showsRouteDataWhenLoaded() = runComposeUiTest {
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
    fun showsLoadingMessageWhenCalculating() = runComposeUiTest {
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
    fun callsOnCloseWhenButtonClicked() = runComposeUiTest {
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
