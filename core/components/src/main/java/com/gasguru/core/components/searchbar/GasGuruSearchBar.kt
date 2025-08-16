package com.gasguru.core.components.searchbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.rounded.Close
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
import com.gasguru.core.components.searchbar.state.RecentSearchQueriesUiState
import com.gasguru.core.components.searchbar.state.SearchResultUiState
import com.gasguru.core.components.searchbar.state.rememberGasGuruSearchBarState
import com.gasguru.core.model.data.RecentSearchQuery
import com.gasguru.core.model.data.SearchPlace
import com.gasguru.core.uikit.components.placeitem.PlaceItem
import com.gasguru.core.uikit.components.placeitem.PlaceItemModel
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
            containerColor = GasGuruTheme.colors.neutralWhite,
            dividerColor = GasGuruTheme.colors.neutralWhite
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
                        if (recentState.recentQueries.isEmpty()) {
                            EmptyRecentSearchesBody()
                        } else {
                            RecentSearchQueriesBody(
                                recentSearchQueries = recentState.recentQueries,
                                onRecentSearchClicked = { recentQuery ->
                                    val searchPlace =
                                        SearchPlace(name = recentQuery.name, id = recentQuery.id)
                                    onEvent(GasGuruSearchBarEvent.UpdateSearchQuery(recentQuery.name))
                                    model.onRecentSearchClicked(searchPlace)
                                    state.deactivate()
                                },
                                onClearRecentSearches = {
                                    onEvent(GasGuruSearchBarEvent.ClearRecentSearches)
                                }
                            )
                        }
                    }

                    is RecentSearchQueriesUiState.Loading -> {
                        // Show loading or empty state
                    }
                }
            }

            SearchResultUiState.EmptySearchResult -> {
                EmptyResultBody()
            }

            is SearchResultUiState.Success -> {
                SearchResultBody(
                    places = searchResultUiState.places,
                    onPlaceSelected = { place ->
                        state.deactivate()
                        model.onActiveChange(false)
                        onEvent(GasGuruSearchBarEvent.InsertRecentSearch(place))
                        onEvent(GasGuruSearchBarEvent.UpdateSearchQuery(place.name))
                        model.onPlaceSelected(place)
                    }
                )
            }
        }
    }
}

@Composable
private fun SearchResultBody(
    places: List<SearchPlace>,
    onPlaceSelected: (SearchPlace) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = GasGuruTheme.colors.neutralWhite)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.label_suggestion),
            modifier = Modifier.align(Alignment.Start),
            style = GasGuruTheme.typography.baseBold,
            color = GasGuruTheme.colors.textMain
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            items(places) { place ->
                PlaceItem(
                    model = PlaceItemModel(
                        id = place.id,
                        icon = Icons.Outlined.LocationOn,
                        name = place.name,
                        onClickItem = { onPlaceSelected(place) }
                    ),
                    isLastItem = false
                )
            }
        }
    }
}

@Composable
private fun EmptyResultBody() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = GasGuruTheme.colors.neutralWhite)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.label_suggestion),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp),
            style = GasGuruTheme.typography.h6,
            color = GasGuruTheme.colors.textMain
        )
        Text(
            text = stringResource(id = R.string.label_empty_suggestions),
            modifier = Modifier.align(Alignment.Start),
            style = GasGuruTheme.typography.baseRegular,
            color = GasGuruTheme.colors.textSubtle
        )
    }
}

@Composable
private fun EmptyRecentSearchesBody() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = GasGuruTheme.colors.neutralWhite)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.label_recent),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp),
            style = GasGuruTheme.typography.h6,
            color = GasGuruTheme.colors.textMain
        )
        Text(
            text = stringResource(id = R.string.label_empty_recents),
            modifier = Modifier.align(Alignment.Start),
            style = GasGuruTheme.typography.baseRegular,
            color = GasGuruTheme.colors.textSubtle
        )
    }
}

@Composable
private fun RecentSearchQueriesBody(
    recentSearchQueries: List<RecentSearchQuery>,
    onRecentSearchClicked: (RecentSearchQuery) -> Unit = {},
    onClearRecentSearches: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = GasGuruTheme.colors.neutralWhite)
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = stringResource(id = R.string.label_recent),
                style = GasGuruTheme.typography.baseBold,
                color = GasGuruTheme.colors.textMain
            )
            if (recentSearchQueries.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Clear recent searches",
                    tint = GasGuruTheme.colors.neutralBlack,
                    modifier = Modifier
                        .align(Alignment.Top)
                        .clickable { onClearRecentSearches() }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(recentSearchQueries) { recentSearchQuery ->
                PlaceItem(
                    model = PlaceItemModel(
                        id = recentSearchQuery.id,
                        icon = Icons.Outlined.AccessTime,
                        name = recentSearchQuery.name,
                        onClickItem = { onRecentSearchClicked(recentSearchQuery) }
                    ),
                    isLastItem = false
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
private fun RecentSearchQueriesBodyPreview() {
    MyApplicationTheme {
        RecentSearchQueriesBody(
            recentSearchQueries = listOf(
                RecentSearchQuery("Barcelona", "1"),
                RecentSearchQuery("Madrid", "2"),
                RecentSearchQuery("Valencia", "3"),
            )
        )
    }
}

@Composable
@ThemePreviews
private fun EmptyRecentSearchQueriesBodyPreview() {
    MyApplicationTheme {
        EmptyRecentSearchesBody()
    }
}

@Composable
@ThemePreviews
private fun SearchResultBodyPreview() {
    MyApplicationTheme {
        SearchResultBody(
            places = listOf(
                SearchPlace("Barcelona", "1"),
                SearchPlace("Madrid", "2"),
                SearchPlace("Valencia", "3"),
            ),
            onPlaceSelected = {}
        )
    }
}

@Composable
@ThemePreviews
private fun EmptyResultBodyPreview() {
    MyApplicationTheme {
        EmptyResultBody()
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
