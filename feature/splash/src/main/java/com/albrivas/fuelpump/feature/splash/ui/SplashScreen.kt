package com.albrivas.fuelpump.feature.splash.ui

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.albrivas.fuelpump.core.uikit.R
import com.albrivas.fuelpump.core.ui.GreenPrimary
import com.albrivas.fuelpump.feature.splash.viewmodel.SplashViewModel

@Composable
fun SplashScreenRoute(navigateToOnboarding: () -> Unit, navigateToHome: () -> Unit) {
    SplashScreen(navigateToOnboarding, navigateToHome)
}

@Composable
internal fun SplashScreen(
    navigateToOnboarding: () -> Unit = {},
    navigateToHome: () -> Unit = {},
    viewModel: SplashViewModel = hiltViewModel(),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenPrimary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_fuel_pump),
            contentDescription = "",
        )
    }
}

@Composable
@Preview
private fun SplashScreenPreview() {
    SplashScreen()
}