package com.gasguru.core.uikit.components.information_card

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.gasguru.core.uikit.R
import com.gasguru.core.uikit.theme.FuelPumpTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.Neutral300
import com.gasguru.core.uikit.theme.Neutral500
import com.gasguru.core.uikit.theme.Primary500
import com.gasguru.core.uikit.theme.TextMain
import com.gasguru.core.uikit.theme.TextSubtle

@Composable
fun InformationCard(model: InformationCardModel) = with(model) {
    var open by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color = Color.White)
            .border(1.dp, Neutral300, RoundedCornerShape(8.dp))
            .clickable {
                if (type == InformationCardModel.InformationCardType.EXPANDABLE) {
                    open = !open
                } else {
                    onClick()
                }
            }
            .padding(12.dp),
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            val (textGroup, image) = createRefs()

            Column(
                modifier = Modifier.constrainAs(textGroup) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(image.start)
                    width = Dimension.fillToConstraints
                }
            ) {
                Text(
                    text = title,
                    style = FuelPumpTheme.typography.smallRegular,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = TextSubtle
                )
                Text(
                    text = subtitle,
                    style = FuelPumpTheme.typography.smallRegular,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = if (type == InformationCardModel.InformationCardType.EXPANDABLE) Primary500 else TextMain
                )
            }

            when (type) {
                InformationCardModel.InformationCardType.NONE -> {
                    icon?.let {
                        Image(
                            modifier = Modifier
                                .constrainAs(image) {
                                    top.linkTo(parent.top)
                                    end.linkTo(parent.end)
                                    bottom.linkTo(parent.bottom)
                                }
                                .clip(CircleShape)
                                .clickable { onClick() }
                                .padding(start = 12.dp),
                            painter = painterResource(id = icon),
                            contentDescription = "Icon direction"
                        )
                    }
                }

                InformationCardModel.InformationCardType.EXPANDABLE -> {
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .clickable { open = !open }
                            .constrainAs(image) {
                                top.linkTo(parent.top)
                                end.linkTo(parent.end)
                                bottom.linkTo(textGroup.bottom)
                            },
                        imageVector = if (open) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Neutral500
                    )
                }
            }
        }

        description?.let {
            AnimatedVisibility(visible = open) {
                Text(
                    text = description,
                    style = FuelPumpTheme.typography.smallRegular,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp),
                    color = TextMain
                )
            }
        }
    }
}

@Preview
@Composable
private fun InformationCardDefaultPreview() {
    MyApplicationTheme {
        InformationCard(
            model = InformationCardModel(
                title = "Direccion",
                subtitle = "Avenida de la constitucion 1, 10D",
                icon = R.drawable.ic_direction,
                onClick = {},
                type = InformationCardModel.InformationCardType.NONE
            )
        )
    }
}

@Preview
@Composable
private fun InformationCardExpandablePreview() {
    MyApplicationTheme {
        InformationCard(
            model = InformationCardModel(
                title = "Opening hours",
                subtitle = "open 24 hours",
                description = "L-D 24 hours",
                type = InformationCardModel.InformationCardType.EXPANDABLE
            )
        )
    }
}
