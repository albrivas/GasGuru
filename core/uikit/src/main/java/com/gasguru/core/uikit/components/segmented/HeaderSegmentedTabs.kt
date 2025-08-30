package com.gasguru.core.uikit.components.segmented

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderSegmentedTabs(
    modifier: Modifier = Modifier,
    model: HeaderSegmentedTabsModel,
) = with(model) {
    var selectedIndex by remember { mutableIntStateOf(selectedTab) }

    SingleChoiceSegmentedButtonRow(space = 1.dp, modifier = modifier) {
        tabs.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = tabs.size),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = GasGuruTheme.colors.primary700,
                    inactiveContainerColor = GasGuruTheme.colors.neutralWhite
                ),
                onClick = {
                    selectedIndex = index
                    onSelectedTab(index)
                },
                selected = index == selectedIndex,
                icon = {}
            ) {
                Text(
                    text = label,
                    color = GasGuruTheme.colors.textMain,
                    style = GasGuruTheme.typography.baseRegular
                )
            }
        }
    }
}

@Composable
@ThemePreviews
private fun SegmentedButtonPreview() {
    MyApplicationTheme {
        HeaderSegmentedTabs(
            model = HeaderSegmentedTabsModel(
                tabs = listOf("Price", "Distance"),
                onSelectedTab = {}
            )
        )
    }
}
