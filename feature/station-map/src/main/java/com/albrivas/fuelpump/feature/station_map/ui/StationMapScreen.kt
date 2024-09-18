package com.albrivas.fuelpump.feature.station_map.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.albrivas.fuelpump.core.common.centerOnLocation
import com.albrivas.fuelpump.core.common.toLatLng
import com.albrivas.fuelpump.core.model.data.FuelStation
import com.albrivas.fuelpump.core.model.data.FuelType
import com.albrivas.fuelpump.core.model.data.RecentSearchQuery
import com.albrivas.fuelpump.core.model.data.SearchPlace
import com.albrivas.fuelpump.core.ui.getPrice
import com.albrivas.fuelpump.core.ui.toBrandStationIcon
import com.albrivas.fuelpump.core.ui.toColor
import com.albrivas.fuelpump.core.uikit.components.fuelItem.FuelStationItem
import com.albrivas.fuelpump.core.uikit.components.fuelItem.FuelStationItemModel
import com.albrivas.fuelpump.core.uikit.components.marker.StationMarker
import com.albrivas.fuelpump.core.uikit.components.marker.StationMarkerModel
import com.albrivas.fuelpump.core.uikit.theme.GrayBackground
import com.albrivas.fuelpump.core.uikit.theme.GrayExtraLight
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme
import com.albrivas.fuelpump.feature.station_map.R
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.albrivas.fuelpump.core.uikit.R as RUikit

@Composable
fun StationMapScreenRoute(
    navigateToDetail: (Int) -> Unit,
    viewModel: StationMapViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResult by viewModel.searchResultUiState.collectAsStateWithLifecycle()
    val recentSearchQuery by viewModel.recentSearchQueriesUiState.collectAsStateWithLifecycle()
    StationMapScreen(
        stations = state.fuelStations,
        centerMap = state.centerMap,
        zoomLevel = state.zoomLevel,
        userSelectedFuelType = state.selectedType,
        searchResultUiState = searchResult,
        searchQuery = searchQuery,
        showListStations = state.showListStations,
        recentSearchQueries = recentSearchQuery,
        event = viewModel::handleEvent,
        navigateToDetail = navigateToDetail
    )
}

@Composable
internal fun StationMapScreen(
    stations: List<FuelStation>,
    centerMap: LatLng?,
    zoomLevel: Float,
    searchQuery: String,
    userSelectedFuelType: FuelType?,
    searchResultUiState: SearchResultUiState,
    recentSearchQueries: RecentSearchQueriesUiState,
    showListStations: Boolean,
    event: (StationMapEvent) -> Unit = {},
    navigateToDetail: (Int) -> Unit = {},
) {
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(40.0, -4.0), 5.5f)
    }

    LaunchedEffect(key1 = centerMap) {
        centerMap?.let {
            cameraState.centerOnLocation(location = centerMap, zoomLevel = zoomLevel)
        }
        event(StationMapEvent.ResetMapCenter)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = showListStations,
            label = "animation map and list",
        ) { isListVisible ->
            if (isListVisible) {
                ListFuelStations(
                    stations = stations,
                    selectedFuel = userSelectedFuelType,
                    navigateToDetail = navigateToDetail
                )
            } else {
                MapView(
                    stations = stations,
                    cameraState = cameraState,
                    searchQuery = searchQuery,
                    userSelectedFuelType = userSelectedFuelType,
                    searchResultUiState = searchResultUiState,
                    recentSearchQueries = recentSearchQueries,
                    navigateToDetail = navigateToDetail,
                    event = event
                )
            }
        }
        FABLocation(
            modifier = Modifier.align(Alignment.BottomEnd),
            event = event,
            showListStations = showListStations
        )
    }
}

@Composable
fun ListFuelStations(
    modifier: Modifier = Modifier,
    stations: List<FuelStation>,
    selectedFuel: FuelType?,
    navigateToDetail: (Int) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(color = GrayBackground)
            .statusBarsPadding(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 140.dp)
    ) {
        itemsIndexed(stations) { index, item ->
            FuelStationItem(
                model = FuelStationItemModel(
                    idServiceStation = item.idServiceStation,
                    icon = item.brandStationBrandsType.toBrandStationIcon(),
                    name = item.brandStationName,
                    direction = item.direction,
                    distance = item.formatDistance(),
                    price = selectedFuel.getPrice(item),
                    index = index,
                    categoryColor = item.priceCategory.toColor(),
                    onItemClick = navigateToDetail
                )
            )
        }
    }
}

@Composable
fun MapView(
    stations: List<FuelStation>,
    cameraState: CameraPositionState,
    searchQuery: String,
    userSelectedFuelType: FuelType?,
    searchResultUiState: SearchResultUiState,
    recentSearchQueries: RecentSearchQueriesUiState,
    event: (StationMapEvent) -> Unit = {},
    navigateToDetail: (Int) -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize()) {
        CompositionLocalProvider(
            value = LocalTextStyle provides MaterialTheme.typography.displayMedium.copy(
                color = Color.Black
            )
        ) {
            SearchPlaces(
                searchQuery = searchQuery,
                searchResultUiState = searchResultUiState,
                recentSearchQueries = recentSearchQueries,
                event = event,
            )
        }
        val markerStates = remember { mutableStateMapOf<Int, MarkerState>() }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            googleMapOptionsFactory = { GoogleMapOptions().mapId("da696d048f7d52b8") },
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false,
                compassEnabled = false,
                mapToolbarEnabled = false,
            ),
            properties = MapProperties(
                isMyLocationEnabled = true,
                mapType = MapType.NORMAL,
            ),
            onMyLocationButtonClick = {
                true
            },
            onMapLoaded = { },
        ) {
            var selectedLocation by remember { mutableStateOf<Int?>(null) }

            stations.forEach { station ->
                val state =
                    markerStates.getOrPut(
                        station.idServiceStation
                    ) { MarkerState(position = station.location.toLatLng()) }
                val isSelected = selectedLocation == station.idServiceStation

                MarkerComposable(
                    keys = arrayOf(station.idServiceStation),
                    state = state,
                    onClick = {
                        state.showInfoWindow()
                        selectedLocation = station.idServiceStation
                        event(StationMapEvent.CenterMapStation(station.location.toLatLng()))
                        navigateToDetail(station.idServiceStation)
                        false
                    },
                    contentDescription = "Marker ${station.brandStationName}",
                ) {
                    StationMarker(
                        model = StationMarkerModel(
                            icon = station.brandStationBrandsType.toBrandStationIcon(),
                            price = "€${userSelectedFuelType.getPrice(station)}",
                            color = station.priceCategory.toColor(),
                            isSelected = isSelected,
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun FABLocation(
    modifier: Modifier,
    showListStations: Boolean,
    event: (StationMapEvent) -> Unit = {},
) {
    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FloatingActionButton(
            onClick = {
                event(StationMapEvent.CenterMapInCurrentLocation)
                event(StationMapEvent.GetStationByCurrentLocation)
            },
            modifier = modifier,
            shape = CircleShape,
            containerColor = Color.White,
            contentColor = Color.Black,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = RUikit.drawable.ic_my_location),
                contentDescription = "User location",
            )
        }

        FloatingActionButton(
            onClick = {
                event(StationMapEvent.ShowListStations(show = !showListStations))
            },
            modifier = modifier,
            shape = CircleShape,
            containerColor = Color.White,
            contentColor = Color.Black,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(
                    id = if (showListStations) RUikit.drawable.ic_map else RUikit.drawable.ic_list
                ),
                contentDescription = "User location",
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPlaces(
    searchQuery: String,
    searchResultUiState: SearchResultUiState,
    recentSearchQueries: RecentSearchQueriesUiState,
    event: (StationMapEvent) -> Unit = {},
) {
    var active by remember { mutableStateOf(false) }

    val paddingAnimation: Dp by animateDpAsState(
        targetValue = if (active) 0.dp else 16.dp,
        animationSpec = tween(durationMillis = 300),
        label = ""
    )

    val statusBarPaddingAnimation: Dp by animateDpAsState(
        targetValue = 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = ""
    )

    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = statusBarPaddingAnimation,
                start = paddingAnimation,
                end = paddingAnimation
            ),
        query = searchQuery,
        onQueryChange = { event(StationMapEvent.UpdateSearchQuery(it)) },
        onSearch = {},
        placeholder = {
            Text(
                text = stringResource(id = R.string.hint_search_bar),
                style = MaterialTheme.typography.displayMedium,
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
                IconButton(onClick = { event(StationMapEvent.UpdateSearchQuery("")) }) {
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
                                event(StationMapEvent.UpdateSearchQuery(it.name))
                                event(StationMapEvent.GetStationByPlace(it.id))
                                active = false
                            },
                            event = event,
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
                    onActiveChange = { active = it },
                    event = event,
                )
            }
        }
    }
}

@Composable
fun SearchResultBody(
    places: List<SearchPlace>,
    onActiveChange: (Boolean) -> Unit,
    event: (StationMapEvent) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
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
                .fillMaxWidth(),
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
                                event(StationMapEvent.InsertRecentSearch(place))
                                event(StationMapEvent.GetStationByPlace(place.id))
                                event(StationMapEvent.UpdateSearchQuery(place.name))
                            },
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(
                                id = com.albrivas.fuelpump.core.uikit.R.drawable.ic_default_marker
                            ),
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
            .fillMaxWidth()
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
            .fillMaxWidth()
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
    event: (StationMapEvent) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        ) {
            Text(
                text = stringResource(id = R.string.label_recent),
                style = MaterialTheme.typography.titleMedium
            )
            if (recentSearchQueries.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Clear recent searches",
                    tint = Color.Black,
                    modifier = Modifier
                        .align(Alignment.Top)
                        .clickable { event(StationMapEvent.ClearRecentSearches) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(recentSearchQueries) { recentSearchQuery ->
                Text(
                    text = recentSearchQuery.name,
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier
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
    MyApplicationTheme {
        StationMapScreen(
            stations = emptyList(),
            centerMap = LatLng(0.0, 0.0),
            zoomLevel = 15f,
            userSelectedFuelType = FuelType.GASOLINE_95,
            searchResultUiState = SearchResultUiState.EmptyQuery,
            searchQuery = "",
            recentSearchQueries = RecentSearchQueriesUiState.Loading,
            showListStations = false,
            navigateToDetail = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecentSearchQueryBodyPreview() {
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

@Preview(showBackground = true)
@Composable
private fun EmptyRecentSearchQueryBodyPreview() {
    MyApplicationTheme {
        EmptyRecentSearchesBody()
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchResultBodyPreview() {
    MyApplicationTheme {
        SearchResultBody(
            places = listOf(
                SearchPlace("Barcelona", "1"),
                SearchPlace("Madrid", "2"),
                SearchPlace("Valencia", "3"),
            ),
            onActiveChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptySearchResultBodyPreview() {
    MyApplicationTheme {
        EmptyResultBody()
    }
}
