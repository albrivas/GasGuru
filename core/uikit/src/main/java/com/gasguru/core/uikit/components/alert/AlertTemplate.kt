package com.gasguru.core.uikit.components.alert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gasguru.core.uikit.R
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.GrayLight
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.Neutral100

@Composable
fun AlertTemplate(model: AlertTemplateModel) {
    with(model) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animation))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Neutral100)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            LottieAnimation(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(androidx.compose.ui.Alignment.CenterHorizontally),
                composition = composition,
                iterations = LottieConstants.IterateForever,
            )
            Text(
                text = description,
                color = GrayLight,
                textAlign = TextAlign.Center,
                style = GasGuruTheme.typography.baseRegular
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AlertMoleculePreview() {
    MyApplicationTheme {
        AlertTemplate(
            model = AlertTemplateModel(
                animation = 0,
                description = stringResource(R.string.alert_template_description_preview),
            )
        )
    }
}
