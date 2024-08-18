package com.albrivas.fuelpump.feature.detail_station.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
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
import com.albrivas.fuelpump.core.ui.getFuelPrices
import com.albrivas.fuelpump.core.ui.iconTint
import com.albrivas.fuelpump.core.ui.isStationOpen
import com.albrivas.fuelpump.core.ui.toBrandStationIcon
import com.albrivas.fuelpump.core.uikit.components.FuelPumpButton
import com.albrivas.fuelpump.core.uikit.components.table.FuelPriceTable
import com.albrivas.fuelpump.core.uikit.components.table.FuelPriceTableModel
import com.albrivas.fuelpump.core.uikit.components.text.InformationText
import com.albrivas.fuelpump.core.uikit.components.text.InformationTextModel
import com.albrivas.fuelpump.core.uikit.theme.YellowFavorite
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme
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
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = station.brandStationName,
                style = typography.titleMedium
            )
            InformationText(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .testTag("address"),
                model = InformationTextModel(
                    icon = R.drawable.ic_home,
                    title = station.formatDirection(),
                    description = "Direction of station icon"
                )
            )
            InformationText(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .testTag("country"),
                model = InformationTextModel(
                    icon = R.drawable.ic_flag,
                    title = station.municipality,
                    description = "Municipality of station icon"
                )
            )
            InformationText(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .testTag("distance"),
                model = InformationTextModel(
                    icon = R.drawable.ic_car,
                    title = station.formatDistance(),
                    description = "Distance station icon"
                )
            )
            InformationText(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .testTag("schedule"),
                model = InformationTextModel(
                    icon = R.drawable.ic_schedule,
                    title = stringResource(id = if (station.isStationOpen()) R.string.open else R.string.close),
                    description = "Icon state of station"
                )
            )
            InformationText(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .testTag("calendar"),
                model = InformationTextModel(
                    icon = R.drawable.ic_calendar,
                    title = station.scheduleList.joinToString(separator = "\n"),
                    description = "Schedule station icon"
                )
            )
            Spacer(modifier = Modifier.height(40.dp))
            FuelPriceTable(
                model = FuelPriceTableModel(
                    headers = stringResource(id = R.string.fuels) to stringResource(id = R.string.price),
                    rows = station.getFuelPrices()
                )
            )
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
                imageVector = Icons.Default.Star,
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

@Preview(showBackground = true)
@Composable
private fun DetailStationPreview() {
    MyApplicationTheme {
        DetailStationScreen(
            uiState = DetailStationUiState.Success(
                previewFuelStationDomain().copy(
                    isFavorite = true
                )
            )
        )
    }
}
