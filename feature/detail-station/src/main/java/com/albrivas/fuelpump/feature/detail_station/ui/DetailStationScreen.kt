package com.albrivas.fuelpump.feature.detail_station.ui

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.albrivas.fuelpump.core.common.generateStaticMapUrl
import com.albrivas.fuelpump.core.common.startRoute
import com.albrivas.fuelpump.core.model.data.FuelStation
import com.albrivas.fuelpump.core.model.data.previewFuelStationDomain
import com.albrivas.fuelpump.core.ui.getFuelPriceItems
import com.albrivas.fuelpump.core.ui.iconTint
import com.albrivas.fuelpump.core.ui.isStationOpen
import com.albrivas.fuelpump.core.ui.toBrandStationIcon
import com.albrivas.fuelpump.core.uikit.components.expandable.InformationCardExpandable
import com.albrivas.fuelpump.core.uikit.components.expandable.InformationCardExpandableModel
import com.albrivas.fuelpump.core.uikit.components.information_card.InformationCard
import com.albrivas.fuelpump.core.uikit.components.information_card.InformationCardModel
import com.albrivas.fuelpump.core.uikit.components.price.PriceItem
import com.albrivas.fuelpump.core.uikit.theme.AccentRed
import com.albrivas.fuelpump.core.uikit.theme.FuelPumpTheme
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme
import com.albrivas.fuelpump.core.uikit.theme.Neutral100
import com.albrivas.fuelpump.core.uikit.theme.Neutral300
import com.albrivas.fuelpump.core.uikit.theme.Primary500
import com.albrivas.fuelpump.core.uikit.theme.TextSubtle
import com.albrivas.fuelpump.feature.detail_station.BuildConfig
import com.albrivas.fuelpump.feature.detail_station.R

@Composable
internal fun DetailStationScreenRoute(
    onBack: () -> Unit,
    viewModel: DetailStationViewModel = hiltViewModel(),
) {
    val state by viewModel.fuelStation.collectAsStateWithLifecycle()
    DetailStationScreen(
        uiState = state,
        onBack = onBack,
        onFavoriteClick = viewModel::onFavoriteClick
    )
}

@Composable
internal fun DetailStationScreen(
    uiState: DetailStationUiState,
    onBack: () -> Unit = {},
    onFavoriteClick: (Boolean) -> Unit = {},
) {
    when (uiState) {
        DetailStationUiState.Error -> Unit
        DetailStationUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag("loading")
                )
            }
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
                    DetailStationContent(station = uiState.station)
                }
            }
        }
    }
}

@Composable
fun DetailStationContent(station: FuelStation) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
                    style = FuelPumpTheme.typography.h3,
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
                        style = FuelPumpTheme.typography.baseRegular,
                        color = TextSubtle,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.testTag("distance")
                    )
                    Text(
                        text = " · ",
                        style = FuelPumpTheme.typography.baseRegular,
                        color = TextSubtle
                    )
                    Text(
                        text = isOpen,
                        style = FuelPumpTheme.typography.baseRegular,
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
                        width = 1.dp,
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
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        FuelTypes(station = station)
        Spacer(modifier = Modifier.height(24.dp))
        InformationStation(
            station = station,
            navigateToGoogleMaps = { startRoute(context = context, location = station.location) }
        )
    }
}

@Composable
fun FuelTypes(station: FuelStation) {
    Text(
        text = stringResource(id = R.string.fuel_types),
        style = FuelPumpTheme.typography.h5,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
    val fuelItems = station.getFuelPriceItems()
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(fuelItems) { item ->
            PriceItem(model = item)
        }
    }
}

@Composable
fun InformationStation(station: FuelStation, navigateToGoogleMaps: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(id = R.string.station_detail),
            style = FuelPumpTheme.typography.h5,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        InformationCardExpandable(
            model = InformationCardExpandableModel(
                title = stringResource(id = R.string.schedule),
                subtitle = if (station.isStationOpen()) "Open" else "Close",
                description = station.scheduleList.joinToString(separator = "\n")
            )
        )
        InformationCard(
            model = InformationCardModel(
                title = stringResource(id = R.string.direction),
                description = station.formatDirection(),
                icon = com.albrivas.fuelpump.core.uikit.R.drawable.ic_direction,
                onClick = navigateToGoogleMaps
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
        apiKey = BuildConfig.staticMapApiKey
    )
    Box(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(Color.Gray),
            model = staticMapUrl,
            contentDescription = "Detail station map",
            contentScale = ContentScale.Crop
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
                modifier = Modifier.testTag("icon_favorite").semantics {
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
                    schedule = "L-V: 06:00-22:00; S: 07:00-22:00; D: 08:00-22:00"
                )
            )
        )
    }
}
