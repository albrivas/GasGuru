package com.albrivas.fuelpump.core.uikit.components.information_card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.albrivas.fuelpump.core.uikit.R
import com.albrivas.fuelpump.core.uikit.theme.FuelPumpTheme
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme
import com.albrivas.fuelpump.core.uikit.theme.Neutral300
import com.albrivas.fuelpump.core.uikit.theme.TextMain
import com.albrivas.fuelpump.core.uikit.theme.TextSubtle

@Composable
fun InformationCard(model: InformationCardModel) = with(model) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color = Color.White)
            .border(1.dp, Neutral300, RoundedCornerShape(8.dp))
            .clickable { onClick() }
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
                    text = description,
                    style = FuelPumpTheme.typography.smallRegular,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier,
                    color = TextMain
                )
            }

            Image(
                modifier = Modifier.constrainAs(image) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }.clickable { onClick() },
                painter = painterResource(id = icon),
                contentDescription = "Icon direction"
            )
        }
    }
}

@Preview
@Composable
private fun InformationCardPreview() {
    MyApplicationTheme {
        InformationCard(
            model = InformationCardModel(
                title = "Direccion",
                description = "Avenida de la constitucion 1, 10D",
                icon = R.drawable.ic_direction,
                onClick = {}
            )
        )
    }
}
