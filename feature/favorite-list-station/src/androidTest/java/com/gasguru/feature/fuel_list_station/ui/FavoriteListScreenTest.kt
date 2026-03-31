package com.gasguru.feature.fuel_list_station.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.previewFuelStationDomain
import com.gasguru.core.testing.BaseTest
import com.gasguru.core.ui.mapper.toUiModel
import com.gasguru.feature.favorite_list_station.R
import com.gasguru.feature.favorite_list_station.ui.FavoriteListStationScreen
import com.gasguru.feature.favorite_list_station.ui.FavoriteStationListUiState
import com.gasguru.feature.favorite_list_station.ui.SelectedTabUiState
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class FavoriteListScreenTest : BaseTest() {

    @Test
    @DisplayName("Empty favorites stations")
    fun emptyFavoriteStations() = extension.use {
        setContent {
            FavoriteListStationScreen(
                uiState = FavoriteStationListUiState.EmptyFavorites,
                tabState = SelectedTabUiState(),
                navigateToDetail = {},
                event = {}
            )
        }

        onNodeWithText(text = getStringResource(R.string.empty_favorites_title)).assertIsDisplayed()
        onNodeWithText(text = getStringResource(R.string.empty_favorites_subtitle)).assertIsDisplayed()
    }

    @Test
    @DisplayName("Show favorite stations")
    fun favoriteStations() = extension.use {
        setContent {
            FavoriteListStationScreen(
                uiState = FavoriteStationListUiState.Favorites(
                    favoriteStations = listOf(
                        previewFuelStationDomain().toUiModel()
                    ), userSelectedFuelType = FuelType.GASOLINE_95
                ),
                tabState = SelectedTabUiState(),
                navigateToDetail = {},
                event = {}
            )
        }

        onNodeWithTag(testTag = "item 0").isDisplayed()
    }

    @Test
    @DisplayName("Remove item list making swipe")
    fun checkSwipe() = extension.use {
        setContent {
            FavoriteListStationScreen(
                uiState = FavoriteStationListUiState.Favorites(
                    favoriteStations = listOf(
                        previewFuelStationDomain(idServiceStation = 0),
                        previewFuelStationDomain(idServiceStation = 1)
                    ).map { it.toUiModel() }, userSelectedFuelType = FuelType.GASOLINE_95
                ),
                tabState = SelectedTabUiState(),
                navigateToDetail = {},
                event = {}
            )
        }

        onNodeWithTag(testTag = "item 0").isDisplayed()
        onNodeWithTag(testTag = "item 0").performTouchInput { swipeLeft() }
        onNodeWithTag(testTag = "item 0").isNotDisplayed()
    }
}