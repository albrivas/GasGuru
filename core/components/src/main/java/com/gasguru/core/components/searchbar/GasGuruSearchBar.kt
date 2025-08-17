package com.gasguru.core.components.searchbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gasguru.core.components.R
import com.gasguru.core.components.searchbar.state.GasGuruSearchBarEvent
import com.gasguru.core.components.searchbar.state.GasGuruSearchBarState
import com.gasguru.core.components.searchbar.state.SearchResultUiState
import com.gasguru.core.components.searchbar.state.rememberGasGuruSearchBarState
import com.gasguru.core.model.data.RecentSearchQuery
import com.gasguru.core.model.data.SearchPlace
import com.gasguru.core.ui.RecentSearchQueriesUiState
import com.gasguru.core.uikit.components.placeitem.PlaceItemModel
import com.gasguru.core.uikit.components.searchlist.SearchList
import com.gasguru.core.uikit.components.searchlist.SearchListModel
import com.gasguru.core.uikit.components.searchlist.SearchListType
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import com.gasguru.core.uikit.utils.maestroTestTag

@Composable
fun GasGuruSearchBar(
    model: GasGuruSearchBarModel,
    viewModel: GasGuruSearchBarViewModel = hiltViewModel(),
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResultUiState by viewModel.searchResultUiState.collectAsStateWithLifecycle()
    val recentSearchQueriesUiState by viewModel.recentSearchQueriesUiState.collectAsStateWithLifecycle()

    GasGuruSearchBarContent(
        model = model,
        searchQuery = searchQuery,
        searchResultUiState = searchResultUiState,
        recentSearchQueriesUiState = recentSearchQueriesUiState,
        state = rememberGasGuruSearchBarState(alwaysActive = model.alwaysActive),
        onEvent = viewModel::handleEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GasGuruSearchBarContent(
    model: GasGuruSearchBarModel,
    searchQuery: String,
    searchResultUiState: SearchResultUiState,
    recentSearchQueriesUiState: RecentSearchQueriesUiState,
    state: GasGuruSearchBarState,
    onEvent: (GasGuruSearchBarEvent) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current

    SearchBar(
        inputField = {
            TextField(
                textStyle = GasGuruTheme.typography.baseRegular,
                value = searchQuery,
                onValueChange = { onEvent(GasGuruSearchBarEvent.UpdateSearchQuery(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            state.onFocusReceived()
                            model.onActiveChange(state.active)
                        }
                    },
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.hint_search_bar),
                        style = GasGuruTheme.typography.baseRegular,
                        color = GasGuruTheme.colors.textSubtle
                    )
                },
                leadingIcon = {
                    if (state.active) {
                        IconButton(onClick = {
                            if (model.alwaysActive) {
                                model.onBackPressed()
                            } else {
                                state.deactivateWithFocusClear { focusManager.clearFocus() }
                                model.onActiveChange(false)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                tint = GasGuruTheme.colors.neutralBlack,
                                contentDescription = "Icon back"
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Search,
                            tint = GasGuruTheme.colors.neutralBlack,
                            contentDescription = "Icon search"
                        )
                    }
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            onEvent(GasGuruSearchBarEvent.UpdateSearchQuery(""))
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                tint = GasGuruTheme.colors.neutralBlack,
                                contentDescription = "Icon search",
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = GasGuruTheme.colors.textMain,
                    unfocusedTextColor = GasGuruTheme.colors.textMain,
                    cursorColor = GasGuruTheme.colors.primary600
                ),
                singleLine = true
            )
        },
        expanded = state.active,
        onExpandedChange = { newActive ->
            state.onExpandedChange(newActive)
            model.onActiveChange(newActive)
        },
        modifier = model.modifier
            .fillMaxWidth()
            .padding(
                top = state.statusBarPaddingAnimation,
                start = state.paddingAnimation,
                end = state.paddingAnimation
            )
            .onGloballyPositioned { model.onHeight(it.size.height) }
            .maestroTestTag("search_bar"),
        shadowElevation = 2.dp,
        colors = SearchBarDefaults.colors(
            containerColor = GasGuruTheme.colors.neutral100,
            dividerColor = GasGuruTheme.colors.neutral100
        )
    ) {
        when (searchResultUiState) {
            SearchResultUiState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth()) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                    )
                }
            }

            SearchResultUiState.LoadFailed -> {
                Unit
            }

            SearchResultUiState.EmptyQuery -> {
                when (val recentState = recentSearchQueriesUiState) {
                    is RecentSearchQueriesUiState.Success -> {
                        SearchList(
                            model = SearchListModel(
                                type = SearchListType.RECENT,
                                items = recentState.recentQueries.map { recentQuery ->
                                    PlaceItemModel(
                                        id = recentQuery.id,
                                        icon = Icons.Outlined.AccessTime,
                                        name = recentQuery.name,
                                        onClickItem = {
                                            val searchPlace = SearchPlace(name = recentQuery.name, id = recentQuery.id)
                                            onEvent(GasGuruSearchBarEvent.UpdateSearchQuery(recentQuery.name))
                                            model.onRecentSearchClicked(searchPlace)
                                            state.deactivate()
                                            focusManager.clearFocus()
                                        }
                                    )
                                },
                                onClear = {
                                    onEvent(GasGuruSearchBarEvent.ClearRecentSearches)
                                }
                            ),
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    is RecentSearchQueriesUiState.Loading -> {
                        // Show loading or empty state
                    }
                }
            }

            SearchResultUiState.EmptySearchResult -> {
                SearchList(
                    model = SearchListModel(
                        type = SearchListType.SUGGESTIONS,
                        items = emptyList()
                    )
                )
            }

            is SearchResultUiState.Success -> {
                SearchList(
                    model = SearchListModel(
                        type = SearchListType.SUGGESTIONS,
                        items = searchResultUiState.places.map { place ->
                            PlaceItemModel(
                                id = place.id,
                                icon = Icons.Outlined.LocationOn,
                                name = place.name,
                                onClickItem = {
                                    state.deactivate()
                                    model.onActiveChange(false)
                                    focusManager.clearFocus()
                                    onEvent(GasGuruSearchBarEvent.InsertRecentSearch(place))
                                    onEvent(GasGuruSearchBarEvent.UpdateSearchQuery(place.name))
                                    model.onPlaceSelected(place)
                                }
                            )
                        }
                    ),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}


@Composable
@ThemePreviews
private fun GasGuruSearchBarPreview() {
    MyApplicationTheme {
        GasGuruSearchBarContent(
            model = GasGuruSearchBarModel(),
            searchQuery = "",
            searchResultUiState = SearchResultUiState.EmptyQuery,
            recentSearchQueriesUiState = RecentSearchQueriesUiState.Loading,
            state = rememberGasGuruSearchBarState(alwaysActive = false)
        )
    }
}


@Composable
@ThemePreviews
private fun GasGuruSearchBarExpandedPreview() {
    MyApplicationTheme {
        GasGuruSearchBarContent(
            model = GasGuruSearchBarModel(),
            searchQuery = "",
            searchResultUiState = SearchResultUiState.EmptyQuery,
            recentSearchQueriesUiState = RecentSearchQueriesUiState.Success(
                recentQueries = listOf(
                    RecentSearchQuery("Barcelona", "1"),
                    RecentSearchQuery("Madrid", "2"),
                    RecentSearchQuery("Valencia", "3"),
                )
            ),
            state = rememberGasGuruSearchBarState(alwaysActive = true)
        )
    }
}
