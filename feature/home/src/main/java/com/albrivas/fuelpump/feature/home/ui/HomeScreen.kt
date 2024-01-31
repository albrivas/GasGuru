package com.albrivas.fuelpump.feature.home.ui


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.albrivas.fuelpump.core.model.data.previewFuelStationDomain
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme

@Composable
fun HomeScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    HomeScreen(modifier = modifier, uiState = viewModel.state)
}


@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            when (uiState) {
                HomeUiState.Error,
                HomeUiState.Loading -> Unit

                is HomeUiState.Success -> {
                    items(uiState.fuelStations) { item ->
                        FuelStationItem(item = item)
                    }
                }
            }
        }
    }
}

// Previews

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        HomeScreen(uiState = HomeUiState.Success(listOf(previewFuelStationDomain())))
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun PortraitPreview() {
    MyApplicationTheme {
        HomeScreen(uiState = HomeUiState.Success(listOf(previewFuelStationDomain())))
    }
}
