package com.albrivas.fuelpump.core.uikit.components.price

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.albrivas.fuelpump.core.uikit.R
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme

@Composable
fun PriceItem(model: PriceItemModel) = with(model) {
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(8.dp)),
            painter = painterResource(id = icon),
            contentScale = ContentScale.Crop,
            contentDescription = "Icon fuel"
        )
        Column(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        ) {
            Text(text = price, style = typography.bodySmall)
            Text(text = fuelName, style = typography.displaySmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PriceItemPreview() {
    MyApplicationTheme {
        PriceItem(
            model = PriceItemModel(
                icon = R.drawable.ic_gasoline_95,
                price = "1.456 €/L",
                fuelName = "Gasolina 95"
            )
        )
    }
}
