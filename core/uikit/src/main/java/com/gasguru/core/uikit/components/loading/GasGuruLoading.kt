package com.gasguru.core.uikit.components.loading

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.Primary800

@Composable
fun GasGuruLoading(
    modifier: Modifier = Modifier,
    model: GasGuruLoadingModel,
) = with(model) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GasGuruLoadingPreview() {
    MyApplicationTheme {
        GasGuruLoading(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            model = GasGuruLoadingModel(color = Primary800)
        )
    }
}
