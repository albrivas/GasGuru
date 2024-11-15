package com.gasguru.core.uikit.components.chip

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.R
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.Neutral400
import com.gasguru.core.uikit.theme.Primary800
import com.gasguru.core.uikit.theme.TextMain

@Composable
fun FilterChipGroup(
    modifier: Modifier = Modifier,
    model: FilterGroupChipModel,
) = with(model) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
    ) {
        val contentDesc = stringResource(id = R.string.filter_content_desc)
        filters.forEachIndexed { filterIndex, filter ->
            val selectedCount = filter.options.count { it.isSelected }
            val selectedOption = filter.options.find { it.isSelected }
            val filterLabel = when (filter.type) {
                FilterType.Brand -> stringResource(R.string.filter_brand, selectedCount)
                FilterType.NumberOfStations -> stringResource(R.string.filter_number_nearby)
                FilterType.Schedule ->
                    selectedOption?.label
                        ?: stringResource(R.string.filter_schedule)
            }

            FilterChip(
                modifier = Modifier.semantics { contentDescription = "$contentDesc $filterIndex" },
                selected = selectedCount > 0,
                enabled = true,
                onClick = { onFilterSelected(filter.type) },
                label = { Text(text = filterLabel, style = GasGuruTheme.typography.smallRegular) },
                shape = CircleShape,
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = Neutral400,
                    enabled = selectedCount > 0,
                    selected = selectedCount > 0
                ),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Primary800,
                    containerColor = Color.White,
                    disabledLabelColor = TextMain,
                    disabledContainerColor = Color.White,
                    disabledTrailingIconColor = TextMain,
                    labelColor = TextMain,
                    selectedLabelColor = Color.White,
                    selectedTrailingIconColor = Color.White
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Dropdown Icon",
                        modifier = Modifier
                            .size(FilterChipDefaults.IconSize)
                            .align(Alignment.CenterVertically)
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FilterChipPreview() {
    val filters = listOf(
        Filter(
            options = listOf(
                FilterOption(label = "Repsol", isSelected = true),
                FilterOption(label = "Cepsa", isSelected = true),
                FilterOption(label = "BP")
            ),
            isMultiSelect = true,
            type = FilterType.Brand
        ),
        Filter(
            options = listOf(
                FilterOption(label = "Open now", isSelected = false),
                FilterOption(label = "Open 24h", isSelected = false),
            ),
            isMultiSelect = false,
            type = FilterType.Schedule
        )
    )
    FilterChipGroup(model = FilterGroupChipModel(filters, onFilterSelected = { _ -> }))
}
