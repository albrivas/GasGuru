package com.gasguru.feature.route_planner.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gasguru.core.model.data.RecentSearchQuery
import com.gasguru.core.ui.RecentSearchQueriesUiState
import com.gasguru.core.uikit.components.GasGuruButton
import com.gasguru.core.uikit.components.divider.DividerLength
import com.gasguru.core.uikit.components.divider.DividerThickness
import com.gasguru.core.uikit.components.divider.GasGuruDivider
import com.gasguru.core.uikit.components.divider.GasGuruDividerModel
import com.gasguru.core.uikit.components.placeitem.PlaceItemModel
import com.gasguru.core.uikit.components.searchlist.SearchList
import com.gasguru.core.uikit.components.searchlist.SearchListModel
import com.gasguru.core.uikit.components.searchlist.SearchListType
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import com.gasguru.feature.route_planner.R
import com.gasguru.navigation.models.PlaceArgs
import com.gasguru.navigation.models.RoutePlanArgs

@Composable
fun RoutePlannerScreenRoute(
    selectedPlaceId: PlaceArgs? = null,
    onBack: () -> Unit = {},
    navigateToSearch: () -> Unit = {},
    popBackToMapScreen: (RoutePlanArgs) -> Unit = {},
    viewModel: RoutePlannerViewModel = hiltViewModel(),
) {
    val recents by viewModel.recentSearchQueriesUiState.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isRouteEnabled by viewModel.isRouteEnabled.collectAsStateWithLifecycle()

    RoutePlannerScreen(
        uiState = state,
        selectedPlace = selectedPlaceId,
        isRouteEnabled = isRouteEnabled,
        recentPlacesState = recents,
        onBack = onBack,
        navigateToSearch = navigateToSearch,
        onEvent = viewModel::handleEvent,
        onStartRoute = {
            popBackToMapScreen(
                RoutePlanArgs(
                    originId = state.startQuery.id.takeIf { it.isNotEmpty() },
                    destinationId = state.endQuery.id.takeIf { it.isNotEmpty() }
                )
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RoutePlannerScreen(
    uiState: RoutePlannerUiState,
    selectedPlace: PlaceArgs?,
    isRouteEnabled: Boolean,
    recentPlacesState: RecentSearchQueriesUiState,
    onBack: () -> Unit = {},
    navigateToSearch: () -> Unit = {},
    onEvent: (RoutePlannerUiEvent) -> Unit = {},
    onStartRoute: () -> Unit = {},
) {
    LaunchedEffect(selectedPlace) {
        if (selectedPlace != null) {
            onEvent(
                RoutePlannerUiEvent.SelectPlace(
                    placeId = selectedPlace.id,
                    placeName = selectedPlace.name
                )
            )
        }
    }

    Scaffold(
        containerColor = GasGuruTheme.colors.neutral100,
        contentColor = GasGuruTheme.colors.neutral100,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GasGuruTheme.colors.neutral100,
                ),
                title = {
                    Text(
                        text = stringResource(R.string.route_planner_title),
                        style = GasGuruTheme.typography.baseBold,
                        color = GasGuruTheme.colors.textMain
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            tint = GasGuruTheme.colors.neutralBlack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                GasGuruButton(
                    onClick = onStartRoute,
                    enabled = isRouteEnabled,
                    text = stringResource(id = R.string.start_route),
                    modifier = Modifier
                        .fillMaxWidth()
                        .systemBarsPadding()
                )
            }
        },
        modifier = Modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxSize()
        ) {
            RoutePickerCard(
                origin = if (uiState.startQuery.isCurrentLocation) {
                    stringResource(id = R.string.your_location)
                } else {
                    uiState.startQuery.name
                },
                destination = if (uiState.endQuery.isCurrentLocation) {
                    stringResource(id = R.string.your_location)
                } else {
                    uiState.endQuery.name
                },
                onPickOrigin = {
                    navigateToSearch()
                    onEvent(RoutePlannerUiEvent.ChangeCurrentInput(input = InputField.START))
                },
                onPickDestination = {
                    navigateToSearch()
                    onEvent(RoutePlannerUiEvent.ChangeCurrentInput(input = InputField.END))
                },
                onClearOrigin = {
                    onEvent(RoutePlannerUiEvent.ClearStartDestinationField)
                },
                onClearDestination = {
                    onEvent(RoutePlannerUiEvent.ClearEndDestinationField)
                },
                onSwap = { onEvent(RoutePlannerUiEvent.ChangeDestinations) }
            )
            LocationContent(onClick = {
                onEvent(RoutePlannerUiEvent.SelectCurrentLocation)
            })
            when (recentPlacesState) {
                RecentSearchQueriesUiState.Loading -> {
                    Unit
                }

                is RecentSearchQueriesUiState.Success -> {
                    SearchList(
                        model = SearchListModel(
                            type = SearchListType.RECENT,
                            items = recentPlacesState.recentQueries.map { recentQuery ->
                                PlaceItemModel(
                                    id = recentQuery.id,
                                    icon = Icons.Outlined.AccessTime,
                                    name = recentQuery.name,
                                    onClickItem = {
                                        onEvent(
                                            RoutePlannerUiEvent.SelectRecentPlace(
                                                placeId = recentQuery.id,
                                                placeName = recentQuery.name
                                            )
                                        )
                                    }
                                )
                            },
                            onClear = {
                                onEvent(RoutePlannerUiEvent.ClearRecentSearches)
                            },
                        ),
                        modifier = Modifier
                            .background(color = GasGuruTheme.colors.neutral100)
                            .padding(top = 20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LocationContent(onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 24.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.NearMe,
            contentDescription = null,
            tint = GasGuruTheme.colors.neutralBlack
        )
        Text(
            text = stringResource(id = R.string.your_location),
            style = GasGuruTheme.typography.baseRegular,
            color = GasGuruTheme.colors.textMain,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun RoutePickerCard(
    modifier: Modifier = Modifier,
    origin: String?,
    destination: String?,
    onPickOrigin: () -> Unit = {},
    onPickDestination: () -> Unit = {},
    onClearOrigin: () -> Unit = {},
    onClearDestination: () -> Unit = {},
    onSwap: () -> Unit = {},
) {
    val border = BorderStroke(1.dp, GasGuruTheme.colors.neutralBlack)
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = border,
        colors = CardDefaults.cardColors(containerColor = GasGuruTheme.colors.neutral100)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_current_location),
                    contentDescription = null,
                    tint = GasGuruTheme.colors.primary700,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.height(2.dp))
                repeat(times = 3) { count ->
                    Box(
                        Modifier
                            .size(3.dp)
                            .background(color = GasGuruTheme.colors.neutralBlack, CircleShape)
                    )
                    if (count < 2) Spacer(Modifier.height(4.dp))
                }
                Spacer(Modifier.height(2.dp))
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = GasGuruTheme.colors.primary700
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                ClickableFieldRow(
                    text = origin,
                    placeholder = stringResource(R.string.route_origin_placeholder),
                    onClick = onPickOrigin,
                    onClear = onClearOrigin,
                )
                GasGuruDivider(
                    model = GasGuruDividerModel(
                        color = GasGuruTheme.colors.neutral300,
                        length = DividerLength.FULL,
                        thickness = DividerThickness.MEDIUM
                    )
                )
                ClickableFieldRow(
                    text = destination,
                    placeholder = stringResource(R.string.route_destination_placeholder),
                    onClick = onPickDestination,
                    onClear = onClearDestination,
                )
            }

            IconButton(onClick = onSwap, modifier = Modifier.padding(start = 8.dp)) {
                Icon(
                    tint = GasGuruTheme.colors.neutralBlack,
                    painter = painterResource(id = R.drawable.ic_swap),
                    contentDescription = stringResource(R.string.route_swap_locations)
                )
            }
        }
    }
}

@Composable
private fun ClickableFieldRow(
    modifier: Modifier = Modifier,
    text: String?,
    placeholder: String,
    onClick: () -> Unit = {},
    onClear: () -> Unit = {},
) {
    val placeholderColor = GasGuruTheme.colors.textSubtle
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 0.dp, vertical = 12.dp)
    ) {
        Text(
            text = text?.takeIf { it.isNotBlank() } ?: placeholder,
            style = GasGuruTheme.typography.baseRegular,
            color = if (text.isNullOrBlank()) {
                placeholderColor
            } else {
                GasGuruTheme.colors.textMain
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(end = if (!text.isNullOrEmpty()) 32.dp else 0.dp)
        )

        if (!text.isNullOrEmpty()) {
            IconButton(
                onClick = onClear,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    tint = GasGuruTheme.colors.neutralBlack,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
@ThemePreviews
private fun RoutePlannerScreenPreview() {
    MyApplicationTheme {
        RoutePlannerScreen(
            uiState = RoutePlannerUiState(startQuery = RouteQuery(name = "Talavera de la reina")),
            selectedPlace = null,
            isRouteEnabled = false,
            recentPlacesState = RecentSearchQueriesUiState.Success(
                recentQueries = listOf(
                    RecentSearchQuery("Barcelona", "1"),
                    RecentSearchQuery("Madrid", "2"),
                    RecentSearchQuery("Valencia", "3"),
                    RecentSearchQuery("Barcelona", "1"),
                    RecentSearchQuery("Madrid", "2"),
                    RecentSearchQuery("Valencia", "3"),
                    RecentSearchQuery("Barcelona", "1"),
                    RecentSearchQuery("Madrid", "2"),
                    RecentSearchQuery("Valencia", "3"),
                    RecentSearchQuery("Barcelona", "1"),
                    RecentSearchQuery("Madrid", "2"),
                    RecentSearchQuery("Valencia", "3"),
                )
            )
        )
    }
}
