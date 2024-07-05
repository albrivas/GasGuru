package com.albrivas.fuelpump.feature.fuel_list_station.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.albrivas.fuelpump.core.model.data.FuelStation
import com.albrivas.fuelpump.core.model.data.FuelType
import com.albrivas.fuelpump.core.model.data.previewFuelStationDomain
import com.albrivas.fuelpump.core.uikit.components.alert.AlertTemplate
import com.albrivas.fuelpump.core.uikit.components.alert.AlertTemplateModel
import com.albrivas.fuelpump.core.uikit.theme.GrayBackground
import com.albrivas.fuelpump.feature.fuel_list_station.R

@Composable
fun FuelStationListScreenRoute(
    viewModel: FuelListStationViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    FuelStationListScreen(uiState = state, checkLocationEnabled = viewModel::checkLocationEnabled)
}

@Composable
internal fun FuelStationListScreen(
    uiState: FuelStationListUiState,
    checkLocationEnabled: () -> Unit,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedStation by remember { mutableStateOf<FuelStation?>(null) }

    when (uiState) {
        FuelStationListUiState.Error -> Unit
        FuelStationListUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .statusBarsPadding(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        is FuelStationListUiState.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = GrayBackground)
                    .padding(start = 16.dp, end = 16.dp)
                    .statusBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp)
                ) {
                    items(uiState.fuelStations) { item ->
                        FuelStationItem(
                            item = item,
                            userSelectedFuelType = uiState.userSelectedFuelType
                        ) { station ->
                            showBottomSheet = true
                            selectedStation = station
                        }
                    }
                }
            }
        }

        FuelStationListUiState.DisableLocation -> AlertTemplate(
            model = AlertTemplateModel(
                animation = com.albrivas.fuelpump.core.ui.R.raw.enable_location,
                description = stringResource(id = R.string.location_disable_description),
                buttonText = stringResource(id = R.string.button_enable_location),
                onClick = checkLocationEnabled
            )
        )
    }
}

@Preview
@Composable
fun FuelListStationScreenPreview() {
    FuelStationListScreen(
        uiState = FuelStationListUiState.Success(
            listOf(previewFuelStationDomain()),
            userSelectedFuelType = FuelType.GASOLINE_95
        ),
        checkLocationEnabled = {},
    )
}
