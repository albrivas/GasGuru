package com.albrivas.fuelpump.core.uikit.components.expandable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.albrivas.fuelpump.core.uikit.theme.FuelPumpTheme
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme
import com.albrivas.fuelpump.core.uikit.theme.Neutral300
import com.albrivas.fuelpump.core.uikit.theme.Neutral500
import com.albrivas.fuelpump.core.uikit.theme.Primary500
import com.albrivas.fuelpump.core.uikit.theme.TextMain
import com.albrivas.fuelpump.core.uikit.theme.TextSubtle

@Composable
fun InformationCardExpandable(model: InformationCardExpandableModel) = with(model) {
    var open by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color = Color.White)
            .border(1.dp, Neutral300, RoundedCornerShape(8.dp))
            .clickable { open = ! open }
            .padding(12.dp)
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (textGroup, image) = createRefs()

            Column(
                modifier = Modifier.constrainAs(textGroup) {
                    top.linkTo(image.top)
                    start.linkTo(parent.start)
                    end.linkTo(image.start)
                    bottom.linkTo(image.bottom)
                    width = Dimension.fillToConstraints
                }
            ) {
                Text(
                    text = title,
                    style = FuelPumpTheme.typography.smallRegular,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier,
                    color = TextSubtle
                )
                Text(
                    text = subtitle,
                    style = FuelPumpTheme.typography.smallRegular,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier,
                    color = Primary500
                )
            }

            IconButton(
                onClick = { open = !open },
                modifier = Modifier.constrainAs(image) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = if (open) {
                        Icons.Filled.KeyboardArrowUp
                    } else {
                        Icons.Filled.KeyboardArrowDown
                    },
                    contentDescription = "",
                    tint = Neutral500
                )
            }
        }
        AnimatedVisibility(open) {
            Text(
                text = description,
                style = FuelPumpTheme.typography.smallRegular,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier,
                color = TextMain
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InformationCardExpandablePreview() {
    MyApplicationTheme {
        InformationCardExpandable(
            model = InformationCardExpandableModel(
                title = "Opening hours",
                subtitle = "24 hours",
                description = "rdghdrtghderthg"
            )
        )
    }
}
