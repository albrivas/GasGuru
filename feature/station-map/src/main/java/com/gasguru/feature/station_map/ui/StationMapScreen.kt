package com.gasguru.feature.station_map.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gasguru.core.common.centerOnMap
import com.gasguru.core.common.dpToPx
import com.gasguru.core.common.toLatLng
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelStationBrandsType
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.RecentSearchQuery
import com.gasguru.core.model.data.SearchPlace
import com.gasguru.core.ui.getPrice
import com.gasguru.core.ui.toBrandStationIcon
import com.gasguru.core.ui.toColor
import com.gasguru.core.uikit.components.chip.FilterType
import com.gasguru.core.uikit.components.chip.SelectableFilter
import com.gasguru.core.uikit.components.chip.SelectableFilterModel
import com.gasguru.core.uikit.components.filter_sheet.FilterSheet
import com.gasguru.core.uikit.components.filter_sheet.FilterSheetModel
import com.gasguru.core.uikit.components.fuelItem.FuelStationItem
import com.gasguru.core.uikit.components.fuelItem.FuelStationItemModel
import com.gasguru.core.uikit.components.marker.StationMarker
import com.gasguru.core.uikit.components.marker.StationMarkerModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.GrayExtraLight
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.Neutral100
import com.gasguru.core.uikit.theme.Neutral300
import com.gasguru.core.uikit.theme.Primary600
import com.gasguru.core.uikit.theme.TextSubtle
import com.gasguru.feature.station_map.BuildConfig
import com.gasguru.feature.station_map.R
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import com.gasguru.core.uikit.R as RUikit

@Composable
fun StationMapScreenRoute(
    navigateToDetail: (Int) -> Unit,
    viewModel: StationMapViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResult by viewModel.searchResultUiState.collectAsStateWithLifecycle()
    val recentSearchQuery by viewModel.recentSearchQueriesUiState.collectAsStateWithLifecycle()
    val filterGroup by viewModel.filters.collectAsStateWithLifecycle()
    StationMapScreen(
        uiState = state,
        searchQuery = searchQuery,
        searchResultUiState = searchResult,
        recentSearchQueries = recentSearchQuery,
        filterUiState = filterGroup,
        event = viewModel::handleEvent,
        navigateToDetail = navigateToDetail
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StationMapScreen(
    uiState: StationMapUiState,
    searchQuery: String,
    searchResultUiState: SearchResultUiState,
    recentSearchQueries: RecentSearchQueriesUiState,
    filterUiState: FilterUiState,
    event: (StationMapEvent) -> Unit = {},
    navigateToDetail: (Int) -> Unit = {},
) = with(uiState) {
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(40.0, -4.0), 5.5f)
    }

    var filtersHeightPx by remember { mutableIntStateOf(0) }
    var searchBarHeightPx by remember { mutableIntStateOf(0) }
    val bottomBarHeightPx = 90.dpToPx()
    val peekHeight = 60.dp

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val maxHeightSheetDp = remember(filtersHeightPx, searchBarHeightPx) {
        val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
        with(density) {
            (
                screenHeightPx -
                    filtersHeightPx -
                    searchBarHeightPx -
                    bottomBarHeightPx -
                    peekHeight.toPx()
                ).toDp()
        }
    }

    LaunchedEffect(key1 = mapBounds) {
        mapBounds?.let {
            cameraState.centerOnMap(bounds = mapBounds, padding = 60)
        }
        event(StationMapEvent.ResetMapCenter)
    }

    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutine = rememberCoroutineScope()
    BottomSheetScaffold(
        sheetContainerColor = Neutral100,
        sheetContentColor = Neutral100,
        scaffoldState = scaffoldState,
        sheetShadowElevation = 32.dp,
        sheetPeekHeight = peekHeight,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetDragHandle = {
            Surface(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.LightGray,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Box(
                    modifier = Modifier.size(
                        width = 32.dp,
                        height = 4.0.dp
                    )
                )
            }
        },
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .heightIn(max = maxHeightSheetDp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 17.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.sheet_title),
                        style = GasGuruTheme.typography.baseBold,
                        color = TextSubtle
                    )
                    Text(
                        modifier = Modifier.clickable {
                            coroutine.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        },
                        text = stringResource(id = R.string.sheet_button),
                        style = GasGuruTheme.typography.baseRegular,
                        color = Primary600
                    )
                }
                ListFuelStations(
                    stations = fuelStations,
                    selectedFuel = selectedType,
                    navigateToDetail = navigateToDetail
                )
            }
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                MapView(
                    stations = fuelStations,
                    cameraState = cameraState,
                    userSelectedFuelType = selectedType,
                    loading = loading,
                    navigateToDetail = navigateToDetail,
                    modifier = Modifier.fillMaxSize()
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart)
                ) {
                    SearchPlaces(
                        searchQuery = searchQuery,
                        searchResultUiState = searchResultUiState,
                        recentSearchQueries = recentSearchQueries,
                        onHeight = { searchBarHeightPx = it },
                        event = event,
                    )
                    FilterGroup(
                        modifier = Modifier,
                        event = event,
                        onHeight = { filtersHeightPx = it },
                        filterUiState = filterUiState,
                    )
                }
                FABLocation(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    event = event,
                )
            }
        }
    )
}

@Composable
fun ListFuelStations(
    modifier: Modifier = Modifier,
    stations: List<FuelStation>,
    selectedFuel: FuelType?,
    navigateToDetail: (Int) -> Unit = {},
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .verticalScroll(rememberScrollState())
            .border(1.dp, Neutral300, RoundedCornerShape(8.dp))
    ) {
        stations.forEachIndexed { index, item ->
            FuelStationItem(
                model = FuelStationItemModel(
                    idServiceStation = item.idServiceStation,
                    icon = item.brandStationBrandsType.toBrandStationIcon(),
                    name = item.brandStationName,
                    distance = item.formatDistance(),
                    price = selectedFuel.getPrice(context, item),
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
    userSelectedFuelType: FuelType?,
    loading: Boolean,
    navigateToDetail: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var selectedLocation by remember { mutableStateOf<Int?>(null) }
    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false,
                compassEnabled = false,
                mapToolbarEnabled = false,
            )
        )
    }
    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = true,
            )
        )
    }

    Box(
        modifier = modifier
    ) {
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .zIndex(1f)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            googleMapOptionsFactory = { GoogleMapOptions().mapId(BuildConfig.googleStyleId) },
            uiSettings = uiSettings,
            properties = mapProperties,
            contentPadding = PaddingValues(bottom = 60.dp)
        ) {
            stations.forEach { station ->
                val state = remember(station.idServiceStation) {
                    MarkerState(position = station.location.toLatLng())
                }
                val isSelected = selectedLocation == station.idServiceStation

                val price by remember(userSelectedFuelType, station) {
                    derivedStateOf { userSelectedFuelType.getPrice(context, station) }
                }
                val color by remember(station) {
                    derivedStateOf { station.priceCategory.toColor() }
                }

                MarkerComposable(
                    keys = arrayOf(station.idServiceStation, price, color),
                    state = state,
                    onClick = {
                        selectedLocation = station.idServiceStation
                        navigateToDetail(station.idServiceStation)
                        false
                    },
                    contentDescription = "Marker ${station.brandStationName}",
                ) {
                    StationMarker(
                        model = StationMarkerModel(
                            icon = station.brandStationBrandsType.toBrandStationIcon(),
                            price = userSelectedFuelType.getPrice(context, station),
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
    event: (StationMapEvent) -> Unit = {},
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 76.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FloatingActionButton(
            onClick = {
                event(StationMapEvent.GetStationByCurrentLocation)
            },
            modifier = modifier,
            shape = CircleShape,
            containerColor = Color.White,
            contentColor = Color.Black,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = RUikit.drawable.ic_my_location),
                tint = TextSubtle,
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
    onHeight: (Int) -> Unit,
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

    ProvideTextStyle(value = GasGuruTheme.typography.baseRegular) {
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = statusBarPaddingAnimation,
                    start = paddingAnimation,
                    end = paddingAnimation
                )
                .onGloballyPositioned { onHeight(it.size.height) },
            query = searchQuery,
            onQueryChange = { event(StationMapEvent.UpdateSearchQuery(it)) },
            onSearch = {},
            placeholder = {
                Text(
                    text = stringResource(id = R.string.hint_search_bar),
                    style = GasGuruTheme.typography.baseRegular,
                    color = TextSubtle
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
            shadowElevation = 2.dp,
            colors = SearchBarDefaults.colors(containerColor = Color.White)
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
            style = GasGuruTheme.typography.baseBold
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
                                id = com.gasguru.core.uikit.R.drawable.ic_default_marker
                            ),
                            contentDescription = "location image",
                            colorFilter = ColorFilter.tint(Color.Black)
                        )
                        Text(
                            modifier = Modifier,
                            text = place.name,
                            style = GasGuruTheme.typography.baseRegular
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
            style = GasGuruTheme.typography.h6
        )
        Text(
            text = stringResource(id = R.string.label_empty_suggestions),
            modifier = Modifier.align(Alignment.Start),
            style = GasGuruTheme.typography.baseRegular
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
            style = GasGuruTheme.typography.h6
        )
        Text(
            text = stringResource(id = R.string.label_empty_recents),
            modifier = Modifier.align(Alignment.Start),
            style = GasGuruTheme.typography.baseRegular
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
                style = GasGuruTheme.typography.h6
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
                    style = GasGuruTheme.typography.baseRegular,
                    modifier = Modifier
                        .clickable { onRecentSearchClicked(recentSearchQuery) }
                        .fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun FilterGroup(
    filterUiState: FilterUiState,
    event: (StationMapEvent) -> Unit,
    onHeight: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showFilter by remember { mutableStateOf(false) }
    var filterType by remember { mutableStateOf<FilterType>(FilterType.Brand) }

    val filterModels = getFiltersModel(
        filterUiState = filterUiState,
        onFilterClick = { type ->
            filterType = type
            showFilter = true
        }
    )
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { onHeight(it.size.height) },
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(filterModels) { filterModel ->
            SelectableFilter(model = filterModel)
        }
    }

    if (showFilter) {
        ShowFilterSheet(
            filterType = filterType,
            filterUiState = filterUiState,
            showFilter = { showFilter = false },
            event = event
        )
    }
}

@Composable
private fun getFiltersModel(
    filterUiState: FilterUiState,
    onFilterClick: (FilterType) -> Unit,
): List<SelectableFilterModel> = listOf(
    SelectableFilterModel(
        filterType = FilterType.NumberOfStations,
        label = stringResource(id = R.string.filter_number_nearby),
        selectedLabel = stringResource(id = R.string.filter_number_nearby),
        isSelected = true,
        onFilterClick = { onFilterClick(it) }
    ),
    SelectableFilterModel(
        filterType = FilterType.Brand,
        label = stringResource(id = R.string.filter_brand),
        selectedLabel = stringResource(
            id = R.string.filter_brand_number,
            filterUiState.filterBrand.size
        ),
        isSelected = filterUiState.filterBrand.isNotEmpty(),
        onFilterClick = { onFilterClick(it) }
    ),
    SelectableFilterModel(
        filterType = FilterType.Schedule,
        label = stringResource(id = R.string.filter_schedule),
        selectedLabel = stringResource(id = filterUiState.filterSchedule.resId),
        isSelected = filterUiState.filterSchedule != FilterUiState.OpeningHours.NONE,
        onFilterClick = { onFilterClick(it) }
    ),
)

@Composable
fun ShowFilterSheet(
    filterType: FilterType,
    filterUiState: FilterUiState,
    showFilter: () -> Unit,
    event: (StationMapEvent) -> Unit,
) {
    val context = LocalContext.current

    when (filterType) {
        FilterType.Brand -> {
            FilterSheet(
                model = FilterSheetModel(
                    title = stringResource(R.string.filter_brand_title),
                    buttonText = stringResource(id = R.string.filter_button),
                    isMultiOption = true,
                    isMustSelection = false,
                    options = FuelStationBrandsType.entries
                        .filter { it.value != FuelStationBrandsType.UNKNOWN.value }
                        .sortedBy { it.value.lowercase() }
                        .map { it.value },
                    optionsSelected = filterUiState.filterBrand,
                    onDismiss = { showFilter() },
                    onSaveButton = { event(StationMapEvent.UpdateBrandFilter(it)) }
                )
            )
        }

        FilterType.NumberOfStations -> {
            FilterSheet(
                model = FilterSheetModel(
                    title = stringResource(R.string.filter_number_nearby_title),
                    buttonText = stringResource(id = R.string.filter_button),
                    isMultiOption = false,
                    isMustSelection = true,
                    options = listOf("10", "15", "20", "25"),
                    optionsSelected = listOf(filterUiState.filterStationsNearby.toString()),
                    onDismiss = { showFilter() },
                    onSaveButton = { event(StationMapEvent.UpdateNearbyFilter(it.first())) }
                )
            )
        }

        FilterType.Schedule -> {
            FilterSheet(
                model = FilterSheetModel(
                    title = stringResource(id = R.string.filter_schedule),
                    buttonText = stringResource(id = R.string.filter_button),
                    isMultiOption = false,
                    isMustSelection = false,
                    options = FilterUiState.OpeningHours.entries
                        .filter { it != FilterUiState.OpeningHours.NONE }
                        .map { stringResource(id = it.resId) },
                    optionsSelected = listOf(stringResource(id = filterUiState.filterSchedule.resId)),
                    onDismiss = { showFilter() },
                    onSaveButton = {
                        val schedule = if (it.isEmpty()) {
                            FilterUiState.OpeningHours.NONE
                        } else {
                            FilterUiState.OpeningHours.fromTranslatedString(it.first(), context)
                        }
                        event(StationMapEvent.UpdateScheduleFilter(schedule))
                    }
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StationMapScreenPreview() {
    MyApplicationTheme {
        StationMapScreen(
            uiState = StationMapUiState(),
            searchResultUiState = SearchResultUiState.EmptyQuery,
            searchQuery = "",
            recentSearchQueries = RecentSearchQueriesUiState.Loading,
            filterUiState = FilterUiState(),
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
