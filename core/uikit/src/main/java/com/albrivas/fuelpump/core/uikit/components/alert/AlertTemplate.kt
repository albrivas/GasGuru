package com.albrivas.fuelpump.core.uikit.components.alert

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.albrivas.fuelpump.core.uikit.components.FuelPumpButton
import com.albrivas.fuelpump.core.uikit.theme.GrayLight
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme

@Composable
fun AlertTemplate(model: AlertTemplateModel) {
    with(model) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animation))

        Scaffold(
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
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
                        text = description, color = GrayLight,
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.displayMedium
                    )
                }
            },
            bottomBar = {
                FuelPumpButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    text = buttonText,
                    onClick = onClick
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AlertMoleculePreview() {
    MyApplicationTheme {
        AlertTemplate(
            model = AlertTemplateModel(
                animation = 0,
                description = "Para poder ver las estaciones cercanas a tu ubicación necesitamos que actives la localización en tu movil",
                buttonText = "Activar localización",
                onClick = {}
            )
        )
    }
}