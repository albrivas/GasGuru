package com.albrivas.fuelpump.feature.fuel_list_station.ui

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.albrivas.fuelpump.core.model.data.FuelType
import com.albrivas.fuelpump.core.model.data.previewFuelStationDomain
import com.albrivas.fuelpump.core.testing.BaseTest
import com.albrivas.fuelpump.feature.fuel_list_station.R
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import com.albrivas.fuelpump.core.uikit.R as RUikit

class FuelStationListScreenTest : BaseTest() {

    private lateinit var filterChipAllContentDesc: String
    private lateinit var filterChipFavoritesContentDesc: String

    @BeforeEach
    fun setUp() {
        filterChipAllContentDesc = getStringResource(id = RUikit.string.filter_content_desc) + " 0"
        filterChipFavoritesContentDesc =
            getStringResource(id = RUikit.string.filter_content_desc) + " 1"
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
                selectedFilter = 1,
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
        onNodeWithTag("item 0").isDisplayed()
    }
}