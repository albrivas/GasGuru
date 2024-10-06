package com.albrivas.fuelpump.core.uikit.components.selectedItem

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.albrivas.fuelpump.core.uikit.R
import com.albrivas.fuelpump.core.uikit.theme.FuelPumpTheme
import com.albrivas.fuelpump.core.uikit.theme.GreenDark
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme
import com.albrivas.fuelpump.core.uikit.theme.Neutral300
import com.albrivas.fuelpump.core.uikit.theme.Neutral500
import com.albrivas.fuelpump.core.uikit.theme.Primary600

@Composable
fun BasicSelectedItem(
    modifier: Modifier = Modifier,
    model: BasicSelectedItemModel,
) = with(model) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .then(if (model.isRoundedItem) Modifier.clip(RoundedCornerShape(8.dp)) else Modifier)
            .selectable(
                selected = true,
                onClick = { onItemSelected(model) }
            )
            .then(
                if (model.isRoundedItem) {
                    Modifier.border(
                        width = if(isSelected) 2.dp else 0.5.dp,
                        color = if (isSelected) Primary600 else Neutral300,
                        shape = RoundedCornerShape(8.dp)
                    )
                } else {
                    Modifier
                }
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp)),
            painter = painterResource(id = image),
            contentScale = ContentScale.Crop,
            contentDescription = "Icon fuel"
        )
        Text(
            modifier = Modifier.weight(1f).padding(start = 8.dp),
            style = if (isSelected) FuelPumpTheme.typography.baseBold else FuelPumpTheme.typography.baseRegular,
            color = Color.Black,
            textAlign = TextAlign.Start,
            text = stringResource(id = title),
        )

        RadioButton(
            selected = isSelected,
            onClick = { onItemSelected(model) },
            colors = RadioButtonDefaults.colors(selectedColor = GreenDark, unselectedColor = Neutral500),
            modifier = Modifier.testTag("radio_button_$title")
        )
    }
}

@Composable
@Preview(name = "Item selected", backgroundColor = 0xFFFFFFFF, showBackground = true)
private fun BasicSelectedItemPreview() {
    MyApplicationTheme {
        BasicSelectedItem(
            model = BasicSelectedItemModel(
                title = R.string.preview_fuel_type,
                isSelected = true,
                isRoundedItem = true,
                image = R.drawable.ic_gasoline_95
            )
        )
    }
}

@Composable
@Preview(name = "Item not selected", backgroundColor = 0xFFFFFFFF, showBackground = true)
private fun BasicItemPreview() {
    MyApplicationTheme {
        BasicSelectedItem(
            model = BasicSelectedItemModel(
                title = R.string.preview_fuel_type,
                isSelected = false,
                isRoundedItem = true,
                image = R.drawable.ic_gasoline_95
            )
        )
    }
}
