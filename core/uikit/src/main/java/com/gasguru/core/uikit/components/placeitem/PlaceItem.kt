package com.gasguru.core.uikit.components.placeitem

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.Neutral300
import com.gasguru.core.uikit.utils.drawBottomDivider

@Composable
fun PlaceItem(model: PlaceItemModel) = with(model) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RectangleShape)
            .clickable { onClickItem() }
            .padding(horizontal = 12.dp)
            .drawBottomDivider()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color = Neutral300)
            ) {
                Image(
                    imageVector = icon,
                    contentDescription = "recent item",
                    colorFilter = ColorFilter.tint(Color.Black),
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.Center)
                )
            }
            Text(
                modifier = Modifier,
                text = name,
                style = GasGuruTheme.typography.baseRegular
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaceItemPreview() {
    MyApplicationTheme {
        PlaceItem(
            model = PlaceItemModel(
                id = "1",
                icon = Icons.Outlined.Schedule,
                name = "Talavera de la Reina, Spain",
                onClickItem = { })
        )
    }
}