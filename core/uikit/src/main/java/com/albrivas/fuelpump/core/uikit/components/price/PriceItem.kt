package com.albrivas.fuelpump.core.uikit.components.price

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.albrivas.fuelpump.core.uikit.R
import com.albrivas.fuelpump.core.uikit.theme.FuelPumpTheme
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme
import com.albrivas.fuelpump.core.uikit.theme.Neutral300
import com.albrivas.fuelpump.core.uikit.theme.TextSubtle

@Composable
fun PriceItem(model: PriceItemModel) = with(model) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color = Color.White)
            .border(1.dp, Neutral300, RoundedCornerShape(8.dp))
            .padding(12.dp),
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
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        ) {
            Text(
                text = fuelName,
                style = FuelPumpTheme.typography.smallRegular,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = TextSubtle
            )
            Text(
                text = price,
                style = FuelPumpTheme.typography.baseRegular,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
