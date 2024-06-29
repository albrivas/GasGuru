package com.albrivas.fuelpump.core.uikit.components.table

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme
import com.albrivas.fuelpump.core.uikit.utils.borderWithoutTopBorder

@Composable
fun FuelPriceTable(model: FuelPriceTableModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        TableHeader(model.headers)
        TableItem(model.rows)
    }
}

@Composable
private fun TableHeader(header: Pair<String, String>) {
    Row(
        modifier = Modifier
            .height(35.dp)
            .border(1.dp, Color.Black),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = header.first,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            style = MaterialTheme.typography.titleSmall
        )
        VerticalDivider(
            modifier = Modifier
                .width(1.dp), color = Color.Black
        )
        Text(
            text = header.second,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun TableItem(row: List<Pair<String, Double>>) {
    row.forEach { item ->
        Row(
            modifier = Modifier
                .height(30.dp)
                .borderWithoutTopBorder(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = item.first,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                style = MaterialTheme.typography.bodySmall
            )
            VerticalDivider(
                modifier = Modifier
                    .width(1.dp), color = Color.Black
            )
            Text(
                text = "${item.second}€",
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FuelPriceTablePreview() {
    MyApplicationTheme {
        FuelPriceTable(
            FuelPriceTableModel(
                headers = "Fuel" to "Price",
                rows = listOf("Gasoline 95" to 1.765, "Diesel" to 1.876)
            )
        )

    }
}

@Preview(showBackground = true)
@Composable
private fun TableItemPreview() {
    MyApplicationTheme {
        TableItem(listOf("Gasoline 95" to 1.876))
    }
}

@Preview(showBackground = true)
@Composable
private fun TableHeaderPreview() {
    MyApplicationTheme {
        TableHeader("Fuel" to "Price")
    }
}