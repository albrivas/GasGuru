package com.albrivas.feature.station_map.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.albrivas.fuelpump.feature.station_map.R
import com.albrivas.fuelpump.core.common.centerOnLocation
import com.albrivas.fuelpump.core.common.toLatLng
import com.albrivas.fuelpump.core.model.data.FuelStation
import com.albrivas.fuelpump.core.model.data.FuelType
import com.albrivas.fuelpump.core.model.data.RecentSearchQuery
import com.albrivas.fuelpump.core.model.data.SearchPlace
import com.albrivas.fuelpump.core.ui.getPrice
import com.albrivas.fuelpump.core.ui.toBrandStationIcon
import com.albrivas.fuelpump.core.ui.toColor
import com.albrivas.fuelpump.core.uikit.components.marker.StationMarker
import com.albrivas.fuelpump.core.uikit.components.marker.StationMarkerModel
import com.albrivas.fuelpump.core.uikit.theme.GrayExtraLight
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun StationMapScreenRoute(viewModel: StationMapViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResult by viewModel.searchResultUiState.collectAsStateWithLifecycle()
    val recentSearchQuery by viewModel.recentSearchQueriesUiState.collectAsStateWithLifecycle()
    StationMapScreen(
        getStationByCurrentLocation = viewModel::getStationByCurrentLocation,
        stations = state.fuelStations,
        centerMap = state.centerMap,
        zoomLevel = state.zoomLevel,
        userSelectedFuelType = state.selectedType,
        searchResultUiState = searchResult,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        searchQuery = searchQuery,
        getStationsByPlace = viewModel::getStationByPlace,
        recentSearchQueries = recentSearchQuery,
        saveSearchResultClicked = viewModel::insertRecentSearch,
        clearRecentSearches = viewModel::clearRecentSearches,
    )
}

@Composable
internal fun StationMapScreen(
    getStationByCurrentLocation: () -> Unit,
    stations: List<FuelStation>,
    centerMap: LatLng,
    zoomLevel: Float,
    userSelectedFuelType: FuelType?,
    searchResultUiState: SearchResultUiState,
    onSearchQueryChanged: (String) -> Unit,
    searchQuery: String,
    getStationsByPlace: (String) -> Unit,
    recentSearchQueries: RecentSearchQueriesUiState,
    saveSearchResultClicked: (SearchPlace) -> Unit,
    clearRecentSearches: () -> Unit,
) {
    val cameraState = rememberCameraPositionState()
    LaunchedEffect(key1 = centerMap) {
        cameraState.centerOnLocation(location = centerMap, zoomLevel = zoomLevel)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        CompositionLocalProvider(
            value = LocalTextStyle provides MaterialTheme.typography.bodyMedium.copy(
                color = Color.Black
            )
        ) {
            SearchPlaces(
                searchQuery = searchQuery,
                onSearchQueryChanged = onSearchQueryChanged,
                searchResultUiState = searchResultUiState,
                getStationsByPlace = getStationsByPlace,
                recentSearchQueries = recentSearchQueries,
                saveSearchResultClicked = saveSearchResultClicked,
                clearRecentSearches = clearRecentSearches,
            )
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = true,
                zoomControlsEnabled = false,
                compassEnabled = false,
            ),
            properties = MapProperties(
                isMyLocationEnabled = false
            ),
            onMyLocationButtonClick = {
                true
            },
            onMapLoaded = getStationByCurrentLocation,
        ) {
            stations.forEach { station ->
                val state = MarkerState(position = station.location.toLatLng())
                MarkerComposable(state = state) {
                    StationMarker(
                        model = StationMarkerModel(
                            icon = station.brandStationBrandsType.toBrandStationIcon(),
                            price = "€${userSelectedFuelType.getPrice(station)}",
                            color = station.priceCategory.toColor()
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPlaces(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    searchResultUiState: SearchResultUiState,
    getStationsByPlace: (String) -> Unit,
    recentSearchQueries: RecentSearchQueriesUiState,
    saveSearchResultClicked: (SearchPlace) -> Unit,
    clearRecentSearches: () -> Unit,
) {

    var active by remember { mutableStateOf(false) }

    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(if (active) 0.dp else 16.dp),
        query = searchQuery,
        onQueryChange = onSearchQueryChanged,
        onSearch = {},
        placeholder = {
            Text(
                text = stringResource(id = R.string.hint_search_bar),
                style = MaterialTheme.typography.displayMedium
            )
        },
        leadingIcon = {
            if (active) {
                IconButton(onClick = { active = false }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = "Icon back to map"
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Search,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = "Icon search"
                )
            }
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChanged("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = "Icon search",
                    )
                }
            }
        },
        active = active,
        onActiveChange = { active = it },
        shadowElevation = 8.dp,
        colors = SearchBarDefaults.colors(containerColor = Color.White)
    ) {
        when (searchResultUiState) {
            SearchResultUiState.Loading,
            SearchResultUiState.LoadFailed,
            -> Unit

            SearchResultUiState.EmptyQuery -> {
                if (recentSearchQueries is RecentSearchQueriesUiState.Success) {
                    if (recentSearchQueries.recentQueries.isEmpty()) {
                        EmptyRecentSearchesBody()
                    } else {
                        RecentSearchQueriesBody(
                            recentSearchQueries = recentSearchQueries.recentQueries,
                            onRecentSearchClicked = {
                                onSearchQueryChanged(it.name)
                                getStationsByPlace(it.id)
                                active = false
                            },
                            onClearRecentSearches = clearRecentSearches,
                        )
                    }
                }
            }

            SearchResultUiState.EmptySearchResult -> {
                EmptyResultBody()
            }

            is SearchResultUiState.Success -> {
                SearchResultBody(
                    places = searchResultUiState.places,
                    onSearchQueryChanged = onSearchQueryChanged,
                    getStationsByPlace = getStationsByPlace,
                    onActiveChange = { active = it },
                    onSearchResultClicked = saveSearchResultClicked
                )
            }
        }
    }
}

@Composable
fun SearchResultBody(
    places: List<SearchPlace>,
    onActiveChange: (Boolean) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    getStationsByPlace: (String) -> Unit,
    onSearchResultClicked: (SearchPlace) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.label_suggestion),
            modifier = Modifier
                .align(Alignment.Start),
            style = MaterialTheme.typography.titleMedium
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            items(places) { place ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                onActiveChange(false)
                                onSearchQueryChanged(place.name)
                                getStationsByPlace(place.id)
                                onSearchResultClicked(place)
                            },
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = com.albrivas.fuelpump.core.uikit.R.drawable.ic_default_marker),
                            contentDescription = "location image",
                            colorFilter = ColorFilter.tint(Color.Black)
                        )
                        Text(
                            modifier = Modifier,
                            text = place.name,
                            style = MaterialTheme.typography.displayMedium
                        )
                    }
                }
                HorizontalDivider(
                    color = GrayExtraLight,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyResultBody() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.label_suggestion),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(id = R.string.label_empty_suggestions),
            modifier = Modifier.align(Alignment.Start),
            style = MaterialTheme.typography.displayMedium
        )

    }
}

@Composable
fun EmptyRecentSearchesBody() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.label_recent),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(id = R.string.label_empty_recents),
            modifier = Modifier.align(Alignment.Start),
            style = MaterialTheme.typography.displayMedium
        )

    }
}

@Composable
fun RecentSearchQueriesBody(
    recentSearchQueries: List<RecentSearchQuery>,
    onRecentSearchClicked: (RecentSearchQuery) -> Unit = {},
    onClearRecentSearches: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(id = R.string.label_recent),
                modifier = Modifier
                    .padding(bottom = 8.dp),
                style = MaterialTheme.typography.titleMedium
            )
            if (recentSearchQueries.isNotEmpty()) {
                IconButton(
                    onClick = onClearRecentSearches,
                    modifier = Modifier.padding(horizontal = 8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Clear recent searches",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(recentSearchQueries) { recentSearchQuery ->
                Text(
                    text = recentSearchQuery.name,
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable { onRecentSearchClicked(recentSearchQuery) }
                        .fillMaxWidth(),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StationMapScreenPreview() {
    StationMapScreen(
        getStationByCurrentLocation = {},
        stations = emptyList(),
        centerMap = LatLng(0.0, 0.0),
        zoomLevel = 15f,
        userSelectedFuelType = FuelType.GASOLINE_95,
        searchResultUiState = SearchResultUiState.EmptyQuery,
        onSearchQueryChanged = {},
        searchQuery = "",
        getStationsByPlace = {},
        recentSearchQueries = RecentSearchQueriesUiState.Loading,
        saveSearchResultClicked = {},
        clearRecentSearches = {},
    )
}