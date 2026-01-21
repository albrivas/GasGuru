package com.gasguru.core.uikit.components.segmented

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews

@Composable
fun HeaderFilterTabs(
    modifier: Modifier = Modifier,
    model: HeaderFilterTabsModel,
) = with(model) {
    var selectedIndex by remember { mutableIntStateOf(selectedTab) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.take(2).forEachIndexed { index, label ->
            FilterChip(
                selected = index == selectedIndex,
                onClick = {
                    selectedIndex = index
                    onSelectedTab(index)
                },
                label = {
                    Text(
                        text = label,
                        style = GasGuruTheme.typography.smallRegular
                    )
                },
                shape = CircleShape,
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = GasGuruTheme.colors.neutral400,
                    selected = index == selectedIndex,
                    enabled = true
                ),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GasGuruTheme.colors.primary800,
                    containerColor = GasGuruTheme.colors.neutralWhite,
                    labelColor = GasGuruTheme.colors.textMain,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
@ThemePreviews
private fun HeaderFilterTabsPreview() {
    MyApplicationTheme {
        HeaderFilterTabs(
            model = HeaderFilterTabsModel(
                tabs = listOf("Price", "Distance"),
                onSelectedTab = {}
            )
        )
    }
}
