package com.albrivas.fuelpump.core.uikit.components.marker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.albrivas.fuelpump.core.uikit.R
import com.albrivas.fuelpump.core.uikit.shape.CircleArrowShape
import com.albrivas.fuelpump.core.uikit.theme.FuelPumpTheme
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme

@Composable
fun StationMarker(model: StationMarkerModel) {
    Row(
        modifier = Modifier
            .clip(CircleArrowShape())
            .background(color = model.color)
            .padding(bottom = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .padding(start = 4.dp, top = 4.dp, bottom = 4.dp)
                .clip(CircleShape)
                .border(width = 1.dp, color = model.color.copy(0.50f), shape = CircleShape)
                .size(24.dp)
                .background(color = Color.White)

        ) {
            Image(
                modifier = Modifier.size(24.dp).padding(3.dp),
                painter = painterResource(id = model.icon),
                contentScale = ContentScale.Fit,
                contentDescription = "Station icon",
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${model.price} €/l",
            style = FuelPumpTheme.typography.smallBold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(top = 6.dp, end = 8.dp, bottom = 6.dp)
        )
    }
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
private fun StationMarkerPreview() {
    MyApplicationTheme {
        StationMarker(
            model = StationMarkerModel(
                icon = R.drawable.ic_logo_repsol,
                price = "1.235",
                color = Color.Red,
                isSelected = false,
            )
        )
    }
}
