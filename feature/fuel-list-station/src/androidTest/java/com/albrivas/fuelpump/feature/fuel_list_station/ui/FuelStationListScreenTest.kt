package com.albrivas.fuelpump.feature.fuel_list_station.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.albrivas.fuelpump.core.model.data.FuelType
import com.albrivas.fuelpump.core.model.data.PriceCategory
import com.albrivas.fuelpump.core.model.data.previewFuelStationDomain
import com.albrivas.fuelpump.core.testing.BaseTest
import com.albrivas.fuelpump.core.ui.BackgroundColorKey
import com.albrivas.fuelpump.core.uikit.theme.secondaryLight
import com.albrivas.fuelpump.feature.fuel_list_station.R
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import com.albrivas.fuelpump.core.uikit.R as RUikit

class FuelStationListScreenTest : BaseTest() {

    private lateinit var filterChipAllContentDesc: String
    private lateinit var filterChipFavoritesContentDesc: String
    private lateinit var fuelItemContentDesc: String
    private lateinit var fuelItemPriceColorContentDesc: String

    @BeforeEach
    fun setUp() {
        filterChipAllContentDesc = getStringResource(id = RUikit.string.filter_content_desc) + " 0"
        filterChipFavoritesContentDesc =
            getStringResource(id = RUikit.string.filter_content_desc) + " 1"
        fuelItemContentDesc = getStringResource(id = R.string.content_description_fuel_item, 0)
        fuelItemPriceColorContentDesc =
            getStringResource(id = R.string.content_description_fuel_item_price_box)
    }

    @Test
    @DisplayName("The filter chip called All is selected")
    fun selectedFirstFilterChip() = extension.use {
        setContent {
            FuelStationListScreen(
                uiState = FuelStationListUiState.Loading,
                selectedFilter = 0,
                navigateToDetail = {},
                checkLocationEnabled = {},
                updateFilter = {}
            )
        }

        onNodeWithContentDescription(filterChipAllContentDesc).assertIsSelected()
        onNodeWithContentDescription(filterChipAllContentDesc).assertHasClickAction()
        onNodeWithContentDescription(filterChipFavoritesContentDesc).assertIsNotSelected()
        onNodeWithContentDescription(filterChipFavoritesContentDesc).assertHasClickAction()
    }

    @Test
    @DisplayName("The filter chip called Favorite is selected")
    fun selectedSecondFilterChip() = extension.use {
        setContent {
            FuelStationListScreen(
                uiState = FuelStationListUiState.Loading,
                selectedFilter = 0,
                navigateToDetail = {},
                checkLocationEnabled = {},
                updateFilter = {}
            )
        }

        onNodeWithContentDescription(filterChipFavoritesContentDesc).performClick()
        onNodeWithContentDescription(filterChipAllContentDesc).assertIsNotSelected()
        onNodeWithContentDescription(filterChipFavoritesContentDesc).assertIsSelected()
    }

    @Test
    @DisplayName("Empty favorites stations")
    fun emptyFavoriteStations() = extension.use {
        setContent {
            FuelStationListScreen(
                uiState = FuelStationListUiState.EmptyFavorites,
                selectedFilter = 0,
                navigateToDetail = {},
                checkLocationEnabled = {},
                updateFilter = {}
            )
        }

        onNodeWithContentDescription(filterChipFavoritesContentDesc).performClick()
        onNodeWithText(getStringResource(R.string.empty_favorites)).assertIsDisplayed()
    }

    @Test
    @DisplayName("Show favorite stations")
    fun favoriteStations() = extension.use {
        setContent {
            FuelStationListScreen(
                uiState = FuelStationListUiState.Favorites(
                    favoriteStations = listOf(
                        previewFuelStationDomain()
                    ), userSelectedFuelType = FuelType.GASOLINE_95
                ),
                selectedFilter = 0,
                navigateToDetail = {},
                checkLocationEnabled = {},
                updateFilter = {}
            )
        }

        onNodeWithContentDescription(filterChipFavoritesContentDesc).performClick()
        onNodeWithContentDescription(fuelItemContentDesc).assertIsDisplayed()
        onNodeWithContentDescription(fuelItemContentDesc).assertHasClickAction()
    }

    @Test
    @DisplayName("Show fuel station list by location")
    fun fuelStationListByLocation() = extension.use {
        setContent {
            FuelStationListScreen(
                uiState = FuelStationListUiState.Success(
                    fuelStations = listOf(
                        previewFuelStationDomain()
                    ), userSelectedFuelType = FuelType.GASOLINE_95
                ),
                selectedFilter = 0,
                navigateToDetail = {},
                checkLocationEnabled = {},
                updateFilter = {}
            )
        }

        onNodeWithContentDescription(filterChipAllContentDesc).performClick()
        onNodeWithContentDescription(fuelItemContentDesc).assertIsDisplayed()
        onNodeWithContentDescription(fuelItemContentDesc).performClick()
    }

    @Test
    @DisplayName("Background color in favorite item")
    fun backgroundColorFavoriteItem() = extension.use {
        setContent {
            FuelStationListScreen(
                uiState = FuelStationListUiState.Favorites(
                    favoriteStations = listOf(
                        previewFuelStationDomain().copy(priceCategory = PriceCategory.NONE)
                    ), userSelectedFuelType = FuelType.GASOLINE_95
                ),
                selectedFilter = 0,
                navigateToDetail = {},
                checkLocationEnabled = {},
                updateFilter = {}
            )
        }

        onNodeWithContentDescription(filterChipFavoritesContentDesc).performClick()
        onNodeWithContentDescription(fuelItemContentDesc, useUnmergedTree = true)
            .onChildren()
            .filterToOne(hasContentDescription(fuelItemPriceColorContentDesc))
            .assert(
                hasColor(
                    secondaryLight
                )
            )
    }

    private fun hasColor(expectedColor: Color) =
        SemanticsMatcher.expectValue(BackgroundColorKey, expectedColor)
}