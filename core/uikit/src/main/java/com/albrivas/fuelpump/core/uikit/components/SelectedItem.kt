package com.albrivas.fuelpump.core.uikit.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.albrivas.fuelpump.core.uikit.R
import com.albrivas.fuelpump.core.uikit.theme.GreenDark

@Composable
fun BasicSelectedItem(
    modifier: Modifier = Modifier,
    model: BasicSelectedItemModel,
    onItemSelected: (BasicSelectedItemModel) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
                selected = true,
                onClick = { onItemSelected(model) }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Start,
            text = stringResource(id = model.title),
            fontWeight = FontWeight.Bold
        )

        RadioButton(
            selected = model.isSelected,
            onClick = { onItemSelected(model) },
            colors = RadioButtonDefaults.colors(GreenDark),
        )
    }
}

@Composable
@Preview(name = "List - Selected fuel preview")
private fun PreviewBasicSelectedItem() {
    val previewModel = BasicSelectedItemModel(R.string.preview_fuel_type, true)
    BasicSelectedItem(model = previewModel) { }
}
