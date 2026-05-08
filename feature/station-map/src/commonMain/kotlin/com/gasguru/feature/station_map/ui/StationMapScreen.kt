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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Directions
import androidx.compose.material.icons.outlined.GppBad
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gasguru.core.components.searchbar.GasGuruSearchBar
import com.gasguru.core.components.searchbar.GasGuruSearchBarModel
import com.gasguru.core.model.data.FuelStationBrandsType
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.generated.resources.Res
import com.gasguru.core.ui.generated.resources.alert_location_rationale_description
import com.gasguru.core.ui.generated.resources.alert_location_rationale_primary_button
import com.gasguru.core.ui.generated.resources.alert_location_rationale_title
import com.gasguru.core.ui.generated.resources.alert_permission_denied_description
import com.gasguru.core.ui.generated.resources.alert_permission_denied_primary_button
import com.gasguru.core.ui.generated.resources.alert_permission_denied_title
import com.gasguru.core.ui.generated.resources.nearby_stations
import com.gasguru.core.ui.mapper.toStationListItems
import com.gasguru.core.ui.mapper.toUiModel
import com.gasguru.core.ui.models.FuelStationUiModel
import com.gasguru.core.uikit.components.alert.GasGuruAlertDialog
import com.gasguru.core.uikit.components.alert.GasGuruAlertDialogModel
import com.gasguru.core.uikit.components.chip.FilterType
import com.gasguru.core.uikit.components.chip.SelectableFilter
import com.gasguru.core.uikit.components.chip.SelectableFilterModel
import com.gasguru.core.uikit.components.drag_handle.DragHandle
import com.gasguru.core.uikit.components.filter_sheet.FilterSheet
import com.gasguru.core.uikit.components.filter_sheet.FilterSheetModel
import com.gasguru.core.uikit.components.filter_sheet.FilterSheetType
import com.gasguru.core.uikit.components.filterable_station_list.FilterableStationList
import com.gasguru.core.uikit.components.filterable_station_list.FilterableStationListModel
import com.gasguru.core.uikit.components.route_navigation_card.RouteNavigationCard
import com.gasguru.core.uikit.components.route_navigation_card.RouteNavigationCardModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import com.gasguru.feature.station_map.generated.resources.filter_brand
import com.gasguru.feature.station_map.generated.resources.filter_brand_number
import com.gasguru.feature.station_map.generated.resources.filter_brand_title
import com.gasguru.feature.station_map.generated.resources.filter_button
import com.gasguru.feature.station_map.generated.resources.filter_number_nearby
import com.gasguru.feature.station_map.generated.resources.filter_number_nearby_title
import com.gasguru.feature.station_map.generated.resources.filter_open_24
import com.gasguru.feature.station_map.generated.resources.filter_open_now
import com.gasguru.feature.station_map.generated.resources.filter_schedule
import com.gasguru.feature.station_map.generated.resources.route_error_message
import com.gasguru.feature.station_map.generated.resources.route_station_count
import com.gasguru.feature.station_map.generated.resources.sheet_button
import com.gasguru.feature.station_map.generated.resources.tab_distance
import com.gasguru.feature.station_map.generated.resources.tab_price
import com.gasguru.feature.station_map.platform.PlatformMapView
import com.gasguru.feature.station_map.platform.rememberLocationPermissionState
import com.gasguru.feature.station_map.ui.models.RouteUiModel
import com.gasguru.navigation.LocalDeepLinkStateHolder
import com.gasguru.navigation.LocalNavigationManager
import com.gasguru.navigation.manager.NavigationDestination
import com.gasguru.navigation.models.RoutePlanArgs
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import com.gasguru.feature.station_map.generated.resources.Res as StationMapRes

@Composable
fun StationMapScreenRoute(
    routePlanner: RoutePlanArgs?,
    onRoutePlanConsumed: () -> Unit = {},
    viewModel: StationMapViewModel = koinViewModel(),
) {
    val navigationManager = LocalNavigationManager.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val filterGroup by viewModel.filters.collectAsStateWithLifecycle()
    val tabState by viewModel.tabState.collectAsStateWithLifecycle()

    val deepLinkStateHolder = LocalDeepLinkStateHolder.current
    val pendingStationId by deepLinkStateHolder.pendingStationId.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = pendingStationId) {
        val stationId = pendingStationId ?: return@LaunchedEffect
        snapshotFlow { state.loading to state.mapStations }
            .first { (isLoading, stations) -> !isLoading && stations.isNotEmpty() }
        deepLinkStateHolder.clear()
        navigationManager.navigateTo(
            destination = NavigationDestination.DetailStation(
                idServiceStation = stationId,
                presentAsDialog = true,
            ),
        )
    }

    val permissionState = rememberLocationPermissionState()

    LaunchedEffect(permissionState.isGranted) {
        if (permissionState.isGranted) {
            viewModel.handleEvent(event = StationMapEvent.GetStationByCurrentLocation)
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val routeErrorMessage = stringResource(StationMapRes.string.route_error_message)

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                StationMapEffect.ShowRouteError ->
                    snackbarHostState.showSnackbar(message = routeErrorMessage)
            }
        }
    }

    if (!permissionState.isGranted) {
        if (permissionState.isDenied) {
            GasGuruAlertDialog(
                model = GasGuruAlertDialogModel(
                    icon = Icons.Outlined.GppBad,
                    iconTint = GasGuruTheme.colors.red500,
                    iconBackgroundColor = GasGuruTheme.colors.red500.copy(alpha = 0.2f),
                    title = stringResource(Res.string.alert_permission_denied_title),
                    description = stringResource(Res.string.alert_permission_denied_description),
                    primaryButtonText = stringResource(Res.string.alert_permission_denied_primary_button),
                ),
                onPrimaryButtonClick = permissionState.openSettings,
            )
        } else {
            GasGuruAlertDialog(
                model = GasGuruAlertDialogModel(
                    icon = Icons.Outlined.MyLocation,
                    iconTint = Color(0xFF3B82F6),
                    iconBackgroundColor = Color(0xFFEFF6FF),
                    title = stringResource(Res.string.alert_location_rationale_title),
                    description = stringResource(Res.string.alert_location_rationale_description),
                    primaryButtonText = stringResource(Res.string.alert_location_rationale_primary_button),
                ),
                onPrimaryButtonClick = permissionState.requestPermission,
            )
        }
        return
    }

    StationMapScreen(
        uiState = state,
        filterUiState = filterGroup,
        tabState = tabState,
        routePlanner = routePlanner,
        onRoutePlanConsumed = onRoutePlanConsumed,
        isLocationPermissionGranted = permissionState.isGranted,
        snackbarHostState = snackbarHostState,
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
    isLocationPermissionGranted: Boolean = false,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    event: (StationMapEvent) -> Unit = {},
    navigateToDetail: (Int) -> Unit = {},
    navigateToRoutePlanner: () -> Unit = {},
) = with(uiState) {
    val peekHeight = 60.dp
    var isSearchActive by remember { mutableStateOf(false) }
    val isRouteActive = route != null || (loading && routeDestinationName != null)

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

    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutine = rememberCoroutineScope()
    BottomSheetScaffold(
        sheetContainerColor = GasGuruTheme.colors.neutral100,
        sheetContentColor = GasGuruTheme.colors.neutral100,
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        sheetShadowElevation = 32.dp,
        sheetPeekHeight = if (isSearchActive) 0.dp else peekHeight,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetDragHandle = { DragHandle() },
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.nearby_stations),
                        style = GasGuruTheme.typography.baseBold,
                        color = GasGuruTheme.colors.textSubtle,
                    )
                    if (isSheetPartiallyExpanded(scaffoldState)) {
                        Text(
                            modifier = Modifier.clickable {
                                coroutine.launch {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            },
                            text = stringResource(StationMapRes.string.sheet_button),
                            style = GasGuruTheme.typography.baseRegular,
                            color = GasGuruTheme.colors.primary600,
                        )
                    }
                }
                FilterableStationList(
                    model = FilterableStationListModel(
                        stations = listStations.toStationListItems(selectedType ?: return@Column),
                        selectedTab = tabState.selectedTab.value,
                        onTabChange = { selectedTab ->
                            event(StationMapEvent.ChangeTab(selected = StationSortTab.fromValue(selectedTab)))
                        },
                        onStationClick = navigateToDetail,
                        swipeConfig = null,
                        testTag = "map_station_list",
                        tabNames = listOf(
                            stringResource(StationMapRes.string.tab_price),
                            stringResource(StationMapRes.string.tab_distance),
                        ),
                    )
                )
            }
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = GasGuruTheme.colors.neutral300),
            ) {
                PlatformMapView(
                    stations = mapStations,
                    route = route,
                    selectedStationId = selectedStationId,
                    userSelectedFuelType = selectedType,
                    loading = loading,
                    isLocationPermissionGranted = isLocationPermissionGranted,
                    mapBounds = mapBounds,
                    shouldCenterMap = shouldCenterMap,
                    userLocationToCenter = userLocationToCenter,
                    onStationClick = { stationId ->
                        event(StationMapEvent.SelectStation(stationId = stationId))
                        navigateToDetail(stationId)
                    },
                    onMapCentered = { event(StationMapEvent.OnMapCentered) },
                    onUserLocationCentered = { event(StationMapEvent.OnUserLocationCentered) },
                    modifier = Modifier.fillMaxSize(),
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
                                    StationMapRes.string.route_station_count,
                                    mapStations.size,
                                ),
                                distance = route?.distanceText,
                                duration = route?.durationText,
                                onClose = { event(StationMapEvent.CancelRoute) },
                            ),
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 56.dp,
                                bottom = 16.dp,
                            ),
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
                onClick = { event(StationMapEvent.GetStationByCurrentLocation) },
                modifier = modifier,
                shape = CircleShape,
                containerColor = GasGuruTheme.colors.neutralWhite,
                contentColor = GasGuruTheme.colors.neutralBlack,
            ) {
                Icon(
                    painter = org.jetbrains.compose.resources.painterResource(
                        com.gasguru.core.uikit.components.icon.UiKitIcons.MyLocation
                    ),
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
            event = event,
        )
    }
}

@Composable
private fun openingHoursLabel(openingHours: FilterUiState.OpeningHours): String =
    stringResource(
        when (openingHours) {
            FilterUiState.OpeningHours.NONE -> StationMapRes.string.filter_schedule
            FilterUiState.OpeningHours.OPEN_NOW -> StationMapRes.string.filter_open_now
            FilterUiState.OpeningHours.OPEN_24_H -> StationMapRes.string.filter_open_24
        }
    )

@Composable
private fun getFiltersModel(
    filterUiState: FilterUiState,
    onFilterClick: (FilterType) -> Unit,
): List<SelectableFilterModel> = listOf(
    SelectableFilterModel(
        filterType = FilterType.NumberOfStations,
        label = stringResource(StationMapRes.string.filter_number_nearby),
        selectedLabel = stringResource(StationMapRes.string.filter_number_nearby),
        isSelected = true,
        onFilterClick = { onFilterClick(it) },
    ),
    SelectableFilterModel(
        filterType = FilterType.Brand,
        label = stringResource(StationMapRes.string.filter_brand),
        selectedLabel = stringResource(StationMapRes.string.filter_brand_number, filterUiState.filterBrand.size),
        isSelected = filterUiState.filterBrand.isNotEmpty(),
        onFilterClick = { onFilterClick(it) },
    ),
    SelectableFilterModel(
        filterType = FilterType.Schedule,
        label = stringResource(StationMapRes.string.filter_schedule),
        selectedLabel = openingHoursLabel(filterUiState.filterSchedule),
        isSelected = filterUiState.filterSchedule != FilterUiState.OpeningHours.NONE,
        onFilterClick = { onFilterClick(it) },
    ),
)

@Composable
fun ShowFilterSheet(
    filterType: FilterType,
    filterUiState: FilterUiState,
    showFilter: () -> Unit,
    event: (StationMapEvent) -> Unit,
) {
    val brands = FuelStationBrandsType.entries
        .filter { it.value != FuelStationBrandsType.UNKNOWN.value }
        .sortedBy { it.value.lowercase() }

    when (filterType) {
        FilterType.Brand -> {
            FilterSheet(
                model = FilterSheetModel(
                    title = stringResource(StationMapRes.string.filter_brand_title),
                    buttonText = stringResource(StationMapRes.string.filter_button),
                    isMultiOption = true,
                    isMustSelection = false,
                    options = brands.map { it.value },
                    optionsSelected = filterUiState.filterBrand,
                    onDismiss = { showFilter() },
                    onSaveButton = { event(StationMapEvent.UpdateBrandFilter(it)) },
                    type = FilterSheetType.ICON,
                    iconMap = brands.associate { it.value to it.toUiModel().iconRes },
                )
            )
        }

        FilterType.NumberOfStations -> {
            FilterSheet(
                model = FilterSheetModel(
                    title = stringResource(StationMapRes.string.filter_number_nearby_title),
                    buttonText = stringResource(StationMapRes.string.filter_button),
                    isMultiOption = false,
                    isMustSelection = true,
                    options = listOf("10", "15", "20", "25"),
                    optionsSelected = listOf(filterUiState.filterStationsNearby.toString()),
                    onDismiss = { showFilter() },
                    onSaveButton = { event(StationMapEvent.UpdateNearbyFilter(it.first())) },
                )
            )
        }

        FilterType.Schedule -> {
            val openNowLabel = stringResource(StationMapRes.string.filter_open_now)
            val open24hLabel = stringResource(StationMapRes.string.filter_open_24)
            val labelToSchedule = remember(openNowLabel, open24hLabel) {
                mapOf(
                    openNowLabel to FilterUiState.OpeningHours.OPEN_NOW,
                    open24hLabel to FilterUiState.OpeningHours.OPEN_24_H,
                )
            }
            val currentSelectedLabel = if (filterUiState.filterSchedule == FilterUiState.OpeningHours.NONE) {
                emptyList()
            } else {
                listOf(openingHoursLabel(filterUiState.filterSchedule))
            }
            FilterSheet(
                model = FilterSheetModel(
                    title = stringResource(StationMapRes.string.filter_schedule),
                    buttonText = stringResource(StationMapRes.string.filter_button),
                    isMultiOption = false,
                    isMustSelection = false,
                    options = labelToSchedule.keys.toList(),
                    optionsSelected = currentSelectedLabel,
                    onDismiss = { showFilter() },
                    onSaveButton = { selectedLabels ->
                        val schedule = if (selectedLabels.isEmpty()) {
                            FilterUiState.OpeningHours.NONE
                        } else {
                            labelToSchedule[selectedLabels.first()] ?: FilterUiState.OpeningHours.NONE
                        }
                        event(StationMapEvent.UpdateScheduleFilter(schedule))
                    },
                )
            )
        }
    }
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
            isLocationPermissionGranted = false,
            navigateToDetail = {},
        )
    }
}
