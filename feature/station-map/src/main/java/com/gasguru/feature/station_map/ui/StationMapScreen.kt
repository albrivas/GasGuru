package com.gasguru.feature.station_map.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Directions
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gasguru.core.common.centerOnLocation
import com.gasguru.core.common.centerOnMap
import com.gasguru.core.common.toGoogleLatLng
import com.gasguru.core.components.searchbar.GasGuruSearchBar
import com.gasguru.core.components.searchbar.GasGuruSearchBarModel
import com.gasguru.core.model.data.FuelStationBrandsType
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.getPrice
import com.gasguru.core.ui.mapper.toStationListItems
import com.gasguru.core.ui.mapper.toUiModel
import com.gasguru.core.ui.models.FuelStationUiModel
import com.gasguru.core.ui.toColor
import com.gasguru.core.uikit.components.chip.FilterType
import com.gasguru.core.uikit.components.chip.SelectableFilter
import com.gasguru.core.uikit.components.chip.SelectableFilterModel
import com.gasguru.core.uikit.components.drag_handle.DragHandle
import com.gasguru.core.uikit.components.filter_sheet.FilterSheet
import com.gasguru.core.uikit.components.filter_sheet.FilterSheetModel
import com.gasguru.core.uikit.components.filter_sheet.FilterSheetType
import com.gasguru.core.uikit.components.filterable_station_list.FilterableStationList
import com.gasguru.core.uikit.components.filterable_station_list.FilterableStationListModel
import com.gasguru.core.uikit.components.loading.GasGuruLoading
import com.gasguru.core.uikit.components.loading.GasGuruLoadingModel
import com.gasguru.core.uikit.components.marker.StationMarker
import com.gasguru.core.uikit.components.marker.StationMarkerModel
import com.gasguru.core.uikit.components.route_navigation_card.RouteNavigationCard
import com.gasguru.core.uikit.components.route_navigation_card.RouteNavigationCardModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import com.gasguru.core.uikit.utils.maestroTestTag
import com.gasguru.feature.station_map.BuildConfig
import com.gasguru.feature.station_map.R
import com.gasguru.feature.station_map.ui.models.RouteUiModel
import com.gasguru.navigation.LocalDeepLinkStateHolder
import com.gasguru.navigation.LocalNavigationManager
import com.gasguru.navigation.manager.NavigationDestination
import com.gasguru.navigation.models.RoutePlanArgs
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import com.gasguru.core.uikit.R as RUikit

@Composable
fun StationMapScreenRoute(
    routePlanner: RoutePlanArgs?,
    onRoutePlanConsumed: () -> Unit = {},
    viewModel: StationMapViewModel = hiltViewModel(),
) {
    val navigationManager = LocalNavigationManager.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val filterGroup by viewModel.filters.collectAsStateWithLifecycle()
    val tabState by viewModel.tabState.collectAsStateWithLifecycle()

    // Access DeepLinkStateHolder via CompositionLocal
    val deepLinkStateHolder = LocalDeepLinkStateHolder.current
    val pendingStationId by deepLinkStateHolder.pendingStationId.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = state.loading, key2 = pendingStationId, key3 = state.mapStations) {
        if (!state.loading && state.mapStations.isNotEmpty()) {
            pendingStationId?.let { stationId ->
                deepLinkStateHolder.clear()
                navigationManager.navigateTo(
                    destination = NavigationDestination.DetailStation(
                        idServiceStation = stationId,
                        presentAsDialog = true,
                    ),
                )
            }
        }
    }

    StationMapScreen(
        uiState = state,
        filterUiState = filterGroup,
        tabState = tabState,
        routePlanner = routePlanner,
        onRoutePlanConsumed = onRoutePlanConsumed,
        event = viewModel::handleEvent,
        navigateToDetail = { stationId ->
            navigationManager.navigateTo(
                destination = NavigationDestination.DetailStation(
                    idServiceStation = stationId,
                    presentAsDialog = true,
                )
            )
        },
        navigateToRoutePlanner = {
            navigationManager.navigateTo(destination = NavigationDestination.RoutePlanner)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
internal fun StationMapScreen(
    uiState: StationMapUiState,
    filterUiState: FilterUiState,
    tabState: SelectedTabUiState,
    routePlanner: RoutePlanArgs?,
    onRoutePlanConsumed: () -> Unit = {},
    event: (StationMapEvent) -> Unit = {},
    navigateToDetail: (Int) -> Unit = {},
    navigateToRoutePlanner: () -> Unit = {},
) = with(uiState) {
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(40.0, -4.0), 5.5f)
    }

    val peekHeight = 60.dp
    var isSearchActive by remember { mutableStateOf(false) }
    val isRouteActive = route != null || (loading && routeDestinationName != null)

    val maxHeightSheetDp = calculateMaxSheetHeight(peekHeight = peekHeight)

    LaunchedEffect(mapBounds, shouldCenterMap) {
        if (mapBounds != null && shouldCenterMap) {
            cameraState.centerOnMap(bounds = mapBounds, padding = 60)
            event(StationMapEvent.OnMapCentered)
        }
    }

    LaunchedEffect(routePlanner) {
        if (routePlanner != null) {
            event(
                StationMapEvent.StartRoute(
                    originId = routePlanner.originId,
                    destinationId = routePlanner.destinationId,
                    destinationName = routePlanner.destinationName,
                )
            )
            onRoutePlanConsumed()
        }
    }

    LaunchedEffect(userLocationToCenter) {
        if (userLocationToCenter != null) {
            cameraState.centerOnLocation(location = userLocationToCenter)
            event(StationMapEvent.OnUserLocationCentered)
        }
    }

    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutine = rememberCoroutineScope()
    BottomSheetScaffold(
        sheetContainerColor = GasGuruTheme.colors.neutral100,
        sheetContentColor = GasGuruTheme.colors.neutral100,
        scaffoldState = scaffoldState,
        sheetShadowElevation = 32.dp,
        sheetPeekHeight = if (isSearchActive) 0.dp else peekHeight,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetDragHandle = { DragHandle() },
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
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = com.gasguru.core.ui.R.string.nearby_stations),
                        style = GasGuruTheme.typography.baseBold,
                        color = GasGuruTheme.colors.textSubtle
                    )
                    if (isSheetPartiallyExpanded(scaffoldState)) {
                        Text(
                            modifier = Modifier.clickable {
                                coroutine.launch {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            },
                            text = stringResource(id = R.string.sheet_button),
                            style = GasGuruTheme.typography.baseRegular,
                            color = GasGuruTheme.colors.primary600
                        )
                    }
                }
                FilterableStationList(
                    model = FilterableStationListModel(
                        stations = listStations.toStationListItems(selectedType ?: return@Column),
                        selectedTab = tabState.selectedTab.value,
                        onTabChange = { selectedTab ->
                            event(
                                StationMapEvent.ChangeTab(
                                    selected = StationSortTab.fromValue(
                                        selectedTab
                                    )
                                )
                            )
                        },
                        onStationClick = navigateToDetail,
                        swipeConfig = null,
                        testTag = "map_station_list",
                        tabNames = listOf(
                            stringResource(com.gasguru.core.uikit.R.string.tab_price),
                            stringResource(com.gasguru.core.uikit.R.string.tab_distance)
                        )
                    )
                )
            }
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = GasGuruTheme.colors.neutral300)
            ) {
                MapView(
                    stations = mapStations,
                    cameraState = cameraState,
                    userSelectedFuelType = selectedType,
                    loading = loading,
                    route = route,
                    selectedStationId = selectedStationId,
                    navigateToDetail = navigateToDetail,
                    event = event,
                    modifier = Modifier.fillMaxSize()
                )
                AnimatedContent(
                    targetState = isRouteActive,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart),
                    label = "route_content_animation",
                ) { isActive ->
                    if (isActive) {
                        RouteNavigationCard(
                            model = RouteNavigationCardModel(
                                destination = routeDestinationName.orEmpty(),
                                stationCountText = stringResource(
                                    id = R.string.route_station_count,
                                    mapStations.size
                                ),
                                distance = route?.distanceText,
                                duration = route?.durationText,
                                onClose = { event(StationMapEvent.CancelRoute) },
                            ),
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 56.dp, bottom = 16.dp),
                        )
                    } else {
                        Column {
                            GasGuruSearchBar(
                                model = GasGuruSearchBarModel(
                                    onActiveChange = { isSearchActive = it },
                                    onPlaceSelected = { place ->
                                        event(StationMapEvent.GetStationByPlace(place.id))
                                    },
                                    onRecentSearchClicked = { place ->
                                        event(StationMapEvent.GetStationByPlace(place.id))
                                    },
                                )
                            )
                            FilterGroup(
                                modifier = Modifier,
                                event = event,
                                onHeight = { },
                                filterUiState = filterUiState,
                            )
                        }
                    }
                }
                FloatingButtons(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    isVisible = !isSearchActive,
                    showRoutePlannerButton = !isRouteActive,
                    event = event,
                    navigateToRoutePlanner = navigateToRoutePlanner,
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun isSheetPartiallyExpanded(state: BottomSheetScaffoldState): Boolean =
    state.bottomSheetState.currentValue == SheetValue.PartiallyExpanded

@Composable
fun MapView(
    stations: List<FuelStationUiModel>,
    cameraState: CameraPositionState,
    userSelectedFuelType: FuelType?,
    loading: Boolean,
    route: RouteUiModel?,
    selectedStationId: Int,
    modifier: Modifier = Modifier,
    navigateToDetail: (Int) -> Unit = {},
    event: (StationMapEvent) -> Unit = {},
) {
    val context = LocalContext.current
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
            GasGuruLoading(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GasGuruTheme.colors.neutralBlack.copy(alpha = 0.5f))
                    .zIndex(1f)
                    .maestroTestTag("loading_map"),
                model = GasGuruLoadingModel(color = GasGuruTheme.colors.primary800)
            )
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            googleMapOptionsFactory = { GoogleMapOptions().mapId(BuildConfig.googleStyleId) },
            uiSettings = uiSettings,
            properties = mapProperties,
            contentPadding = PaddingValues(bottom = 60.dp),
            mapColorScheme = if (GasGuruTheme.colors.isDark) ComposeMapColorScheme.DARK else ComposeMapColorScheme.LIGHT
        ) {
            route?.let {
                val googleMapsPoints = it.route.map { point ->
                    LatLng(point.latitude, point.longitude)
                }
                Polyline(
                    points = googleMapsPoints,
                    width = 20f,
                    jointType = JointType.ROUND,
                    color = GasGuruTheme.colors.primary900
                )
            }
            stations.forEach { station ->
                val priceCategoryColor = station.fuelStation.priceCategory.toColor()
                val state = remember(station.fuelStation.idServiceStation) {
                    MarkerState(position = station.fuelStation.location.toGoogleLatLng())
                }
                val isSelected = selectedStationId == station.fuelStation.idServiceStation

                val price by remember(userSelectedFuelType, station) {
                    derivedStateOf {
                        userSelectedFuelType.getPrice(
                            context = context,
                            fuelStation = station.fuelStation
                        )
                    }
                }
                val color by remember(station) {
                    derivedStateOf { priceCategoryColor }
                }

                MarkerComposable(
                    keys = arrayOf(station.fuelStation.idServiceStation, price, color),
                    state = state,
                    onClick = {
                        event(StationMapEvent.SelectStation(stationId = station.fuelStation.idServiceStation))
                        navigateToDetail(station.fuelStation.idServiceStation)
                        false
                    },
                    contentDescription = "Marker ${station.fuelStation.brandStationName}",
                ) {
                    StationMarker(
                        model = StationMarkerModel(
                            icon = station.brandIcon,
                            price = price,
                            color = color,
                            isSelected = isSelected,
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun FloatingButtons(
    modifier: Modifier,
    isVisible: Boolean = true,
    showRoutePlannerButton: Boolean = true,
    event: (StationMapEvent) -> Unit = {},
    navigateToRoutePlanner: () -> Unit = {},
) {
    if (isVisible) {
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
                containerColor = GasGuruTheme.colors.neutralWhite,
                contentColor = GasGuruTheme.colors.neutralBlack,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = RUikit.drawable.ic_my_location),
                    tint = GasGuruTheme.colors.textSubtle,
                    contentDescription = "User location",
                )
            }

            if (showRoutePlannerButton) {
                FloatingActionButton(
                    onClick = navigateToRoutePlanner,
                    modifier = modifier,
                    containerColor = GasGuruTheme.colors.primary100,
                    contentColor = GasGuruTheme.colors.neutralBlack,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Directions,
                        tint = GasGuruTheme.colors.textSubtle,
                        contentDescription = "Create route",
                    )
                }
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

    val brands = FuelStationBrandsType.entries
        .filter { it.value != FuelStationBrandsType.UNKNOWN.value }
        .sortedBy { it.value.lowercase() }

    when (filterType) {
        FilterType.Brand -> {
            FilterSheet(
                model = FilterSheetModel(
                    title = stringResource(R.string.filter_brand_title),
                    buttonText = stringResource(id = R.string.filter_button),
                    isMultiOption = true,
                    isMustSelection = false,
                    options = brands.map { it.value },
                    optionsSelected = filterUiState.filterBrand,
                    onDismiss = { showFilter() },
                    onSaveButton = { event(StationMapEvent.UpdateBrandFilter(it)) },
                    type = FilterSheetType.ICON,
                    iconMap = brands.associate {
                        it.value to it.toUiModel().iconRes
                    }
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

@Composable
internal fun calculateMaxSheetHeight(peekHeight: Dp): Dp {
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val systemBarsHeight = WindowInsets.systemBars.asPaddingValues().let {
        it.calculateTopPadding() + it.calculateBottomPadding()
    }
    val appNavigationBarHeight = 80.dp

    return with(density) {
        windowInfo.containerSize.height.toDp()
    } - systemBarsHeight - appNavigationBarHeight - peekHeight
}

@Composable
@ThemePreviews
private fun StationMapScreenPreview() {
    MyApplicationTheme {
        StationMapScreen(
            uiState = StationMapUiState(),
            filterUiState = FilterUiState(),
            tabState = SelectedTabUiState(),
            routePlanner = null,
            navigateToDetail = {}
        )
    }
}
