package com.albrivas.fuelpump.feature.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.albrivas.fuelpump.feature.splash.ui.SplashScreenRoute
import kotlinx.serialization.Serializable

const val splashRoute = "splash_route"
fun NavGraphBuilder.splashScreen(navigateToOnboarding: () -> Unit, navigateToHome: () -> Unit) {
    composable<SplashRoute> {
        SplashScreenRoute(
            navigateToOnboarding = navigateToOnboarding,
            navigateToHome = navigateToHome
        )
    }
}

@Serializable
data object SplashRoute
