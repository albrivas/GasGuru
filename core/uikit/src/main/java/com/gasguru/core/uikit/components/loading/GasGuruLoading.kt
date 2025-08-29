package com.gasguru.core.uikit.components.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews

@Composable
fun GasGuruLoading(
    modifier: Modifier = Modifier,
    model: GasGuruLoadingModel,
) = with(model) {
    Box(
        modifier = modifier.background(color = GasGuruTheme.colors.neutral100),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = color,
        )
    }
}

@Composable
@ThemePreviews
private fun GasGuruLoadingPreview() {
    MyApplicationTheme {
        GasGuruLoading(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            model = GasGuruLoadingModel(color = GasGuruTheme.colors.primary800)
        )
    }
}
