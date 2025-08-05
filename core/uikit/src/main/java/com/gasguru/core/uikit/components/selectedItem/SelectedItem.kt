package com.gasguru.core.uikit.components.selectedItem

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.R
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews

@Composable
fun BasicSelectedItem(
    modifier: Modifier = Modifier,
    model: BasicSelectedItemModel,
) = with(model) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (model.isRoundedItem) Modifier.clip(RoundedCornerShape(8.dp)) else Modifier)
            .background(color = GasGuruTheme.colors.neutralWhite)
            .selectable(
                selected = true,
                onClick = { onItemSelected(model) }
            )
            .then(
                if (model.isRoundedItem) {
                    Modifier.border(
                        width = if (isSelected) 2.dp else 0.5.dp,
                        color = if (isSelected) GasGuruTheme.colors.primary600 else GasGuruTheme.colors.neutral300,
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
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            style = if (isSelected) GasGuruTheme.typography.baseBold else GasGuruTheme.typography.baseRegular,
            color = GasGuruTheme.colors.neutralBlack,
            textAlign = TextAlign.Start,
            text = stringResource(id = title),
        )

        RadioButton(
            selected = isSelected,
            onClick = { onItemSelected(model) },
            colors = RadioButtonDefaults.colors(
                selectedColor = GasGuruTheme.colors.primary600,
                unselectedColor = GasGuruTheme.colors.neutral500
            ),
            modifier = Modifier.testTag("radio_button_$title")
        )
    }
}

@Composable
@ThemePreviews
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
@ThemePreviews
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
