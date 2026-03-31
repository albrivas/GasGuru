package com.gasguru.core.uikit.components.selectedItem

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.R
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import com.gasguru.core.uikit.utils.backgroundColor

@Composable
fun SelectedItem(
    modifier: Modifier = Modifier,
    model: SelectedItemModel,
) = with(model) {
    val backgroundColor = if (isSelected) {
        GasGuruTheme.colors.accentGreen.copy(alpha = 0.2f)
    } else {
        GasGuruTheme.colors.neutral200
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color = backgroundColor)
            .semantics { this.backgroundColor = backgroundColor }
            .selectable(
                selected = isSelected,
                onClick = { onItemSelected(model) },
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp)),
            painter = painterResource(id = image),
            contentScale = ContentScale.Crop,
            contentDescription = "Icon fuel",
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp),
            style = if (isSelected) GasGuruTheme.typography.baseBold else GasGuruTheme.typography.baseRegular,
            color = GasGuruTheme.colors.neutralBlack,
            textAlign = TextAlign.Start,
            text = stringResource(id = title),
        )
        RadioButton(
            selected = isSelected,
            onClick = { onItemSelected(model) },
            colors = RadioButtonDefaults.colors(
                selectedColor = GasGuruTheme.colors.accentGreen,
                unselectedColor = GasGuruTheme.colors.neutral400,
            ),
            modifier = Modifier.testTag("radio_button_$title"),
        )
    }
}

@Composable
@ThemePreviews
private fun SelectedItemPreview() {
    MyApplicationTheme {
        Column(modifier = Modifier.background(GasGuruTheme.colors.neutral100)) {
            SelectedItem(
                model = SelectedItemModel(
                    title = R.string.preview_fuel_type,
                    isSelected = true,
                    image = R.drawable.ic_gasoline_95,
                ),
            )
        }
    }
}

@Composable
@ThemePreviews
private fun ItemPreview() {
    MyApplicationTheme {
        Column(modifier = Modifier.background(GasGuruTheme.colors.neutral100)) {
            SelectedItem(
                model = SelectedItemModel(
                    title = R.string.preview_fuel_type,
                    isSelected = false,
                    image = R.drawable.ic_gasoline_95,
                ),
            )
        }
    }
}
