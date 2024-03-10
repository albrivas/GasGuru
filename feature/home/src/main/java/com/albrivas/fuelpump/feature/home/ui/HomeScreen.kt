package com.albrivas.fuelpump.feature.home.ui


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.albrivas.fuelpump.core.model.data.FuelStation
import com.albrivas.fuelpump.core.model.data.previewFuelStationDomain
import com.albrivas.fuelpump.core.ui.toBrandStationIcon
import com.albrivas.fuelpump.core.uikit.components.FuelPumpButton
import com.albrivas.fuelpump.core.uikit.theme.GrayBackground
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme
import com.albrivas.fuelpump.feature.home.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@Composable
fun HomeScreenRoute(
    modifier: Modifier = Modifier,
    navigateToDetailStation: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    HomeScreen(modifier = modifier, uiState = state, navigateToDetailStation)
}


@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    navigateToDetailStation: (String) -> Unit = {}
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

        HomeUiState.DisableLocation -> Unit
    }

    if (showBottomSheet)
        DetailBottomSheet(
            onDismiss = { showBottomSheet = false; selectedStation = null },
            selectedStation
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailBottomSheet(onDismiss: () -> Unit, station: FuelStation?) {

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
    ) {
        station?.let {
            ContentBottomSheet(station = it)
        }
    }
}

@Composable
private fun ContentBottomSheet(station: FuelStation) {

    val scroll = rememberScrollState()
    val context = LocalContext.current

    with(station) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 24.dp, end = 24.dp, bottom = 32.dp)
                .verticalScroll(scroll)
        ) {
            Image(
                painter = painterResource(id = brandStationBrandsType.toBrandStationIcon()),
                contentDescription = "station image"
            )
            Text(text = "Dirección:", style = MaterialTheme.typography.bodyLarge)
            Text(text = direction.trim())
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Gasoline 95: $priceGasoline95_E5 €/L")
            Text(text = "Gasoline 98: $priceGasoline98_E5 €/L")
            Text(text = "Diesel: $priceGasoilA €/L")

            Spacer(modifier = Modifier.height(16.dp))
            FuelPumpButton(
                onClick = {
                    startRoute(context, location.latitude, location.longitude)
                },
                enabled = true,
                text = R.string.go_station
            )
        }
    }
}


private fun startRoute(context: Context, lat: Double, lng: Double) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data =
        Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng&mode=driving")
    startActivity(context, intent, null)
}

@Preview(showBackground = true)
@Composable
private fun ContentBottomSheetPreview() {
    ContentBottomSheet(previewFuelStationDomain())
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
