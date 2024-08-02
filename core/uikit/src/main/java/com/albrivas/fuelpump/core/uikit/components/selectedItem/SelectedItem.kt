package com.albrivas.fuelpump.core.uikit.components.selectedItem

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.albrivas.fuelpump.core.uikit.R
import com.albrivas.fuelpump.core.uikit.theme.GreenDark
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme

@Composable
fun BasicSelectedItem(
    modifier: Modifier = Modifier,
    model: BasicSelectedItemModel,
) = with(model) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (model.isRoundedItem) Modifier.clip(RoundedCornerShape(16.dp)) else Modifier)
            .selectable(
                selected = true,
                onClick = { onItemSelected(model) }
            )
            .then(
                if (model.isRoundedItem) {
                    Modifier.border(
                        width = 0.5.dp,
                        color = if (isSelected) GreenDark else Color.Black,
                        shape = RoundedCornerShape(16.dp)
                    )
                } else {
                    Modifier
                }
            )
            .padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Start,
            text = stringResource(id = title),
        )

        RadioButton(
            selected = isSelected,
            onClick = { onItemSelected(model) },
            colors = RadioButtonDefaults.colors(GreenDark),
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
            )
        )
    }
}
