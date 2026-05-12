package com.gasguru.core.uikit.components.information_card

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.generated.resources.Res
import com.gasguru.core.uikit.generated.resources.ic_direction
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import org.jetbrains.compose.resources.painterResource

@Composable
fun InformationCard(model: InformationCardModel) = with(model) {
    var open by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color = GasGuruTheme.colors.neutralWhite)
            .border(1.dp, GasGuruTheme.colors.neutral300, RoundedCornerShape(8.dp))
            .clickable {
                if (type == InformationCardModel.InformationCardType.EXPANDABLE) {
                    open = !open
                } else {
                    onClick()
                }
            }
            .padding(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = GasGuruTheme.typography.smallRegular,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = GasGuruTheme.colors.textSubtle,
                )
                Text(
                    text = subtitle,
                    style = GasGuruTheme.typography.smallRegular,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 8,
                    color = subtitleColor,
                )
            }

            when (type) {
                InformationCardModel.InformationCardType.NONE -> {
                    icon?.let {
                        Image(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { onClick() }
                                .padding(start = 12.dp),
                            painter = painterResource(icon),
                            contentDescription = "Icon direction",
                        )
                    }
                }

                InformationCardModel.InformationCardType.EXPANDABLE -> {
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .clickable { open = !open },
                        imageVector = if (open) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = GasGuruTheme.colors.neutral500,
                    )
                }
            }
        }

        description?.let {
            AnimatedVisibility(visible = open) {
                Text(
                    text = description,
                    style = GasGuruTheme.typography.smallRegular,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp),
                    color = GasGuruTheme.colors.textMain,
                )
            }
        }
    }
}

@Composable
@ThemePreviews
private fun InformationCardDefaultPreview() {
    MyApplicationTheme {
        InformationCard(
            model = InformationCardModel(
                title = "Direccion",
                subtitle = "Avenida de la constitucion 1, 10D",
                icon = Res.drawable.ic_direction,
                onClick = {},
                type = InformationCardModel.InformationCardType.NONE,
                subtitleColor = GasGuruTheme.colors.textMain,
            ),
        )
    }
}

@Composable
@ThemePreviews
private fun InformationCardExpandablePreview() {
    MyApplicationTheme {
        InformationCard(
            model = InformationCardModel(
                title = "Opening hours",
                subtitle = "open 24 hours",
                description = "L-D 24 hours",
                type = InformationCardModel.InformationCardType.EXPANDABLE,
                subtitleColor = GasGuruTheme.colors.textMain,
            ),
        )
    }
}
