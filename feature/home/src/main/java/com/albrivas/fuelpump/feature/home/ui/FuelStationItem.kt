package com.albrivas.fuelpump.feature.home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.albrivas.fuelpump.core.domain.model.FuelStationDomain
import com.albrivas.fuelpump.core.domain.model.previewFuelStationDomain
import com.albrivas.fuelpump.core.ui.MyApplicationTheme
import com.albrivas.fuelpump.core.uikit.icon.FuelStationIcons

@Composable
fun FuelStationItem(item: FuelStationDomain, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(),
        colors = CardDefaults.cardColors(),
        //backgroundColor = Color.White,
        shape = RoundedCornerShape(corner = CornerSize(16.dp))

    ) {
        Image(
            painter = painterResource(id = FuelStationIcons.Repsol),
            contentDescription = "Fuel station brand"
        )
        Row(
            modifier = Modifier
                .background(Color.Blue)
                .fillMaxWidth()
        ) {
            //PuppyImage(puppy)
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Black)
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = item.brandStationName, style = typography.labelMedium)
                Text(text = "${item.priceGasoline95_E5} €", style = typography.labelMedium)

            }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Black)
                    .fillMaxWidth()
            ) {
                Text(text = item.brandStationName, style = typography.labelMedium)
                Text(text = "${item.priceGasoline95_E5} €", style = typography.labelMedium)

            }
        }
    }
}

@Composable
@Preview
fun PreviewFuelItem() {
    MyApplicationTheme {
        FuelStationItem(item = previewFuelStationDomain())
    }
}
