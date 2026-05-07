package com.gasguru.feature.detail_station.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.gasguru.core.analytics.LocalAnalyticsHelper
import com.gasguru.core.model.data.FuelStationBrandsType
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.VehicleType
import com.gasguru.core.model.data.previewFuelStationDomain
import com.gasguru.core.ui.iconTint
import com.gasguru.core.ui.mapper.toPriceUiModel
import com.gasguru.core.ui.mapper.toUiModel
import com.gasguru.core.ui.models.FuelTypeUiModel
import com.gasguru.core.ui.review.rememberInAppReviewManager
import com.gasguru.core.uikit.components.GasGuruButton
import com.gasguru.core.uikit.components.fuel_type_chip.FuelTypeChipModel
import com.gasguru.core.uikit.components.icon.UiKitIcons
import com.gasguru.core.uikit.components.information_card.InformationCard
import com.gasguru.core.uikit.components.information_card.InformationCardModel
import com.gasguru.core.uikit.components.loading.GasGuruLoading
import com.gasguru.core.uikit.components.loading.GasGuruLoadingModel
import com.gasguru.core.uikit.components.number_wheel_picker.NumberWheelPicker
import com.gasguru.core.uikit.components.number_wheel_picker.NumberWheelPickerModel
import com.gasguru.core.uikit.components.price.PriceItem
import com.gasguru.core.uikit.components.price.PriceItemModel
import com.gasguru.core.uikit.components.pulse_dot.PulseDot
import com.gasguru.core.uikit.components.tank_cost_card.TankCostCard
import com.gasguru.core.uikit.components.tank_cost_card.TankCostCardModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import com.gasguru.feature.detail_station.analytics.trackInAppReviewCompleted
import com.gasguru.feature.detail_station.analytics.trackInAppReviewFailed
import com.gasguru.feature.detail_station.formatSchedule
import com.gasguru.feature.detail_station.generated.resources.Res
import com.gasguru.feature.detail_station.generated.resources.capacity_picker_confirm
import com.gasguru.feature.detail_station.generated.resources.capacity_picker_range
import com.gasguru.feature.detail_station.generated.resources.capacity_picker_title
import com.gasguru.feature.detail_station.generated.resources.direction
import com.gasguru.feature.detail_station.generated.resources.fuel_types
import com.gasguru.feature.detail_station.generated.resources.schedule
import com.gasguru.feature.detail_station.generated.resources.station_detail
import com.gasguru.feature.detail_station.generated.resources.vehicle_default_name
import com.gasguru.feature.detail_station.getTimeElapsedString
import com.gasguru.feature.detail_station.platform.rememberNavigateToMapsAction
import com.gasguru.feature.detail_station.platform.rememberNotificationPermissionRequester
import com.gasguru.feature.detail_station.platform.rememberShareAction
import com.gasguru.navigation.LocalNavigationManager
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun DetailStationScreenRoute(
    viewModel: DetailStationViewModel = koinViewModel(),
) {
    val navigationManager = LocalNavigationManager.current
    val uiState by viewModel.fuelStation.collectAsStateWithLifecycle()
    val staticMapUrl by viewModel.staticMapUrl.collectAsStateWithLifecycle()
    val lastUpdate by viewModel.lastUpdate.collectAsStateWithLifecycle()
    val vehicle by viewModel.vehicle.collectAsStateWithLifecycle()

    DetailStationScreen(
        uiState = uiState,
        staticMapUrl = staticMapUrl,
        lastUpdate = lastUpdate,
        vehicle = vehicle,
        onBack = { navigationManager.navigateBack() },
        onEvent = viewModel::onEvent,
    )
}

@Composable
internal fun DetailStationScreen(
    uiState: DetailStationUiState,
    staticMapUrl: String?,
    lastUpdate: Long,
    vehicle: Vehicle? = null,
    onBack: () -> Unit = {},
    onEvent: (DetailStationEvent) -> Unit = {},
) {
    when (uiState) {
        DetailStationUiState.Error -> {
            // Handle error state
        }

        DetailStationUiState.Loading -> {
            GasGuruLoading(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                model = GasGuruLoadingModel(color = GasGuruTheme.colors.primary800),
            )
        }

        is DetailStationUiState.Success -> {
            val stationState = rememberDetailStationState(station = uiState.stationModel, isOpen = uiState.isOpen)
            val shareText = stationState.buildShareText(address = uiState.address)

            val shareStation = rememberShareAction()
            val navigateToMaps = rememberNavigateToMapsAction()
            val requestNotificationPermission = rememberNotificationPermissionRequester(
                onPermissionGranted = {
                    onEvent(DetailStationEvent.TogglePriceAlert(!stationState.hasPriceAlert))
                },
            )

            Scaffold(
                topBar = {
                    HeaderStation(
                        stationState = stationState,
                        staticMapUrl = staticMapUrl,
                        onBack = onBack,
                        onShareClick = {
                            onEvent(DetailStationEvent.ShareStation)
                            shareStation(shareText)
                        },
                        onPriceAlertClick = { requestNotificationPermission() },
                        onEvent = onEvent,
                    )
                },
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = GasGuruTheme.colors.neutral100)
                        .padding(padding),
                ) {
                    DetailStationContent(
                        stationState = stationState,
                        address = uiState.address,
                        lastUpdate = lastUpdate,
                        vehicle = vehicle,
                        onUpdateTankCapacity = { newCapacity ->
                            onEvent(DetailStationEvent.UpdateTankCapacity(capacity = newCapacity))
                        },
                        onNavigateToMaps = { navigateToMaps(stationState.location) },
                    )
                }
            }
        }
    }
}

@Composable
fun DetailStationContent(
    stationState: DetailStationState,
    address: String?,
    lastUpdate: Long,
    vehicle: Vehicle? = null,
    onUpdateTankCapacity: (Int) -> Unit = {},
    onNavigateToMaps: () -> Unit = {},
) {
    var showCapacitySheet by remember { mutableStateOf(value = false) }

    if (showCapacitySheet && vehicle != null) {
        CapacityPickerSheet(
            initialCapacity = vehicle.tankCapacity,
            onDismiss = { showCapacitySheet = false },
            onConfirm = { newCapacity ->
                onUpdateTankCapacity(newCapacity)
                showCapacitySheet = false
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stationState.formattedName,
                    style = GasGuruTheme.typography.h3,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.testTag("name-station"),
                    color = GasGuruTheme.colors.textMain,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.wrapContentHeight().padding(top = 8.dp),
                ) {
                    DistanceBadge(
                        distance = stationState.formattedDistance,
                        modifier = Modifier.testTag("distance"),
                    )
                    StatusBadge(
                        text = stationState.openCloseText,
                        color = stationState.colorStationOpen,
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = GasGuruTheme.colors.neutral300,
                        shape = CircleShape,
                    ),
            ) {
                Image(
                    painter = painterResource(stationState.brandIcon),
                    contentDescription = "Fuel station brand",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                        .clip(CircleShape),
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        vehicle?.let { currentVehicle ->
            val fuelUiModel = FuelTypeUiModel.ALL_FUELS.find { it.type == currentVehicle.fuelType }
            val priceModel = currentVehicle.fuelType.toPriceUiModel(fuelStation = stationState.station.fuelStation)
            if (fuelUiModel != null && priceModel.hasPrice) {
                TankCostCard(
                    model = TankCostCardModel(
                        fuelTypeChip = FuelTypeChipModel(
                            nameRes = stringResource(fuelUiModel.translationRes),
                        ),
                        totalCost = "${formatPrice(priceModel.rawPrice * currentVehicle.tankCapacity)} €",
                        litres = "${currentVehicle.tankCapacity} L",
                        pricePerLitre = priceModel.formattedPrice,
                        vehicleName = currentVehicle.name.takeUnless { it.isNullOrBlank() }
                            ?: stringResource(Res.string.vehicle_default_name),
                        onEditClick = { showCapacitySheet = true },
                    ),
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        FuelTypes(fuelItems = stationState.fuelItems, lastUpdate = lastUpdate)
        Spacer(modifier = Modifier.height(24.dp))
        InformationStation(
            stationState = stationState,
            address = address,
            navigateToGoogleMaps = onNavigateToMaps,
        )
    }
}

private fun formatPrice(value: Double): String {
    val cents = (value * 100).toLong()
    val intPart = cents / 100
    val decPart = (cents % 100).toString().padStart(2, '0')
    return "$intPart.$decPart"
}

@Composable
fun FuelTypes(
    fuelItems: List<PriceItemModel>,
    lastUpdate: Long,
) {
    Column(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(Res.string.fuel_types),
            style = GasGuruTheme.typography.h5,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = GasGuruTheme.colors.textMain,
        )
        val height = calculateHeight(fuelItems.size)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(fuelItems) { item ->
                PriceItem(model = item)
            }
        }
        Text(
            text = getTimeElapsedString(lastUpdate),
            style = GasGuruTheme.typography.captionRegular,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = GasGuruTheme.colors.textMain,
        )
    }
}

fun calculateHeight(size: Int): Dp {
    val numRanges = (size + 1) / 2
    return (numRanges * 80).dp
}

@Composable
fun InformationStation(
    stationState: DetailStationState,
    address: String?,
    navigateToGoogleMaps: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(Res.string.station_detail),
            color = GasGuruTheme.colors.neutralBlack,
            style = GasGuruTheme.typography.h5,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        InformationCard(
            model = InformationCardModel(
                title = stringResource(Res.string.schedule),
                subtitle = stationState.openCloseText,
                description = formatSchedule(stationState.schedule),
                type = InformationCardModel.InformationCardType.EXPANDABLE,
                subtitleColor = stationState.colorStationOpen,
            ),
        )
        InformationCard(
            model = InformationCardModel(
                title = stringResource(Res.string.direction),
                subtitle = address ?: stationState.formattedDirection,
                icon = UiKitIcons.Direction,
                onClick = navigateToGoogleMaps,
                type = InformationCardModel.InformationCardType.NONE,
                subtitleColor = GasGuruTheme.colors.textMain,
            ),
        )
    }
}

@Composable
fun HeaderStation(
    stationState: DetailStationState,
    staticMapUrl: String?,
    onBack: () -> Unit,
    onShareClick: () -> Unit,
    onPriceAlertClick: () -> Unit,
    onEvent: (DetailStationEvent) -> Unit,
) {
    val reviewManager = rememberInAppReviewManager()
    val coroutineScope = rememberCoroutineScope()
    val analyticsHelper = LocalAnalyticsHelper.current

    Box(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(staticMapUrl)
                .memoryCacheKey("map_${stationState.idServiceStation}")
                .diskCacheKey("map_${stationState.idServiceStation}")
                .build(),
            contentDescription = "Detail station map",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(GasGuruTheme.colors.neutral300),
        )
        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 16.dp)
                .clip(CircleShape),
            onClick = onBack,
            colors = IconButtonDefaults.iconButtonColors(containerColor = GasGuruTheme.colors.neutralWhite),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "Back to map",
                tint = GasGuruTheme.colors.neutralBlack,
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(end = 16.dp),
        ) {
            IconButton(
                modifier = Modifier
                    .clip(CircleShape)
                    .testTag("button_share"),
                onClick = onShareClick,
                colors = IconButtonDefaults.iconButtonColors(containerColor = GasGuruTheme.colors.neutralWhite),
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share station",
                    tint = GasGuruTheme.colors.neutralBlack,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                modifier = Modifier
                    .clip(CircleShape)
                    .testTag("button_price_alert"),
                onClick = onPriceAlertClick,
                colors = IconButtonDefaults.iconButtonColors(containerColor = GasGuruTheme.colors.neutralWhite),
            ) {
                val accentBlue = GasGuruTheme.colors.primary600
                val black = GasGuruTheme.colors.neutralBlack
                Icon(
                    modifier = Modifier
                        .testTag("icon_price_alert")
                        .semantics {
                            iconTint = if (stationState.hasPriceAlert) accentBlue else black
                        },
                    imageVector = if (stationState.hasPriceAlert) Icons.Outlined.NotificationsActive else Icons.Outlined.Notifications,
                    contentDescription = "Price alert icon",
                    tint = if (stationState.hasPriceAlert) accentBlue else black,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                modifier = Modifier
                    .clip(CircleShape)
                    .testTag("button_favorite"),
                onClick = {
                    val wasNotFavorite = !stationState.isFavorite
                    onEvent(DetailStationEvent.ToggleFavorite(wasNotFavorite))

                    if (wasNotFavorite) {
                        coroutineScope.launch {
                            reviewManager?.launchReviewFlow(
                                onReviewCompleted = {
                                    analyticsHelper.trackInAppReviewCompleted()
                                },
                                onReviewFailed = { exception ->
                                    analyticsHelper.trackInAppReviewFailed(
                                        errorMessage = exception.message.orEmpty(),
                                    )
                                },
                            )
                        }
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(containerColor = GasGuruTheme.colors.neutralWhite),
            ) {
                val accentRed = GasGuruTheme.colors.accentRed
                val black = GasGuruTheme.colors.neutralBlack
                Icon(
                    modifier = Modifier
                        .testTag("icon_favorite")
                        .semantics {
                            iconTint = if (stationState.isFavorite) accentRed else black
                        },
                    imageVector = if (stationState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite icon",
                    tint = if (stationState.isFavorite) accentRed else black,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CapacityPickerSheet(
    initialCapacity: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    var currentCapacity by remember { mutableIntStateOf(value = initialCapacity) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = GasGuruTheme.colors.neutralWhite,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.capacity_picker_title),
                style = GasGuruTheme.typography.h5,
                color = GasGuruTheme.colors.neutralBlack,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.capacity_picker_range),
                style = GasGuruTheme.typography.captionRegular,
                color = GasGuruTheme.colors.neutral600,
            )
            Spacer(modifier = Modifier.height(16.dp))
            NumberWheelPicker(
                model = NumberWheelPickerModel(
                    min = 40,
                    max = 999,
                    initialValue = initialCapacity,
                    onValueChanged = { newCapacity -> currentCapacity = newCapacity },
                ),
                modifier = Modifier
                    .height(156.dp)
                    .fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))
            GasGuruButton(
                onClick = { onConfirm(currentCapacity) },
                text = stringResource(Res.string.capacity_picker_confirm),
            )
        }
    }
}

@Composable
private fun DistanceBadge(distance: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(GasGuruTheme.colors.neutral200)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = GasGuruTheme.colors.textSubtle,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = distance,
            style = GasGuruTheme.typography.smallBold,
            color = GasGuruTheme.colors.textSubtle,
            maxLines = 1,
        )
    }
}

@Composable
private fun StatusBadge(text: String, color: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        PulseDot(color = color)
        Text(
            text = text,
            style = GasGuruTheme.typography.smallBold,
            color = color,
            maxLines = 1,
        )
    }
}

@Composable
@ThemePreviews
private fun DetailStationPreview() {
    MyApplicationTheme {
        DetailStationScreen(
            uiState = DetailStationUiState.Success(
                stationModel = previewFuelStationDomain().copy(
                    isFavorite = true,
                    schedule = "L-V: 06:00-22:00; S: 07:00-22:00; D: 08:00-22:00",
                    brandStationBrandsType = FuelStationBrandsType.AZUL_OIL,
                ).toUiModel(),
                address = null,
                isOpen = true,
            ),
            staticMapUrl = null,
            lastUpdate = 0,
            vehicle = Vehicle(
                name = "Mi coche",
                fuelType = FuelType.GASOLINE_95,
                tankCapacity = 50,
                vehicleType = VehicleType.CAR,
                isPrincipal = true,
            ),
            onEvent = {},
        )
    }
}
