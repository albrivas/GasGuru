package com.gasguru.core.uikit.components.chip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
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
import com.gasguru.core.uikit.theme.primaryContainerLight

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterChip(
    modifier: Modifier = Modifier,
    model: FilterChipModel,
) = with(model) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
    ) {
        val contentDesc = stringResource(id = R.string.filter_content_desc)
        options.forEachIndexed { index, label ->
            FilterChip(
                modifier = Modifier.semantics { contentDescription = "$contentDesc $index" },
                selected = selectedChip == index,
                enabled = enabled,
                label = { Text(text = label) },
                shape = CircleShape,
                border = FilterChipDefaults.filterChipBorder(
                    enabled = false,
                    selected = selectedChip == index,
                    disabledBorderColor = Color.Transparent
                ),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = primaryContainerLight,
                    containerColor = Color.LightGray
                ),
                onClick = {
                    onFilterSelected(index)
                },
                leadingIcon = if (selectedChip == index) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
            )
        }
    }
}

@Preview
@Composable
private fun FilterChipPreview() {
    FilterChip(model = FilterChipModel(options = listOf("All", "Favorites"), onFilterSelected = {}))
}
