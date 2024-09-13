package com.albrivas.fuelpump.feature.detail_station.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.albrivas.fuelpump.core.uikit.components.FuelPumpButton
import com.albrivas.fuelpump.core.uikit.components.chip.StatusChip
import com.albrivas.fuelpump.core.uikit.components.chip.StatusChipModel
import com.albrivas.fuelpump.core.uikit.components.price.PriceItem
import com.albrivas.fuelpump.core.uikit.components.text.InformationText
import com.albrivas.fuelpump.core.uikit.components.text.InformationTextModel
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme
import com.albrivas.fuelpump.core.uikit.theme.PriceCheap
import com.albrivas.fuelpump.core.uikit.theme.PriceExpensive
import com.albrivas.fuelpump.core.uikit.theme.YellowFavorite
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
    val context = LocalContext.current
    when (uiState) {
        DetailStationUiState.Error -> Unit
        DetailStationUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
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
                bottomBar = {
                    FuelPumpButton(
                        onClick = { startRoute(context, uiState.station.location) },
                        text = stringResource(id = R.string.go_station),
                        modifier = Modifier
                            .systemBarsPadding()
                            .padding(16.dp)
                            .testTag("button_go_station")
                    )
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.White)
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
            .background(color = Color.White)
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            val isOpen =
                stringResource(id = if (station.isStationOpen()) R.string.open else R.string.close)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.wrapContentWidth().testTag("name-station"),
                    text = station.brandStationName,
                    style = typography.titleSmall
                )
                Text(
                    modifier = Modifier.wrapContentWidth().testTag("distance"),
                    text = station.formatDistance(),
                    style = typography.displaySmall
                )
                StatusChip(
                    modifier = Modifier.testTag("status-station"),
                    model = StatusChipModel(
                        text = isOpen,
                        color = if (station.isStationOpen()) PriceCheap else PriceExpensive
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                color = Color.LightGray,
                thickness = 0.5.dp
            )
            InformationText(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .testTag("address"),
                model = InformationTextModel(
                    icon = R.drawable.ic_home,
                    title = stringResource(id = R.string.direction),
                    description = station.formatDirection()
                )
            )
            InformationText(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .testTag("calendar"),
                model = InformationTextModel(
                    icon = R.drawable.ic_calendar,
                    title = stringResource(id = R.string.schedule),
                    description = station.scheduleList.joinToString(separator = "\n")
                )
            )
            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(
                color = Color.LightGray,
                thickness = 0.5.dp
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
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        Image(
            modifier = Modifier
                .padding(start = 16.dp)
                .size(80.dp)
                .align(Alignment.BottomStart)
                .offset(y = 30.dp, x = 0.dp)
                .shadow(elevation = 8.dp, shape = CircleShape)
                .background(Color.White, shape = CircleShape),
            painter = painterResource(id = station.brandStationBrandsType.toBrandStationIcon()),
            contentDescription = "Detail station map",
            contentScale = androidx.compose.ui.layout.ContentScale.None
        )
        IconButton(
            modifier = Modifier
                .padding(end = 16.dp)
                .align(Alignment.BottomEnd)
                .offset(y = 30.dp, x = 0.dp)
                .size(48.dp)
                .shadow(elevation = 8.dp, shape = CircleShape)
                .background(Color.White, shape = CircleShape)
                .testTag("button_favorite"),
            onClick = { onFavoriteClick(!station.isFavorite) }
        ) {
            Icon(
                painter = painterResource(id = com.albrivas.fuelpump.core.uikit.R.drawable.ic_bookmark),
                contentDescription = "Mark as favorite",
                tint = if (station.isFavorite) YellowFavorite else Color.LightGray,
                modifier = Modifier
                    .size(24.dp)
                    .testTag("icon_favorite")
                    .semantics {
                        iconTint = if (station.isFavorite) YellowFavorite else Color.LightGray
                    }
            )
        }
        Icon(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 16.dp, top = 16.dp)
                .clickable { onBack() },
            imageVector = Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = "Back to map",
            tint = Color.Black,
        )
    }
}

@Preview(showBackground = true, apiLevel = 33)
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
