package com.gasguru.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gasguru.R
import com.gasguru.core.uikit.components.alert_bar.AlertBar
import com.gasguru.core.uikit.components.alert_bar.AlertBarModel
import com.gasguru.feature.onboarding_welcome.navigation.OnboardingRoutes
import com.gasguru.navigation.DeepLinkManager
import com.gasguru.navigation.root.GasGuruNavHost

@Composable
fun GasGuruApp(
    appState: GasGuruAppState,
    deepLinkManager: DeepLinkManager,
    startDestination: Any = OnboardingRoutes.OnboardingWelcomeRoute,
) {
    val isOffline by appState.isOffline.collectAsStateWithLifecycle()
    var showOfflineAlert by remember { mutableStateOf(false) }

    LaunchedEffect(isOffline) {
        showOfflineAlert = isOffline
    }

    Box(Modifier.fillMaxSize()) {
        GasGuruNavHost(
            deepLinkManager = deepLinkManager,
            startDestination = startDestination
        )

        AnimatedVisibility(
            visible = showOfflineAlert,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(
                    WindowInsets.statusBars.only(WindowInsetsSides.Top)
                )
        ) {
            AlertBar(
                modifier = Modifier.padding(16.dp),
                model = AlertBarModel(
                    message = stringResource(id = R.string.not_connected),
                    onDismiss = { showOfflineAlert = false }
                )
            )
        }
    }
}
