package com.gasguru.core.uikit.components.fuelItem

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import com.gasguru.core.uikit.generated.resources.Res
import com.gasguru.core.uikit.generated.resources.ic_logo_repsol
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class FuelStationItemTest {

    @Test
    fun checkAllItemsAreVisible() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                FuelStationItem(
                    model = FuelStationItemModel(
                        idServiceStation = 0,
                        icon = Res.drawable.ic_logo_repsol,
                        name = "Repsol",
                        distance = "1.2 km",
                        price = "Without fuel diesel premium plus ultra clean",
                        index = 1,
                        categoryColor = GasGuruTheme.colors.primary500,
                        onItemClick = {},
                    ),
                    isLastItem = true,
                )
            }
        }

        val nameTag = onNodeWithTag("station-name", useUnmergedTree = true)
        val distanceTag = onNodeWithTag("station-distance", useUnmergedTree = true)
        val priceTag = onNodeWithTag("station-price", useUnmergedTree = true)

        nameTag.assertIsDisplayed()
        distanceTag.assertIsDisplayed()
        priceTag.assertIsDisplayed()

        val nameBounds = nameTag.fetchSemanticsNode().boundsInRoot
        val distanceBounds = distanceTag.fetchSemanticsNode().boundsInRoot
        val priceBounds = priceTag.fetchSemanticsNode().boundsInRoot

        assertTrue(priceBounds.left >= nameBounds.right)
        assertTrue(priceBounds.left >= distanceBounds.right)
    }
}
