package com.albrivas.fuelpump.feature.detail_station.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.albrivas.fuelpump.core.model.data.FuelStation
import com.albrivas.fuelpump.core.model.data.previewFuelStationDomain
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme

@Composable
fun DetailStationScreenRoute(
    modifier: Modifier = Modifier,
    idStation: String?
) {
    DetailStationScreen(modifier = modifier, idStation = idStation)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DetailStationScreen(
    modifier: Modifier = Modifier,
    idStation: String?
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = { },
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Desplaza hacia arriba",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp)
            )
            Text(text = "Información de la dirección")
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Gasoline 95: 1,35€")

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Ir a la estación")
            }
        }
    }
}

@Preview()
@Composable
private fun DetailStationPreview() {
    MyApplicationTheme {
        DetailStationScreen(idStation = "")
    }
}
