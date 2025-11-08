package com.gasguru.feature.detail_station.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.gasguru.core.model.data.FuelStationBrandsType
import com.gasguru.core.model.data.previewFuelStationDomain
import com.gasguru.core.ui.iconTint
import com.gasguru.core.ui.toUiModel
import com.gasguru.core.uikit.components.information_card.InformationCard
import com.gasguru.core.uikit.components.information_card.InformationCardModel
import com.gasguru.core.uikit.components.loading.GasGuruLoading
import com.gasguru.core.uikit.components.loading.GasGuruLoadingModel
import com.gasguru.core.uikit.components.price.PriceItem
import com.gasguru.core.uikit.components.price.PriceItemModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import com.gasguru.feature.detail_station.R
import com.gasguru.feature.detail_station.formatSchedule
import com.gasguru.feature.detail_station.getTimeElapsedString

@Composable
internal fun DetailStationScreenRoute(
    onBack: () -> Unit,
    viewModel: DetailStationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.fuelStation.collectAsStateWithLifecycle()
    val staticMapUrl by viewModel.staticMapUrl.collectAsStateWithLifecycle()
    val lastUpdate by viewModel.lastUpdate.collectAsStateWithLifecycle()

    DetailStationScreen(
        uiState = uiState,
        staticMapUrl = staticMapUrl,
        lastUpdate = lastUpdate,
        onBack = onBack,
        onEvent = viewModel::onEvent
    )
}

@Composable
internal fun DetailStationScreen(
    uiState: DetailStationUiState,
    staticMapUrl: String?,
    lastUpdate: Long,
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
                model = GasGuruLoadingModel(color = GasGuruTheme.colors.primary800)
            )
        }
        is DetailStationUiState.Success -> {
            val stationState = rememberDetailStationState(uiState.stationModel)
            val context = LocalContext.current

            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    onEvent(DetailStationEvent.TogglePriceAlert(!stationState.hasPriceAlert))
                } else {
                    openNotificationSettings(context)
                }
            }

            Scaffold(
                topBar = {
                    HeaderStation(
                        stationState = stationState,
                        staticMapUrl = staticMapUrl,
                        onBack = onBack,
                        onPriceAlertClick = {
                            handlePriceAlertWithPermissions(
                                context = context,
                                stationState = stationState,
                                permissionLauncher = notificationPermissionLauncher,
                                onEvent = onEvent
                            )
                        },
                        onEvent = onEvent
                    )
                },
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = GasGuruTheme.colors.neutral100)
                        .padding(padding)
                ) {
                    DetailStationContent(
                        stationState = stationState,
                        address = uiState.address,
                        lastUpdate = lastUpdate,
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
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val context = LocalContext.current

        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (textGroup, image) = createRefs()

            Column(
                modifier = Modifier.constrainAs(textGroup) {
                    top.linkTo(image.top)
                    start.linkTo(parent.start)
                    end.linkTo(image.start)
                    bottom.linkTo(image.bottom)
                    width = Dimension.fillToConstraints
                }
            ) {
                Text(
                    text = stationState.formattedName,
                    style = GasGuruTheme.typography.h3,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.testTag("name-station"),
                    color = GasGuruTheme.colors.textMain
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.wrapContentHeight()
                ) {
                    Text(
                        text = stationState.formattedDistance,
                        style = GasGuruTheme.typography.baseRegular,
                        color = GasGuruTheme.colors.textSubtle,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.testTag("distance")
                    )
                    Text(
                        text = " · ",
                        style = GasGuruTheme.typography.baseRegular,
                        color = GasGuruTheme.colors.textSubtle
                    )
                    Text(
                        text = stationState.openCloseText,
                        style = GasGuruTheme.typography.baseRegular,
                        color = stationState.colorStationOpen,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = GasGuruTheme.colors.neutral300,
                        shape = CircleShape
                    )
                    .constrainAs(image) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                Image(
                    painter = painterResource(id = stationState.brandIcon),
                    contentDescription = "Fuel station brand",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                        .clip(CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        FuelTypes(fuelItems = stationState.fuelItems, lastUpdate = lastUpdate)
        Spacer(modifier = Modifier.height(24.dp))
        InformationStation(
            stationState = stationState,
            address = address,
            navigateToGoogleMaps = {
                startRoute(
                    context = context,
                    location = stationState.location
                )
            }
        )
    }
}

@Composable
fun FuelTypes(
    fuelItems: List<PriceItemModel>,
    lastUpdate: Long,
) {
    Column(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(id = R.string.fuel_types),
            style = GasGuruTheme.typography.h5,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = GasGuruTheme.colors.textMain
        )
        val height = calculateHeight(fuelItems.size)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
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
            color = GasGuruTheme.colors.textMain
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
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.station_detail),
            color = GasGuruTheme.colors.neutralBlack,
            style = GasGuruTheme.typography.h5,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        InformationCard(
            model = InformationCardModel(
                title = stringResource(id = R.string.schedule),
                subtitle = stationState.openCloseText,
                description = formatSchedule(stationState.schedule),
                type = InformationCardModel.InformationCardType.EXPANDABLE,
                subtitleColor = stationState.colorStationOpen
            )
        )
        InformationCard(
            model = InformationCardModel(
                title = stringResource(id = R.string.direction),
                subtitle = address ?: stationState.formattedDirection,
                icon = com.gasguru.core.uikit.R.drawable.ic_direction,
                onClick = navigateToGoogleMaps,
                type = InformationCardModel.InformationCardType.NONE,
                subtitleColor = GasGuruTheme.colors.textMain
            )
        )
    }
}

@Composable
fun HeaderStation(
    stationState: DetailStationState,
    staticMapUrl: String?,
    onBack: () -> Unit,
    onPriceAlertClick: () -> Unit,
    onEvent: (DetailStationEvent) -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(GasGuruTheme.colors.neutral300),
            model = coil.request.ImageRequest.Builder(LocalContext.current)
                .data(staticMapUrl)
                .crossfade(300)
                .memoryCacheKey("map_${stationState.idServiceStation}")
                .diskCacheKey("map_${stationState.idServiceStation}")
                .build(),
            contentDescription = "Detail station map",
            contentScale = ContentScale.FillBounds
        )
        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 16.dp)
                .clip(CircleShape),
            onClick = onBack,
            colors = IconButtonDefaults.iconButtonColors(containerColor = GasGuruTheme.colors.neutralWhite)
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
                .padding(end = 16.dp)
        ) {
            IconButton(
                modifier = Modifier
                    .clip(CircleShape)
                    .testTag("button_price_alert"),
                onClick = onPriceAlertClick,
                colors = IconButtonDefaults.iconButtonColors(containerColor = GasGuruTheme.colors.neutralWhite)
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
                onClick = { onEvent(DetailStationEvent.ToggleFavorite(!stationState.isFavorite)) },
                colors = IconButtonDefaults.iconButtonColors(containerColor = GasGuruTheme.colors.neutralWhite)
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

@SuppressLint("QueryPermissionsNeeded")
private fun startRoute(context: Context, location: Location) {
    val lat = location.latitude
    val lng = location.longitude

    val intents = mutableListOf<Intent>()

    intents.add(
        Intent(Intent.ACTION_VIEW).apply {
            data = "google.navigation:q=$lat,$lng".toUri()
            setPackage("com.google.android.apps.maps")
        }
    )

    val wazeIntent = Intent(Intent.ACTION_VIEW).apply {
        data = "waze://?ll=$lat,$lng&navigate=yes".toUri()
        setPackage("com.waze")
    }
    if (wazeIntent.resolveActivity(context.packageManager) != null) {
        intents.add(wazeIntent)
    }

    val chooserIntent =
        Intent.createChooser(
            intents.removeAt(0),
            context.getString(R.string.navigate_with)
        ).apply {
            putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toTypedArray())
        }
    context.startActivity(chooserIntent)
}

fun openNotificationSettings(context: Context) {
    val intent = Intent().apply {
        action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    }
    context.startActivity(intent)
}

fun handlePriceAlertWithPermissions(
    context: Context,
    stationState: DetailStationState,
    permissionLauncher: ActivityResultLauncher<String>,
    onEvent: (DetailStationEvent) -> Unit
) {
    val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }

    if (hasPermission) {
        onEvent(DetailStationEvent.TogglePriceAlert(!stationState.hasPriceAlert))
    } else {
        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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
                    brandStationBrandsType = FuelStationBrandsType.AZUL_OIL
                ).toUiModel(),
                address = null
            ),
            staticMapUrl = null,
            lastUpdate = 0,
            onEvent = {}
        )
    }
}
