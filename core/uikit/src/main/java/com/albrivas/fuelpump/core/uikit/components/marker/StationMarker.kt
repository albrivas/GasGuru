package com.albrivas.fuelpump.core.uikit.components.marker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.albrivas.fuelpump.core.uikit.R
import com.albrivas.fuelpump.core.uikit.shape.CircleArrowShape
import com.albrivas.fuelpump.core.uikit.theme.GrayLight

@Composable
fun StationMarker(model: StationMarkerModel) {
    Row(
        modifier = Modifier
            .clip(CircleArrowShape())
            .background(color = if (model.isSelected) Color.White else model.color)
            .border(
                width = if (model.isSelected) 2.dp else 1.dp,
                color = if (model.isSelected) model.color else GrayLight,
                shape = CircleArrowShape()
            )
            .padding(top = 4.dp, start = 4.dp, end = 4.dp, bottom = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(16.dp),
            painter = painterResource(id = model.icon),
            contentDescription = "Station icon",
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(text = model.price, style = MaterialTheme.typography.labelSmall)
    }
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
private fun StationMarkerPreview() {
    StationMarker(
        model = StationMarkerModel(
            icon = R.drawable.ic_logo_bp,
            price = "1.23 €",
            color = Color.Red,
            isSelected = false,
        )
    )
}
