package com.gasguru.core.uikit.components.search_area

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews

@Composable
fun SearchThisAreaButton(
    model: SearchThisAreaButtonModel,
    modifier: Modifier = Modifier,
) = with(model) {
    Surface(
        onClick = onClick,
        enabled = !isLoading,
        modifier = modifier,
        shape = CircleShape,
        color = GasGuruTheme.colors.neutralWhite,
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = GasGuruTheme.colors.primary600,
                )
            }
            Text(
                text = text,
                style = GasGuruTheme.typography.smallBold,
                color = GasGuruTheme.colors.primary600,
            )
        }
    }
}

@Composable
@ThemePreviews
private fun SearchThisAreaButtonPreview() {
    MyApplicationTheme {
        SearchThisAreaButton(
            model = SearchThisAreaButtonModel(
                text = "Search this area",
                isLoading = false,
                onClick = {},
            ),
        )
    }
}

@Composable
@ThemePreviews
private fun SearchThisAreaButtonLoadingPreview() {
    MyApplicationTheme {
        SearchThisAreaButton(
            model = SearchThisAreaButtonModel(
                text = "Search this area",
                isLoading = true,
                onClick = {},
            ),
        )
    }
}
