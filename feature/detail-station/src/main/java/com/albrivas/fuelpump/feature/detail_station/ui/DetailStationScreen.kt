package com.albrivas.fuelpump.feature.detail_station.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.albrivas.fuelpump.core.model.data.FuelStation
import com.albrivas.fuelpump.core.model.data.previewFuelStationDomain
import com.albrivas.fuelpump.core.ui.toBrandStationIcon
import com.albrivas.fuelpump.core.uikit.components.FuelPumpButton
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme
import com.albrivas.fuelpump.feature.detail_station.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailStationScreen(
    modifier: Modifier = Modifier,
    station: FuelStation?,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        modifier = modifier.fillMaxWidth(),
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
                .padding(top = 8.dp, start = 24.dp, end = 24.dp)
                .verticalScroll(scroll)
                .navigationBarsPadding()
        ) {
            Image(
                painter = painterResource(id = brandStationBrandsType.toBrandStationIcon()),
                contentDescription = "station image"
            )
            Text(
                text = "Distancia: ${formatDistance()}",
                style = MaterialTheme.typography.bodyLarge
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
                text = stringResource(id = R.string.go_station)
            )
        }
    }
}

private fun startRoute(context: Context, lat: Double, lng: Double) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data =
        Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng&mode=driving")
    ContextCompat.startActivity(context, intent, null)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DetailStationPreview() {
    MyApplicationTheme {
        ContentBottomSheet(station = previewFuelStationDomain())
    }
}
