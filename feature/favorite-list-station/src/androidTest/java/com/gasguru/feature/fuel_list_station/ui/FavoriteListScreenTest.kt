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
import com.gasguru.feature.favorite_list_station.generated.resources.Res
import com.gasguru.feature.favorite_list_station.generated.resources.empty_favorites_subtitle
import com.gasguru.feature.favorite_list_station.generated.resources.empty_favorites_title
import com.gasguru.feature.favorite_list_station.ui.FavoriteListStationScreen
import com.gasguru.feature.favorite_list_station.ui.FavoriteStationListUiState
import com.gasguru.feature.favorite_list_station.ui.SelectedTabUiState
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("FavoriteListScreenTest")
class FavoriteListScreenTest : BaseTest() {

    @Test
    @DisplayName("Empty favorites stations")
    fun emptyFavoriteStations() = extension.use {
        setContent {
            FavoriteListStationScreen(
                uiState = FavoriteStationListUiState.EmptyFavorites,
                tabState = SelectedTabUiState(),
                navigateToDetail = {},
                event = {},
            )
        }

        onNodeWithText(text = getCmpString(resource = Res.string.empty_favorites_title)).assertIsDisplayed()
        onNodeWithText(text = getCmpString(resource = Res.string.empty_favorites_subtitle)).assertIsDisplayed()
    }

    @Test
    @DisplayName("Show favorite stations")
    fun favoriteStations() = extension.use {
        setContent {
            FavoriteListStationScreen(
                uiState = FavoriteStationListUiState.Favorites(
                    favoriteStations = listOf(previewFuelStationDomain().toUiModel()),
                    userSelectedFuelType = FuelType.GASOLINE_95,
                ),
                tabState = SelectedTabUiState(),
                navigateToDetail = {},
                event = {},
            )
        }

        onNodeWithTag(testTag = "home_station_item_0").isDisplayed()
    }

    @Test
    @DisplayName("Remove item list making swipe")
    fun checkSwipe() = extension.use {
        setContent {
            FavoriteListStationScreen(
                uiState = FavoriteStationListUiState.Favorites(
                    favoriteStations = listOf(
                        previewFuelStationDomain(idServiceStation = 0),
                        previewFuelStationDomain(idServiceStation = 1),
                    ).map { it.toUiModel() },
                    userSelectedFuelType = FuelType.GASOLINE_95,
                ),
                tabState = SelectedTabUiState(),
                navigateToDetail = {},
                event = {},
            )
        }

        onNodeWithTag(testTag = "home_station_item_0").isDisplayed()
        onNodeWithTag(testTag = "home_station_item_0").performTouchInput { swipeLeft() }
        onNodeWithTag(testTag = "home_station_item_0").isNotDisplayed()
    }
}