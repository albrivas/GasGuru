package com.albrivas.fuelpump.feature.detail_station.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme

@Composable
fun DetailStationScreenRoute(
    modifier: Modifier = Modifier,
    idStation: String?
) {
    DetailStationScreen(modifier = modifier, idStation = idStation)
}

@Composable
internal fun DetailStationScreen(
    modifier: Modifier = Modifier,
    idStation: String?
) {

}

@Preview(showBackground = true)
@Composable
private fun DetailStationPreview() {
    MyApplicationTheme {
        DetailStationScreen(idStation = "")
    }
}
