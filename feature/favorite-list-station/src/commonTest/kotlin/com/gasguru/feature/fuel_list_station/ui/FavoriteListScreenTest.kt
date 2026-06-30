package com.gasguru.feature.fuel_list_station.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.swipeLeft
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.previewFuelStationDomain
import com.gasguru.core.ui.mapper.toUiModel
import com.gasguru.feature.favorite_list_station.generated.resources.Res
import com.gasguru.feature.favorite_list_station.generated.resources.empty_favorites_subtitle
import com.gasguru.feature.favorite_list_station.generated.resources.empty_favorites_title
import com.gasguru.feature.favorite_list_station.ui.FavoriteListStationScreen
import com.gasguru.feature.favorite_list_station.ui.FavoriteStationListUiState
import com.gasguru.feature.favorite_list_station.ui.SelectedTabUiState
import org.jetbrains.compose.resources.stringResource
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class FavoriteListScreenTest {

    @Test
    fun emptyFavoriteStations() = runComposeUiTest {
        var emptyTitle = ""
        var emptySubtitle = ""

        setContent {
            emptyTitle = stringResource(Res.string.empty_favorites_title)
            emptySubtitle = stringResource(Res.string.empty_favorites_subtitle)
            FavoriteListStationScreen(
                uiState = FavoriteStationListUiState.EmptyFavorites,
                tabState = SelectedTabUiState(),
                navigateToDetail = {},
                event = {},
            )
        }

        onNodeWithText(emptyTitle).assertIsDisplayed()
        onNodeWithText(emptySubtitle).assertIsDisplayed()
    }

    @Test
    fun favoriteStations() = runComposeUiTest {
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
    fun checkSwipe() = runComposeUiTest {
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
