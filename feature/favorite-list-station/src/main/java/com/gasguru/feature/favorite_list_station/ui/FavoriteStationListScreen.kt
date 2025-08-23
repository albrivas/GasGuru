package com.gasguru.feature.favorite_list_station.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.previewFuelStationDomain
import com.gasguru.core.ui.getPrice
import com.gasguru.core.ui.models.FuelStationUiModel
import com.gasguru.core.ui.toColor
import com.gasguru.core.ui.toUiModel
import com.gasguru.core.uikit.components.alert.AlertTemplate
import com.gasguru.core.uikit.components.alert.AlertTemplateModel
import com.gasguru.core.uikit.components.fuelItem.FuelStationItem
import com.gasguru.core.uikit.components.fuelItem.FuelStationItemModel
import com.gasguru.core.uikit.components.loading.GasGuruLoading
import com.gasguru.core.uikit.components.loading.GasGuruLoadingModel
import com.gasguru.core.uikit.components.swipe.SwipeItem
import com.gasguru.core.uikit.components.swipe.SwipeItemModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import com.gasguru.feature.favorite_list_station.R

@Composable
fun FavoriteListStationScreenRoute(
    navigateToDetail: (Int) -> Unit,
    viewModel: FavoriteListStationViewModel = hiltViewModel(),
) {
    val state by viewModel.favoriteStations.collectAsStateWithLifecycle()
    FavoriteListStationScreen(
        uiState = state,
        navigateToDetail = navigateToDetail,
        event = viewModel::handleEvents
    )
}

@Composable
internal fun FavoriteListStationScreen(
    uiState: FavoriteStationListUiState,
    navigateToDetail: (Int) -> Unit,
    event: (FavoriteStationEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = GasGuruTheme.colors.neutral100)
            .padding(horizontal = 16.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (uiState) {
            FavoriteStationListUiState.Error -> Unit
            FavoriteStationListUiState.Loading -> {
                GasGuruLoading(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding(),
                    model = GasGuruLoadingModel(color = GasGuruTheme.colors.primary800)
                )
            }

            is FavoriteStationListUiState.Favorites -> ListFuelStations(
                stations = uiState.favoriteStations,
                selectedFuel = uiState.userSelectedFuelType,
                navigateToDetail = navigateToDetail,
                event = event
            )

            FavoriteStationListUiState.DisableLocation -> AlertTemplate(
                model = AlertTemplateModel(
                    animation = com.gasguru.core.ui.R.raw.enable_location,
                    description = stringResource(id = R.string.location_disable_description),
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
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_file_search),
                        contentDescription = null,
                        contentScale = ContentScale.Inside,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(224.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = R.string.empty_favorites_title),
                        style = GasGuruTheme.typography.h4,
                        textAlign = TextAlign.Center,
                        color = GasGuruTheme.colors.textMain
                    )
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = R.string.empty_favorites_subtitle),
                        style = GasGuruTheme.typography.baseRegular,
                        textAlign = TextAlign.Center,
                        color = GasGuruTheme.colors.textSubtle
                    )
                }
            }
        }
    }
}

@Composable
fun ListFuelStations(
    modifier: Modifier = Modifier,
    stations: List<FuelStationUiModel>,
    selectedFuel: FuelType,
    event: (FavoriteStationEvent) -> Unit,
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
            style = GasGuruTheme.typography.h5,
            color = GasGuruTheme.colors.textMain
        )
        LazyColumn(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, GasGuruTheme.colors.neutral300, RoundedCornerShape(8.dp))
                .background(color = GasGuruTheme.colors.neutralWhite)
                .wrapContentHeight()
                .testTag("favorite_list"),
        ) {
            itemsIndexed(
                items = stations,
                key = { _, item -> item.fuelStation.idServiceStation }
            ) { index, item ->
                SwipeItem(
                    modifier = Modifier.animateItem(),
                    model = SwipeItemModel(
                        enableDismissFromEndToStart = true,
                        enableDismissFromStartToEnd = true,
                        iconAnimated = com.gasguru.core.ui.R.raw.trash_animated,
                        backgroundColor = GasGuruTheme.colors.red500,
                        onClick = {
                            event(FavoriteStationEvent.RemoveFavoriteStation(item.fuelStation.idServiceStation))
                        },
                    ) {
                        FuelStationItem(
                            modifier = Modifier.testTag("item $index"),
                            model = FuelStationItemModel(
                                idServiceStation = item.fuelStation.idServiceStation,
                                icon = item.brandIcon,
                                name = item.formattedName,
                                distance = item.formattedDistance,
                                price = selectedFuel.getPrice(item.fuelStation),
                                index = index,
                                categoryColor = item.fuelStation.priceCategory.toColor(),
                                onItemClick = navigateToDetail
                            ),
                            isLastItem = index == stations.size - 1
                        )
                    }
                )
            }
        }
    }
}

@Composable
@ThemePreviews
fun EmptyFavoritesPreview() {
    MyApplicationTheme {
        FavoriteListStationScreen(
            uiState = FavoriteStationListUiState.EmptyFavorites,
            navigateToDetail = {},
            event = {}
        )
    }
}

@Composable
@ThemePreviews
fun FavoriteFuelStationsPreview() {
    MyApplicationTheme {
        FavoriteListStationScreen(
            uiState = FavoriteStationListUiState.Favorites(
                favoriteStations = listOf(previewFuelStationDomain().toUiModel()),
                userSelectedFuelType = FuelType.GASOLINE_95_E10
            ),
            navigateToDetail = {},
            event = {}
        )
    }
}
