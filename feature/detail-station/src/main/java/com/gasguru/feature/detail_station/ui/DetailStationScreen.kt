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
import androidx.compose.ui.graphics.Color
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.gasguru.core.common.startRoute
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelStationBrandsType
import com.gasguru.core.model.data.previewFuelStationDomain
import com.gasguru.core.ui.iconTint
import com.gasguru.core.ui.toBrandStationIcon
import com.gasguru.core.uikit.components.information_card.InformationCard
import com.gasguru.core.uikit.components.information_card.InformationCardModel
import com.gasguru.core.uikit.components.loading.GasGuruLoading
import com.gasguru.core.uikit.components.loading.GasGuruLoadingModel
import com.gasguru.core.uikit.components.price.PriceItem
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
            val stationState = rememberDetailStationState(uiState.station)

            Scaffold(
                topBar = {
                    HeaderStation(
                        station = uiState.station,
                        staticMapUrl = staticMapUrl,
                        onBack = onBack,
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
                        station = uiState.station,
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
    station: FuelStation,
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
                    painter = painterResource(id = station.brandStationBrandsType.toBrandStationIcon()),
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
            station = station,
            address = address,
            isStationOpen = stationState.isOpen,
            colorStationOpen = stationState.colorStationOpen,
            navigateToGoogleMaps = { startRoute(context = context, location = station.location) }
        )
    }
}

@Composable
fun FuelTypes(
    fuelItems: List<com.gasguru.core.uikit.components.price.PriceItemModel>,
    lastUpdate: Long
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
    station: FuelStation,
    address: String?,
    isStationOpen: Boolean,
    colorStationOpen: Color,
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
        val textOpenClose = if (isStationOpen) {
            stringResource(id = R.string.open)
        } else {
            stringResource(id = R.string.close)
        }

        InformationCard(
            model = InformationCardModel(
                title = stringResource(id = R.string.schedule),
                subtitle = textOpenClose,
                description = formatSchedule(station.schedule),
                type = InformationCardModel.InformationCardType.EXPANDABLE,
                subtitleColor = colorStationOpen
            )
        )
        InformationCard(
            model = InformationCardModel(
                title = stringResource(id = R.string.direction),
                subtitle = address ?: station.formatDirection(),
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
    station: FuelStation,
    staticMapUrl: String?,
    onBack: () -> Unit,
    onEvent: (DetailStationEvent) -> Unit
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
                .memoryCacheKey("map_${station.idServiceStation}")
                .diskCacheKey("map_${station.idServiceStation}")
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
        IconButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(end = 16.dp)
                .clip(CircleShape)
                .testTag("button_favorite"),
            onClick = { onEvent(DetailStationEvent.ToggleFavorite(!station.isFavorite)) },
            colors = IconButtonDefaults.iconButtonColors(containerColor = GasGuruTheme.colors.neutralWhite)
        ) {
            val accentRed = GasGuruTheme.colors.accentRed
            val black = GasGuruTheme.colors.neutralBlack
            Icon(
                modifier = Modifier
                    .testTag("icon_favorite")
                    .semantics {
                        iconTint = if (station.isFavorite) accentRed else black
                    },
                imageVector = if (station.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite icon",
                tint = if (station.isFavorite) accentRed else black,
            )
        }
    }
}

@Composable
@ThemePreviews
private fun DetailStationPreview() {
    MyApplicationTheme {
        DetailStationScreen(
            uiState = DetailStationUiState.Success(
                station = previewFuelStationDomain().copy(
                    isFavorite = true,
                    schedule = "L-V: 06:00-22:00; S: 07:00-22:00; D: 08:00-22:00",
                    brandStationBrandsType = FuelStationBrandsType.AZUL_OIL
                ),
                address = null
            ),
            staticMapUrl = null,
            lastUpdate = 0,
            onEvent = {}
        )
    }
}