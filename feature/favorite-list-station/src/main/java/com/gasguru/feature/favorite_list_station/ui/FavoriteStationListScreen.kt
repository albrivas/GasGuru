package com.gasguru.feature.favorite_list_station.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.getPrice
import com.gasguru.core.ui.toBrandStationIcon
import com.gasguru.core.ui.toColor
import com.gasguru.core.uikit.components.alert.AlertTemplate
import com.gasguru.core.uikit.components.alert.AlertTemplateModel
import com.gasguru.core.uikit.components.fuelItem.FuelStationItem
import com.gasguru.core.uikit.components.fuelItem.FuelStationItemModel
import com.gasguru.core.uikit.theme.FuelPumpTheme
import com.gasguru.core.uikit.theme.Neutral100
import com.gasguru.core.uikit.theme.Neutral300
import com.gasguru.feature.favorite_list_station.R

@Composable
fun FavoriteListStationScreenRoute(
    navigateToDetail: (Int) -> Unit,
    viewModel: FavoriteListStationViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    FavoriteListStationScreen(
        uiState = state,
        navigateToDetail = navigateToDetail,
        checkLocationEnabled = viewModel::checkLocationEnabled,
    )
}

@Composable
internal fun FavoriteListStationScreen(
    uiState: FavoriteStationListUiState,
    navigateToDetail: (Int) -> Unit,
    checkLocationEnabled: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Neutral100)
            .padding(horizontal = 16.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (uiState) {
            FavoriteStationListUiState.Error -> Unit
            FavoriteStationListUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            is FavoriteStationListUiState.Favorites -> ListFuelStations(
                stations = uiState.favoriteStations,
                selectedFuel = uiState.userSelectedFuelType,
                navigateToDetail = navigateToDetail
            )

            FavoriteStationListUiState.DisableLocation -> AlertTemplate(
                model = AlertTemplateModel(
                    animation = com.gasguru.core.ui.R.raw.enable_location,
                    description = stringResource(id = R.string.location_disable_description),
                    buttonText = stringResource(id = R.string.button_enable_location),
                    onClick = checkLocationEnabled
                )
            )

            is FavoriteStationListUiState.EmptyFavorites -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(top = 64.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_empty_favorites),
                        contentDescription = "",
                        contentScale = ContentScale.None
                    )
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = R.string.empty_favorites),
                        style = FuelPumpTheme.typography.h4,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun ListFuelStations(
    modifier: Modifier = Modifier,
    stations: List<FuelStation>,
    selectedFuel: FuelType,
    navigateToDetail: (Int) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            modifier = Modifier,
            text = stringResource(id = R.string.favorites),
            style = FuelPumpTheme.typography.h5
        )
        LazyColumn(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Neutral300, RoundedCornerShape(8.dp))
                .background(color = Color.White)
                .wrapContentHeight(),
        ) {
            itemsIndexed(stations) { index, item ->
                FuelStationItem(
                    modifier = Modifier.testTag("item $index"),
                    model = FuelStationItemModel(
                        idServiceStation = item.idServiceStation,
                        icon = item.brandStationBrandsType.toBrandStationIcon(),
                        name = item.brandStationName,
                        distance = item.formatDistance(),
                        price = selectedFuel.getPrice(item),
                        index = index,
                        categoryColor = item.priceCategory.toColor(),
                        onItemClick = navigateToDetail
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun EmptyFavoritesPreview() {
    FavoriteListStationScreen(
        uiState = FavoriteStationListUiState.EmptyFavorites,
        navigateToDetail = {},
        checkLocationEnabled = {},
    )
}
