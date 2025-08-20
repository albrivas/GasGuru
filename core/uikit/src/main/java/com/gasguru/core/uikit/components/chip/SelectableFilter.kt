package com.gasguru.core.uikit.components.chip

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.gasguru.core.uikit.R
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.ThemePreviews

@Composable
fun SelectableFilter(
    modifier: Modifier = Modifier,
    model: SelectableFilterModel,
) = with(model) {
    val label = if (isSelected) selectedLabel else label
    val contentDesc = stringResource(id = R.string.filter_content_desc)

    FilterChip(
        modifier = modifier.semantics { contentDescription = "$contentDesc $label" },
        selected = isSelected,
        enabled = true,
        onClick = { onFilterClick(filterType) },
        label = { Text(text = label, style = GasGuruTheme.typography.smallRegular) },
        shape = CircleShape,
        border = FilterChipDefaults.filterChipBorder(
            borderColor = GasGuruTheme.colors.neutral400,
            selected = isSelected,
            enabled = isSelected
        ),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = GasGuruTheme.colors.primary800,
            containerColor = GasGuruTheme.colors.neutralWhite,
            disabledLabelColor = GasGuruTheme.colors.textMain,
            disabledContainerColor = GasGuruTheme.colors.neutralWhite,
            disabledTrailingIconColor = GasGuruTheme.colors.textMain,
            labelColor = GasGuruTheme.colors.textMain,
            iconColor = GasGuruTheme.colors.textMain,
            selectedLabelColor = Color.White,
            selectedTrailingIconColor = Color.White,
        ),
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Dropdown Icon",
                modifier = Modifier
                    .size(FilterChipDefaults.IconSize)
            )
        }
    )
}

@Composable
@ThemePreviews
private fun SelectableFilterMultiSelectionPreview() {
    SelectableFilter(
        model = SelectableFilterModel(
            filterType = FilterType.Brand,
            label = "Brand",
            selectedLabel = "Brands (3)",
            isSelected = true,
            onFilterClick = {}
        )
    )
}

@Composable
@ThemePreviews
private fun SelectableFilterUnselectedPreview() {
    SelectableFilter(
        model = SelectableFilterModel(
            filterType = FilterType.Brand,
            label = "Schedule",
            selectedLabel = "Open now",
            isSelected = false,
            onFilterClick = {}
        )
    )
}
