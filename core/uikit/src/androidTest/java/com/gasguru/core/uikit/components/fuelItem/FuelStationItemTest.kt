package com.gasguru.core.uikit.components.fuelItem

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import com.gasguru.core.testing.BaseTest
import com.gasguru.core.uikit.R
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.Primary500
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class FuelStationItemTest : BaseTest() {

    @Test
    @DisplayName("Check that name, distance and price are visible and don´t overlap")
    fun checkAllItemsAreVisible() = extension.use {
        setContent {
            MyApplicationTheme {
                FuelStationItem(
                    model = FuelStationItemModel(
                        idServiceStation = 0,
                        icon = R.drawable.ic_logo_repsol,
                        name = "Repsol",
                        distance = "1.2 km",
                        price = "Without fuel diesel premium plus ultra clean",
                        index = 1,
                        categoryColor = Primary500,
                        onItemClick = {}
                    )
                )
            }
        }

        val nameTag = onNodeWithTag("station-name", useUnmergedTree = true)
        val distanceTag= onNodeWithTag("station-distance", useUnmergedTree = true)
        val priceTag = onNodeWithTag("station-price", useUnmergedTree = true)

        nameTag.assertIsDisplayed()
        distanceTag.assertIsDisplayed()
        priceTag.assertIsDisplayed()

        val nameBounds = nameTag.fetchSemanticsNode().boundsInRoot
        val distanceBounds = distanceTag.fetchSemanticsNode().boundsInRoot
        val priceBounds = priceTag.fetchSemanticsNode().boundsInRoot

        assert(priceBounds.left >= nameBounds.right)
        assert(priceBounds.left >= distanceBounds.right)
    }
}