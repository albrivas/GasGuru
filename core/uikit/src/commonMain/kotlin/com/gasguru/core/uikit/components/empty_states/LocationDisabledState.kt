package com.gasguru.core.uikit.components.empty_states

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.components.animation.GasGuruLottie
import com.gasguru.core.uikit.generated.resources.Res
import com.gasguru.core.uikit.generated.resources.location_disabled_button
import com.gasguru.core.uikit.generated.resources.location_disabled_description
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import org.jetbrains.compose.resources.stringResource

@Composable
fun LocationDisabledState(
    modifier: Modifier = Modifier,
    onEnableClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = GasGuruTheme.colors.neutral100)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GasGuruLottie(
            filePath = "files/enable_location.json",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
        )
        Text(
            text = stringResource(Res.string.location_disabled_description),
            style = GasGuruTheme.typography.baseRegular,
            textAlign = TextAlign.Center,
            color = GasGuruTheme.colors.textMain,
        )
        Button(
            onClick = onEnableClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = GasGuruTheme.colors.primary800,
            ),
        ) {
            Text(
                text = stringResource(Res.string.location_disabled_button),
                style = GasGuruTheme.typography.baseBold,
                color = GasGuruTheme.colors.neutralWhite,
            )
        }
    }
}

@Composable
@ThemePreviews
private fun LocationDisabledStatePreview() {
    MyApplicationTheme {
        LocationDisabledState(onEnableClick = {})
    }
}
