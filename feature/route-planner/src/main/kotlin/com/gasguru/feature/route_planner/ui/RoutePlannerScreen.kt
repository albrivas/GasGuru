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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.SwapVert
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gasguru.core.model.data.RecentSearchQuery
import com.gasguru.core.ui.RecentSearchQueriesUiState
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

@Composable
fun RoutePlannerScreenRoute(
    onBack: () -> Unit = {},
    navigateToSearch: () -> Unit = {},
    viewModel: RoutePlannerViewModel = hiltViewModel(),
) {
    val recents by viewModel.recentSearchQueriesUiState.collectAsStateWithLifecycle()
    RoutePlannerScreen(
        onBack = onBack,
        navigateToSearch = navigateToSearch,
        recentPlacesState = recents,
        onEvent = viewModel::handleEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RoutePlannerScreen(
    onBack: () -> Unit = {},
    navigateToSearch: () -> Unit = {},
    recentPlacesState: RecentSearchQueriesUiState,
    onEvent: (RoutePlannerUiEvent) -> Unit = {},
) {
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
        bottomBar = {},
        modifier = Modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxSize()
        ) {
            RoutePickerCard(
                origin = null,
                destination = null,
                onPickOrigin = navigateToSearch,
                onPickDestination = navigateToSearch
            )
            LocationContent()
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
                                    onClickItem = { }
                                )
                            },
                            onClear = {
                            },
                        ),
                        modifier = Modifier
                            .background(color = GasGuruTheme.colors.neutral100)
                            .padding(top = 28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LocationContent() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.NearMe,
            contentDescription = null,
            tint = GasGuruTheme.colors.neutralBlack
        )
        Text(
            text = "Tu ubicación",
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
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = GasGuruTheme.colors.primary700
                )
                Spacer(Modifier.height(2.dp))
                repeat(3) { count ->
                    Box(
                        Modifier
                            .size(3.dp)
                            .background(color = GasGuruTheme.colors.neutralBlack, CircleShape)
                    )
                    if (count < 2) Spacer(Modifier.height(4.dp))
                }
                Spacer(Modifier.height(2.dp))
                Icon(
                    Icons.Default.Place,
                    contentDescription = null,
                    tint = GasGuruTheme.colors.primary700
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(
                Modifier
                    .weight(1f)
            ) {
                ClickableFieldRow(
                    text = origin,
                    placeholder = stringResource(R.string.route_origin_placeholder),
                    onClick = onPickOrigin,
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
                )
            }

            IconButton(onClick = onSwap, modifier = Modifier.padding(start = 8.dp)) {
                Icon(
                    tint = GasGuruTheme.colors.neutralBlack,
                    imageVector = Icons.Default.SwapVert,
                    contentDescription = stringResource(R.string.route_swap_locations)
                )
            }
        }
    }
}

@Composable
private fun ClickableFieldRow(
    text: String?,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val placeholderColor = GasGuruTheme.colors.textSubtle
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 0.dp, vertical = 13.dp)
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
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}

@Composable
@ThemePreviews
private fun RoutePlannerScreenPreview() {
    MyApplicationTheme {
        RoutePlannerScreen(
            recentPlacesState = RecentSearchQueriesUiState.Success(
                recentQueries = listOf(
                    RecentSearchQuery("Barcelona", "1"),
                    RecentSearchQuery("Madrid", "2"),
                    RecentSearchQuery("Valencia", "3"),
                )
            )
        )
    }
}
