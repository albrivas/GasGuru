package com.gasguru.composeApp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.analytics.LocalAnalyticsHelper
import com.gasguru.core.data.util.NetworkMonitor
import com.gasguru.core.domain.location.IsLocationEnabledUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.feature.onboarding_welcome.navigation.OnboardingRoutes
import com.gasguru.navigation.LocalDeepLinkStateHolder
import com.gasguru.navigation.LocalNavigationManager
import com.gasguru.navigation.deeplink.DeepLinkStateHolder
import com.gasguru.navigation.manager.NavigationManager
import com.gasguru.ui.GasGuruApp
import com.gasguru.ui.rememberGasGuruAppState
import org.koin.compose.koinInject

@Composable
fun App(
    themeMode: ThemeMode,
    onOpenLocationSettings: () -> Unit,
    startDestination: Any = OnboardingRoutes.NewOnboardingRoute,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val networkMonitor: NetworkMonitor = koinInject()
    val isLocationEnabledUseCase: IsLocationEnabledUseCase = koinInject()
    val getUserDataUseCase: GetUserDataUseCase = koinInject()
    val navigationManager: NavigationManager = koinInject()
    val deepLinkStateHolder: DeepLinkStateHolder = koinInject()
    val analyticsHelper: AnalyticsHelper = koinInject()

    val appState = rememberGasGuruAppState(
        networkMonitor = networkMonitor,
        isLocationEnabledUseCase = isLocationEnabledUseCase,
        getUserDataUseCase = getUserDataUseCase,
    )

    CompositionLocalProvider(
        LocalNavigationManager provides navigationManager,
        LocalDeepLinkStateHolder provides deepLinkStateHolder,
        LocalAnalyticsHelper provides analyticsHelper,
    ) {
        MyApplicationTheme(darkTheme = darkTheme) {
            GasGuruApp(
                appState = appState,
                onOpenLocationSettings = onOpenLocationSettings,
                startDestination = startDestination,
            )
        }
    }
}
