package com.gasguru.core.components.searchbar

import androidx.compose.runtime.remember
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import com.gasguru.core.components.searchbar.state.GasGuruSearchBarState
import com.gasguru.core.components.searchbar.state.SearchResultUiState
import com.gasguru.core.model.data.RecentSearchQuery
import com.gasguru.core.model.data.SearchPlace
import com.gasguru.core.ui.RecentSearchQueriesUiState
import com.gasguru.core.uikit.theme.MyApplicationTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class GasGuruSearchBarContentTest {

    @Test
    fun searchBarIsDisplayed() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                GasGuruSearchBarContent(
                    model = GasGuruSearchBarModel(),
                    searchQuery = "",
                    searchResultUiState = SearchResultUiState.EmptyQuery,
                    recentSearchQueriesUiState = RecentSearchQueriesUiState.Loading,
                    state = GasGuruSearchBarState(),
                )
            }
        }

        onNodeWithTag("search_bar").assertIsDisplayed()
    }

    @Test
    fun searchIconVisibleWhenInactive() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                GasGuruSearchBarContent(
                    model = GasGuruSearchBarModel(),
                    searchQuery = "",
                    searchResultUiState = SearchResultUiState.EmptyQuery,
                    recentSearchQueriesUiState = RecentSearchQueriesUiState.Loading,
                    state = GasGuruSearchBarState(),
                )
            }
        }

        onNodeWithContentDescription("Icon search").assertIsDisplayed()
    }

    @Test
    fun backIconVisibleWhenActive() = runComposeUiTest {
        setContent {
            MyApplicationTheme {
                val state = remember {
                    GasGuruSearchBarState().also { it.onExpandedChange(newActive = true) }
                }
                GasGuruSearchBarContent(
                    model = GasGuruSearchBarModel(),
                    searchQuery = "",
                    searchResultUiState = SearchResultUiState.EmptyQuery,
                    recentSearchQueriesUiState = RecentSearchQueriesUiState.Loading,
                    state = state,
                )
            }
        }

        onNodeWithContentDescription("Icon back").assertIsDisplayed()
    }

    @Test
    fun placeNamesVisibleInSuccessState() = runComposeUiTest {
        val places = listOf(
            SearchPlace(name = "Madrid", id = "1"),
            SearchPlace(name = "Barcelona", id = "2"),
        )

        setContent {
            MyApplicationTheme {
                val state = remember {
                    GasGuruSearchBarState().also { it.onExpandedChange(newActive = true) }
                }
                GasGuruSearchBarContent(
                    model = GasGuruSearchBarModel(),
                    searchQuery = "Mad",
                    searchResultUiState = SearchResultUiState.Success(places = places),
                    recentSearchQueriesUiState = RecentSearchQueriesUiState.Loading,
                    state = state,
                )
            }
        }

        onNodeWithText("Madrid").assertIsDisplayed()
        onNodeWithText("Barcelona").assertIsDisplayed()
    }

    @Test
    fun recentSearchesVisibleInEmptyQueryState() = runComposeUiTest {
        val recentQueries = listOf(
            RecentSearchQuery(name = "Valencia", id = "3"),
            RecentSearchQuery(name = "Sevilla", id = "4"),
        )

        setContent {
            MyApplicationTheme {
                val state = remember {
                    GasGuruSearchBarState().also { it.onExpandedChange(newActive = true) }
                }
                GasGuruSearchBarContent(
                    model = GasGuruSearchBarModel(),
                    searchQuery = "",
                    searchResultUiState = SearchResultUiState.EmptyQuery,
                    recentSearchQueriesUiState = RecentSearchQueriesUiState.Success(
                        recentQueries = recentQueries,
                    ),
                    state = state,
                )
            }
        }

        onNodeWithText("Valencia").assertIsDisplayed()
        onNodeWithText("Sevilla").assertIsDisplayed()
    }

    @Test
    fun alwaysActiveModelRequestsBackOnBackPress() = runComposeUiTest {
        var backPressedCount = 0
        val model = GasGuruSearchBarModel(
            alwaysActive = true,
            onBackPressed = { backPressedCount++ },
        )

        setContent {
            MyApplicationTheme {
                val state = remember {
                    GasGuruSearchBarState().also { it.onExpandedChange(newActive = true) }
                }
                GasGuruSearchBarContent(
                    model = model,
                    searchQuery = "",
                    searchResultUiState = SearchResultUiState.EmptyQuery,
                    recentSearchQueriesUiState = RecentSearchQueriesUiState.Loading,
                    state = state,
                )
            }
        }

        onNodeWithTag("search_bar").assertIsDisplayed()
    }
}
