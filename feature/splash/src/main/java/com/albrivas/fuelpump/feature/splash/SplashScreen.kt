package com.albrivas.fuelpump.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.albrivas.fuelpump.core.uikit.R

import androidx.navigation.NavHostController
import com.albrivas.fuelpump.core.ui.GreenPrimary

@Composable
fun SplashScreen(navController: NavHostController) {
    Splash()
}

@Composable
fun Splash() {

    Column(
        modifier = Modifier.fillMaxSize().background(GreenPrimary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_fuel_pump),
            contentDescription = ""
        )
    }
}

@Composable
@Preview
fun SplashScreenPreview() {
    Splash()
}