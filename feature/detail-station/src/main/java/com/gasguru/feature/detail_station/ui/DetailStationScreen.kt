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
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.gasguru.core.common.CommonUtils.isStationOpen
import com.gasguru.core.common.generateStaticMapUrl
import com.gasguru.core.common.startRoute
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelStationBrandsType
import com.gasguru.core.model.data.previewFuelStationDomain
import com.gasguru.core.ui.getFuelPriceItems
import com.gasguru.core.ui.iconTint
import com.gasguru.core.ui.toBrandStationIcon
import com.gasguru.core.uikit.components.information_card.InformationCard
import com.gasguru.core.uikit.components.information_card.InformationCardModel
import com.gasguru.core.uikit.components.loading.GasGuruLoading
import com.gasguru.core.uikit.components.loading.GasGuruLoadingModel
import com.gasguru.core.uikit.components.price.PriceItem
import com.gasguru.core.uikit.theme.AccentRed
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.Neutral100
import com.gasguru.core.uikit.theme.Neutral300
import com.gasguru.core.uikit.theme.Primary500
import com.gasguru.core.uikit.theme.Primary800
import com.gasguru.core.uikit.theme.TextSubtle
import com.gasguru.feature.detail_station.BuildConfig
import com.gasguru.feature.detail_station.R
import com.gasguru.feature.detail_station.formatSchedule
import com.gasguru.feature.detail_station.getTimeElapsedString

@Composable
internal fun DetailStationScreenRoute(
    onBack: () -> Unit,
    viewModel: DetailStationViewModel = hiltViewModel(),
) {
    val state by viewModel.fuelStation.collectAsStateWithLifecycle()
    val lastUpdate by viewModel.lastUpdate.collectAsStateWithLifecycle()
    DetailStationScreen(
        uiState = state,
        lastUpdate = lastUpdate,
        onBack = onBack,
        onFavoriteClick = viewModel::onFavoriteClick
    )
}

@Composable
internal fun DetailStationScreen(
    uiState: DetailStationUiState,
    lastUpdate: Long,
    onBack: () -> Unit = {},
    onFavoriteClick: (Boolean) -> Unit = {},
) {
    when (uiState) {
        DetailStationUiState.Error -> Unit
        DetailStationUiState.Loading -> {
            GasGuruLoading(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                model = GasGuruLoadingModel(color = Primary800)
            )
        }

        is DetailStationUiState.Success -> {
            Scaffold(
                topBar = {
                    HeaderStation(
                        station = uiState.station,
                        onBack = onBack,
                        onFavoriteClick = onFavoriteClick
                    )
                },
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Neutral100)
                        .padding(padding)
                ) {
                    DetailStationContent(
                        station = uiState.station,
                        lastUpdate = lastUpdate,
                        address = uiState.address
                    )
                }
            }
        }
    }
}

@Composable
fun DetailStationContent(station: FuelStation, lastUpdate: Long, address: String?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val context = LocalContext.current
        val isOpen = if (station.isStationOpen()) "Open" else "Closed"
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
                    text = station.brandStationName.toLowerCase(Locale.current)
                        .replaceFirstChar {
                            if (it.isLowerCase()) {
                                it.titlecase(
                                    java.util.Locale.getDefault()
                                )
                            } else {
                                it.toString()
                            }
                        },
                    style = GasGuruTheme.typography.h3,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.testTag("name-station")
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.wrapContentHeight()
                ) {
                    Text(
                        text = station.formatDistance(),
                        style = GasGuruTheme.typography.baseRegular,
                        color = TextSubtle,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.testTag("distance")
                    )
                    Text(
                        text = " · ",
                        style = GasGuruTheme.typography.baseRegular,
                        color = TextSubtle
                    )
                    Text(
                        text = isOpen,
                        style = GasGuruTheme.typography.baseRegular,
                        color = if (station.isStationOpen()) Primary500 else AccentRed,
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
                        color = Neutral300,
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
        FuelTypes(station = station, lastUpdate = lastUpdate)
        Spacer(modifier = Modifier.height(24.dp))
        InformationStation(
            station = station,
            address = address,
            navigateToGoogleMaps = { startRoute(context = context, location = station.location) }
        )
    }
}

@Composable
fun FuelTypes(station: FuelStation, lastUpdate: Long) {
    Column(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(id = R.string.fuel_types),
            style = GasGuruTheme.typography.h5,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        val fuelItems = station.getFuelPriceItems()
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
            overflow = TextOverflow.Ellipsis
        )
    }
}

fun calculateHeight(size: Int): Dp {
    val numRanges = (size + 1) / 2
    return (numRanges * 80).dp
}

@Composable
fun InformationStation(station: FuelStation, address: String?, navigateToGoogleMaps: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(id = R.string.station_detail),
            style = GasGuruTheme.typography.h5,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        val textOpenClose = if (station.isStationOpen()) {
            stringResource(
                id = R.string.open
            )
        } else {
            stringResource(id = R.string.close)
        }

        InformationCard(
            model = InformationCardModel(
                title = stringResource(id = R.string.schedule),
                subtitle = textOpenClose,
                description = formatSchedule(station.schedule),
                type = InformationCardModel.InformationCardType.EXPANDABLE,
                subtitleColor = if (station.isStationOpen()) Primary500 else AccentRed
            )
        )
        InformationCard(
            model = InformationCardModel(
                title = stringResource(id = R.string.direction),
                subtitle = address ?: station.formatDirection(),
                icon = com.gasguru.core.uikit.R.drawable.ic_direction,
                onClick = navigateToGoogleMaps,
                type = InformationCardModel.InformationCardType.NONE
            )
        )
    }
}

@Composable
fun HeaderStation(station: FuelStation, onBack: () -> Unit, onFavoriteClick: (Boolean) -> Unit) {
    val staticMapUrl = generateStaticMapUrl(
        location = station.location,
        zoom = 17,
        width = 400,
        height = 240,
        apiKey = BuildConfig.googleApiKey
    )
    Box(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(Color.Gray),
            model = staticMapUrl,
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
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "Back to map",
                tint = Color.Black,
            )
        }
        IconButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(end = 16.dp)
                .clip(CircleShape)
                .testTag("button_favorite"),
            onClick = { onFavoriteClick(!station.isFavorite) },
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White)
        ) {
            Icon(
                modifier = Modifier
                    .testTag("icon_favorite")
                    .semantics {
                        iconTint = if (station.isFavorite) AccentRed else Color.Black
                    },
                imageVector = if (station.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite icon",
                tint = if (station.isFavorite) AccentRed else Color.Black,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailStationPreview() {
    MyApplicationTheme {
        DetailStationScreen(
            uiState = DetailStationUiState.Success(
                previewFuelStationDomain().copy(
                    isFavorite = true,
                    schedule = "L-V: 06:00-22:00; S: 07:00-22:00; D: 08:00-22:00",
                    brandStationBrandsType = FuelStationBrandsType.AZUL_OIL
                ),
                address = null
            ),
            lastUpdate = 0
        )
    }
}
