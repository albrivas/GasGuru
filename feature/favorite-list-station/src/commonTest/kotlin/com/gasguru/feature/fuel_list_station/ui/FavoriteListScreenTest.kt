package com.gasguru.feature.fuel_list_station.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
import com.gasguru.feature.favorite_list_station.ui.FavoriteStationEvent
import com.gasguru.feature.favorite_list_station.ui.FavoriteStationListUiState
import com.gasguru.feature.favorite_list_station.ui.SelectedTabUiState
import org.jetbrains.compose.resources.stringResource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
    fun swipeRevealsDeleteActionWithoutRemovingTheStation() = runComposeUiTest {
        val events = mutableListOf<FavoriteStationEvent>()
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
                event = { favoriteEvent -> events.add(favoriteEvent) },
            )
        }

        onNodeWithTag(testTag = "home_station_item_0").performTouchInput { swipeLeft() }
        waitForIdle()

        onNodeWithTag(testTag = "home_station_item_0").assertIsDisplayed()
        onNodeWithTag(testTag = "swipe_action_delete").assertIsDisplayed()
        assertTrue(events.none { favoriteEvent -> favoriteEvent is FavoriteStationEvent.RemoveFavoriteStation })
    }

    @Test
    fun tapOnRevealedDeleteActionRemovesTheStation() = runComposeUiTest {
        val events = mutableListOf<FavoriteStationEvent>()
        setContent {
            FavoriteListStationScreen(
                uiState = FavoriteStationListUiState.Favorites(
                    favoriteStations = listOf(previewFuelStationDomain(idServiceStation = 7).toUiModel()),
                    userSelectedFuelType = FuelType.GASOLINE_95,
                ),
                tabState = SelectedTabUiState(),
                navigateToDetail = {},
                event = { favoriteEvent -> events.add(favoriteEvent) },
            )
        }

        onNodeWithTag(testTag = "home_station_item_0").performTouchInput { swipeLeft() }
        onNodeWithTag(testTag = "swipe_action_delete").performClick()

        assertEquals(listOf<FavoriteStationEvent>(FavoriteStationEvent.RemoveFavoriteStation(idStation = 7)), events)
    }
}
