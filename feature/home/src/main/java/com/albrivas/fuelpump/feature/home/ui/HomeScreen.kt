package com.albrivas.fuelpump.feature.home.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.albrivas.fuelpump.core.model.data.FuelStation
import com.albrivas.fuelpump.core.model.data.previewFuelStationDomain
import com.albrivas.fuelpump.core.uikit.components.alert.AlertTemplate
import com.albrivas.fuelpump.core.uikit.components.alert.AlertTemplateModel
import com.albrivas.fuelpump.core.uikit.theme.GrayBackground
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme
import com.albrivas.fuelpump.feature.detail_station.ui.DetailStationScreen
import com.albrivas.fuelpump.feature.home.R

@Composable
fun HomeScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    HomeScreen(modifier = modifier, uiState = state)
}


@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
) {

    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedStation by remember { mutableStateOf<FuelStation?>(null) }

    when (uiState) {
        HomeUiState.Error -> Unit
        HomeUiState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        is HomeUiState.Success -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(color = GrayBackground)
                    .padding(start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp)
                ) {
                    items(uiState.fuelStations) { item ->
                        FuelStationItem(item = item) { station ->
                            showBottomSheet = true
                            selectedStation = station
                        }
                    }
                }
            }
        }

        HomeUiState.DisableLocation -> AlertTemplate(model = AlertTemplateModel(
            animation = com.albrivas.fuelpump.core.ui.R.raw.enable_location,
            description = stringResource(id = R.string.location_disable_description),
            buttonText = stringResource(id = R.string.button_enable_location),
            onClick = { }
        ))
    }

    if (showBottomSheet)
        DetailStationScreen(
            onDismiss = { showBottomSheet = false; selectedStation = null },
            station = selectedStation
        )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        HomeScreen(uiState = HomeUiState.Success(listOf(previewFuelStationDomain())))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoadingPreview() {
    MyApplicationTheme {
        HomeScreen(uiState = HomeUiState.Loading)
    }
}
